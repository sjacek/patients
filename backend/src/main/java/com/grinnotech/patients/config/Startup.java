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
package com.grinnotech.patients.config;

import com.grinnotech.patients.dao.*;
import com.grinnotech.patients.model.*;
import com.mongodb.client.MongoCollection;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;

import static com.grinnotech.patients.model.Authority.*;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

@Component
class Startup {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MongoDb mongoDb;

    private final PasswordEncoder passwordEncoder;

    private final OrganizationRepository organizationRepository;

    private final UserRepository userRepository;

    private final ContactRepository contactRepository;

    private final CountryDictionaryRepository addressDictionaryRepository;

    private final ZipCodePolandRepository zipCodePolandRepository;
    
    private final PatientRepository patientRepository;

    @Autowired
    public Startup(MongoDb mongoDb,
            OrganizationRepository organizationRepository, UserRepository userRepository, PatientRepository patientRepository,
            ContactRepository contactRepository, CountryDictionaryRepository addressDictionaryRepository, ZipCodePolandRepository zipCodePolandRepository,
            PasswordEncoder passwordEncoder) {
        this.mongoDb = mongoDb;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.contactRepository = contactRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressDictionaryRepository = addressDictionaryRepository;
        this.zipCodePolandRepository = zipCodePolandRepository;
        init();
    }

    private void init() {
        initOrganizations();
        initUsers();
        initContactMethods();
        initAddressDictionary();
        initZipCodePoland();
//        setOrganizationOnUsers();
//        setOrganizationOnPatients();
    }

    private void setOrganizationOnUsers() {
        Organization ppmd = organizationRepository.findByCodeActive("PPMDPoland");
        List<User> users = userRepository.findAllNotDeleted();
        users.forEach(user -> user.setOrganizationId(ppmd.getId()));
        userRepository.save(users);
    }

    private void setOrganizationOnPatients() {
        Organization ppmd = organizationRepository.findByCodeActive("PPMDPoland");
        List<Patient> patients = patientRepository.findAllActive();
        patients.forEach(patient -> patient.setOrganizationId(ppmd.getId()));
        patientRepository.save(patients);
    }

    private final UUID uuidRoot = randomUUID();

    private final UUID uuidPpmdPoland = randomUUID();

    private void initOrganizations() {
        LOGGER.debug("initOrganizations start");
        if (organizationRepository.count() == 0) {
            Organization root = new Organization();
            root.setId(uuidRoot.toString());
            root.setName("ROOT");
            root.setCode("ROOT");
            insert(root);

            Organization ppmdPoland = new Organization();
            ppmdPoland.setId(uuidPpmdPoland.toString());
            ppmdPoland.setName("Fundacja Parent Project Muscular Dystrophy");
            ppmdPoland.setCode("PPMDPoland");
            ppmdPoland.setParentId(root.getId());
            insert(ppmdPoland);

            Organization test = new Organization();
            test.setId(randomUUID().toString());
            test.setName("Test");
            test.setCode("test");
            test.setParentId(root.getId());
            insert(test);
        }
    }

    private void insert(Organization record) {
        record.setVersion(1);
        record.setActive(true);

        Organization organization = organizationRepository.insert(record);
        organization.setChainId(organization.getId());
        organizationRepository.save(organization);
    }
    
    private void initUsers() {
        LOGGER.debug("initUsers start");
        MongoCollection<User> userCollection = mongoDb.getCollection(User.class);
        if (userCollection.count() == 0) {
            // admin user
            User adminUser = new User();
            adminUser.setEmail("admin@starter.com");
            adminUser.setFirstName("admin");
            adminUser.setLastName("admin");
            adminUser.setLocale("pl");
            adminUser.setOrganizationId(uuidRoot.toString());
            adminUser.setPasswordHash(passwordEncoder.encode("admin"));
            adminUser.setEnabled(true);
            adminUser.setAuthorities(asList(ADMIN.name()));
            userCollection.insertOne(adminUser);

            // normal user
            User normalUser = new User();
            normalUser.setEmail("user@starter.com");
            normalUser.setFirstName("user");
            normalUser.setLastName("user");
            normalUser.setLocale("pl");
            normalUser.setOrganizationId(uuidPpmdPoland.toString());
            normalUser.setPasswordHash(passwordEncoder.encode("user"));
            normalUser.setEnabled(true);
            normalUser.setAuthorities(asList(USER.name(), EMPLOYEE.name()));
            userCollection.insertOne(normalUser);
        }
    }

