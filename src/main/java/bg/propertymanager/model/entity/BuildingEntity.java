package bg.propertymanager.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
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
    private Set<UserEntity> neighbours;
    private Set<TaxEntity> taxes;
    private Set<MessageEntity> messages;

    public BuildingEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "floors", nullable = false)
    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
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

    @Column(name = "elevators", nullable = false)
    public int getElevators() {
        return elevators;
    }

    public void setElevators(int elevators) {
        this.elevators = elevators;
    }

    @Column(name = "image_url", nullable = false)
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Column(name = "balance", nullable = false)
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Column(name = "tax_per_person", nullable = false)
    public BigDecimal getTaxPerPerson() {
        return taxPerPerson;
    }

    public void setTaxPerPerson(BigDecimal taxPerPerson) {
        this.taxPerPerson = taxPerPerson;
    }

    @Column(name = "tax_per_dog", nullable = false)
    public BigDecimal getTaxPerDog() {
        return taxPerDog;
    }

    public void setTaxPerDog(BigDecimal taxPerDog) {
        this.taxPerDog = taxPerDog;
    }

    @Column(name = "tax_per_elevator_chip", nullable = false)
    public BigDecimal getTaxPerElevatorChip() {
        return taxPerElevatorChip;
    }

    public void setTaxPerElevatorChip(BigDecimal taxPerElevatorChip) {
        this.taxPerElevatorChip = taxPerElevatorChip;
    }

    @ManyToOne()
    public UserEntity getManager() {
        return manager;
    }

    public void setManager(UserEntity manager) {
        this.manager = manager;
    }


    @ManyToMany
    public Set<UserEntity> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Set<UserEntity> neighbours) {
        this.neighbours = neighbours;
    }
@OneToMany(mappedBy = "building")
    public Set<TaxEntity> getTaxes() {
        return taxes;
    }

    public void setTaxes(Set<TaxEntity> taxes) {
        this.taxes = taxes;
    }
@OneToMany(mappedBy = "building")
    public Set<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(Set<MessageEntity> messages) {
        this.messages = messages;
    }
}
