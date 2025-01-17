/*
 * Copyright (C) 2017 Pivotal Software, Inc.
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
package com.grinnotech.patients.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grinnotech.patients.domain.AbstractPersistable;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

import ch.rasc.extclassgenerator.Model;
import ch.rasc.extclassgenerator.ModelField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Jacek Sztajnke
 */
@Document(collection = "organization")
//@CompoundIndexes(
//TODO: @CompoundIndex(name = "name", def = "{ ... }")
//TODO: @CompoundIndex(name = "code", def = "{ ... }")
//)
@Model(value = "Patients.model.Organization", createMethod = "organizationService.update", readMethod = "organizationService.read", updateMethod = "organizationService.update", destroyMethod = "organizationService.destroy", paging = true, identifier = "uuid")
@JsonInclude(NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Organization extends AbstractPersistable {

	@NotBlank(message = "{fieldrequired}")
	@Indexed
	private String name;

	@Indexed
	private String code;

	@Indexed
	private String parentId;

	@Transient
	@ModelField
	@Builder.Default
	private Organization parent = null;
}
