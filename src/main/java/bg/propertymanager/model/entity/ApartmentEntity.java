package bg.propertymanager.model.entity;

import jakarta.persistence.*;

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

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "apartment_number", nullable = false)
    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    @Column(name = "floor", nullable = false)
    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Column(name = "area", nullable = false)
    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    @Column(name = "elevator_chips_count", nullable = false)
    public int getElevatorChipsCount() {
        return elevatorChipsCount;
    }

    public void setElevatorChipsCount(int elevatorChipsCount) {
        this.elevatorChipsCount = elevatorChipsCount;
    }

    @Column(name = "dogs_count", nullable = false)
    public int getDogsCount() {
        return dogsCount;
    }

    public void setDogsCount(int dogsCount) {
        this.dogsCount = dogsCount;
    }

    @Column(name = "roommates_count", nullable = false)
    public int getRoommateCount() {
        return roommateCount;
    }

    public void setRoommateCount(int roommateCount) {
        this.roommateCount = roommateCount;
    }

    @Column(name = "periodic_tax", nullable = false)
    public BigDecimal getPeriodicTax() {
        return periodicTax;
    }

    public void setPeriodicTax(BigDecimal periodicTax) {
        this.periodicTax = periodicTax;
    }

    @Column(name = "money_owed")
    public BigDecimal getMoneyOwed() {
        return moneyOwed;
    }

    public void setMoneyOwed(BigDecimal moneyOwed) {
        this.moneyOwed = moneyOwed;
    }

    @Column(name = "total_paid_money")
    public BigDecimal getTotalMoneyPaid() {
        return totalMoneyPaid;
    }

    public void setTotalMoneyPaid(BigDecimal totalMoneyPaid) {
        this.totalMoneyPaid = totalMoneyPaid;
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

    @OneToMany(mappedBy = "apartment")
    public Set<TaxEntity> getTaxes() {
        return taxes;
    }

    public void setTaxes(Set<TaxEntity> taxes) {
        this.taxes = taxes;
    }
}
