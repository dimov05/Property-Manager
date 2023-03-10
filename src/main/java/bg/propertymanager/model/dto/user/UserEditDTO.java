package bg.propertymanager.model.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class UserEditDTO {
    private Long id;
    private String username;
    private String email;

    private String fullName;
    private String phoneNumber;

    private String country;

    private String city;

    private String street;

    public UserEditDTO() {
    }

    public Long getId() {
        return id;
    }

    public UserEditDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserEditDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    @NotEmpty(message = "Email can not be empty")
    @Email
    public String getEmail() {
        return email;
    }

    public UserEditDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    @NotEmpty(message = "Phone number must not be empty")
    @Pattern(regexp = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{3,6}$", message = "Phone number must be valid - +359*********")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserEditDTO setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public UserEditDTO setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    @NotEmpty(message = "Country can not be empty")
    public String getCountry() {
        return country;
    }

    public UserEditDTO setCountry(String country) {
        this.country = country;
        return this;
    }

    @NotEmpty(message = "City can not be empty")
    public String getCity() {
        return city;
    }

    public UserEditDTO setCity(String city) {
        this.city = city;
        return this;
    }

    @NotEmpty(message = "Street Address can not be empty")
    public String getStreet() {
        return street;
    }

    public UserEditDTO setStreet(String street) {
        this.street = street;
        return this;
    }
}
