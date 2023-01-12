package bg.propertymanager.web;

import bg.propertymanager.model.dto.apartment.ApartmentAddDTO;
import bg.propertymanager.model.dto.apartment.ApartmentEditDTO;
import bg.propertymanager.model.dto.apartment.ApartmentViewDTO;
import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.service.ApartmentService;
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

@Controller
public class ApartmentController {
    private final ApartmentService apartmentService;
    private final BuildingService buildingService;
    private final TaxService taxService;

    public ApartmentController(ApartmentService apartmentService, BuildingService buildingService, TaxService taxService) {
        this.apartmentService = apartmentService;
        this.buildingService = buildingService;
        this.taxService = taxService;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/buildings/{buildingId}/add-apartment")
    public String addApartment(@PathVariable("buildingId") Long buildingId, Model model) {
        if (!model.containsAttribute("apartmentAddDTO")) {
            model.addAttribute("apartmentAddDTO", new ApartmentAddDTO());
        }
        model.addAttribute("building", buildingService.findById(buildingId));
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        return "add-apartment";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/buildings/{buildingId}/add-apartment")
    public String addApartmentConfirm(@Valid ApartmentAddDTO apartmentAddDTO,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      @PathVariable("buildingId") Long buildingId) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("apartmentAddDTO", apartmentAddDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.apartmentAddDTO", bindingResult);
            return String.format("redirect:/admin/buildings/%d/add-apartment", buildingId);
        }
        apartmentService.addApartment(apartmentAddDTO, buildingId);
        return "redirect:/admin/buildings/view/" + buildingId;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/buildings/{buildingId}/apartment/{apartmentId}")
    public ModelAndView editApartment(@PathVariable("buildingId") Long buildingId,
                                      @PathVariable("apartmentId") Long apartmentId,
                                      Model model) {
        if (!model.containsAttribute("apartmentEditDTO")) {
            model.addAttribute("apartmentEditDTO", new ApartmentEditDTO());
        }
        BuildingViewDTO buildingEdit = buildingService.findById(buildingId);
        ApartmentViewDTO apartmentView = apartmentService.findViewById(apartmentId);
        ModelAndView mav = new ModelAndView("edit-apartment");
        mav.addObject("building", buildingEdit);
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        mav.addObject("apartment", apartmentView);
        return mav;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/buildings/{buildingId}/apartment/{apartmentId}")
    public String editApartmentConfirm(@Valid ApartmentEditDTO apartmentEditDTO,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       @PathVariable("buildingId") Long buildingId,
                                       @PathVariable("apartmentId") Long apartmentId) {
        apartmentEditDTO.setId(apartmentId);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("apartmentEditDTO", apartmentEditDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.apartmentEditDTO", bindingResult);
            return String.format("redirect:/admin/buildings/%d/apartment/%d",
                    buildingId, apartmentId);
        }
        apartmentService.updateApartment(apartmentEditDTO);
        return "redirect:/admin/buildings/view/" + buildingId;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/add-apartment")
    public String addApartmentAsManager(@PathVariable("buildingId") Long buildingId, Model model) {
        if (!model.containsAttribute("apartmentAddDTO")) {
            model.addAttribute("apartmentAddDTO", new ApartmentAddDTO());
        }
        model.addAttribute("building", buildingService.findById(buildingId));
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        return "add-apartment-as-manager";
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/manager/buildings/{buildingId}/add-apartment")
    public String addApartmentAsManagerConfirm(@Valid ApartmentAddDTO apartmentAddDTO,
                                               BindingResult bindingResult,
                                               RedirectAttributes redirectAttributes,
                                               @PathVariable("buildingId") Long buildingId) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("apartmentAddDTO", apartmentAddDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.apartmentAddDTO", bindingResult);
            return String.format("redirect:/manager/buildings/%d/add-apartment", buildingId);
        }
        apartmentService.addApartment(apartmentAddDTO, buildingId);
        return "redirect:/manager/buildings/view/" + buildingId;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/apartment/{apartmentId}")
    public ModelAndView editApartmentAsManager(@PathVariable("buildingId") Long buildingId,
                                               @PathVariable("apartmentId") Long apartmentId,
                                               Model model) {
        if (!model.containsAttribute("apartmentEditDTO")) {
            model.addAttribute("apartmentEditDTO", new ApartmentEditDTO());
        }
        BuildingViewDTO buildingEdit = buildingService.findById(buildingId);
        ApartmentViewDTO apartmentView = apartmentService.findViewById(apartmentId);
        ModelAndView mav = new ModelAndView("edit-apartment-as-manager");
        mav.addObject("building", buildingEdit);
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        mav.addObject("apartment", apartmentView);
        return mav;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/manager/buildings/{buildingId}/apartment/{apartmentId}")
    public String editApartmentAsManagerConfirm(@Valid ApartmentEditDTO apartmentEditDTO,
                                                BindingResult bindingResult,
                                                RedirectAttributes redirectAttributes,
                                                @PathVariable("buildingId") Long buildingId,
                                                @PathVariable("apartmentId") Long apartmentId) {
        apartmentEditDTO.setId(apartmentId);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("apartmentEditDTO", apartmentEditDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.apartmentEditDTO", bindingResult);
            return String.format("redirect:/manager/buildings/%d/apartment/%d",
                    buildingId, apartmentId);
        }
        apartmentService.updateApartment(apartmentEditDTO);
        return "redirect:/manager/buildings/view/" + buildingId;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/manager/buildings/{buildingId}/delete-apartment/{apartmentId}")
    public String deleteApartmentAsManagerConfirm(@PathVariable("buildingId") Long buildingId,
                                                  @PathVariable("apartmentId") Long apartmentId) {
        apartmentService.deleteApartmentWithId(apartmentId, buildingId);
        return "redirect:/manager/buildings/view/" + buildingId;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin/buildings/{buildingId}/delete-apartment/{apartmentId}")
    public String deleteApartmentConfirm(@PathVariable("buildingId") Long buildingId,
                                         @PathVariable("apartmentId") Long apartmentId) {
        apartmentService.deleteApartmentWithId(apartmentId, buildingId);
        return "redirect:/manager/buildings/view/" + buildingId;
    }

}
