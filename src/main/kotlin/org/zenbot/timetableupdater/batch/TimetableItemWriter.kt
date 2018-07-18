package org.zenbot.timetableupdater.batch

import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemWriter
import org.zenbot.timetableupdater.dao.RouteRepository
import org.zenbot.timetableupdater.domain.*
import java.util.*

open class TimetableItemWriter(private val routeRepository: RouteRepository) : ItemWriter<Timetable> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun write(list: List<Timetable>) {
        checkConstraints(list)
        val timetable = list[0]
        log.info("Writing busRoute [#{} from={}, stop={}] to database", timetable.routeName, timetable.startBusStopName, timetable.activeStopName)
        val busRoute = getRoute(timetable)
        val busRouteLine = getRoutePath(timetable, busRoute)
        val busStop = BusStop()

        buildBusStopTimeTable(timetable, busStop)
        setRoutePathProperties(busRouteLine, timetable, busStop)
        addRoutePath(busRoute, busRouteLine)

        log.info("Saving to database [#{}]", busRoute.routename)
        routeRepository.save(busRoute)
    }

    private fun addRoutePath(busRoute: BusRoute, busRouteLine: BusRouteLine) {
        if (busRoute.hasNoRoutePath(busRouteLine)) {
            busRoute.busRouteLines.add(busRouteLine)
        }
    }

    private fun setRoutePathProperties(busRouteLine: BusRouteLine, timetable: Timetable, busStop: BusStop) {
        busRouteLine.addBusStopTimetable(busStop)
        busRouteLine.startBusStop = timetable.startBusStopName
        busRouteLine.endBusStop = timetable.endBusStopName
    }

    private fun buildBusStopTimeTable(timetable: Timetable, busStop: BusStop) {
        log.debug("Setting bust stop properties")
        busStop.busStopName = timetable.activeStopName
        val workdaySchedule = Schedule()
        val saturdaySchedule = Schedule()
        val sundaySchedule = Schedule()

        setScheduleValues(timetable, workdaySchedule, "weekday")
        setScheduleValues(timetable, saturdaySchedule, "saturday")
        setScheduleValues(timetable, sundaySchedule, "sunday")

        busStop.workDaySchedule = workdaySchedule
        busStop.saturdaySchedule = saturdaySchedule
        busStop.sundaySchedule = sundaySchedule
    }

    private fun setScheduleValues(timetable: Timetable, workdaySchedule: Schedule, day: String) {
        log.debug("Building schedule for day: [{}]", day)
        val busArrivalsByHour = ArrayList<BusArrival>()
        for ((key, value) in timetable.timetable) {
            val arrivals = value[day]
            val arrivalsSplitted = arrivals!!.split(",".toRegex()).toList()
            val busArrivals = ArrayList<BusArrival>()
            for (arrival in arrivalsSplitted) {
                val busArrival = BusArrival()
                busArrival.arrivalHour = key
                busArrival.arrivalMinute = if (arrival.isEmpty()) null else Integer.valueOf(arrival)
                busArrivals.add(busArrival)
            }
            busArrivalsByHour.addAll(busArrivals)
        }
        workdaySchedule.busArrivals = busArrivalsByHour

    }

    private fun getRoutePath(timetable: Timetable, busRoute: BusRoute): BusRouteLine {
        log.debug("Fetching routepath from busRoute [#{}]", busRoute.routename)
        return busRoute.getRoutePathByStartStopName(timetable.startBusStopName)
    }

    private fun getRoute(timetable: Timetable): BusRoute {
        log.debug("Fetching busRoute from database")
        var busRoute: BusRoute? = routeRepository.findByRoutename(timetable.routeName)
        if (busRoute == null) {
            log.debug("BusRoute not found! Creating a new one!")
            busRoute = BusRoute()
            busRoute.busRouteLines = ArrayList()
        }
        busRoute.routename = timetable.routeName
        return busRoute
    }

    private fun checkConstraints(list: List<Timetable>) {
        if (list.size != 1) {
            throw IllegalArgumentException("The size of the list to write should be one! Writing items one by one is accepted")
        }
    }
}