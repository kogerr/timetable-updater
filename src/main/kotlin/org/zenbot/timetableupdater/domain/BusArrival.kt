package org.zenbot.timetableupdater.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class BusArrival(
        @Id var id: String? = null,
        var lowfloor: Boolean = false,
        var arrivalHour: Int = 0,
        // Might be null as there could be no arrival in a particular hour
        var arrivalMinute: Int? = null
)