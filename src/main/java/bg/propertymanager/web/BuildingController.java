package bg.propertymanager.web;

import bg.propertymanager.model.dto.building.BuildingAddDTO;
import bg.propertymanager.model.dto.building.BuildingEditDTO;
import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.enums.ImagesOfBuildings;
import bg.propertymanager.service.BuildingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
public class BuildingController {
    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/buildings")
    public ModelAndView manageBuildings() {
        ModelAndView mav = new ModelAndView("manage-buildings");
        List<BuildingViewDTO> buildings = buildingService.findAll();
        mav.addObject("buildings", buildings);
        return mav;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/buildings/add")
    public String addBuilding(Model model) {
        if (!model.containsAttribute("buildingAddDTO")) {
            model.addAttribute("buildingAddDTO", new BuildingAddDTO());
        }
        model.addAttribute("images", ImagesOfBuildings.values());
        return "add-building";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/buildings/add")
    public String addBuildingConfirm(@Valid BuildingAddDTO buildingAddDTO,
                                     BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("buildingAddDTO", buildingAddDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.buildingAddDTO", bindingResult);
            return "redirect:/admin/buildings/add";
        }
        buildingService.register(buildingAddDTO);
        return "redirect:/admin/buildings";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/buildings/view/{id}")
    public ModelAndView viewBuilding(@PathVariable("id") Long id) {
        BuildingViewDTO buildingViewDTO = buildingService.findById(id);
        ModelAndView mav = new ModelAndView("view-building");
        mav.addObject("building", buildingViewDTO);
        return mav;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/buildings/edit/{id}")
    public ModelAndView editBuilding(@PathVariable("id") Long id, Model model) {
        if (!model.containsAttribute("buildingEditDTO")) {
            model.addAttribute("buildingEditDTO", new BuildingEditDTO());
        }
        BuildingViewDTO buildingEdit = buildingService.findById(id);
        ModelAndView mav = new ModelAndView("edit-building");
        mav.addObject("building", buildingEdit);
        return mav;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/buildings/edit/{id}")
    public String editBuildingConfirm(@Valid BuildingEditDTO buildingEditDTO,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      @PathVariable("id") Long id) {
        buildingEditDTO.setId(id);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("buildingEditDTO", buildingEditDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.buildingEditDTO", bindingResult);
            return "redirect:/admin/buildings/edit/" + id;
        }
        buildingService.updateBuilding(buildingEditDTO);
        return "redirect:/admin/buildings/view/" + id;
    }
}
