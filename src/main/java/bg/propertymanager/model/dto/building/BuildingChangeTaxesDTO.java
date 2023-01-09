package bg.propertymanager.model.dto.building;

import bg.propertymanager.model.entity.UserEntity;

import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class BuildingChangeTaxesDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private BigDecimal taxPerPerson;
    private BigDecimal taxPerDog;
    private BigDecimal taxPerElevatorChip;
    private UserEntity manager;
    private String country;
    private String city;
    private String street;

    public BuildingChangeTaxesDTO() {
    }

    public Long getId() {
        return id;
    }

    public BuildingChangeTaxesDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public BuildingChangeTaxesDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BuildingChangeTaxesDTO setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
    @PositiveOrZero(message = "Tax per person must be positive or 0")
    public BigDecimal getTaxPerPerson() {
        return taxPerPerson;
    }

    public BuildingChangeTaxesDTO setTaxPerPerson(BigDecimal taxPerPerson) {
        this.taxPerPerson = taxPerPerson;
        return this;
    }
    @PositiveOrZero(message = "Tax per dog must be positive or 0")
    public BigDecimal getTaxPerDog() {
        return taxPerDog;
    }

    public BuildingChangeTaxesDTO setTaxPerDog(BigDecimal taxPerDog) {
        this.taxPerDog = taxPerDog;
        return this;
    }
    @PositiveOrZero(message = "Tax per elevator chip must be positive or 0")
    public BigDecimal getTaxPerElevatorChip() {
        return taxPerElevatorChip;
    }

    public BuildingChangeTaxesDTO setTaxPerElevatorChip(BigDecimal taxPerElevatorChip) {
        this.taxPerElevatorChip = taxPerElevatorChip;
        return this;
    }

    public UserEntity getManager() {
        return manager;
    }

    public BuildingChangeTaxesDTO setManager(UserEntity manager) {
        this.manager = manager;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public BuildingChangeTaxesDTO setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCity() {
        return city;
    }

    public BuildingChangeTaxesDTO setCity(String city) {
        this.city = city;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public BuildingChangeTaxesDTO setStreet(String street) {
        this.street = street;
        return this;
    }
}
