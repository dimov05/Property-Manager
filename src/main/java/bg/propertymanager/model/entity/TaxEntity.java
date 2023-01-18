package bg.propertymanager.model.entity;

import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;

import javax.persistence.*;

import java.math.BigDecimal;

import java.time.LocalDateTime;

@Entity
@Table(name = "taxes")
public class TaxEntity {
    private Long id;
    private TaxTypeEnum taxType;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private TaxStatusEnum taxStatus;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private BuildingEntity building;
    private ApartmentEntity apartment;
    private ExpenseEntity expense;
    private UserEntity manager;

    public TaxEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public TaxEntity setId(Long id) {
        this.id = id;
        return this;
    }

    @Enumerated(EnumType.STRING)
    public TaxTypeEnum getTaxType() {
        return taxType;
    }

    public TaxEntity setTaxType(TaxTypeEnum taxType) {
        this.taxType = taxType;
        return this;
    }

    @Column(name = "amount", nullable = false)
    public BigDecimal getAmount() {
        return amount;
    }

    public TaxEntity setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
    @Column(name = "paid_amount", nullable = false)
    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public TaxEntity setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
        return this;
    }

    @Enumerated(EnumType.STRING)
    public TaxStatusEnum getTaxStatus() {
        return taxStatus;
    }

    public TaxEntity setTaxStatus(TaxStatusEnum taxStatus) {
        this.taxStatus = taxStatus;
        return this;
    }

    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    public TaxEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    @Column(name = "start_date", nullable = false)
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public TaxEntity setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    @Column(name = "due_date", nullable = false)
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public TaxEntity setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    @ManyToOne
    public BuildingEntity getBuilding() {
        return building;
    }

    public TaxEntity setBuilding(BuildingEntity building) {
        this.building = building;
        return this;
    }

    @ManyToOne
    public ApartmentEntity getApartment() {
        return apartment;
    }

    public TaxEntity setApartment(ApartmentEntity apartment) {
        this.apartment = apartment;
        return this;
    }

    @ManyToOne
    public ExpenseEntity getExpense() {
        return expense;
    }

    public TaxEntity setExpense(ExpenseEntity expense) {
        this.expense = expense;
        return this;
    }

    @ManyToOne
    public UserEntity getManager() {
        return manager;
    }

    public TaxEntity setManager(UserEntity manager) {
        this.manager = manager;
        return this;
    }
}
