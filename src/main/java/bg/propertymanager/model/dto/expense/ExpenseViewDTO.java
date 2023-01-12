package bg.propertymanager.model.dto.expense;

import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class ExpenseViewDTO {
    private Long id;
    private TaxTypeEnum taxType;
    private BigDecimal amount;
    private TaxStatusEnum taxStatus;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private BuildingEntity building;
    private UserEntity manager;
    private Set<TaxEntity> taxes;

    public ExpenseViewDTO() {
    }

    public Long getId() {
        return id;
    }

    public ExpenseViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public TaxTypeEnum getTaxType() {
        return taxType;
    }

    public ExpenseViewDTO setTaxType(TaxTypeEnum taxType) {
        this.taxType = taxType;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ExpenseViewDTO setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TaxStatusEnum getTaxStatus() {
        return taxStatus;
    }

    public ExpenseViewDTO setTaxStatus(TaxStatusEnum taxStatus) {
        this.taxStatus = taxStatus;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ExpenseViewDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public ExpenseViewDTO setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public ExpenseViewDTO setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public BuildingEntity getBuilding() {
        return building;
    }

    public ExpenseViewDTO setBuilding(BuildingEntity building) {
        this.building = building;
        return this;
    }

    public UserEntity getManager() {
        return manager;
    }

    public ExpenseViewDTO setManager(UserEntity manager) {
        this.manager = manager;
        return this;
    }

    public Set<TaxEntity> getTaxes() {
        return taxes;
    }

    public ExpenseViewDTO setTaxes(Set<TaxEntity> taxes) {
        this.taxes = taxes;
        return this;
    }
}
