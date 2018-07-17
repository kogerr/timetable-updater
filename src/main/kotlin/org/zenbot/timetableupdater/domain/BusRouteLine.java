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
    private String endBusStop;
    private List<BusStop> busStops;

    public void addBusStopTimetable(BusStop busStop) {
        busStops.add(busStop);
    }
}
