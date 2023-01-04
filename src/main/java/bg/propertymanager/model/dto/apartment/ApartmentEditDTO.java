package bg.propertymanager.model.dto.apartment;

import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

public class ApartmentEditDTO {
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

    public ApartmentEditDTO() {
    }

    public Long getId() {
        return id;
    }

    public ApartmentEditDTO setId(Long id) {
        this.id = id;
        return this;
    }

    @NotEmpty(message = "Apartment number should not be empty")
    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public ApartmentEditDTO setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
        return this;
    }

    @Min(value = -2, message = "Minimum floor of apartment is -2")
    public int getFloor() {
        return floor;
    }

    public ApartmentEditDTO setFloor(int floor) {
        this.floor = floor;
        return this;
    }

    @Min(value = 5, message = "Minimum area of apartment is 5m^2")
    public double getArea() {
        return area;
    }

    public ApartmentEditDTO setArea(double area) {
        this.area = area;
        return this;
    }

    @Min(value = 0, message = "Minimum elevator chips per apartment is 0")
    public int getElevatorChipsCount() {
        return elevatorChipsCount;
    }

    public ApartmentEditDTO setElevatorChipsCount(int elevatorChipsCount) {
        this.elevatorChipsCount = elevatorChipsCount;
        return this;
    }

    @Min(value = 0, message = "Minimum dogs count per apartment is 0")
    public int getDogsCount() {
        return dogsCount;
    }

    public ApartmentEditDTO setDogsCount(int dogsCount) {
        this.dogsCount = dogsCount;
        return this;
    }

    @Min(value = 0, message = "Minimum roommates count per apartment is 0")
    public int getRoommateCount() {
        return roommateCount;
    }

    public ApartmentEditDTO setRoommateCount(int roommateCount) {
        this.roommateCount = roommateCount;
        return this;
    }

    public BuildingEntity getBuilding() {
        return building;
    }

    public ApartmentEditDTO setBuilding(BuildingEntity building) {
        this.building = building;
        return this;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public ApartmentEditDTO setOwner(UserEntity owner) {
        this.owner = owner;
        return this;
    }

    @DecimalMin("0")
    public BigDecimal getPeriodicTax() {
        return periodicTax;
    }

    public ApartmentEditDTO setPeriodicTax(BigDecimal periodicTax) {
        this.periodicTax = periodicTax;
        return this;
    }

    public BigDecimal getMoneyOwed() {
        return moneyOwed;
    }

    public ApartmentEditDTO setMoneyOwed(BigDecimal moneyOwed) {
        this.moneyOwed = moneyOwed;
        return this;
    }

    public BigDecimal getTotalMoneyPaid() {
        return totalMoneyPaid;
    }

    public ApartmentEditDTO setTotalMoneyPaid(BigDecimal totalMoneyPaid) {
        this.totalMoneyPaid = totalMoneyPaid;
        return this;
    }
}
