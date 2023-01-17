package bg.propertymanager.web;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.service.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {
    private final BuildingService buildingService;
    private final TaxService taxService;
    private final ApartmentService apartmentService;
    private final MessageService messageService;
    private final ExpenseService expenseService;

    public DashboardController(BuildingService buildingService, TaxService taxService, ApartmentService apartmentService, MessageService messageService, ExpenseService expenseService) {
        this.buildingService = buildingService;
        this.taxService = taxService;
        this.apartmentService = apartmentService;
        this.messageService = messageService;
        this.expenseService = expenseService;
    }

    @PreAuthorize("principal.username == @buildingService.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/dashboard")
    public String viewDashboardAsManager(@PathVariable("buildingId") Long buildingId,
                                         Model model) {
        BuildingViewDTO building = buildingService.findById(buildingId);
        model.addAttribute("building", building);
        model.addAttribute("buildingBalance", taxService.calculateBuildingBalance(buildingId));
        model.addAttribute("totalMonthlyPeriodicTaxes", apartmentService.calculateTotalMonthlyPeriodicTaxesByBuildingId(buildingId));
        model.addAttribute("totalNeighbours", apartmentService.findTotalCountOfNeighboursInBuilding(buildingId));
        model.addAttribute("totalDogs", apartmentService.findTotalCountOfDogsByBuildingId(buildingId));
        model.addAttribute("totalElevatorChips", apartmentService.findTotalCountOfElevatorChipsByBuildingId(buildingId));
        model.addAttribute("dateOfLastMessageFromManager", messageService.findDateOfLastMessageFromManagerByBuilding(building));
        model.addAttribute("totalCollectedTaxes", taxService.findAmountOfAllPaidTaxesByBuildingId(buildingId));
        model.addAttribute("totalUncollectedTaxes", taxService.findAmountOfAllUnpaidTaxesByBuildingId(buildingId));
        model.addAttribute("totalPaidExpenses", expenseService.findAmountOfAllPaidExpensesByBuildingId(buildingId));
        model.addAttribute("totalUnpaidExpenses", expenseService.findAmountOfAllUnpaidExpensesByBuildingId(buildingId));
        model.addAttribute("topFiveApartmentsByDebt", taxService.findTopFiveApartmentsInBuildingByDebt(buildingId));
        return "view-dashboard-as-manager";
    }
}
