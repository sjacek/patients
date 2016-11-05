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
    requires: ['Ext.data.BufferedStore'],

    data: {
        selectedObject: null,
        totalCount: null
    },
    stores: {
        objects: {
            model: 'Patients.model.Patient',
            autoLoad: false,
            buffered: true,
            remoteSort: true,
            remoteFilter: true,
            sorters: [{
                    property: 'lastName',
                    direction: 'ASC'
                }],
            listeners: {
                load: 'onObjectStoreLoad'
            },
            pageSize: 100,
            leadingBufferZone: 200
        },
    formulas: {
        isUserDisabled: {
            bind: {
                bindTo: '{selectedObject}',
                deep: true
            },
            get: function (selectedObject) {
                return !selectedObject || selectedObject.phantom || !selectedObject.get('enabled');
            }
        },
        isPhantomObject: {
            bind: {
                bindTo: '{selectedObject}',
                deep: true
            },
            get: function (selectedObject) {
                return !selectedObject || selectedObject.phantom;
            }
        }
    }
//        patients: {
//            model: 'Patients.model.Patient',
//            autoLoad: true,
//            proxy: {
//                type: 'ajax',
//                url: 'patient/findAll.json',
//                reader: {
//                    type: 'json',
//                    rootProperty: 'data'
//                },
//                writer: {
//                    type: 'json'
//                }
//            }
//        }
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
