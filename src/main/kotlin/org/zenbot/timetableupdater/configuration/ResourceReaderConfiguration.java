package org.zenbot.timetableupdater.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@EnableConfigurationProperties(TimetableResourceLocationProperties.class)
public class ResourceReaderConfiguration {

    private final Environment environment;
    private final TimetableResourceLocationProperties properties;

    public ResourceReaderConfiguration(Environment environment, TimetableResourceLocationProperties properties) {
        this.environment = environment;
        this.properties = properties;
    }

    @Bean
    public ResourceReader resourceReader() {
        return new ResourceReader(environment, properties);
    }
}
