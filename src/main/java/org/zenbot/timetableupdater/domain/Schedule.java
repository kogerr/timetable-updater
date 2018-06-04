package org.zenbot.timetableupdater.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document
public class Schedule {

    @Id
    private String id;
    private Map<Integer, List<BusArrival>> busArrivalsByHour;
}
