package bg.propertymanager.web;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.tax.TaxAddDTO;
import bg.propertymanager.model.dto.tax.TaxEditDTO;
import bg.propertymanager.model.dto.tax.TaxViewDTO;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.TaxService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TaxController {
    private final TaxService taxService;
    private final BuildingService buildingService;

    public TaxController(TaxService taxService, BuildingService buildingService) {
        this.taxService = taxService;
        this.buildingService = buildingService;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/taxes")
    public String viewTaxesAsManager(@PathVariable("buildingId") Long buildingId,
                                     Model model) {
        BuildingViewDTO building = buildingService.findById(buildingId);
        List<TaxViewDTO> allTaxes = taxService.findAllTaxes(building);
        model.addAttribute("building", building);
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        model.addAttribute("taxes", allTaxes);
        return "view-taxes-as-manager";
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/add-tax")
    public String addTaxAsManager(@PathVariable("buildingId") Long buildingId, Model model) {
        if (!model.containsAttribute("taxAddDTO")) {
            model.addAttribute("taxAddDTO", new TaxAddDTO().setSelectedApartments(new ArrayList<>()));
        }
        model.addAttribute("taxTypes", TaxTypeEnum.values());
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        model.addAttribute("building", buildingService.findById(buildingId));
        return "add-tax-as-manager";
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/manager/buildings/{buildingId}/add-tax")
    public String addTaxAsManagerConfirm(@Valid TaxAddDTO taxAddDTO,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes,
                                         @PathVariable("buildingId") Long buildingId) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("taxAddDTO", taxAddDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.taxAddDTO", bindingResult);
            return String.format("redirect:/manager/buildings/%d/add-tax", buildingId);
        }
        taxService.addTax(taxAddDTO, buildingId);
        return String.format("redirect:/manager/buildings/%d/taxes", buildingId);
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/tax/{taxId}")
    public String editTaxAsManager(@PathVariable("buildingId") Long buildingId,
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
    public String editTaxAsManagerConfirm(@Valid TaxEditDTO taxEditDTO,
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
        taxService.updateTax(taxEditDTO);
        return String.format("redirect:/manager/buildings/%d/taxes", buildingId);
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/manager/buildings/{buildingId}/delete-tax/{taxId}")
    public String deleteTaxAsManagerConfirm(@PathVariable("buildingId") Long buildingId,
                                            @PathVariable("taxId") Long taxId) {
        taxService.deleteTaxWithId(taxId, buildingId);
        return String.format("redirect:/manager/buildings/%d/taxes", buildingId);
    }
}
