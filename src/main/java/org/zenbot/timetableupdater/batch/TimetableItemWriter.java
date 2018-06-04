package org.zenbot.timetableupdater.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.zenbot.timetableupdater.dao.RouteRepository;
import org.zenbot.timetableupdater.domain.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TimetableItemWriter implements ItemWriter<Timetable> {

    private final RouteRepository routeRepository;

    public TimetableItemWriter(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public void write(List<? extends Timetable> list) {
        checkConstraints(list);
        Timetable timetable = list.get(0);
        log.info("Writing route [#{} from={}, stop={}] to database", timetable.getRouteName(), timetable.getStartBusStopName(), timetable.getActiveStopName());
        Route route = getRoute(timetable);
        RoutePath routePath = getRoutePath(timetable, route);
        BusStopTimetable busStopTimetable = new BusStopTimetable();

        buildBusStopTimeTable(timetable, busStopTimetable);
        setRoutePathProperties(routePath, timetable, route, busStopTimetable);
        addRoutePath(route, routePath);

        log.info("Saving to database [#{}]", route.getRoutename());
        routeRepository.save(route);
    }

    private void addRoutePath(Route route, RoutePath routePath) {
        if (route.hasNoRoutePath(routePath)) {
            route.getRoutePaths().add(routePath);
        }
    }

    private void setRoutePathProperties(RoutePath routePath, Timetable timetable, Route route, BusStopTimetable busStopTimetable) {
        routePath.addBusStopTimetable(busStopTimetable);
        routePath.setStartBusStop(timetable.getStartBusStopName());
    }

    private void buildBusStopTimeTable(Timetable timetable, BusStopTimetable busStopTimetable) {
        log.debug("Setting bust stop properties");
        busStopTimetable.setBusStopName(timetable.getActiveStopName());
        Schedule workdaySchedule = new Schedule();
        Schedule saturdaySchedule = new Schedule();
        Schedule sundaySchedule = new Schedule();

        setScheduleValues(timetable, workdaySchedule, TimetableProcessor.WEEKDAY_KEY);
        setScheduleValues(timetable, saturdaySchedule, TimetableProcessor.SATURDAY_KEY);
        setScheduleValues(timetable, sundaySchedule, TimetableProcessor.SUNDAY_KEY);

        busStopTimetable.setWorkDaySchedule(workdaySchedule);
        busStopTimetable.setSaturdaySchedule(saturdaySchedule);
        busStopTimetable.setSundaySchedule(sundaySchedule);
    }

    private void setScheduleValues(Timetable timetable, Schedule workdaySchedule, String day) {
        log.debug("Building schedule for day: [{}]", day);
        Map<Integer, List<BusArrival>> busArrivalsByHour = new HashMap<>();
        for (Map.Entry<Integer, Map<String, String>> entries : timetable.getTimetable().entrySet()) {
            Integer key = entries.getKey();
            Map<String, String> value = entries.getValue();
            String arrivals = value.get(day);
            String[] arrivalsSplitted = arrivals.split(",");
            List<BusArrival> busArrivals = new ArrayList<>();
            for (String arrival : arrivalsSplitted) {
                BusArrival busArrival = new BusArrival();
                busArrival.setArrivalMinute(arrival.isEmpty() ? null : Integer.valueOf(arrival));
                busArrivals.add(busArrival);
            }
            busArrivalsByHour.put(key, busArrivals);
        }
        workdaySchedule.setBusArrivalsByHour(busArrivalsByHour);

    }

    private RoutePath getRoutePath(Timetable timetable, Route route) {
        log.debug("Fetching routepath from route [#{}]", route.getRoutename());
        return route.getRoutePathByStartStopName(timetable.getStartBusStopName());
    }

    private Route getRoute(Timetable timetable) {
        log.debug("Fetching route from database");
        Route route = routeRepository.findByRoutename(timetable.getRouteName());
        if (route == null) {
            log.debug("Route not found! Creating a new one!");
            route = new Route();
            route.setRoutePaths(new ArrayList<>());
        }
        route.setRoutename(timetable.getRouteName());
        return route;
    }

    private void checkConstraints(List<? extends Timetable> list) {
        if (list.size() != 1) {
            throw new IllegalArgumentException("The size of the list to write should be one! Writing items one by one is accepted");
        }
    }
}
