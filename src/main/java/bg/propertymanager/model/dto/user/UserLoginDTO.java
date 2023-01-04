package bg.propertymanager.model.dto.user;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class UserLoginDTO {
    private String username;

    private String password;

    public UserLoginDTO() {
    }

    @NotEmpty(message = "Username can not be empty")
    @Length(min = 5, max = 100, message = "Username should be at least 5 symbols")
    public String getUsername() {
        return username;
    }

    public UserLoginDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    @NotEmpty(message = "Password can not be empty")
    @Length(min = 6, max = 100, message = "Password should be at least 6 symbols")
    public String getPassword() {
        return password;
    }

    public UserLoginDTO setPassword(String password) {
        this.password = password;
        return this;
    }
}
