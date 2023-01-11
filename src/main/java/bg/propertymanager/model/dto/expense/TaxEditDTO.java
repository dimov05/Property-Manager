package bg.propertymanager.model.dto.expense;

import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TaxEditDTO {
    private Long id;
    private TaxTypeEnum taxType;
    private BigDecimal amount;
    private TaxStatusEnum taxStatus;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private BuildingEntity building;
    private UserEntity owner;
    private ApartmentEntity apartment;
    private UserEntity manager;

    public TaxEditDTO() {
    }

    public Long getId() {
        return id;
    }

    public TaxEditDTO setId(Long id) {
        this.id = id;
        return this;
    }

    @NotNull(message = "Tax type should not be null")
    public TaxTypeEnum getTaxType() {
        return taxType;
    }

    public TaxEditDTO setTaxType(TaxTypeEnum taxType) {
        this.taxType = taxType;
        return this;
    }

    @DecimalMin("0")
    public BigDecimal getAmount() {
        return amount;
    }

    public TaxEditDTO setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TaxStatusEnum getTaxStatus() {
        return taxStatus;
    }

    public TaxEditDTO setTaxStatus(TaxStatusEnum taxStatus) {
        this.taxStatus = taxStatus;
        return this;
    }

    @NotEmpty(message = "Description of tax should not be empty")
    public String getDescription() {
        return description;
    }

    public TaxEditDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    @NotNull
    @Past(message = "Start date must not be in the past")
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public TaxEditDTO setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    @NotNull
    @Past(message = "Due date must not be in the past")
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public TaxEditDTO setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public BuildingEntity getBuilding() {
        return building;
    }

    public TaxEditDTO setBuilding(BuildingEntity building) {
        this.building = building;
        return this;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public TaxEditDTO setOwner(UserEntity owner) {
        this.owner = owner;
        return this;
    }

    public ApartmentEntity getApartment() {
        return apartment;
    }

    public TaxEditDTO setApartment(ApartmentEntity apartment) {
        this.apartment = apartment;
        return this;
    }

    public UserEntity getManager() {
        return manager;
    }

    public TaxEditDTO setManager(UserEntity manager) {
        this.manager = manager;
        return this;
    }
}
