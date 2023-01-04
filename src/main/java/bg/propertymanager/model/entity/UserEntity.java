package bg.propertymanager.model.entity;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String country;
    private String city;
    private String street;
    private LocalDate registrationDate;
    private List<RoleEntity> roles;
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


    public UserEntity setId(Long id) {
        this.id = id;
        return this;
    }

    @Column(name = "username", unique = true, nullable = false)
    public String getUsername() {
        return username;
    }

    public UserEntity setUsername(String username) {
        this.username = username;
        return this;
    }


    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public UserEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    @Column(name = "email", unique = true, nullable = false)
    public String getEmail() {
        return email;
    }

    public UserEntity setEmail(String email) {
        this.email = email;
        return this;
    }


    @Column(name = "full_name", nullable = false)
    public String getFullName() {
        return fullName;
    }

    public UserEntity setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    @Column(name = "phone_number", nullable = false)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserEntity setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    @Column(name = "country", nullable = false)
    public String getCountry() {
        return country;
    }

    public UserEntity setCountry(String country) {
        this.country = country;
        return this;
    }

    @Column(name = "city", nullable = false)
    public String getCity() {
        return city;
    }

    public UserEntity setCity(String city) {
        this.city = city;
        return this;
    }

    @Column(name = "street", nullable = false)
    public String getStreet() {
        return street;
    }

    public UserEntity setStreet(String street) {
        this.street = street;
        return this;
    }

    @Column(name = "registration_date",columnDefinition = "DATE", nullable = false)
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public UserEntity setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
        return this;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    public List<RoleEntity> getRoles() {
        return roles;
    }

    public UserEntity setRoles(List<RoleEntity> roles) {
        this.roles = roles;
        return this;
    }

    @OneToMany(mappedBy = "manager")
    public Set<BuildingEntity> getManagerInBuildings() {
        return managerInBuildings;
    }

    public UserEntity setManagerInBuildings(Set<BuildingEntity> managerInBuildings) {
        this.managerInBuildings = managerInBuildings;
        return this;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "owners_buildings",
            joinColumns = {@JoinColumn(name = "owner_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "building_id", referencedColumnName = "id")}
    )
    public Set<BuildingEntity> getOwnerInBuildings() {
        return ownerInBuildings;
    }

    public UserEntity setOwnerInBuildings(Set<BuildingEntity> ownerInBuildings) {
        this.ownerInBuildings = ownerInBuildings;
        return this;
    }

    @OneToMany(mappedBy = "owner")
    public Set<ApartmentEntity> getApartments() {
        return apartments;
    }

    public UserEntity setApartments(Set<ApartmentEntity> apartments) {
        this.apartments = apartments;
        return this;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "selected_building_id", referencedColumnName = "id")
    public BuildingEntity getSelectedBuilding() {
        return selectedBuilding;
    }

    public UserEntity setSelectedBuilding(BuildingEntity selectedBuilding) {
        this.selectedBuilding = selectedBuilding;
        return this;
    }


    @OneToMany(mappedBy = "author")
    public Set<MessageEntity> getMessages() {
        return messages;
    }

    public UserEntity setMessages(Set<MessageEntity> messages) {
        this.messages = messages;
        return this;
    }


    @OneToMany(mappedBy = "owner")
    public Set<TaxEntity> getTaxes() {
        return taxes;
    }

    public UserEntity setTaxes(Set<TaxEntity> taxes) {
        this.taxes = taxes;
        return this;
    }


}
