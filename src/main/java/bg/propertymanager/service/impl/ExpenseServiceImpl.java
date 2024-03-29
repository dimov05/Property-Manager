package bg.propertymanager.service.impl;

import bg.propertymanager.model.dto.expense.ExpenseAddDTO;
import bg.propertymanager.model.dto.expense.ExpenseEditDTO;
import bg.propertymanager.model.dto.expense.ExpenseViewDTO;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.ExpenseEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.repository.ExpenseRepository;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.ExpenseService;
import bg.propertymanager.service.TaxService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ModelMapper modelMapper;
    private final BuildingService buildingService;
    private final TaxService taxService;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, ModelMapper modelMapper, BuildingService buildingService, @Lazy TaxService taxService) {
        this.expenseRepository = expenseRepository;
        this.modelMapper = modelMapper;
        this.buildingService = buildingService;
        this.taxService = taxService;
    }

    @Override
    public void addExpenseAndTaxesForItToBuilding(ExpenseAddDTO expenseAddDTO, Long buildingId) {
        BuildingEntity buildingToUpdate = getBuildingEntity(buildingId);
        ExpenseEntity newExpenseToAdd = mapDataFromExpenseAddDTO(expenseAddDTO, buildingToUpdate);
        expenseRepository.save(newExpenseToAdd);
        Set<TaxEntity> expenseTaxes = taxService.addTaxForEachApartment(expenseAddDTO.getSelectedApartments(), buildingToUpdate, newExpenseToAdd);

        newExpenseToAdd.setTaxes(expenseTaxes);

        expenseRepository.save(newExpenseToAdd);
    }

    @Override
    public void addExpenseToBuilding(ExpenseAddDTO expenseAddDTO, Long buildingId) {
        BuildingEntity buildingToUpdate = getBuildingEntity(buildingId);
        ExpenseEntity newExpenseToAdd = mapDataFromExpenseAddDTO(expenseAddDTO, buildingToUpdate);
        expenseRepository.save(newExpenseToAdd);

    }

    @Override
    public ExpenseViewDTO findViewById(Long expenseId) {
        return expenseRepository
                .findById(expenseId)
                .map(expense -> modelMapper.map(expense, ExpenseViewDTO.class))
                .orElseThrow(() -> new NullPointerException("There is no expense with this Id " + expenseId));
    }

    @Override
    public void updateExpenseStatus(ExpenseEditDTO expenseEditDTO) {
        ExpenseEntity expenseToUpdate = expenseRepository
                .findById(expenseEditDTO.getId())
                .orElseThrow(() -> new NullPointerException("No expense is existing with id " + expenseEditDTO.getId()));
        expenseToUpdate
                .setTaxStatus(expenseEditDTO.getTaxStatus());
        expenseRepository.save(expenseToUpdate);
    }

    @Override
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

    @Override
    public ExpenseEntity findById(Long expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new NullPointerException("No expense is existing with id " + expenseId));
    }

    @Override
    public BigDecimal findAmountOfAllPaidExpensesByBuildingId(Long buildingId) {
        return expenseRepository
                .findAmountOfPaidExpensesByBuildingId(buildingId)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal findAmountOfAllUnpaidExpensesByBuildingId(Long buildingId) {
        return expenseRepository
                .findAmountOfUnpaidExpensesByBuildingId(buildingId)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public Page<ExpenseEntity> findAllExpensesByBuildingIdPaginated(Pageable pageable, Long buildingId) {
        List<ExpenseEntity> expenses = expenseRepository
                .findAllExpensesByBuildingOrderByDueDateAsc(buildingId);
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<ExpenseEntity> list;
        list = getExpenseEntities(expenses, pageSize, startItem);
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), expenses.size());
    }

    @Override
    public BigDecimal getExpenseAmountById(Long expenseId) {
        ExpenseEntity expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new NullPointerException("No expense is existing with id " + expenseId));
        return expense.getAmount();
    }

    private BuildingEntity getBuildingEntity(Long buildingId) {
        return buildingService.findEntityById(buildingId);
    }

    private static ExpenseEntity mapDataFromExpenseAddDTO(ExpenseAddDTO expenseAddDTO, BuildingEntity buildingToUpdate) {
        return new ExpenseEntity()
                .setTaxType(expenseAddDTO.getTaxType())
                .setTaxStatus(TaxStatusEnum.UNPAID)
                .setDescription(expenseAddDTO.getDescription())
                .setStartDate(expenseAddDTO.getStartDate())
                .setDueDate(expenseAddDTO.getDueDate())
                .setAmount(expenseAddDTO.getAmount())
                .setManager(buildingToUpdate.getManager())
                .setBuilding(buildingToUpdate)
                .setTaxes(Collections.emptySet());
    }

    private static List<ExpenseEntity> getExpenseEntities(List<ExpenseEntity> expenses, int pageSize, int startItem) {
        List<ExpenseEntity> list;
        if (expenses.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, expenses.size());
            list = expenses.subList(startItem, toIndex);
        }
        return list;
    }
}
