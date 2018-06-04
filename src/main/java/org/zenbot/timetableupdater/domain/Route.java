package org.zenbot.timetableupdater.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Route {

    @Id
    private String id;
    private String routename;
    private List<RoutePath> routePaths;

    public RoutePath getRoutePathByStartStopName(String startBusStopName) {
        RoutePath result = new RoutePath();
        result.setBusStopTimetables(new ArrayList<>());
        for (RoutePath routePath : routePaths) {
            if (routePath.getStartBusStop().equals(startBusStopName)) {
                result = routePath;
            }
        }
        return result;
    }

    public boolean hasNoRoutePath(RoutePath routePath) {
        boolean result = true;
        for (RoutePath routePathItem : routePaths) {
            if (routePathItem.getStartBusStop().equals(routePath.getStartBusStop())) {
                result = false;
            }
        }
        return result;
    }
}
