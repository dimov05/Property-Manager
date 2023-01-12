package bg.propertymanager.model.dto.apartment;

import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;

import java.math.BigDecimal;

public class ApartmentViewDTO {
    private Long id;
    private String apartmentNumber;
    private int floor;
    private double area;
    private int elevatorChipsCount;
    private int dogsCount;
    private int roommateCount;
    private BigDecimal periodicTax;
    private BuildingEntity building;
    private UserEntity owner;

    public ApartmentViewDTO() {
    }

    public Long getId() {
        return id;
    }

    public ApartmentViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public ApartmentViewDTO setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
        return this;
    }

    public int getFloor() {
        return floor;
    }

    public ApartmentViewDTO setFloor(int floor) {
        this.floor = floor;
        return this;
    }

    public double getArea() {
        return area;
    }

    public ApartmentViewDTO setArea(double area) {
        this.area = area;
        return this;
    }

    public int getElevatorChipsCount() {
        return elevatorChipsCount;
    }

    public ApartmentViewDTO setElevatorChipsCount(int elevatorChipsCount) {
        this.elevatorChipsCount = elevatorChipsCount;
        return this;
    }

    public int getDogsCount() {
        return dogsCount;
    }

    public ApartmentViewDTO setDogsCount(int dogsCount) {
        this.dogsCount = dogsCount;
        return this;
    }

    public int getRoommateCount() {
        return roommateCount;
    }

    public ApartmentViewDTO setRoommateCount(int roommateCount) {
        this.roommateCount = roommateCount;
        return this;
    }

    public BigDecimal getPeriodicTax() {
        return periodicTax;
    }

    public ApartmentViewDTO setPeriodicTax(BigDecimal periodicTax) {
        this.periodicTax = periodicTax;
        return this;
    }

    public BuildingEntity getBuilding() {
        return building;
    }

    public ApartmentViewDTO setBuilding(BuildingEntity building) {
        this.building = building;
        return this;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public ApartmentViewDTO setOwner(UserEntity owner) {
        this.owner = owner;
        return this;
    }
}
