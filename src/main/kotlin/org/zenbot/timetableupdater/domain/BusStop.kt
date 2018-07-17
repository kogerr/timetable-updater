package org.zenbot.timetableupdater.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.lang.reflect.Constructor

@Document
data class BusStop (
        @Id var id: String? = null,
        var busStopName: String = "",
        var workDaySchedule: Schedule = Schedule(),
        var saturdaySchedule: Schedule = Schedule(),
        var sundaySchedule: Schedule = Schedule()
)