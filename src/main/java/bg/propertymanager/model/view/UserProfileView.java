package bg.propertymanager.model.view;

public class UserProfileView {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String country;
    private String city;
    private String street;

    public UserProfileView() {
    }

    public Long getId() {
        return id;
    }

    public UserProfileView setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserProfileView setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserProfileView setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public UserProfileView setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserProfileView setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public UserProfileView setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCity() {
        return city;
    }

    public UserProfileView setCity(String city) {
        this.city = city;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public UserProfileView setStreet(String street) {
        this.street = street;
        return this;
    }
}
