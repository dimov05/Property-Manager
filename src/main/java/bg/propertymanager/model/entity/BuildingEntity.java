package bg.propertymanager.model.entity;


import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "buildings")
public class BuildingEntity {
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
    private LocalDate registrationDate;
    private Set<UserEntity> neighbours;
    private Set<ApartmentEntity> apartments;
    private Set<TaxEntity> taxes;
    private Set<MessageEntity> messages;

    public BuildingEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public BuildingEntity setId(Long id) {
        this.id = id;
        return this;
    }

    @Column(name = "name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public BuildingEntity setName(String name) {
        this.name = name;
        return this;
    }

    @Column(name = "floors", nullable = false)
    public int getFloors() {
        return floors;
    }

    public BuildingEntity setFloors(int floors) {
        this.floors = floors;
        return this;
    }

    @Column(name = "country", nullable = false)
    public String getCountry() {
        return country;
    }

    public BuildingEntity setCountry(String country) {
        this.country = country;
        return this;
    }

    @Column(name = "city", nullable = false)
    public String getCity() {
        return city;
    }

    public BuildingEntity setCity(String city) {
        this.city = city;
        return this;
    }

    @Column(name = "street", nullable = false)
    public String getStreet() {
        return street;
    }

    public BuildingEntity setStreet(String street) {
        this.street = street;
        return this;
    }

    @Column(name = "elevators", nullable = false)
    public int getElevators() {
        return elevators;
    }

    public BuildingEntity setElevators(int elevators) {
        this.elevators = elevators;
        return this;
    }

    @Column(name = "image_url", nullable = false)
    public String getImageUrl() {
        return imageUrl;
    }

    public BuildingEntity setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    @Column(name = "balance", nullable = false)
    public BigDecimal getBalance() {
        return balance;
    }

    public BuildingEntity setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    @Column(name = "tax_per_person", nullable = false)
    public BigDecimal getTaxPerPerson() {
        return taxPerPerson;
    }

    public BuildingEntity setTaxPerPerson(BigDecimal taxPerPerson) {
        this.taxPerPerson = taxPerPerson;
        return this;
    }

    @Column(name = "tax_per_dog", nullable = false)
    public BigDecimal getTaxPerDog() {
        return taxPerDog;
    }

    public BuildingEntity setTaxPerDog(BigDecimal taxPerDog) {
        this.taxPerDog = taxPerDog;
        return this;
    }

    @Column(name = "tax_per_elevator_chip", nullable = false)
    public BigDecimal getTaxPerElevatorChip() {
        return taxPerElevatorChip;
    }

    public BuildingEntity setTaxPerElevatorChip(BigDecimal taxPerElevatorChip) {
        this.taxPerElevatorChip = taxPerElevatorChip;
        return this;
    }

    @Column(name = "registration_date",columnDefinition = "DATE", nullable = false)
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public BuildingEntity setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
        return this;
    }

    @ManyToOne()
    public UserEntity getManager() {
        return manager;
    }

    public BuildingEntity setManager(UserEntity manager) {
        this.manager = manager;
        return this;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    public Set<UserEntity> getNeighbours() {
        return neighbours;
    }

    public BuildingEntity setNeighbours(Set<UserEntity> neighbours) {

        this.neighbours = neighbours;
        return this;
    }

    @OneToMany(mappedBy = "building")
    public Set<TaxEntity> getTaxes() {
        return taxes;
    }

    public BuildingEntity setTaxes(Set<TaxEntity> taxes) {
        this.taxes = taxes;
        return this;
    }

    @OneToMany(mappedBy = "building", fetch = FetchType.EAGER)
    public Set<MessageEntity> getMessages() {
        return messages;
    }

    public BuildingEntity setMessages(Set<MessageEntity> messages) {
        this.messages = messages;
        return this;
    }

    @OneToMany(mappedBy = "building")
    public Set<ApartmentEntity> getApartments() {
        return apartments;
    }

    public BuildingEntity setApartments(Set<ApartmentEntity> apartments) {
        this.apartments = apartments;
        return this;
    }
}
