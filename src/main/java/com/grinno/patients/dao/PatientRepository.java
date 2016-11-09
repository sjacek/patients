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
package com.grinno.patients.dao;

import com.grinno.patients.model.Patient;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author jacek
 */
public interface PatientRepository extends
        MongoRepository<Patient, String>,
        PagingAndSortingRepository<Patient, String>,
        QueryDslPredicateExecutor<Patient>,
        PatientRepositoryCustom {

//    Patient findByPesel(String pesel);

    @Query("{active:true}")
    List<Patient> findAllActive();

    @Query("{$and: [{ $or:["
            + " {lastName: {$regex:?0,$options:'i'}}, {firstName: {$regex:?0,$options:'i'}}, {pesel: {$regex:?0,$options:'i'}} ]},"
            + " {active:true} ]}")
    List<Patient> findAllWithFilterNotDeleted(String filter);
}
