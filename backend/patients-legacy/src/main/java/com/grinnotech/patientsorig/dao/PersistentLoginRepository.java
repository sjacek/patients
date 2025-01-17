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
package com.grinnotech.patientsorig.dao;

import com.grinnotech.patients.model.PersistentLogin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

/**
 *
 * @author Jacek Sztajnke
 */
public interface PersistentLoginRepository extends
        MongoRepository<PersistentLogin, String>
//        , QueryDslPredicateExecutor<PersistentLogin>
{

    Collection<PersistentLogin> findByUserId(String userId);

    void deletePersistentLoginBySeriesAndUserId(String series, String userId);

    void deleteBySeries(String series);

    void deleteByUserId(String userId);

    PersistentLogin findFirstBySeries(String series);
}
