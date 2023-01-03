package bg.propertymanager.model.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BuildingAddDTO {
    private Long id;
    private String name;
    private int floors;
    private int elevators;
    private BigDecimal taxPerPerson;
    private BigDecimal taxPerDog;
    private BigDecimal taxPerElevatorChip;
    private String country;
    private String city;
    private String street;

    public BuildingAddDTO() {
    }

    public Long getId() {
        return id;
    }

    public BuildingAddDTO setId(Long id) {
        this.id = id;
        return this;
    }

    @NotEmpty(message = "Building name can not be empty")
    public String getName() {
        return name;
    }

    public BuildingAddDTO setName(String name) {
        this.name = name;
        return this;
    }

    @NotNull(message = "Floors count can not be empty")
    public int getFloors() {
        return floors;
    }

    public BuildingAddDTO setFloors(int floors) {
        this.floors = floors;
        return this;
    }

    @NotNull(message = "Elevator count can not be empty.If there are no elevator, put '0'")
    public int getElevators() {
        return elevators;
    }

    public BuildingAddDTO setElevators(int elevators) {
        this.elevators = elevators;
        return this;
    }

    @DecimalMin("0")
    @NotNull(message = "Tax per person can not be empty")
    public BigDecimal getTaxPerPerson() {
        return taxPerPerson;
    }

    public BuildingAddDTO setTaxPerPerson(BigDecimal taxPerPerson) {
        this.taxPerPerson = taxPerPerson;
        return this;
    }

    @DecimalMin("0")
    @NotNull(message = "Tax per dog can not be empty")
    public BigDecimal getTaxPerDog() {
        return taxPerDog;
    }

    public BuildingAddDTO setTaxPerDog(BigDecimal taxPerDog) {
        this.taxPerDog = taxPerDog;
        return this;
    }

    @DecimalMin("0")
    @NotNull(message = "Tax per elevator chip can not be empty")
    public BigDecimal getTaxPerElevatorChip() {
        return taxPerElevatorChip;
    }

    public BuildingAddDTO setTaxPerElevatorChip(BigDecimal taxPerElevatorChip) {
        this.taxPerElevatorChip = taxPerElevatorChip;
        return this;
    }

    @NotEmpty(message = "Country can not be empty")
    public String getCountry() {
        return country;
    }

    public BuildingAddDTO setCountry(String country) {
        this.country = country;
        return this;
    }

    @NotEmpty(message = "City can not be empty")
    public String getCity() {
        return city;
    }

    public BuildingAddDTO setCity(String city) {
        this.city = city;
        return this;
    }

    @NotEmpty(message = "Street Address can not be empty")
    public String getStreet() {
        return street;
    }

    public BuildingAddDTO setStreet(String street) {
        this.street = street;
        return this;
    }
}
