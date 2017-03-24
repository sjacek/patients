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
package com.grinnotech.patients.dao;

import com.grinnotech.patients.model.PersistentLogin;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author jacek
 */
public interface PersistentLoginRepository extends MongoRepository<PersistentLogin, String> /*, QueryDslPredicateExecutor<PersistentLogin> */ {

    List<PersistentLogin> findByUserId(String userId);
    
    int deletePersistentLoginBySeriesAndUserId(String series, String userId);
}
