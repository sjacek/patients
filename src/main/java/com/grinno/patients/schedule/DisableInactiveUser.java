package com.grinno.patients.schedule;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import com.grinno.patients.config.MongoDb;
import com.grinno.patients.model.CUser;
import com.grinno.patients.model.User;

@Component
public class DisableInactiveUser {

    private final MongoDb mongoDb;

    @Autowired
    public DisableInactiveUser(MongoDb mongoDb) {
        this.mongoDb = mongoDb;
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void doCleanup() {
        // Inactivate users that have a lastAccess timestamp that is older than one year
        ZonedDateTime oneYearAgo = ZonedDateTime.now(ZoneOffset.UTC).minusYears(1);
        this.mongoDb.getCollection(User.class).updateMany(
                Filters.lte(CUser.lastAccess, Date.from(oneYearAgo.toInstant())),
                Updates.set(CUser.enabled, false));
    }

}