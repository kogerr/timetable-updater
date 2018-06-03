package org.zenbot.timetableupdater;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication
@EnableEncryptableProperties
public class TimetableUpdaterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimetableUpdaterApplication.class, args);
	}
}
