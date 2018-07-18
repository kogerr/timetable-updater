package org.zenbot.timetableupdater.domain

import org.springframework.data.annotation.Id

data class BusRouteLine (
        @Id var id: String? = null,
        var startBusStop: String = "",
        var endBusStop: String = "",
        var busStops: List<BusStop> = emptyList()
)