package org.zenbot.timetableupdater.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class Schedule {

    @Id
    private String id;
    private List<BusArrival> busArrivals;
}
