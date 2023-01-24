package bg.propertymanager.web;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.view.UserEntityViewModel;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.TaxService;
import bg.propertymanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class NeighbourController {
    private final BuildingService buildingService;
    private final UserService userService;
    private final TaxService taxService;

    @Autowired
    public NeighbourController(BuildingService buildingService, UserService userService, TaxService taxService) {
        this.buildingService = buildingService;
        this.userService = userService;
        this.taxService = taxService;
    }


    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/neighbours")
    public ModelAndView viewNeighboursAsManager(@PathVariable("buildingId") Long buildingId,
                                                @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        ModelAndView mav = new ModelAndView("view-neighbours-as-manager");
        Page<UserEntityViewModel> neighboursPage = userService
                .findAllNeighboursByBuildingPaginated(PageRequest.of(page - 1, size), buildingId);
        mav.addObject("neighboursPage", neighboursPage);
        addPageNumberModels(mav, neighboursPage);
        addBuildingInformationModels(buildingId, mav);
        return mav;
    }

    @PreAuthorize("@buildingService.checkIfUserIsANeighbour(principal.username,#buildingId)")
    @GetMapping("/neighbour/buildings/{buildingId}/neighbours")
    public ModelAndView viewNeighboursAsNeighbour(@PathVariable("buildingId") Long buildingId,
                                            @RequestParam(name = "page", defaultValue = "1") Integer page,
                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        ModelAndView mav = new ModelAndView("view-neighbours-as-neighbour");
        Page<UserEntityViewModel> neighboursPage = userService
                .findAllNeighboursByBuildingPaginated(PageRequest.of(page - 1, size), buildingId);
        mav.addObject("neighboursPage", neighboursPage);
        addPageNumberModels(mav, neighboursPage);
        addBuildingInformationModels(buildingId, mav);
        return mav;
    }

    private static void addPageNumberModels(ModelAndView mav, Page<UserEntityViewModel> neighboursPage) {
        int totalPages = neighboursPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            mav.addObject("pageNumbers", pageNumbers);
        }
    }

    private void addBuildingInformationModels(Long buildingId, ModelAndView mav) {
        BuildingViewDTO building = buildingService.findById(buildingId);
        mav.addObject("building", building);
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(buildingId));
    }
}
