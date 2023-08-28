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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ExpenseController {
    private final BuildingService buildingService;
    private final ExpenseService expenseService;
    private final TaxService taxService;
    private final ApartmentService apartmentService;

    @Autowired
    public ExpenseController(BuildingService buildingService, ExpenseService expenseService, TaxService taxService, ApartmentService apartmentService) {
        this.buildingService = buildingService;
        this.expenseService = expenseService;
        this.taxService = taxService;
        this.apartmentService = apartmentService;
    }

    @PreAuthorize("principal.username == @buildingServiceImpl.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/add-expense")
    public ModelAndView addExpenseAsManager(@PathVariable("buildingId") Long buildingId, Model model) {
        if (!model.containsAttribute("expenseAddDTO")) {
            model.addAttribute("expenseAddDTO", new ExpenseAddDTO().setSelectedApartments(new ArrayList<>()));
        }
        ModelAndView mav = new ModelAndView("add-expense-as-manager");
        List<ApartmentEntity> apartmentsInBuilding = apartmentService.findAllApartmentsByBuildingId(buildingId);
        mav.addObject("apartments", apartmentsInBuilding);
        mav.addObject("taxTypes", TaxTypeEnum.values());
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        mav.addObject("building", buildingService.findById(buildingId));
        return mav;
    }

    @PreAuthorize("principal.username == @buildingServiceImpl.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
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
        if (expenseAddDTO.getSelectedApartments().isEmpty()) {
            expenseService.addExpenseToBuilding(expenseAddDTO, buildingId);
        } else {
            expenseService.addExpenseAndTaxesForItToBuilding(expenseAddDTO, buildingId);
        }
        return String.format("redirect:/manager/buildings/%d/expenses", buildingId);
    }

    @PreAuthorize("principal.username == @buildingServiceImpl.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/expense/{expenseId}")
    public ModelAndView editExpenseStatusAsManager(@PathVariable("buildingId") Long buildingId,
                                                   @PathVariable("expenseId") Long expenseId,
                                                   Model model) {
        if (!model.containsAttribute("expenseEditDTO")) {
            model.addAttribute("expenseEditDTO", new ExpenseEditDTO());
        }
        ExpenseViewDTO expenseView = expenseService.findViewById(expenseId);
        ModelAndView mav = new ModelAndView("edit-expense-as-manager");
        mav.addObject("taxStatus", TaxStatusEnum.values());
        mav.addObject("building", buildingService.findById(buildingId));
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        mav.addObject("expense", expenseView);
        return mav;
    }

    @PreAuthorize("principal.username == @buildingServiceImpl.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/manager/buildings/{buildingId}/expense/{expenseId}")
    public String editExpenseStatusAsManagerConfirm(@Valid ExpenseEditDTO expenseEditDTO,
                                                    BindingResult bindingResult,
                                                    RedirectAttributes redirectAttributes,
                                                    @PathVariable("buildingId") Long buildingId,
                                                    @PathVariable("expenseId") Long expenseId) {
        expenseEditDTO.setId(expenseId);
        if (bindingResult.hasErrors() || !buildingService.checkIfBalanceIsEnoughToPayExpense(buildingId, expenseId)) {
            redirectAttributes.addFlashAttribute("expenseEditDTO", expenseEditDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.expenseEditDTO", bindingResult);
            redirectAttributes.addFlashAttribute("notEnoughBalance", true);
            return String.format("redirect:/manager/buildings/%d/expense/%d",
                    buildingId, expenseId);
        }
        expenseService.updateExpenseStatus(expenseEditDTO);
        return String.format("redirect:/manager/buildings/%d/expenses", buildingId);
    }

    @PreAuthorize("principal.username == @buildingServiceImpl.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
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

    @PreAuthorize("principal.username == @buildingServiceImpl.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/expenses")
    public ModelAndView viewExpensesAsManager(@PathVariable("buildingId") Long buildingId,
                                              @RequestParam(name = "page", defaultValue = "1") Integer page,
                                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        ModelAndView mav = new ModelAndView("view-expenses-as-manager");
        Page<ExpenseEntity> expensesPage = getAllExpensesByBuildingIdPaginated(buildingId, page, size);
        addPageModelsToMAV(mav, expensesPage);
        mav.addObject("expensesPage", expensesPage);
        addBuildingInformationAsModels(buildingId, mav);
        return mav;
    }

    @PreAuthorize("@buildingServiceImpl.checkIfUserIsANeighbour(principal.username,#buildingId)")
    @GetMapping("/neighbour/buildings/{buildingId}/expenses")
    public ModelAndView viewExpensesAsNeighbour(@PathVariable("buildingId") Long buildingId,
                                                @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        ModelAndView mav = new ModelAndView("view-expenses-as-neighbour");
        Page<ExpenseEntity> expensesPage = getAllExpensesByBuildingIdPaginated(buildingId, page, size);
        addPageModelsToMAV(mav, expensesPage);
        mav.addObject("expensesPage", expensesPage);
        addBuildingInformationAsModels(buildingId, mav);
        return mav;
    }

    private void addBuildingInformationAsModels(Long buildingId, ModelAndView mav) {
        BuildingViewDTO building = buildingService.findById(buildingId);
        mav.addObject("building", building);
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(buildingId));
    }

    private Page<ExpenseEntity> getAllExpensesByBuildingIdPaginated(Long buildingId, Integer page, Integer size) {
        return expenseService
                .findAllExpensesByBuildingIdPaginated(PageRequest.of(page - 1, size), buildingId);
    }

    private static void addPageModelsToMAV(ModelAndView mav, Page<ExpenseEntity> expensesPage) {
        int totalPages = expensesPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            mav.addObject("pageNumbers", pageNumbers);
        }
    }
}
