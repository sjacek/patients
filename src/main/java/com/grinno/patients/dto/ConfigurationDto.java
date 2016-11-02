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
package com.grinno.patients.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ConfigurationDto {

    private String logLevel;

    private Integer loginLockMinutes;

    private Integer loginLockAttempts;

    public String getLogLevel() {
        return this.logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public Integer getLoginLockMinutes() {
        return this.loginLockMinutes;
    }

    public void setLoginLockMinutes(Integer loginLockMinutes) {
        this.loginLockMinutes = loginLockMinutes;
    }

    public Integer getLoginLockAttempts() {
        return this.loginLockAttempts;
    }

    public void setLoginLockAttempts(Integer loginLockAttempts) {
        this.loginLockAttempts = loginLockAttempts;
    }

}
