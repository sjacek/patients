/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.grinno.patients.model;

import ch.rasc.extclassgenerator.Model;
import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Jacek Sztajnke
 */
@Document(collection="dictionary")
@Model(value = "Patients.model.Contact",
        readMethod = "contactService.read",
        createMethod = "contactService.update",
        updateMethod = "contactService.update",
        destroyMethod = "contactService.destroy",
        paging = true,
        identifier = "uuid"
)
@JsonInclude(NON_NULL)
public class Contact extends Dictionary {

    @NotBlank(message = "{fieldrequired}")
    @Indexed
    private String method;
    
    private String description;
    
    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getId() + "[" + getMethod() + "]";
    }
}
