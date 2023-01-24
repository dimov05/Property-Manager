package bg.propertymanager.web;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {
    private final BuildingService buildingService;
    private final TaxService taxService;
    private final ApartmentService apartmentService;
    private final MessageService messageService;
    private final ExpenseService expenseService;

    @Autowired
    public DashboardController(BuildingService buildingService, TaxService taxService, ApartmentService apartmentService, MessageService messageService, ExpenseService expenseService) {
        this.buildingService = buildingService;
        this.taxService = taxService;
        this.apartmentService = apartmentService;
        this.messageService = messageService;
        this.expenseService = expenseService;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/dashboard")
    public ModelAndView viewDashboardAsManager(@PathVariable("buildingId") Long buildingId) {
        ModelAndView mav = new ModelAndView("view-dashboard-as-manager");
        addModelsToMAV(buildingId, mav);
        return mav;
    }

    @PreAuthorize("@buildingService.checkIfUserIsANeighbour(principal.username,#buildingId)")
    @GetMapping("/neighbour/buildings/{buildingId}/dashboard")
    public ModelAndView viewDashboardAsNeighbour(@PathVariable("buildingId") Long buildingId) {
        ModelAndView mav = new ModelAndView("view-dashboard-as-neighbour");
        addModelsToMAV(buildingId,mav);
        return mav;
    }

    private void addModelsToMAV(Long buildingId, ModelAndView mav) {
        BuildingViewDTO building = buildingService.findById(buildingId);
        mav.addObject("building", building);
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        mav.addObject("totalMonthlyPeriodicTaxes", apartmentService.calculateTotalMonthlyPeriodicTaxesByBuildingId(buildingId));
        mav.addObject("totalNeighbours", apartmentService.findTotalCountOfNeighboursInBuilding(buildingId));
        mav.addObject("totalDogs", apartmentService.findTotalCountOfDogsByBuildingId(buildingId));
        mav.addObject("totalElevatorChips", apartmentService.findTotalCountOfElevatorChipsByBuildingId(buildingId));
        mav.addObject("dateOfLastMessageFromManager", messageService.findDateOfLastMessageFromManagerByBuilding(building));
        mav.addObject("totalCollectedTaxes", taxService.findAmountOfAllPaidTaxesByBuildingId(buildingId));
        mav.addObject("totalUncollectedTaxes", taxService.findAmountOfAllUnpaidTaxesByBuildingId(buildingId));
        mav.addObject("totalPaidExpenses", expenseService.findAmountOfAllPaidExpensesByBuildingId(buildingId));
        mav.addObject("totalUnpaidExpenses", expenseService.findAmountOfAllUnpaidExpensesByBuildingId(buildingId));
        mav.addObject("topFiveApartmentsByDebt", taxService.findTopFiveApartmentsInBuildingByDebt(buildingId));
    }
}
