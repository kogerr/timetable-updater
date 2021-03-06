package org.zenbot.timetableupdater.configuration

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
@EnableConfigurationProperties(TimetableResourceLocationProperties::class)
open class ResourceReaderConfiguration(val environment: Environment, val properties: TimetableResourceLocationProperties) {

    @Bean open fun resourceReader() = ResourceReader(environment, properties)
}