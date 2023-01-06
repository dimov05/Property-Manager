package bg.propertymanager.model.security;

import bg.propertymanager.model.entity.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;
import java.util.Set;

// Used for thymeleaf engine ( get information about user )
@Component
@SessionScope
public class userViewEntity {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String country;
    private String city;
    private String street;
    private List<RoleEntity> roles;
    private Set<BuildingEntity> managerInBuildings;
    private Set<BuildingEntity> ownerInBuildings;
    private Set<ApartmentEntity> apartments;
    private BuildingEntity selectedBuilding;
    private Set<MessageEntity> messages;
    private Set<TaxEntity> taxes;

    public userViewEntity() {
    }

    public Long getId() {
        return id;
    }

    public userViewEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public userViewEntity setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public userViewEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public userViewEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public userViewEntity setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public userViewEntity setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public userViewEntity setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCity() {
        return city;
    }

    public userViewEntity setCity(String city) {
        this.city = city;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public userViewEntity setStreet(String street) {
        this.street = street;
        return this;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }

    public userViewEntity setRoles(List<RoleEntity> roles) {
        this.roles = roles;
        return this;
    }

    public Set<BuildingEntity> getManagerInBuildings() {
        return managerInBuildings;
    }

    public userViewEntity setManagerInBuildings(Set<BuildingEntity> managerInBuildings) {
        this.managerInBuildings = managerInBuildings;
        return this;
    }

    public Set<BuildingEntity> getOwnerInBuildings() {
        return ownerInBuildings;
    }

    public userViewEntity setOwnerInBuildings(Set<BuildingEntity> ownerInBuildings) {
        this.ownerInBuildings = ownerInBuildings;
        return this;
    }

    public Set<ApartmentEntity> getApartments() {
        return apartments;
    }

    public userViewEntity setApartments(Set<ApartmentEntity> apartments) {
        this.apartments = apartments;
        return this;
    }

    public BuildingEntity getSelectedBuilding() {
        return selectedBuilding;
    }

    public userViewEntity setSelectedBuilding(BuildingEntity selectedBuilding) {
        this.selectedBuilding = selectedBuilding;
        return this;
    }

    public Set<MessageEntity> getMessages() {
        return messages;
    }

    public userViewEntity setMessages(Set<MessageEntity> messages) {
        this.messages = messages;
        return this;
    }

    public Set<TaxEntity> getTaxes() {
        return taxes;
    }

    public userViewEntity setTaxes(Set<TaxEntity> taxes) {
        this.taxes = taxes;
        return this;
    }
}
