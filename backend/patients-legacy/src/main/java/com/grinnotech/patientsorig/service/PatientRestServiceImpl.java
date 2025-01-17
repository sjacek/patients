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
package com.grinnotech.patientsorig.service;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_MODIFY;
import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_READ;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

import com.grinnotech.patientsorig.NotFoundException;
import com.grinnotech.patientsorig.dao.PatientRepository;
import com.grinnotech.patients.model.Patient;
import com.grinnotech.patientsorig.vo.Result;
import com.grinnotech.patientsorig.vo.ResultFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

@Transactional
@Service("patientRestService")
public class PatientRestServiceImpl implements PatientRestService {

    final protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final PatientRepository patientRepository;

	public PatientRestServiceImpl(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	@ExtDirectMethod(STORE_MODIFY)
    @Transactional(readOnly = false, propagation = REQUIRED)
    @Override
    public Result<Patient> update(String idPatient, String firstName, String secondName, String lastName, String pesel, Date birthday) {
        Patient patient = patientRepository.save(new Patient() {{
            setId(idPatient);
            setFirstName(firstName);
            setSecondName(secondName);
            setPesel(pesel);
            setBirthday(birthday);
        }});
        return ResultFactory.getSuccessResult(patient);
    }

    @ExtDirectMethod(STORE_MODIFY)
    @Transactional(readOnly = false, propagation = REQUIRED)
    @Override
    public Result<Patient> destroy(String idPatient) throws NotFoundException {
        Optional<Patient> oPatient = patientRepository.findById(idPatient);
        Patient patient = oPatient.orElseThrow(() -> new NotFoundException("Patient id={} not found", idPatient));
        patientRepository.delete(patient);
        // TODO:
//        String msg = "Patient PESEL=" + patient.getPesel() + " was deleted";
        String msg = "Patient PESEL=... was deleted";
        LOGGER.debug(msg);
        return ResultFactory.getSuccessResultMsg(msg);
    }
    
    @ExtDirectMethod(STORE_READ)
    @Transactional(readOnly = true, propagation = SUPPORTS)
    @Override
    public Result<Patient> read(String idPatient) throws NotFoundException {
        Optional<Patient> oPatient = patientRepository.findById(idPatient);
        Patient patient = oPatient.orElseThrow(() -> new NotFoundException("Patient id={} not found", idPatient));
        return ResultFactory.getSuccessResult(patient);
    }

    @Transactional(readOnly = true, propagation = SUPPORTS)
    @Override
    public Result<List<Patient>> findAll() {
        return ResultFactory.getSuccessResult(patientRepository.findAll());
    }
    
}
