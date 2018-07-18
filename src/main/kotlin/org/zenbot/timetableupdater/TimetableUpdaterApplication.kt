package org.zenbot.timetableupdater

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class TimetableUpdaterApplication

fun main(args: Array<String>) {
    runApplication<TimetableUpdaterApplication>(*args)
}