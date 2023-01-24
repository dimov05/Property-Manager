package bg.propertymanager.service;

import bg.propertymanager.model.dto.expense.ExpenseAddDTO;
import bg.propertymanager.model.dto.expense.ExpenseEditDTO;
import bg.propertymanager.model.dto.expense.ExpenseViewDTO;
import bg.propertymanager.model.entity.ExpenseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ExpenseService {
    void addExpenseAndTaxesForItToBuilding(ExpenseAddDTO expenseAddDTO, Long buildingId);

    void addExpenseToBuilding(ExpenseAddDTO expenseAddDTO, Long buildingId);

    ExpenseViewDTO findViewById(Long expenseId);

    void updateExpenseStatus(ExpenseEditDTO expenseEditDTO);

    boolean deleteExpenseWithId(Long expenseId);

    ExpenseEntity findById(Long expenseId);

    BigDecimal findAmountOfAllPaidExpensesByBuildingId(Long buildingId);

    BigDecimal findAmountOfAllUnpaidExpensesByBuildingId(Long buildingId);

    Page<ExpenseEntity> findAllExpensesByBuildingIdPaginated(Pageable pageable, Long buildingId);

    BigDecimal getExpenseAmountById(Long expenseId);
}
