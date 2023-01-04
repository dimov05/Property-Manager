package bg.propertymanager.model.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "apartments")
public class ApartmentEntity {
    private Long id;
    private String apartmentNumber;
    private int floor;
    private double area;
    private int elevatorChipsCount;
    private int dogsCount;
    private int roommateCount;
    private BigDecimal periodicTax;
    private BigDecimal moneyOwed;
    private BigDecimal totalMoneyPaid;
    private BuildingEntity building;
    private UserEntity owner;
    private Set<TaxEntity> taxes;

    public ApartmentEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public ApartmentEntity setId(Long id) {
        this.id = id;
        return this;
    }

    @Column(name = "apartment_number", nullable = false)
    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public ApartmentEntity setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
        return this;
    }

    @Column(name = "floor", nullable = false)
    public int getFloor() {
        return floor;
    }

    public ApartmentEntity setFloor(int floor) {
        this.floor = floor;
        return this;
    }

    @Column(name = "area", nullable = false)
    public double getArea() {
        return area;
    }

    public ApartmentEntity setArea(double area) {
        this.area = area;
        return this;
    }

    @Column(name = "elevator_chips_count", nullable = false)
    public int getElevatorChipsCount() {
        return elevatorChipsCount;
    }

    public ApartmentEntity setElevatorChipsCount(int elevatorChipsCount) {
        this.elevatorChipsCount = elevatorChipsCount;
        return this;
    }

    @Column(name = "dogs_count", nullable = false)
    public int getDogsCount() {
        return dogsCount;
    }

    public ApartmentEntity setDogsCount(int dogsCount) {
        this.dogsCount = dogsCount;
        return this;
    }

    @Column(name = "roommates_count", nullable = false)
    public int getRoommateCount() {
        return roommateCount;
    }

    public ApartmentEntity setRoommateCount(int roommateCount) {
        this.roommateCount = roommateCount;
        return this;
    }

    @Column(name = "periodic_tax", nullable = false)
    public BigDecimal getPeriodicTax() {
        return periodicTax;
    }

    public ApartmentEntity setPeriodicTax(BigDecimal periodicTax) {
        this.periodicTax = periodicTax;
        return this;
    }

    @Column(name = "money_owed")
    public BigDecimal getMoneyOwed() {
        return moneyOwed;
    }

    public ApartmentEntity setMoneyOwed(BigDecimal moneyOwed) {
        this.moneyOwed = moneyOwed;
        return this;
    }

    @Column(name = "total_paid_money")
    public BigDecimal getTotalMoneyPaid() {
        return totalMoneyPaid;
    }

    public ApartmentEntity setTotalMoneyPaid(BigDecimal totalMoneyPaid) {
        this.totalMoneyPaid = totalMoneyPaid;
        return this;
    }

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    public BuildingEntity getBuilding() {
        return building;
    }

    public ApartmentEntity setBuilding(BuildingEntity building) {
        this.building = building;
        return this;
    }

    @ManyToOne
    public UserEntity getOwner() {
        return owner;
    }

    public ApartmentEntity setOwner(UserEntity owner) {
        this.owner = owner;
        return this;
    }

    @OneToMany(mappedBy = "apartment")
    public Set<TaxEntity> getTaxes() {
        return taxes;
    }

    public ApartmentEntity setTaxes(Set<TaxEntity> taxes) {
        this.taxes = taxes;
        return this;
    }
}
