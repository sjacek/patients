package com.grinnotech.patients.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinnotech.patients.config.profiles.mongodb.MongoDb;
import com.grinnotech.patients.dto.UserDetailDto;
import com.grinnotech.patients.model.CUser;
import com.grinnotech.patients.model.User;
import com.grinnotech.patients.service.SecurityService;
import com.grinnotech.patients.web.CsrfController;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JsonAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final MongoDb mongoDb;

    private final ObjectMapper objectMapper;

    @Autowired
    public JsonAuthSuccessHandler(MongoDb mongoDb, ObjectMapper objectMapper) {
        this.mongoDb = mongoDb;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);

        MongoUserDetails userDetails = (MongoUserDetails) authentication.getPrincipal();
        if (userDetails != null) {
            User user;
            if (!userDetails.isPreAuth()) {

                user = this.mongoDb.getCollection(User.class).findOneAndUpdate(
                        Filters.eq(CUser.id, userDetails.getUserDbId()),
                        Updates.set(CUser.lastAccess, new Date()),
                        new FindOneAndUpdateOptions()
                        .returnDocument(ReturnDocument.AFTER));
            } else {
                user = this.mongoDb.getCollection(User.class)
                        .find(Filters.eq(CUser.id, userDetails.getUserDbId())).first();
            }
            result.put(SecurityService.AUTH_USER, new UserDetailDto(userDetails, user, CsrfController.getCsrfToken(request)));
        }

        response.getWriter().print(this.objectMapper.writeValueAsString(result));
        response.getWriter().flush();
    }

}
