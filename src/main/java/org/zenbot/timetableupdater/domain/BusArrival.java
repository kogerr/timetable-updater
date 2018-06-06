package org.zenbot.timetableupdater.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class BusArrival {

    @Id
    private String id;
    private boolean lowfloor;
    private int arrivalMinute;
}
