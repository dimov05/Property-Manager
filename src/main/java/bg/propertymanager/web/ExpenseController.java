package bg.propertymanager.web;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.expense.ExpenseAddDTO;
import bg.propertymanager.model.dto.expense.ExpenseEditDTO;
import bg.propertymanager.model.dto.expense.ExpenseViewDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.ExpenseEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;
import bg.propertymanager.service.ApartmentService;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.ExpenseService;
import bg.propertymanager.service.TaxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ExpenseController {
    private final BuildingService buildingService;
    private final ExpenseService expenseService;
    private final TaxService taxService;
    private final ApartmentService apartmentService;

    public ExpenseController(BuildingService buildingService, ExpenseService expenseService, TaxService taxService, ApartmentService apartmentService) {
        this.buildingService = buildingService;
        this.expenseService = expenseService;
        this.taxService = taxService;
        this.apartmentService = apartmentService;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/expenses")
    public String viewExpensesAsManager(@PathVariable("buildingId") Long buildingId,
                                        @RequestParam(name = "page") Optional<Integer> page,
                                        @RequestParam(name = "size") Optional<Integer> size,
                                        Model model) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        BuildingViewDTO building = buildingService.findById(buildingId);
        Page<ExpenseEntity> expensesPage = expenseService
                .findAllExpensesByBuildingIdPaginated(PageRequest.of(currentPage - 1, pageSize), buildingId);
        int totalPages = expensesPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("expensesPage", expensesPage);
        model.addAttribute("building", building);
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        return "view-expenses-as-manager";
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/add-expense")
    public String addExpenseAsManager(@PathVariable("buildingId") Long buildingId, Model model) {
        if (!model.containsAttribute("expenseAddDTO")) {
            model.addAttribute("expenseAddDTO", new ExpenseAddDTO().setSelectedApartments(new ArrayList<>()));
        }
        List<ApartmentEntity> apartmentsInBuilding = apartmentService.findAllApartmentsInBuilding(buildingId);
        model.addAttribute("apartments", apartmentsInBuilding);
        model.addAttribute("taxTypes", TaxTypeEnum.values());
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        model.addAttribute("building", buildingService.findById(buildingId));
        return "add-expense-as-manager";
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/manager/buildings/{buildingId}/add-expense")
    public String addExpenseAsManagerConfirm(@Valid ExpenseAddDTO expenseAddDTO,
                                             BindingResult bindingResult,
                                             RedirectAttributes redirectAttributes,
                                             @PathVariable("buildingId") Long buildingId) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("expenseAddDTO", expenseAddDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.expenseAddDTO", bindingResult);
            return String.format("redirect:/manager/buildings/%d/add-expense", buildingId);
        }
        expenseService.addExpenseToBuilding(expenseAddDTO, buildingId);
        return String.format("redirect:/manager/buildings/%d/expenses", buildingId);
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/expense/{expenseId}")
    public String editExpenseStatusAsManager(@PathVariable("buildingId") Long buildingId,
                                             @PathVariable("expenseId") Long expenseId,
                                             Model model) {
        if (!model.containsAttribute("expenseEditDTO")) {
            model.addAttribute("expenseEditDTO", new ExpenseEditDTO());
        }
        ExpenseViewDTO expenseView = expenseService.findViewById(expenseId);
        model.addAttribute("taxStatus", TaxStatusEnum.values());
        model.addAttribute("building", buildingService.findById(buildingId));
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        model.addAttribute("expense", expenseView);
        return "edit-expense-as-manager";
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/manager/buildings/{buildingId}/expense/{expenseId}")
    public String editTaxStatusAsManagerConfirm(@Valid ExpenseEditDTO expenseEditDTO,
                                                BindingResult bindingResult,
                                                RedirectAttributes redirectAttributes,
                                                @PathVariable("buildingId") Long buildingId,
                                                @PathVariable("expenseId") Long expenseId) {
        expenseEditDTO.setId(expenseId);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("expenseEditDTO", expenseEditDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.expenseEditDTO", bindingResult);
            return String.format("redirect:/manager/buildings/%d/expense/%d",
                    buildingId, expenseId);
        }
        expenseService.updateExpenseStatus(expenseEditDTO);
        return String.format("redirect:/manager/buildings/%d/expenses", buildingId);
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/manager/buildings/{buildingId}/delete-expense/{expenseId}")
    public String deleteMessageConfirm(@PathVariable("buildingId") Long buildingId,
                                       @PathVariable("expenseId") Long expenseId,
                                       Principal principal) {
        if (expenseService.deleteExpenseWithId(expenseId)) {
            return String.format("redirect:/manager/buildings/%d/expenses",
                    buildingId);
        } else {
            return "error-delete-expense";
        }

    }
}