    private void initContactMethods() {
        LOGGER.debug("initContactMethods start");
        if (contactRepository.count() == 0) {
            {
                ContactMethod method = new ContactMethod();
                method.setMethod("telefon domowy");
                method.setDescription("Telefon domowy");
                insert(method);
            }
            {
                ContactMethod method = new ContactMethod();
                method.setMethod("telefon komórkowy");
                method.setDescription("Telefon komórkowy");
                insert(method);
            }
            {
                ContactMethod method = new ContactMethod();
                method.setMethod("telefon służbowy");
                method.setDescription("Telefon służbowy");
                insert(method);
            }
            {
                ContactMethod method = new ContactMethod();
                method.setMethod("e-mail");
                method.setDescription("Poczta elektroniczna");
                insert(method);
            }
        }
    }

    private void insert(ContactMethod record) {
        record.setVersion(1);
        record.setActive(true);

        ContactMethod method = contactRepository.insert(record);
        method.setChainId(method.getId());
        contactRepository.save(method);
    }
        
    private void initAddressDictionary() {
        LOGGER.debug("initAddressDictionary start");

        final String CSV = "countries.csv";

        if (addressDictionaryRepository.count() == 0) {
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(new ClassPathResource(CSV).getInputStream()), ';', '"', 1);

//                ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
//                strat.setType(CountryDictionary.class);
//                strat.setColumnMapping(new String[] {"nazwa"});
//                new CsvToBean().parse(strat, reader).forEach(address -> insert((AbstractPersistable) address));
//                HeaderColumnNameMappingStrategy<AddressDictionary> strategy = new HeaderColumnNameMappingStrategy<>();
//                strategy.setType(CountryDictionary.class);
//                
//                CsvToBean<AddressDictionary> csvToBean = new CsvToBean<>();
//                
//                csvToBean.parse(strategy, reader).forEach(address -> insert(address));
                String[] line;
                while ((line = reader.readNext()) != null) {
                    // line[] is an array of values from the line
                    String code = line[0];
                    String country_en = line[2];
                    String country_pl = !"".equals(line[3]) ? line[3] : country_en;
                    String country_de = !"".equals(line[4]) ? line[4] : country_en;

                    LOGGER.debug("initAddressDictionary: " + code + "," + country_en);
                    insert(new CountryDictionary(code, country_en, country_pl, country_de));
                }
            } catch (FileNotFoundException ex) {
                LOGGER.error("File " + CSV + " not found", ex);
            } catch (IOException ex) {
                LOGGER.error("File " + CSV + " not found", ex);
            }
        }
    }

    private void insert(CountryDictionary record) {
        record.setVersion(1);
        record.setActive(true);

        CountryDictionary country = addressDictionaryRepository.insert(record);
        country.setChainId(country.getId());
        addressDictionaryRepository.save(country);
    }
    
    private void initZipCodePoland() {
        LOGGER.debug("initZipCodePoland start");

        final String CSV = "kody-pocztowe_GUS.csv";

        if (zipCodePolandRepository.count() == 0) {
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(new ClassPathResource(CSV).getInputStream()), ';', '"', 1);

                String[] line;
                final char[] delimiters = {' ', ',', '.', '-', '(', ')'};
                while ((line = reader.readNext()) != null) {
                    final String zipCode = line[0];
                    final String postOffice = line[1];
                    final String city = capitalizeFully(line[2], delimiters);
                    final String voivodship = capitalizeFully(line[3], delimiters);
                    final String street = line[4];
                    final String county = capitalizeFully(line[5], delimiters);

                    LOGGER.debug("initZipCodePoland: " + zipCode + ", " + postOffice + ", " + city + ", " + voivodship + ", " + street + ", " + county);

                    int count = zipCodePolandRepository.CountByExample(zipCode, postOffice, city, voivodship, street, county);
                    if (count == 0) {
                        insert(new ZipCodePoland(zipCode, postOffice, city, voivodship, street, county));
                    } else {
                        LOGGER.debug("****************** duplicate found, don't insert!");
                    }
                }
            } catch (FileNotFoundException ex) {
                LOGGER.error("File " + CSV + " not found", ex);
            } catch (IOException ex) {
                LOGGER.error("File " + CSV + " not found", ex);
            }
        }
    }

    private void insert(ZipCodePoland record) {
        record.setVersion(1);
        record.setActive(true);

        ZipCodePoland zipCode = zipCodePolandRepository.insert(record);
        zipCode.setChainId(zipCode.getId());
        zipCodePolandRepository.save(zipCode);
    }

}