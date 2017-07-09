/*
 * Copyright (C) 2016 Jacek Sztajnke
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
package com.grinnotech.patients.service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.filter.StringFilter;
import com.grinnotech.patients.config.security.MongoUserDetails;
import com.grinnotech.patients.dao.OrganizationRepository;
import com.grinnotech.patients.dao.authorities.RequireAdminAuthority;
import com.grinnotech.patients.model.Organization;
import com.grinnotech.patients.util.ValidationMessages;
import com.grinnotech.patients.util.ValidationMessagesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_MODIFY;
import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_READ;
import static com.grinnotech.patients.util.QueryUtil.getSpringSort;

/**
 *
 * @author Jacek Sztajnke
 */
@Service
@RequireAdminAuthority
public class OrganizationService extends AbstractService<Organization> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private OrganizationRepository organizationRepository;

    @ExtDirectMethod(STORE_READ)
    public ExtDirectStoreResult<Organization> read(ExtDirectStoreReadRequest request) {

        StringFilter filter = request.getFirstFilterForField("filter");
        List<Organization> list = (filter != null)
                ? organizationRepository.findAllWithFilterActive(filter.getValue(), getSpringSort(request))
                : organizationRepository.findAllActive(getSpringSort(request));

        list.forEach(organization -> {
            if (organization.getParentId() != null)
                organization.setParent(organizationRepository.findOneActive(organization.getParentId()));
        });
        
        LOGGER.debug("read size:[" + list.size() + "]");

        return new ExtDirectStoreResult<>(list);
    }

    @ExtDirectMethod(STORE_MODIFY)
    public ExtDirectStoreResult<Organization> destroy(@AuthenticationPrincipal MongoUserDetails userDetails, Organization organization) {
        ExtDirectStoreResult<Organization> result = new ExtDirectStoreResult<>();

        LOGGER.debug("destroy 1");
        Organization old = organizationRepository.findOne(organization.getId());

        old.setId(null);
        old.setActive(false);
        organizationRepository.save(old);
        LOGGER.debug("destroy 2 " + old.getId());

        setAttrsForDelete(organization, userDetails, old);
        organizationRepository.save(organization);
        LOGGER.debug("destroy end");
        return result.setSuccess(true);
    }

    @ExtDirectMethod(STORE_MODIFY)
    public ValidationMessagesResult<Organization> update(@AuthenticationPrincipal MongoUserDetails userDetails, Organization organization) {
        List<ValidationMessages> violations = validateEntity(organization, userDetails.getLocale());

        ValidationMessagesResult<Organization> result = new ValidationMessagesResult<>(organization);
        result.setValidations(violations);

        LOGGER.debug("update 1: " + organization.toString());
        if (violations.isEmpty()) {
            Organization old = organizationRepository.findOne(organization.getId());
            if (old != null) {
                old.setId(null);
                old.setActive(false);
                organizationRepository.save(old);
                setAttrsForUpdate(organization, userDetails, old);
            }
            else {
                setAttrsForCreate(organization, userDetails);
            }

            organizationRepository.save(organization);
        }

        LOGGER.debug("update end");
        return result;
    }

    protected List<ValidationMessages> validateEntity(Organization organization, Locale locale) {

        return super.validateEntity(organization);
    }
}
