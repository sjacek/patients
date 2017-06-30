package com.grinnotech.patients.config.profiles.development;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinnotech.patients.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static ch.ralscha.extdirectspring.util.ExtDirectSpringUtil.generateApiString;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.GERMAN;
import static java.util.ResourceBundle.getBundle;
import static org.springframework.boot.autoconfigure.security.SecurityProperties.DEFAULT_FILTER_ORDER;

@Configuration
@Profile("development")
class SetupConfig {

    @Value("${info.app.name}")
    private String appName;
    @Value("${sencha.client-dir}")
    private String senchaClientDir;

    private static void writeEnums(Path clientDir) throws IOException {
        writeEnum(clientDir, "Authority", Authority.values(), true);
        writeEnum(clientDir, "PatientStatus", PatientStatus.values(), false);
        writeEnum(clientDir, "DisabilityLevel", DisabilityLevel.values(), false);
        writeEnum(clientDir, "ProjectStatus", ProjectStatus.values(), false);
        writeEnum(clientDir, "Gender", Gender.values(), false);
    }

    private static void writeEnum(Path clientDir, String name, Enum<?>[] values, boolean writeStore) throws IOException {
        StringBuilder sb = new StringBuilder(200);
        sb.append("Ext.define('Patients.constant.").append(name).append("', {\n").append("\tsingleton: true,\n");
        String valuesString = stream(values)
                .map(e -> format("\t%s: '%s'", e.name(), e.name()))
                .collect(Collectors.joining(",\n"));
        sb.append(valuesString).append("\n});");

        Path constantDir = clientDir.resolve("app").resolve("constant");
        if (Files.notExists(constantDir)) {
            Files.createDirectories(constantDir);
        }

        Files.write(constantDir.resolve(name + ".js"),
                sb.toString().getBytes(StandardCharsets.UTF_8));

        if (writeStore) {
            sb = new StringBuilder(200);

            sb.append("Ext.define('Patients.store.").append(name).append("', {\n")
                    .append("\textend: 'Ext.data.Store',\n")
                    .append("\tstoreId: '").append(StringUtils.uncapitalize(name)).append("',\n")
                    .append("\tdata: [\n");

            valuesString = stream(values)
                    .map(e -> format("\t\t{ value: Patients.constant.%s.%s }", name, e.name()))
                    .collect(Collectors.joining(",\n"));
            sb.append(valuesString).append("\n\t]\n});");

            Files.write(clientDir.resolve("app").resolve("store").resolve(name + ".js"),
                    sb.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        return new FilterRegistrationBean() {{
            setFilter(new CorsFilter(r -> new CorsConfiguration() {{
                setAllowedOrigins(singletonList(ALL));
                setAllowedMethods(singletonList(ALL));
                setAllowedHeaders(singletonList(ALL));
                setAllowCredentials(true);
            }}));
            setUrlPatterns(singleton("/*"));
            setOrder(DEFAULT_FILTER_ORDER - 1);
        }};
    }

    @EventListener
    public void handleContextRefresh(ApplicationReadyEvent event) throws IOException {
        String extDirectConfig = generateApiString(event.getApplicationContext());
        String userDir = getProperty("user.dir");
        Files.write(Paths.get(userDir, senchaClientDir, "api.js"), extDirectConfig.getBytes(StandardCharsets.UTF_8));

        Path clientDir = Paths.get(userDir, senchaClientDir);
        writeI18n(clientDir);
        writeEnums(clientDir);
    }

    private void writeI18n(Path clientDir) throws IOException {
        List<Locale> locales = asList(ENGLISH, GERMAN, new Locale("pl"));
        for (Locale locale : locales) {
            String tag = locale.toLanguageTag();
            String output = "var i18n = " + new ObjectMapper().writeValueAsString(buildMessageMap(locale)) + ";";
            Files.write(clientDir.resolve("i18n-" + tag + ".js"), output.getBytes(StandardCharsets.UTF_8));
        }
    }

    private Map<String, String> buildMessageMap(Locale locale) {
        Map<String, String> messages = new TreeMap<>();

        ResourceBundle rb = getBundle("messages", locale);
        Enumeration<String> e = rb.getKeys();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            messages.put(key, rb.getString(key));
        }

        rb = getBundle("ValidationMessages", locale);
        e = rb.getKeys();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            messages.put(key, rb.getString(key));
        }

        messages.put("app_name", this.appName);

        return messages;
    }
}