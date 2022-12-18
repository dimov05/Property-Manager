package bg.propertymanager.model.entity;

import bg.propertymanager.model.enums.TaxTypeEnum;
import jakarta.persistence.*;

import java.math.BigDecimal;

import java.time.LocalDateTime;

@Entity
@Table(name = "taxes")
public class TaxEntity {
    private Long id;
    private TaxTypeEnum taxType;
    private BigDecimal amount;
    private boolean isPaid;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private BuildingEntity building;
    private UserEntity owner;
    private ApartmentEntity apartment;
    private UserEntity manager;

    public TaxEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
    public TaxTypeEnum getTaxType() {
        return taxType;
    }

    public void setTaxType(TaxTypeEnum taxType) {
        this.taxType = taxType;
    }

    @Column(name = "amount", nullable = false)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "is_paid", nullable = false)
    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "start_date", nullable = false)
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @Column(name = "due_date", nullable = false)
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    @ManyToOne
    public BuildingEntity getBuilding() {
        return building;
    }

    public void setBuilding(BuildingEntity building) {
        this.building = building;
    }

    @ManyToOne
    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    @ManyToOne
    public ApartmentEntity getApartment() {
        return apartment;
    }

    public void setApartment(ApartmentEntity apartment) {
        this.apartment = apartment;
    }

    @ManyToOne
    public UserEntity getManager() {
        return manager;
    }

    public void setManager(UserEntity manager) {
        this.manager = manager;
    }
}
