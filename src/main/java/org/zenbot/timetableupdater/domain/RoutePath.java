package org.zenbot.timetableupdater.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Slf4j
@Data
@Document
public class RoutePath {

    @Id
    private String id;
    private String startBusStop;
    private List<BusStopTimetable> busStopTimetables;

    public void addBusStopTimetable(BusStopTimetable busStopTimetable) {
        log.debug("Adding bus stop timetable");
        if (busStopTimetables.contains(busStopTimetable)) {
            log.debug("Timetable for bus stop [{}] found in route path!", busStopTimetable.getBusStopName());
            log.debug("Updating data");
            BusStopTimetable toUpdate = busStopTimetables.stream()
                    .filter(timetable -> timetable.getBusStopName().equals(busStopTimetable.getBusStopName()))
                    .findFirst()
                    .get();
            toUpdate.setSundaySchedule(busStopTimetable.getSundaySchedule());
            toUpdate.setSaturdaySchedule(busStopTimetable.getSaturdaySchedule());
            toUpdate.setWorkDaySchedule(busStopTimetable.getWorkDaySchedule());
            toUpdate.setBusStopName(busStopTimetable.getBusStopName());
        } else {
            log.debug("Timetable for bus stop [{}] NOT found in route path!", busStopTimetable.getBusStopName());
            log.debug("Adding a new one");
            busStopTimetables.add(busStopTimetable);
        }
    }
}
