package org.zenbot.timetableupdater;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class TimetableUpdaterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimetableUpdaterApplication.class, args);
	}
}
