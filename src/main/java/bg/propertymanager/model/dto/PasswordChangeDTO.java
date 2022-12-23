package bg.propertymanager.model.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class PasswordChangeDTO {
    private Long id;

    private String oldPassword;

    private String newPassword;
    private String matchingNewPassword;

    public PasswordChangeDTO() {
    }

    public Long getId() {
        return id;
    }

    public PasswordChangeDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public PasswordChangeDTO setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
        return this;
    }
    @NotEmpty(message = "Password can not be empty")
    @Length(min = 6, max = 100, message = "Password should be at least 6 symbols")
    public String getNewPassword() {
        return newPassword;
    }

    public PasswordChangeDTO setNewPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }
    @NotEmpty(message = "Password can not be empty")
    @Length(min = 6, max = 100, message = "Password should be at least 6 symbols")
    public String getMatchingNewPassword() {
        return matchingNewPassword;
    }

    public PasswordChangeDTO setMatchingNewPassword(String matchingNewPassword) {
        this.matchingNewPassword = matchingNewPassword;
        return this;
    }
}
