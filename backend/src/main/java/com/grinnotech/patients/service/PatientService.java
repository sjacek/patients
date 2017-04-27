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
import com.grinnotech.patients.dao.PatientRepository;
import com.grinnotech.patients.dao.authorities.RequireUserEmployeeAuthority;
import com.grinnotech.patients.model.Patient;
import com.grinnotech.patients.util.ValidationMessages;
import com.grinnotech.patients.util.ValidationMessagesResult;
import com.grinnotech.patients.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_MODIFY;
import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_READ;
import static com.grinnotech.patients.util.PeselValidator.peselIsValid;
import static com.grinnotech.patients.util.QueryUtil.getSpringSort;

/**
 *
 * @author jacek
 */
@Service
@Cacheable("main")
@RequireUserEmployeeAuthority
public class PatientService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private MessageSource messageSource;

    @ExtDirectMethod(STORE_READ)
    public ExtDirectStoreResult<Patient> read(ExtDirectStoreReadRequest request) {

        StringFilter filter = request.getFirstFilterForField("filter");
        List<Patient> list = (filter != null)
                ? patientRepository.findAllWithFilterActive(filter.getValue(), getSpringSort(request))
                : patientRepository.findAllActive(getSpringSort(request));

        LOGGER.debug("read size:[" + list.size() + "]");

        return new ExtDirectStoreResult<>(list);
    }

    @ExtDirectMethod(STORE_MODIFY)
    public ExtDirectStoreResult<Patient> destroy(@AuthenticationPrincipal MongoUserDetails userDetails, Patient patient) {
        ExtDirectStoreResult<Patient> result = new ExtDirectStoreResult<>();

        LOGGER.debug("destroy 1");
        Patient old = patientRepository.findOne(patient.getId());

        old.setId(null);
        old.setActive(false);
        patientRepository.save(old);
        LOGGER.debug("destroy 2 " + old.getId());

        setAttrsForDelete(patient, userDetails, old);
        patientRepository.save(patient);
        LOGGER.debug("destroy end");
        return result.setSuccess(true);
    }

    @ExtDirectMethod(STORE_MODIFY)
    public ValidationMessagesResult<Patient> update(@AuthenticationPrincipal MongoUserDetails userDetails, Patient patient) {
        List<ValidationMessages> violations = validateEntity(patient, userDetails.getLocale());

        ValidationMessagesResult<Patient> result = new ValidationMessagesResult<>(patient);
        result.setValidations(violations);

        LOGGER.debug("update 1: " + patient.toString());
        if (violations.isEmpty()) {
            Patient old = patientRepository.findOne(patient.getId());
            if (old != null) {
                old.setId(null);
                old.setActive(false);
                patientRepository.save(old);
                setAttrsForUpdate(patient, userDetails, old);
            }
            else {
                setAttrsForCreate(patient, userDetails);
            }

            patientRepository.save(patient);
        }

        LOGGER.debug("update end");
        return result;
    }

    private List<ValidationMessages> validateEntity(Patient patient, Locale locale) {
        List<ValidationMessages> validations = ValidationUtil.validateEntity(validator, patient);

        if (!peselIsValid(patient.getPesel())) {
            ValidationMessages validationError = new ValidationMessages();
            validationError.setField("pesel");
            validationError.setMessage(messageSource.getMessage("patient_pesel_not_valid", null, locale));
            validations.add(validationError);
        }
        return validations;
    }

//    private boolean isEmailUnique(String patientId, String email) {
//        if (StringUtils.hasText(email)) {
//            long count;
//            if (patientId != null) {
//                count = this.mongoDb.getCollection(Patient.class)
//                        .count(Filters.and(
//                                Filters.regex(CPatient.email, "^" + email + "$", "i"),
//                                Filters.ne(CPatient.id, patientId)));
//            } else {
//                count = this.mongoDb.getCollection(Patient.class)
//                        .count(Filters.regex(CPatient.email, "^" + email + "$", "i"));
//            }
//
//            return count == 0;
//        }
//        return true;
//    }
}