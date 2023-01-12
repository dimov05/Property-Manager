package bg.propertymanager.service;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.tax.TaxAddDTO;
import bg.propertymanager.model.dto.tax.TaxEditDTO;
import bg.propertymanager.model.dto.tax.TaxViewDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.repository.TaxRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaxService {
    private final TaxRepository taxRepository;
    private final ModelMapper modelMapper;
    private final BuildingService buildingService;
    private final ApartmentService apartmentService;

    public TaxService(TaxRepository taxRepository, ModelMapper modelMapper, BuildingService buildingService, ApartmentService apartmentService) {
        this.taxRepository = taxRepository;
        this.modelMapper = modelMapper;
        this.buildingService = buildingService;
        this.apartmentService = apartmentService;
    }

    public List<TaxViewDTO> findAllTaxes(BuildingViewDTO building) {
        return taxRepository
                .findAllByBuilding_Id(building.getId())
                .stream()
                .map(tax -> modelMapper.map(tax, TaxViewDTO.class))
                .collect(Collectors.toList());

    }

    public void addTax(TaxAddDTO taxAddDTO, Long buildingId) {
        BigDecimal taxForEachApartment = taxAddDTO.getAmount()
                .divide(BigDecimal.valueOf(taxAddDTO.getSelectedApartments().size()), RoundingMode.HALF_EVEN);
        for (String apartmentNumber : taxAddDTO.getSelectedApartments()) {
            ApartmentEntity apartmentToAddTax = apartmentService.getApartmentByApartmentNumber(apartmentNumber);
            BuildingEntity buildingToAddTax = apartmentToAddTax.getBuilding();

            TaxEntity taxToAdd = new TaxEntity()
                    .setTaxType(taxAddDTO.getTaxType())
                    .setAmount(taxForEachApartment)
                    .setTaxStatus(TaxStatusEnum.UNPAID)
                    .setDescription(taxAddDTO.getDescription())
                    .setStartDate(taxAddDTO.getStartDate())
                    .setDueDate(taxAddDTO.getDueDate())
                    .setBuilding(buildingToAddTax)
                    .setManager(buildingToAddTax.getManager())
                    .setApartment(apartmentToAddTax);

            buildingService.addNewTaxToBuilding(buildingToAddTax, taxToAdd);
            apartmentService.addNewTaxToApartment(apartmentToAddTax, taxToAdd);
            taxRepository.save(taxToAdd);
        }
    }

    public TaxViewDTO findViewById(Long taxId) {
        return taxRepository.findById(taxId)
                .map(tax -> modelMapper.map(tax, TaxViewDTO.class))
                .orElseThrow(() -> new NullPointerException("No tax is existing with id " + taxId));
    }

    public void updateTax(TaxEditDTO taxEditDTO) {
        TaxEntity taxToUpdate = taxRepository
                .findById(taxEditDTO.getId())
                .orElseThrow(() -> new NullPointerException("No tax is existing with id " + taxEditDTO.getId()));


        taxToUpdate
                .setAmount(taxEditDTO.getAmount())
                .setTaxStatus(taxEditDTO.getTaxStatus())
                .setStartDate(taxEditDTO.getStartDate())
                .setDueDate(taxEditDTO.getDueDate());
        if (taxIsPaid(taxToUpdate)) {
//            buildingService.updateBalanceAfterTaxIsPaid(taxToUpdate);
//            apartmentService.updateMoneyOwedAndPaidAfterTaxIsPaid(taxToUpdate);
        }

    }

    public void deleteTaxWithId(Long taxId, Long buildingId) {
        //TODO: implement logic
    }

    private static boolean taxIsPaid(TaxEntity taxToUpdate) {
        return taxToUpdate.getTaxStatus().equals(TaxStatusEnum.PAID);
    }

    public BigDecimal calculateBuildingBalance(Long buildingId) {
        BigDecimal paidTaxes = taxRepository.findBalanceByBuildingId(buildingId);
        if(paidTaxes == null){
            paidTaxes = new BigDecimal("0");
        }
        BigDecimal balance = new BigDecimal("0").add(paidTaxes);

        return balance;
    }
}
