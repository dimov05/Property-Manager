package bg.propertymanager.model.dto.user;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class UserRegisterDTO {

    private String username;

    private String password;

    private String matchingPassword;

    private String email;

    private String phoneNumber;

    private String fullName;

    private String country;

    private String city;

    private String street;

    public UserRegisterDTO() {
    }

    @NotEmpty(message = "Username can not be empty")
    @Length(min = 5, max = 100, message = "Username should be at least 5 symbols")
    public String getUsername() {
        return username;
    }

    public UserRegisterDTO setUsername(String username) {
        this.username = username;
        return this;
    }
    @NotEmpty(message = "Password can not be empty")
    @Length(min = 6, max = 100, message = "Password should be at least 6 symbols")
    public String getPassword() {
        return password;
    }

    public UserRegisterDTO setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public UserRegisterDTO setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
        return this;
    }
    @NotEmpty(message = "Email can not be empty")
    @Email
    public String getEmail() {
        return email;
    }

    public UserRegisterDTO setEmail(String email) {
        this.email = email;
        return this;
    }
    @NotEmpty(message = "Phone number must be valid")
    @Pattern(regexp = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{3,6}$", message = "Phone number must be valid - +359*********")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserRegisterDTO setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    @NotEmpty(message = "Full name can not be empty")
    @Length(min = 4,message = "Full name should be at least 4 characters")
    public String getFullName() {
        return fullName;
    }

    public UserRegisterDTO setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }
    @NotEmpty(message = "Country can not be empty")
    public String getCountry() {
        return country;
    }

    public UserRegisterDTO setCountry(String country) {
        this.country = country;
        return this;
    }

    @NotEmpty(message = "City can not be empty")
    public String getCity() {
        return city;
    }

    public UserRegisterDTO setCity(String city) {
        this.city = city;
        return this;
    }

    @NotEmpty(message = "Street Address can not be empty")
    public String getStreet() {
        return street;
    }

    public UserRegisterDTO setStreet(String street) {
        this.street = street;
        return this;
    }
}
