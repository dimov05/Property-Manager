package bg.propertymanager.model.dto;

import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.entity.UserEntity;

import java.math.BigDecimal;
import java.util.Set;

public class BuildingViewDTO {
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
    private String registrationDate;
    private Set<UserEntity> neighbours;
    private Set<ApartmentEntity> apartments;
    private Set<TaxEntity> taxes;
    private Set<MessageEntity> messages;

    public BuildingViewDTO() {
    }

    public Long getId() {
        return id;
    }

    public BuildingViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public BuildingViewDTO setName(String name) {
        this.name = name;
        return this;
    }

    public int getFloors() {
        return floors;
    }

    public BuildingViewDTO setFloors(int floors) {
        this.floors = floors;
        return this;
    }

    public int getElevators() {
        return elevators;
    }

    public BuildingViewDTO setElevators(int elevators) {
        this.elevators = elevators;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BuildingViewDTO setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BuildingViewDTO setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public BigDecimal getTaxPerPerson() {
        return taxPerPerson;
    }

    public BuildingViewDTO setTaxPerPerson(BigDecimal taxPerPerson) {
        this.taxPerPerson = taxPerPerson;
        return this;
    }

    public BigDecimal getTaxPerDog() {
        return taxPerDog;
    }

    public BuildingViewDTO setTaxPerDog(BigDecimal taxPerDog) {
        this.taxPerDog = taxPerDog;
        return this;
    }

    public BigDecimal getTaxPerElevatorChip() {
        return taxPerElevatorChip;
    }

    public BuildingViewDTO setTaxPerElevatorChip(BigDecimal taxPerElevatorChip) {
        this.taxPerElevatorChip = taxPerElevatorChip;
        return this;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public BuildingViewDTO setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
        return this;
    }

    public UserEntity getManager() {
        return manager;
    }

    public BuildingViewDTO setManager(UserEntity manager) {
        this.manager = manager;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public BuildingViewDTO setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCity() {
        return city;
    }

    public BuildingViewDTO setCity(String city) {
        this.city = city;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public BuildingViewDTO setStreet(String street) {
        this.street = street;
        return this;
    }

    public Set<UserEntity> getNeighbours() {
        return neighbours;
    }

    public BuildingViewDTO setNeighbours(Set<UserEntity> neighbours) {
        this.neighbours = neighbours;
        return this;
    }

    public Set<ApartmentEntity> getApartments() {
        return apartments;
    }

    public BuildingViewDTO setApartments(Set<ApartmentEntity> apartments) {
        this.apartments = apartments;
        return this;
    }

    public Set<TaxEntity> getTaxes() {
        return taxes;
    }

    public BuildingViewDTO setTaxes(Set<TaxEntity> taxes) {
        this.taxes = taxes;
        return this;
    }

    public Set<MessageEntity> getMessages() {
        return messages;
    }

    public BuildingViewDTO setMessages(Set<MessageEntity> messages) {
        this.messages = messages;
        return this;
    }
}
