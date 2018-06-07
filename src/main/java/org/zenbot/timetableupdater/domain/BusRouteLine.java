package org.zenbot.timetableupdater.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Slf4j
@Data
@Document
public class BusRouteLine {

    @Id
    private String id;
    private String startBusStop;
    private List<BusStop> busStops;

    public void addBusStopTimetable(BusStop busStop) {
        log.debug("Adding bus stop timetable");
        if (busStops.contains(busStop)) {
            log.debug("Timetable for bus stop [{}] found in route path!", busStop.getBusStopName());
            log.debug("Updating data");
            BusStop toUpdate = busStops.stream()
                    .filter(timetable -> timetable.getBusStopName().equals(busStop.getBusStopName()))
                    .findFirst()
                    .get();
            toUpdate.setSundaySchedule(busStop.getSundaySchedule());
            toUpdate.setSaturdaySchedule(busStop.getSaturdaySchedule());
            toUpdate.setWorkDaySchedule(busStop.getWorkDaySchedule());
            toUpdate.setBusStopName(busStop.getBusStopName());
        } else {
            log.debug("Timetable for bus stop [{}] NOT found in route path!", busStop.getBusStopName());
            log.debug("Adding a new one");
            busStops.add(busStop);
        }
    }
}
