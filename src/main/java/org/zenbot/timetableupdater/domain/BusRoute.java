package org.zenbot.timetableupdater.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class BusRoute {

    @Id
    private String id;
    private String routename;
    private List<BusRouteLine> busRouteLines;

    public BusRouteLine getRoutePathByStartStopName(String startBusStopName) {
        BusRouteLine result = new BusRouteLine();
        result.setBusStops(new ArrayList<>());
        for (BusRouteLine busRouteLine : busRouteLines) {
            if (busRouteLine.getStartBusStop().equals(startBusStopName)) {
                result = busRouteLine;
            }
        }
        return result;
    }

    public boolean hasNoRoutePath(BusRouteLine busRouteLine) {
        boolean result = true;
        for (BusRouteLine busRouteLineItem : busRouteLines) {
            if (busRouteLineItem.getStartBusStop().equals(busRouteLine.getStartBusStop())) {
                result = false;
            }
        }
        return result;
    }
}
