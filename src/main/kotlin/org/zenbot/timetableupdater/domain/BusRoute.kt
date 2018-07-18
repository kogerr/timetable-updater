package org.zenbot.timetableupdater.domain

import org.springframework.data.annotation.Id
import java.util.ArrayList
import java.util.stream.Collectors

data class BusRoute (
        @Id var id: String? = null,
        var routename: String = "",
        var busRouteLines: MutableList<BusRouteLine> = mutableListOf()
) {
    fun getRoutePathByStartStopName(startBusStopName: String) : BusRouteLine {
        val routeLine = busRouteLines.stream()
                .filter { line -> line.startBusStop == startBusStopName }
                .findFirst()
        return if (routeLine.isPresent) {
            routeLine.get()
        } else {
            val result = BusRouteLine()
            result.busStops = mutableListOf()
            result
        }
    }

    fun hasNoRoutePath(busRouteLine: BusRouteLine): Boolean {
        return !busRouteLines.stream()
                .map { line -> line.startBusStop }
                .collect(Collectors.toList())
                .contains(busRouteLine.startBusStop)
    }
}