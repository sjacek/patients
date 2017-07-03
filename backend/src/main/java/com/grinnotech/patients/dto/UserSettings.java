package com.grinnotech.patients.dto;

import ch.rasc.extclassgenerator.Model;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.grinnotech.patients.model.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Model(value = "Patients.model.UserSettings",
        readMethod = "userConfigService.readSettings",
        updateMethod = "userConfigService.updateSettings",
        rootProperty = "records")
@JsonInclude(NON_NULL)
@Getter
@Setter
public class UserSettings {

    @NotBlank(message = "{fieldrequired}")
    private String firstName;

    @NotBlank(message = "{fieldrequired}")
    private String lastName;

    @NotBlank(message = "{fieldrequired}")
    private String locale;

    @Email(message = "{invalidemail}")
    @NotBlank(message = "{fieldrequired}")
    private String email;

    private String currentPassword;
    private String newPassword;
    private String newPasswordRetype;
    private boolean twoFactorAuth;

    public UserSettings() {
        // default constructor
    }

    public UserSettings(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.locale = user.getLocale();
        this.email = user.getEmail();
        this.twoFactorAuth = user.isTwoFactorAuth();
    }
}
