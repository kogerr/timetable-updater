package org.zenbot.timetableupdater.batch

data class Timetable (
        var routeName: String = "",
        var startBusStopName: String = "",
        var endBusStopName: String = "",
        var activeStopName: String = "",
        var timetable: MutableMap<Int, Map<String, String>> = mutableMapOf()
) {
    fun addRow(hour: Int,values: Map<String, String>) = timetable.put(hour, values)
}