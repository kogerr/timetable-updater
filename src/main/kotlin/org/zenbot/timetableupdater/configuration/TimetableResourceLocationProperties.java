package org.zenbot.timetableupdater.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("timetable.resources")
public class TimetableResourceLocationProperties {
    private String folder;
    private String fileExtension;
    private String commentSign;
}
