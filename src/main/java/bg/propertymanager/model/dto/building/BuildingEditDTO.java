package bg.propertymanager.model.dto.building;

import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.entity.UserEntity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Set;

public class BuildingEditDTO {
    private Long id;
    private String name;
    private int floors;
    private int elevators;
    private String imageUrl;
    private BigDecimal taxPerPerson;
    private BigDecimal taxPerDog;
    private BigDecimal taxPerElevatorChip;
    private UserEntity manager;
    private String country;
    private String city;
    private String street;
    private Set<UserEntity> neighbours;
    private Set<ApartmentEntity> apartments;
    private Set<TaxEntity> taxes;
    private Set<MessageEntity> messages;

    public BuildingEditDTO() {
    }

    public Long getId() {
        return id;
    }

    public BuildingEditDTO setId(Long id) {
        this.id = id;
        return this;
    }
    @NotEmpty(message = "Name can not be empty")
    public String getName() {
        return name;
    }

    public BuildingEditDTO setName(String name) {
        this.name = name;
        return this;
    }
@PositiveOrZero(message = "Floors must be positive or 0")
    public int getFloors() {
        return floors;
    }

    public BuildingEditDTO setFloors(int floors) {
        this.floors = floors;
        return this;
    }
    @PositiveOrZero(message = "Elevators must be positive or 0")
    public int getElevators() {
        return elevators;
    }

    public BuildingEditDTO setElevators(int elevators) {
        this.elevators = elevators;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BuildingEditDTO setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
    @PositiveOrZero(message = "Tax per person must be positive or 0")
    public BigDecimal getTaxPerPerson() {
        return taxPerPerson;
    }

    public BuildingEditDTO setTaxPerPerson(BigDecimal taxPerPerson) {
        this.taxPerPerson = taxPerPerson;
        return this;
    }
    @PositiveOrZero(message = "Tax per dog must be positive or 0")
    public BigDecimal getTaxPerDog() {
        return taxPerDog;
    }

    public BuildingEditDTO setTaxPerDog(BigDecimal taxPerDog) {
        this.taxPerDog = taxPerDog;
        return this;
    }
    @PositiveOrZero(message = "Tax per elevator chip must be positive or 0")
    public BigDecimal getTaxPerElevatorChip() {
        return taxPerElevatorChip;
    }

    public BuildingEditDTO setTaxPerElevatorChip(BigDecimal taxPerElevatorChip) {
        this.taxPerElevatorChip = taxPerElevatorChip;
        return this;
    }
    public UserEntity getManager() {
        return manager;
    }

    public BuildingEditDTO setManager(UserEntity manager) {
        this.manager = manager;
        return this;
    }
    @NotEmpty(message = "Country can not be empty")
    public String getCountry() {
        return country;
    }

    public BuildingEditDTO setCountry(String country) {
        this.country = country;
        return this;
    }
    @NotEmpty(message = "City can not be empty")
    public String getCity() {
        return city;
    }

    public BuildingEditDTO setCity(String city) {
        this.city = city;
        return this;
    }
    @NotEmpty(message = "Street can not be empty")
    public String getStreet() {
        return street;
    }

    public BuildingEditDTO setStreet(String street) {
        this.street = street;
        return this;
    }

    public Set<UserEntity> getNeighbours() {
        return neighbours;
    }

    public BuildingEditDTO setNeighbours(Set<UserEntity> neighbours) {
        this.neighbours = neighbours;
        return this;
    }

    public Set<ApartmentEntity> getApartments() {
        return apartments;
    }

    public BuildingEditDTO setApartments(Set<ApartmentEntity> apartments) {
        this.apartments = apartments;
        return this;
    }

    public Set<TaxEntity> getTaxes() {
        return taxes;
    }

    public BuildingEditDTO setTaxes(Set<TaxEntity> taxes) {
        this.taxes = taxes;
        return this;
    }

    public Set<MessageEntity> getMessages() {
        return messages;
    }

    public BuildingEditDTO setMessages(Set<MessageEntity> messages) {
        this.messages = messages;
        return this;
    }
}
