package bg.propertymanager.model.dto.expense;

import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TaxAddDTO {
    private Long id;
    private TaxTypeEnum taxType;
    private BigDecimal amount;
    private TaxStatusEnum taxStatus;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private List<String> selectedApartments;
    private BuildingEntity building;
    private UserEntity owner;
    private ApartmentEntity apartment;
    private UserEntity manager;

    public TaxAddDTO() {
    }

    public Long getId() {
        return id;
    }

    public TaxAddDTO setId(Long id) {
        this.id = id;
        return this;
    }

    @NotNull(message = "Tax type should not be null")
    public TaxTypeEnum getTaxType() {
        return taxType;
    }

    public TaxAddDTO setTaxType(TaxTypeEnum taxType) {
        this.taxType = taxType;
        return this;
    }

    @NotNull
    @DecimalMin("0")
    public BigDecimal getAmount() {
        return amount;
    }

    public TaxAddDTO setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TaxStatusEnum getTaxStatus() {
        return taxStatus;
    }

    public TaxAddDTO setTaxStatus(TaxStatusEnum taxStatus) {
        this.taxStatus = taxStatus;
        return this;
    }

    @NotEmpty(message = "Description of tax should not be empty")
    public String getDescription() {
        return description;
    }

    public TaxAddDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public TaxAddDTO setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull
    @FutureOrPresent(message = "Due date must not be in the past")
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public TaxAddDTO setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public List<String> getSelectedApartments() {
        return selectedApartments;
    }

    public TaxAddDTO setSelectedApartments(List<String> selectedApartments) {
        this.selectedApartments = selectedApartments;
        return this;
    }

    public BuildingEntity getBuilding() {
        return building;
    }

    public TaxAddDTO setBuilding(BuildingEntity building) {
        this.building = building;
        return this;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public TaxAddDTO setOwner(UserEntity owner) {
        this.owner = owner;
        return this;
    }

    public ApartmentEntity getApartment() {
        return apartment;
    }

    public TaxAddDTO setApartment(ApartmentEntity apartment) {
        this.apartment = apartment;
        return this;
    }

    public UserEntity getManager() {
        return manager;
    }

    public TaxAddDTO setManager(UserEntity manager) {
        this.manager = manager;
        return this;
    }
}
