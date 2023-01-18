package bg.propertymanager.web;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.tax.TaxEditDTO;
import bg.propertymanager.model.dto.tax.TaxPayDTO;
import bg.propertymanager.model.dto.tax.TaxViewDTO;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.repository.ApartmentRepository;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.TaxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class TaxController {
    private final TaxService taxService;
    private final BuildingService buildingService;

    public TaxController(TaxService taxService, BuildingService buildingService,
                         ApartmentRepository apartmentRepository) {
        this.taxService = taxService;
        this.buildingService = buildingService;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/taxes")
    public String viewTaxesAsManager(@PathVariable("buildingId") Long buildingId,
                                     @RequestParam(value = "page", defaultValue = "1") Integer page,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size,
                                     Model model) {
        BuildingViewDTO building = buildingService.findById(buildingId);
        // Implement search/filter with keyword just by fixing Controller. The other logic in Service and Repository is ready
        Page<TaxEntity> taxesPage = taxService
                .findAllTaxesByBuildingIdFilteredAndPaginated(PageRequest.of(page - 1, size), buildingId, null);
        addPageNumbersAttributeIfThereAreEnoughPages(model, taxesPage);
        model.addAttribute("taxesPage", taxesPage);
        model.addAttribute("building", building);
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        return "view-taxes-as-manager";
    }

    @PreAuthorize("@buildingService.checkIfUserIsANeighbour(principal.username,#buildingId)")
    @GetMapping("/neighbour/buildings/{buildingId}/taxes")
    public String viewMyTaxesAsNeighbour(@PathVariable("buildingId") Long buildingId,
                                         @RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         Model model,
                                         Principal principal) {
        BuildingViewDTO building = buildingService.findById(buildingId);

        Page<TaxEntity> taxesPage = taxService
                .findALlMyTaxesByBuildingIdPaginated(PageRequest.of(page - 1, size), buildingId, principal.getName());
        addPageNumbersAttributeIfThereAreEnoughPages(model, taxesPage);
        model.addAttribute("taxesPage", taxesPage);
        model.addAttribute("building", building);
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        return "view-my-taxes-as-neighbour";
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/taxes-neighbour/{neighbourId}")
    public String viewTaxesOfNeighbourAsManager(@PathVariable("buildingId") Long buildingId,
                                                @PathVariable("neighbourId") Long neighbourId,
                                                @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                Model model) {
        BuildingViewDTO building = buildingService.findById(buildingId);
        Page<TaxEntity> taxesPage = taxService
                .findAllTaxesByBuildingIdAndOwnerId(PageRequest.of(page - 1, size), buildingId, neighbourId);
        addPageNumbersAttributeIfThereAreEnoughPages(model, taxesPage);
        model.addAttribute("taxesPage", taxesPage);
        model.addAttribute("building", building);
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        return "view-taxes-of-neighbour-as-manager";
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/tax/{taxId}")
    public String editTaxStatusAsManager(@PathVariable("buildingId") Long buildingId,
                                         @PathVariable("taxId") Long taxId,
                                         Model model) {
        if (!model.containsAttribute("taxEditDTO")) {
            model.addAttribute("taxEditDTO", new TaxEditDTO());
        }
        BuildingViewDTO buildingEdit = buildingService.findById(buildingId);
        TaxViewDTO taxView = taxService.findViewById(taxId);
        model.addAttribute("taxStatus", TaxStatusEnum.values());
        model.addAttribute("building", buildingEdit);
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        model.addAttribute("tax", taxView);
        return "edit-tax-as-manager";
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/manager/buildings/{buildingId}/tax/{taxId}")
    public String editTaxStatusAsManagerConfirm(@Valid TaxEditDTO taxEditDTO,
                                                BindingResult bindingResult,
                                                RedirectAttributes redirectAttributes,
                                                @PathVariable("buildingId") Long buildingId,
                                                @PathVariable("taxId") Long taxId) {
        taxEditDTO.setId(taxId);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("taxEditDTO", taxEditDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.taxEditDTO", bindingResult);
            return String.format("redirect:/manager/buildings/%d/tax/%d",
                    buildingId, taxId);
        }
        taxService.updateTaxStatus(taxEditDTO);
        return String.format("redirect:/manager/buildings/%d/taxes", buildingId);
    }

    @PreAuthorize("@buildingService.checkIfUserIsANeighbour(principal.username,#buildingId)" +
            " AND @taxService.checkIfUserIsOwnerOfTax(principal.username, #taxId)")
    @GetMapping("/neighbour/buildings/{buildingId}/pay-tax/{taxId}")
    public String payTaxAsNeighbour(@PathVariable("buildingId") Long buildingId,
                                    @PathVariable("taxId") Long taxId,
                                    Model model) {
        if (!model.containsAttribute("taxPayDTO")) {
            model.addAttribute("taxPayDTO", new TaxPayDTO());
        }
        BuildingViewDTO buildingEdit = buildingService.findById(buildingId);
        TaxViewDTO taxView = taxService.findViewById(taxId);
        model.addAttribute("building", buildingEdit);
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        model.addAttribute("tax", taxView);
        return "pay-tax-as-neighbour";
    }

    @PreAuthorize("@buildingService.checkIfUserIsANeighbour(principal.username,#buildingId)" +
            " AND @taxService.checkIfUserIsOwnerOfTax(principal.username, #taxId)")
    @PostMapping("/neighbour/buildings/{buildingId}/pay-tax/{taxId}")
    public String payTaxAsNeighbourConfirm(@Valid TaxPayDTO taxPayDTO,
                                           BindingResult bindingResult,
                                           RedirectAttributes redirectAttributes,
                                           @PathVariable("buildingId") Long buildingId,
                                           @PathVariable("taxId") Long taxId) {
        taxPayDTO.setId(taxId);
        if (bindingResult.hasErrors() || taxService.checkIfPaidAmountIsMoreThanTax(taxId, taxPayDTO.getPaidAmount())) {
            redirectAttributes.addFlashAttribute("taxPayDTO", taxPayDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.taxPayDTO", bindingResult);
            redirectAttributes.addFlashAttribute("morePaidMoney", true);
            return String.format("redirect:/neighbour/buildings/%d/pay-tax/%d",
                    buildingId, taxId);
        }
        taxService.payTaxAmount(taxPayDTO);
        return String.format("redirect:/neighbour/buildings/%d/taxes", buildingId);
    }

    private static void addPageNumbersAttributeIfThereAreEnoughPages(Model model, Page<TaxEntity> taxesPage) {
        int totalPages = taxesPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
    }
}
