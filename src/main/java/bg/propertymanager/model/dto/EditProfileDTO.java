package bg.propertymanager.model.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class EditProfileDTO {
    private Long id;

    private String email;

    private String fullName;
    private String phoneNumber;

    private String country;

    private String city;

    private String street;

    public EditProfileDTO() {
    }

    public Long getId() {
        return id;
    }

    public EditProfileDTO setId(Long id) {
        this.id = id;
        return this;
    }

    @NotEmpty(message = "Email can not be empty")
    @Email
    public String getEmail() {
        return email;
    }

    public EditProfileDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    @NotEmpty(message = "Phone number must not be empty")
    @Pattern(regexp = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{3,6}$", message = "Phone number must be valid - +359*********")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public EditProfileDTO setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public EditProfileDTO setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    @NotEmpty(message = "Country can not be empty")
    public String getCountry() {
        return country;
    }

    public EditProfileDTO setCountry(String country) {
        this.country = country;
        return this;
    }

    @NotEmpty(message = "City can not be empty")
    public String getCity() {
        return city;
    }

    public EditProfileDTO setCity(String city) {
        this.city = city;
        return this;
    }

    @NotEmpty(message = "Street Address can not be empty")
    public String getStreet() {
        return street;
    }

    public EditProfileDTO setStreet(String street) {
        this.street = street;
        return this;
    }
}
