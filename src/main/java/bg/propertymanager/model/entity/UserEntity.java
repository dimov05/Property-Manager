package bg.propertymanager.model.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String country;
    private String city;
    private String street;
    private Set<RoleEntity> roles;
    private Set<BuildingEntity> managerInBuildings;
    private Set<BuildingEntity> ownerInBuildings;
    private Set<ApartmentEntity> apartments;
    private BuildingEntity selectedBuilding;
    private Set<MessageEntity> messages;
    private Set<TaxEntity> taxes;

    public UserEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "username", unique = true, nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "email", unique = true, nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "full_name", nullable = false)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "country", nullable = false)
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name = "city", nullable = false)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "street", nullable = false)
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    @OneToMany(mappedBy = "manager")
    public Set<BuildingEntity> getManagerInBuildings() {
        return managerInBuildings;
    }

    public void setManagerInBuildings(Set<BuildingEntity> managerInBuildings) {
        this.managerInBuildings = managerInBuildings;
    }

    @ManyToMany()
    @JoinTable(name = "owners_buildings",
            joinColumns = {@JoinColumn(name = "owner_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "building_id", referencedColumnName = "id")}
    )
    public Set<BuildingEntity> getOwnerInBuildings() {
        return ownerInBuildings;
    }

    public void setOwnerInBuildings(Set<BuildingEntity> ownerInBuildings) {
        this.ownerInBuildings = ownerInBuildings;
    }

    @OneToMany(mappedBy = "owner")
    public Set<ApartmentEntity> getApartments() {
        return apartments;
    }

    public void setApartments(Set<ApartmentEntity> apartments) {
        this.apartments = apartments;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "selected_building_id", referencedColumnName = "id")
    public BuildingEntity getSelectedBuilding() {
        return selectedBuilding;
    }

    public void setSelectedBuilding(BuildingEntity selectedBuilding) {
        this.selectedBuilding = selectedBuilding;
    }

    @OneToMany(mappedBy = "author")
    public Set<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(Set<MessageEntity> messages) {
        this.messages = messages;
    }

    @OneToMany(mappedBy = "owner")
    public Set<TaxEntity> getTaxes() {
        return taxes;
    }

    public void setTaxes(Set<TaxEntity> taxes) {
        this.taxes = taxes;
    }
}
