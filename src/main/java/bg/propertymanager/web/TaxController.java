package bg.propertymanager.web;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.tax.TaxPayDTO;
import bg.propertymanager.model.dto.tax.TaxReturnDTO;
import bg.propertymanager.model.dto.tax.TaxViewDTO;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.TaxService;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class TaxController {
    private final TaxService taxService;
    private final BuildingService buildingService;

    public TaxController(TaxService taxService, BuildingService buildingService) {
        this.taxService = taxService;
        this.buildingService = buildingService;
    }

    @PreAuthorize("principal.username == @buildingServiceImpl.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/taxes")
    public ModelAndView viewTaxesAsManager(@PathVariable("buildingId") Long buildingId,
                                           @RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {
        ModelAndView mav = new ModelAndView("view-taxes-as-manager");
        addInfoAboutBuildingAsModels(buildingId, mav);
        // Implement search/filter with keyword just by fixing Controller. The other logic in Service and Repository is ready
        Page<TaxEntity> taxesPage = taxService
                .findAllTaxesByBuildingIdFilteredAndPaginated(PageRequest.of(page - 1, size), buildingId, null);
        mav.addObject("taxesPage", taxesPage);
        addPageNumbersModelIfThereAreEnoughPages(mav, taxesPage);
        return mav;
    }

    @PreAuthorize("@buildingServiceImpl.checkIfUserIsANeighbour(principal.username,#buildingId)")
    @GetMapping("/neighbour/buildings/{buildingId}/taxes")
    public ModelAndView viewMyTaxesAsNeighbour(@PathVariable("buildingId") Long buildingId,
                                               @RequestParam(value = "page", defaultValue = "1") Integer page,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size,
                                               Principal principal) {
        ModelAndView mav = new ModelAndView("view-my-taxes-as-neighbour");
        addInfoAboutBuildingAsModels(buildingId, mav);

        Page<TaxEntity> taxesPage = taxService
                .findALlMyTaxesByBuildingIdPaginated(PageRequest.of(page - 1, size), buildingId, principal.getName());
        addPageNumbersModelIfThereAreEnoughPages(mav, taxesPage);
        mav.addObject("taxesPage", taxesPage);
        return mav;
    }

    @PreAuthorize("principal.username == @buildingServiceImpl.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/taxes-neighbour/{neighbourId}")
    public ModelAndView viewTaxesOfNeighbourAsManager(@PathVariable("buildingId") Long buildingId,
                                                      @PathVariable("neighbourId") Long neighbourId,
                                                      @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                      @RequestParam(value = "size", defaultValue = "10") Integer size) {
        ModelAndView mav = new ModelAndView("view-taxes-of-neighbour-as-manager");
        Page<TaxEntity> taxesPage = taxService
                .findAllTaxesByBuildingIdAndOwnerId(PageRequest.of(page - 1, size), buildingId, neighbourId);
        mav.addObject("taxesPage", taxesPage);
        addPageNumbersModelIfThereAreEnoughPages(mav, taxesPage);
        addInfoAboutBuildingAsModels(buildingId, mav);
        return mav;
    }

    @PreAuthorize("principal.username == @buildingServiceImpl.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/tax/{taxId}")
    public ModelAndView payTaxAsManager(@PathVariable("buildingId") Long buildingId,
                                        @PathVariable("taxId") Long taxId,
                                        Model model) {
        ModelAndView mav = new ModelAndView("pay-tax-as-manager");
        addInfoAboutBuildingAsModels(buildingId, mav);
        addTaxPayDTOAsModelAttributeIfNull(model);
        addInfoAboutTaxesAsModel(taxId, mav);
        return mav;
    }

    @PreAuthorize("principal.username == @buildingServiceImpl.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/manager/buildings/{buildingId}/tax/{taxId}")
    public String payTaxAsManagerConfirm(@Valid TaxPayDTO taxPayDTO,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes,
                                         @PathVariable("buildingId") Long buildingId,
                                         @PathVariable("taxId") Long taxId) {
        taxPayDTO.setId(taxId);
        if (bindingResult.hasErrors() || taxService.checkIfPaidAmountIsMoreThanRemainingAmount(taxId, taxPayDTO.getPaidAmount())) {
            redirectAttributes.addFlashAttribute("taxPayDTO", taxPayDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.taxPayDTO", bindingResult);
            redirectAttributes.addFlashAttribute("morePaidMoney", true);
            return String.format("redirect:/manager/buildings/%d/tax/%d",
                    buildingId, taxId);
        }
        taxService.payTaxAmount(taxPayDTO);
        return String.format("redirect:/manager/buildings/%d/taxes", buildingId);
    }

    @PreAuthorize("@buildingServiceImpl.checkIfUserIsANeighbour(principal.username,#buildingId)" +
            " AND @taxServiceImpl.checkIfUserIsOwnerOfTax(principal.username, #taxId)")
    @GetMapping("/neighbour/buildings/{buildingId}/pay-tax/{taxId}")
    public ModelAndView payTaxAsNeighbour(@PathVariable("buildingId") Long buildingId,
                                          @PathVariable("taxId") Long taxId,
                                          Model model) {
        ModelAndView mav = new ModelAndView("pay-tax-as-neighbour");
        addInfoAboutBuildingAsModels(buildingId, mav);
        addTaxPayDTOAsModelAttributeIfNull(model);
        addInfoAboutTaxesAsModel(taxId, mav);
        return mav;
    }

    @PreAuthorize("@buildingServiceImpl.checkIfUserIsANeighbour(principal.username,#buildingId)" +
            " AND @taxServiceImpl.checkIfUserIsOwnerOfTax(principal.username, #taxId)")
    @PostMapping("/neighbour/buildings/{buildingId}/pay-tax/{taxId}")
    public String payTaxAsNeighbourConfirm(@Valid TaxPayDTO taxPayDTO,
                                           BindingResult bindingResult,
                                           RedirectAttributes redirectAttributes,
                                           @PathVariable("buildingId") Long buildingId,
                                           @PathVariable("taxId") Long taxId) {
        taxPayDTO.setId(taxId);
        if (bindingResult.hasErrors() || taxService.checkIfPaidAmountIsMoreThanRemainingAmount(taxId, taxPayDTO.getPaidAmount())) {
            redirectAttributes.addFlashAttribute("taxPayDTO", taxPayDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.taxPayDTO", bindingResult);
            redirectAttributes.addFlashAttribute("morePaidMoney", true);
            return String.format("redirect:/neighbour/buildings/%d/pay-tax/%d",
                    buildingId, taxId);
        }
        taxService.payTaxAmount(taxPayDTO);
        return String.format("redirect:/neighbour/buildings/%d/taxes", buildingId);
    }

    @PreAuthorize("principal.username == @buildingServiceImpl.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager/buildings/{buildingId}/return-tax/{taxId}")
    public ModelAndView returnTaxAsManager(@PathVariable("buildingId") Long buildingId,
                                           @PathVariable("taxId") Long taxId,
                                           Model model) {
        ModelAndView mav = new ModelAndView("return-tax-as-manager");
        addInfoAboutBuildingAsModels(buildingId, mav);
        addTaxReturnDTOAsModelAttributeIfNull(model);
        addInfoAboutTaxesAsModel(taxId, mav);
        return mav;
    }

    @PreAuthorize("principal.username == @buildingServiceImpl.findManagerUsername(#buildingId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/manager/buildings/{buildingId}/return-tax/{taxId}")
    public String returnTaxAsManagerConfirm(@Valid TaxReturnDTO taxReturnDTO,
                                            BindingResult bindingResult,
                                            RedirectAttributes redirectAttributes,
                                            @PathVariable("buildingId") Long buildingId,
                                            @PathVariable("taxId") Long taxId) {
        taxReturnDTO.setId(taxId);
        if (bindingResult.hasErrors() || taxService.checkIfReturnedMoneyIsMoreThanPaidMoney(taxId, taxReturnDTO.getReturnedAmount())) {
            redirectAttributes.addFlashAttribute("taxReturnDTO", taxReturnDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.taxReturnDTO", bindingResult);
            redirectAttributes.addFlashAttribute("moreReturnedMoney", true);
            return String.format("redirect:/manager/buildings/%d/return-tax/%d",
                    buildingId, taxId);
        }
        taxService.returnTaxAmount(taxReturnDTO);
        return String.format("redirect:/manager/buildings/%d/taxes", buildingId);
    }

    private static void addPageNumbersModelIfThereAreEnoughPages(ModelAndView mav, Page<TaxEntity> taxesPage) {
        int totalPages = taxesPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            mav.addObject("pageNumbers", pageNumbers);
        }
    }

    private void addInfoAboutBuildingAsModels(Long buildingId, ModelAndView mav) {
        BuildingViewDTO buildingEdit = buildingService.findById(buildingId);
        mav.addObject("building", buildingEdit);
        mav.addObject("buildingBalance", taxService.calculateBuildingBalance(buildingId));
    }

    private void addInfoAboutTaxesAsModel(Long taxId, ModelAndView mav) {
        TaxViewDTO taxView = taxService.findViewById(taxId);
        mav.addObject("tax", taxView);
    }

    private static void addTaxPayDTOAsModelAttributeIfNull(Model model) {
        if (!model.containsAttribute("taxPayDTO")) {
            model.addAttribute("taxPayDTO", new TaxPayDTO());
        }
    }

    private static void addTaxReturnDTOAsModelAttributeIfNull(Model model) {
        if (!model.containsAttribute("taxReturnDTO")) {
            model.addAttribute("taxReturnDTO", new TaxReturnDTO());
        }
    }
}
