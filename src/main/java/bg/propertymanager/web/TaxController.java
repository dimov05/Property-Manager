package bg.propertymanager.web;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.expense.TaxAddDTO;
import bg.propertymanager.model.dto.expense.TaxViewDTO;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.TaxService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
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
        model.addAttribute("taxes", allTaxes);
        return "view-taxes-as-manager";
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/add-tax")
    public String addTaxAsManager(@PathVariable("buildingId") Long buildingId, Model model) {
        if (!model.containsAttribute("taxAddDTO")) {
            model.addAttribute("taxAddDTO", new TaxAddDTO());
        }
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
}
