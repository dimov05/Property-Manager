package bg.propertymanager.model.dto;

import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.entity.UserEntity;

import java.math.BigDecimal;
import java.util.Set;

public class BuildingEditDTO {
    private Long id;
    private String name;
    private int floors;
    private int elevators;
    private String imageUrl;
    private BigDecimal balance;
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

    public String getName() {
        return name;
    }

    public BuildingEditDTO setName(String name) {
        this.name = name;
        return this;
    }

    public int getFloors() {
        return floors;
    }

    public BuildingEditDTO setFloors(int floors) {
        this.floors = floors;
        return this;
    }

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

    public BigDecimal getBalance() {
        return balance;
    }

    public BuildingEditDTO setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public BigDecimal getTaxPerPerson() {
        return taxPerPerson;
    }

    public BuildingEditDTO setTaxPerPerson(BigDecimal taxPerPerson) {
        this.taxPerPerson = taxPerPerson;
        return this;
    }

    public BigDecimal getTaxPerDog() {
        return taxPerDog;
    }

    public BuildingEditDTO setTaxPerDog(BigDecimal taxPerDog) {
        this.taxPerDog = taxPerDog;
        return this;
    }

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

    public String getCountry() {
        return country;
    }

    public BuildingEditDTO setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCity() {
        return city;
    }

    public BuildingEditDTO setCity(String city) {
        this.city = city;
        return this;
    }

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
