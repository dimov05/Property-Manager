package bg.propertymanager.service;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.expense.ExpenseAddDTO;
import bg.propertymanager.model.dto.tax.TaxAddDTO;
import bg.propertymanager.model.dto.tax.TaxEditDTO;
import bg.propertymanager.model.dto.tax.TaxViewDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.ExpenseEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.repository.TaxRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        if (paidTaxes == null) {
            paidTaxes = new BigDecimal("0");
        }
        BigDecimal balance = new BigDecimal("0").add(paidTaxes);

        return balance;
    }

    public BigDecimal findOwedMoney(Long apartmentId) {
        BigDecimal owedMoney = taxRepository.findOwedMoneyByApartmentId(apartmentId);
        if (owedMoney == null) {
            owedMoney = new BigDecimal("0");
        }
        return owedMoney;
    }

    public void save(TaxEntity newTaxToAdd) {
        taxRepository.save(newTaxToAdd);
    }

    public Set<TaxEntity> addTaxForEachApartment(List<String> apartments, BuildingEntity buildingToUpdate,
                                                 ExpenseEntity expense) {
        BigDecimal taxForEachApartment = getTaxForEachApartment(expense, apartments.size());
        Set<TaxEntity> expenseTaxes = new HashSet<>();
        for (String apartmentNumber : apartments) {
            ApartmentEntity apartmentToAddTax = apartmentService.getApartmentByApartmentNumber(apartmentNumber);

            TaxEntity newTaxToAdd = new TaxEntity()
                    .setTaxType(expense.getTaxType())
                    .setAmount(taxForEachApartment)
                    .setTaxStatus(TaxStatusEnum.UNPAID)
                    .setDescription(expense.getDescription())
                    .setStartDate(expense.getStartDate())
                    .setDueDate(expense.getDueDate())
                    .setBuilding(buildingToUpdate)
                    .setManager(buildingToUpdate.getManager())
                    .setApartment(apartmentToAddTax)
                    .setExpense(expense);
            buildingService.addNewTaxToBuilding(buildingToUpdate, newTaxToAdd);
            apartmentService.addNewTaxToApartment(apartmentToAddTax, newTaxToAdd);
            taxRepository.save(newTaxToAdd);
            expenseTaxes.add(newTaxToAdd);
        }
        return expenseTaxes;
    }

    private static BigDecimal getTaxForEachApartment(ExpenseEntity expense, int apartmentsCount) {
        return expense.getAmount()
                .divide(BigDecimal.valueOf(apartmentsCount), RoundingMode.HALF_EVEN);
    }
}
