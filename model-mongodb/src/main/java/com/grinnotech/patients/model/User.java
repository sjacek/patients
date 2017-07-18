package com.grinnotech.patients.model;

import ch.rasc.extclassgenerator.Model;
import ch.rasc.extclassgenerator.ModelField;
import ch.rasc.extclassgenerator.ModelType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.grinnotech.patients.domain.AbstractPersistable;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Document
@Model(value = "Patients.model.User",
        createMethod = "userService.update",
        readMethod = "userService.read",
        updateMethod = "userService.update",
        destroyMethod = "userService.destroy",
        rootProperty = "records",
        paging = true,
        identifier = "uuid")
@ModelField(value = "twoFactorAuth", persist = false, type = ModelType.BOOLEAN)
@JsonInclude(Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User extends AbstractPersistable {

    @NotBlank(message = "{fieldrequired}")
    private String firstName;

    @NotBlank(message = "{fieldrequired}")
    private String lastName;

    @Email(message = "{invalidemail}")
    @NotBlank(message = "{fieldrequired}")
    private String email;

//    @NotNull(message = "{fieldrequired}")
    private Set<String> organizationIds;

    @ch.rasc.bsoncodec.annotation.Transient
//    @javax.persistence.Transient
    @org.springframework.data.annotation.Transient
    private Collection<Organization> organizations;

    private Set<String> authorities;

    @JsonIgnore
    private String passwordHash;

    @NotBlank(message = "{fieldrequired}")
    private String locale;

    private boolean enabled;

    @ModelField(persist = false)
    private int failedLogins;

    @ModelField(dateFormat = "time", persist = false)
    private Date lockedOutUntil;

    @ModelField(dateFormat = "time", persist = false)
    private Date lastAccess;

    @JsonIgnore
    private String passwordResetToken;

    @JsonIgnore
    private Date passwordResetTokenValidUntil;

    @JsonIgnore
    private String secret;

    public boolean isTwoFactorAuth() {
        return StringUtils.hasText(this.getSecret());
    }
}
