package bg.propertymanager.model.dto.apartment;

import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

public class ApartmentAddDTO {
    private Long id;
    private String apartmentNumber;
    private int floor;
    private double area;
    private int elevatorChipsCount;
    private int dogsCount;
    private int roommateCount;
    private BuildingEntity building;
    private UserEntity owner;

    public ApartmentAddDTO() {
    }

    public Long getId() {
        return id;
    }

    public ApartmentAddDTO setId(Long id) {
        this.id = id;
        return this;
    }

    @NotEmpty(message = "Apartment number should not be empty")
    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public ApartmentAddDTO setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
        return this;
    }

    @Min(value = -2, message = "Minimum floor of apartment is -2")
    public int getFloor() {
        return floor;
    }

    public ApartmentAddDTO setFloor(int floor) {
        this.floor = floor;
        return this;
    }

    @Min(value = 5, message = "Minimum area of apartment is 5m^2")
    public double getArea() {
        return area;
    }

    public ApartmentAddDTO setArea(double area) {
        this.area = area;
        return this;
    }

    @Min(value = 0, message = "Minimum elevator chips per apartment is 0")
    public int getElevatorChipsCount() {
        return elevatorChipsCount;
    }

    public ApartmentAddDTO setElevatorChipsCount(int elevatorChipsCount) {
        this.elevatorChipsCount = elevatorChipsCount;
        return this;
    }
    @Min(value = 0, message = "Minimum dogs count per apartment is 0")
    public int getDogsCount() {
        return dogsCount;
    }

    public ApartmentAddDTO setDogsCount(int dogsCount) {
        this.dogsCount = dogsCount;
        return this;
    }
    @Min(value = 0, message = "Minimum roommates count per apartment is 0")
    public int getRoommateCount() {
        return roommateCount;
    }

    public ApartmentAddDTO setRoommateCount(int roommateCount) {
        this.roommateCount = roommateCount;
        return this;
    }

    public BuildingEntity getBuilding() {
        return building;
    }

    public ApartmentAddDTO setBuilding(BuildingEntity building) {
        this.building = building;
        return this;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public ApartmentAddDTO setOwner(UserEntity owner) {
        this.owner = owner;
        return this;
    }
}
