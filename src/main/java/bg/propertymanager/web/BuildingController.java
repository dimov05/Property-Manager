package bg.propertymanager.web;

import bg.propertymanager.model.dto.building.BuildingAddDTO;
import bg.propertymanager.model.dto.building.BuildingChangeTaxesDTO;
import bg.propertymanager.model.dto.building.BuildingEditDTO;
import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.ImagesOfBuildings;
import bg.propertymanager.model.view.UserEntityViewModel;
import bg.propertymanager.service.ApartmentService;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.TaxService;
import bg.propertymanager.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class BuildingController {
    private final BuildingService buildingService;
    private final TaxService taxService;
    private final ApartmentService apartmentService;
    private final UserService userService;

    public BuildingController(BuildingService buildingService, TaxService taxService, ApartmentService apartmentService, UserService userService) {
        this.buildingService = buildingService;
        this.taxService = taxService;
        this.apartmentService = apartmentService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/buildings")
    public ModelAndView manageBuildings(@Param("searchKeyword") String searchKeyword,
                                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        ModelAndView mav = new ModelAndView("manage-buildings");
        Page<BuildingEntity> buildingsPage = buildingService
                .findAllBuildingsWithFilterPaginated(PageRequest.of(page - 1, size), searchKeyword);
        addPageNumberModelAttributeIfThereAreBuildings(mav, buildingsPage);
        mav.addObject("totalBuildings", buildingService.findAll().size());
        mav.addObject("buildingsPage", buildingsPage);
        mav.addObject("searchKeyword", searchKeyword);
        return mav;
    }

    private static void addPageNumberModelAttributeIfThereAreBuildings(ModelAndView mav, Page<BuildingEntity> buildingsPage) {
        int totalPages = buildingsPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            mav.addObject("pageNumbers", pageNumbers);
        }
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
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(id));
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
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(id));
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

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/edit/{buildingId}")
    public ModelAndView editBuildingAsManager(@PathVariable("buildingId") Long buildingId) {
        BuildingViewDTO buildingViewDTO = buildingService.findById(buildingId);
        ModelAndView mav = new ModelAndView("edit-building-as-manager");
        mav.addObject("building", buildingViewDTO);
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        mav.addObject("apartmentsList", apartmentService.findAllApartmentsByBuildingId(buildingId));
        return mav;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/change-taxes/{buildingId}")
    public ModelAndView changeTaxes(@PathVariable("buildingId") Long buildingId, Model model) {
        if (!model.containsAttribute("buildingChangeTaxesDTO")) {
            model.addAttribute("buildingChangeTaxesDTO", new BuildingChangeTaxesDTO());
        }
        BuildingViewDTO buildingEdit = buildingService.findById(buildingId);
        ModelAndView mav = new ModelAndView("change-taxes");
        mav.addObject("building", buildingEdit);
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        return mav;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/manager/buildings/change-taxes/{buildingId}")
    public String changeTaxesConfirm(@Valid BuildingChangeTaxesDTO buildingChangeTaxesDTO,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes,
                                     @PathVariable("buildingId") Long buildingId) {
        buildingChangeTaxesDTO.setId(buildingId);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("buildingChangeTaxesDTO", buildingChangeTaxesDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.buildingChangeTaxesDTO", bindingResult);
            return "redirect:/manager/buildings/view/" + buildingId;
        }
        buildingService.updateBuildingsPerTaxes(buildingChangeTaxesDTO);
        return "redirect:/manager/buildings/view/" + buildingId;
    }

}
