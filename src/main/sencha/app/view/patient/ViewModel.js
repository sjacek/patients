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
/* global Ext */

Ext.define('Patients.view.patient.ViewModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'Patients.model.Patient'
    ],

    // This enables "viewModel: { type: 'patient' }" in the view:
    alias: 'viewmodel.patient',

    stores: {
        patients: {
            model: 'Patients.model.Patient',
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: 'patient/findAll.json',
                reader: {
                    type: 'json',
                    rootProperty: 'data'
                },
                writer: {
                    type: 'json'
                }
            }
        }
//        patients: {
//            model: 'Patients.model.PatientModel',
////            autoLoad: true,
//            remoteFilter: true //,
////            filters: [{
////                property: 'assigneeId',
////                value: '{currentUser.id}'
////            }, {
////                property: 'projectId',
////                value: '{projectId}'
////            }, {
////                property: 'status',
////                value: 2
////            }]
//        }
    }
});