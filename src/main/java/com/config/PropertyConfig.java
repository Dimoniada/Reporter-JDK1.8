package com.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

@Configuration
public abstract class PropertyConfig {
    public static ConfigurableEnvironment environment;

    private static final Logger log = LoggerFactory.getLogger(PropertyConfig.class);

    /**
     * Returns a ConfigurableEnvironment with properties loaded based on the active profile.
     * If a profile is active, it loads application-{profile}.properties, otherwise it loads application.properties.
     *
     * @return ConfigurableEnvironment with the appropriate properties loaded
     * @throws IOException if there's an error loading the properties
     */
    public static ConfigurableEnvironment getEnvironment() throws IOException {
        if (PropertyConfig.environment != null) {
            return PropertyConfig.environment;
        }
        final String activeProfile = System.getProperty("spring.profiles.active", "");
        final ConfigurableEnvironment environment = new StandardEnvironment();

        final String propertiesPath = "classpath:application" +
            (activeProfile.isEmpty() ? "" : "-" + activeProfile) + ".properties";

        environment.getPropertySources().addLast(new ResourcePropertySource(propertiesPath));
        PropertyConfig.environment = environment;
        return environment;
    }

    public static <T> T getProperty(String propertyName, Class<T> targetType) {
        try {
            final ConfigurableEnvironment environment = PropertyConfig.getEnvironment();
            return environment.getProperty(propertyName, targetType);
        } catch (IOException e) {
            log.error("Can't get property '{}' from environment", propertyName, e);
            return null;
        }
    }
}
