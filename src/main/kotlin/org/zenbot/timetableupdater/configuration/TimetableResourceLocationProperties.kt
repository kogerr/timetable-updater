package org.zenbot.timetableupdater.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("timetable.resources")
class TimetableResourceLocationProperties {
    lateinit var folder: String
    lateinit var fileExtension: String
    lateinit var commentSign: String
}

