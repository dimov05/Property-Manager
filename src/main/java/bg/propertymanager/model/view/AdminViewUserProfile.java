package bg.propertymanager.model.view;

import bg.propertymanager.model.entity.*;

import java.util.List;
import java.util.Set;

public class AdminViewUserProfile {
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

    public AdminViewUserProfile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEntity> roles) {
        this.roles = roles;
    }

    public Set<BuildingEntity> getManagerInBuildings() {
        return managerInBuildings;
    }

    public void setManagerInBuildings(Set<BuildingEntity> managerInBuildings) {
        this.managerInBuildings = managerInBuildings;
    }

    public Set<BuildingEntity> getOwnerInBuildings() {
        return ownerInBuildings;
    }

    public void setOwnerInBuildings(Set<BuildingEntity> ownerInBuildings) {
        this.ownerInBuildings = ownerInBuildings;
    }

    public Set<ApartmentEntity> getApartments() {
        return apartments;
    }

    public void setApartments(Set<ApartmentEntity> apartments) {
        this.apartments = apartments;
    }

    public BuildingEntity getSelectedBuilding() {
        return selectedBuilding;
    }

    public void setSelectedBuilding(BuildingEntity selectedBuilding) {
        this.selectedBuilding = selectedBuilding;
    }

    public Set<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(Set<MessageEntity> messages) {
        this.messages = messages;
    }

    public Set<TaxEntity> getTaxes() {
        return taxes;
    }

    public void setTaxes(Set<TaxEntity> taxes) {
        this.taxes = taxes;
    }
}
