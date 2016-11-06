/* 
 * Copyright (C) 2015 jsztajnke
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

/* global Ext */

Ext.define('Patients.store.Patient', {
    extend: 'Ext.data.Store',

    alias: 'store.patient',

    requires: ['Patients.model.Patient'],
    pageSize: 25,
    autoLoad: true,
    autoSync: true,
    model: 'Patients.model.Patient',
    proxy: {
        type: 'direct',
        directFn: 'patientService.read'
//        type: 'ajax',
//        url: 'patientService/read.json',
//        reader: {
//            type: 'json',
//            rootProperty: 'data',
//            totalProperty: 'total'
//        }
    }
});
