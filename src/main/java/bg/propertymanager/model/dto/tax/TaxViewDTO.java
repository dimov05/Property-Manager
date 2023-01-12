package bg.propertymanager.model.dto.tax;

import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TaxViewDTO {
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

    public TaxViewDTO() {
    }

    public Long getId() {
        return id;
    }

    public TaxViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public TaxTypeEnum getTaxType() {
        return taxType;
    }

    public TaxViewDTO setTaxType(TaxTypeEnum taxType) {
        this.taxType = taxType;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TaxViewDTO setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TaxStatusEnum getTaxStatus() {
        return taxStatus;
    }

    public TaxViewDTO setTaxStatus(TaxStatusEnum taxStatus) {
        this.taxStatus = taxStatus;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TaxViewDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public TaxViewDTO setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public TaxViewDTO setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public BuildingEntity getBuilding() {
        return building;
    }

    public TaxViewDTO setBuilding(BuildingEntity building) {
        this.building = building;
        return this;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public TaxViewDTO setOwner(UserEntity owner) {
        this.owner = owner;
        return this;
    }

    public ApartmentEntity getApartment() {
        return apartment;
    }

    public TaxViewDTO setApartment(ApartmentEntity apartment) {
        this.apartment = apartment;
        return this;
    }

    public UserEntity getManager() {
        return manager;
    }

    public TaxViewDTO setManager(UserEntity manager) {
        this.manager = manager;
        return this;
    }
}
