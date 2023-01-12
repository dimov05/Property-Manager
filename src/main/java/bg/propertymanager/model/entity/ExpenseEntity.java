package bg.propertymanager.model.entity;

import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "expenses")
public class ExpenseEntity {
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

    public ExpenseEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public ExpenseEntity setId(Long id) {
        this.id = id;
        return this;
    }

    @Enumerated(EnumType.STRING)
    public TaxTypeEnum getTaxType() {
        return taxType;
    }

    public ExpenseEntity setTaxType(TaxTypeEnum taxType) {
        this.taxType = taxType;
        return this;
    }

    @Column(name = "amount", nullable = false)
    public BigDecimal getAmount() {
        return amount;
    }

    public ExpenseEntity setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    @Enumerated(EnumType.STRING)
    public TaxStatusEnum getTaxStatus() {
        return taxStatus;
    }

    public ExpenseEntity setTaxStatus(TaxStatusEnum taxStatus) {
        this.taxStatus = taxStatus;
        return this;
    }
    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    public ExpenseEntity setDescription(String description) {
        this.description = description;
        return this;
    }
    @Column(name = "start_date", nullable = false)
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public ExpenseEntity setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }
    @Column(name = "due_date", nullable = false)
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public ExpenseEntity setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }
    @ManyToOne
    public BuildingEntity getBuilding() {
        return building;
    }

    public ExpenseEntity setBuilding(BuildingEntity building) {
        this.building = building;
        return this;
    }
    @ManyToOne
    public UserEntity getManager() {
        return manager;
    }

    public ExpenseEntity setManager(UserEntity manager) {
        this.manager = manager;
        return this;
    }
    @OneToMany(mappedBy = "expense")
    public Set<TaxEntity> getTaxes() {
        return taxes;
    }

    public ExpenseEntity setTaxes(Set<TaxEntity> taxes) {
        this.taxes = taxes;
        return this;
    }
}
