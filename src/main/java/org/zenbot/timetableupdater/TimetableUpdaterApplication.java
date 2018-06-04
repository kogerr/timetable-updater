package org.zenbot.timetableupdater;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.URL;

@Slf4j
@SpringBootApplication
@EnableEncryptableProperties
public class TimetableUpdaterApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(TimetableUpdaterApplication.class, args);
		Resource resource = new UrlResource(new URL("http://www.kmkk.hu/component/schedule/?route=10101&city=1"));
	}
}
