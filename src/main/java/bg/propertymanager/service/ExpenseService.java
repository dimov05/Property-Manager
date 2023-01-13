package bg.propertymanager.service;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.expense.ExpenseAddDTO;
import bg.propertymanager.model.dto.expense.ExpenseEditDTO;
import bg.propertymanager.model.dto.expense.ExpenseViewDTO;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.ExpenseEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.repository.ExpenseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ModelMapper modelMapper;
    private final BuildingService buildingService;
    private final ApartmentService apartmentService;
    private final TaxService taxService;

    public ExpenseService(ExpenseRepository expenseRepository, ModelMapper modelMapper, BuildingService buildingService, ApartmentService apartmentService,@Lazy TaxService taxService) {
        this.expenseRepository = expenseRepository;
        this.modelMapper = modelMapper;
        this.buildingService = buildingService;
        this.apartmentService = apartmentService;
        this.taxService = taxService;
    }

    public List<ExpenseViewDTO> findAllExpensesForBuilding(BuildingViewDTO building) {
        return expenseRepository
                .findAllByBuilding_Id(building.getId())
                .stream()
                .map(expense -> modelMapper.map(expense, ExpenseViewDTO.class))
                .collect(Collectors.toList());
    }

    public void addExpenseToBuilding(ExpenseAddDTO expenseAddDTO, Long buildingId) {
        BuildingEntity buildingToUpdate = buildingService.findEntityById(buildingId);
        ExpenseEntity newExpenseToAdd = new ExpenseEntity()
                .setTaxType(expenseAddDTO.getTaxType())
                .setTaxStatus(TaxStatusEnum.UNPAID)
                .setDescription(expenseAddDTO.getDescription())
                .setStartDate(expenseAddDTO.getStartDate())
                .setDueDate(expenseAddDTO.getDueDate())
                .setAmount(expenseAddDTO.getAmount())
                .setManager(buildingToUpdate.getManager())
                .setBuilding(buildingToUpdate)
                .setTaxes(Collections.emptySet());
        expenseRepository.save(newExpenseToAdd);
        Set<TaxEntity> expenseTaxes = taxService.addTaxForEachApartment(expenseAddDTO.getSelectedApartments(), buildingToUpdate, newExpenseToAdd);

        newExpenseToAdd.setTaxes(expenseTaxes);

        expenseRepository.save(newExpenseToAdd);
    }

    private static BigDecimal getTaxForEachApartment(ExpenseAddDTO expenseAddDTO) {
        return expenseAddDTO.getAmount()
                .divide(BigDecimal.valueOf(expenseAddDTO.getSelectedApartments().size()), RoundingMode.HALF_EVEN);
    }

    public ExpenseViewDTO findViewById(Long expenseId) {
        return expenseRepository
                .findById(expenseId)
                .map(expense -> modelMapper.map(expense, ExpenseViewDTO.class))
                .orElseThrow(() -> new NullPointerException("There is no expense with this Id " + expenseId));
    }

    public void updateExpenseStatus(ExpenseEditDTO expenseEditDTO) {
        ExpenseEntity expenseToUpdate = expenseRepository
                .findById(expenseEditDTO.getId())
                .orElseThrow(() -> new NullPointerException("No expense is existing with id " + expenseEditDTO.getId()));


        expenseToUpdate
                .setTaxStatus(expenseEditDTO.getTaxStatus());
        expenseRepository.save(expenseToUpdate);
    }

    public boolean deleteExpenseWithId(Long expenseId) {
        ExpenseEntity expenseToDelete = findById(expenseId);
        Boolean existingPaidTaxesToExpense = taxService.findIfPaidTaxesExistToExpenseById(expenseId);
        if (existingPaidTaxesToExpense) {
            return false;
        } else {
            taxService.deleteAllTaxesAttachedToExpense(expenseToDelete);
            expenseRepository.delete(expenseToDelete);
            return true;
        }
    }

    public ExpenseEntity findById(Long expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new NullPointerException("No expense is existing with id " + expenseId));
    }
}
