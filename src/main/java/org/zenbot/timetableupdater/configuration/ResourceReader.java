package org.zenbot.timetableupdater.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ResourceReader {

    private final Environment environment;
    private final TimetableResourceLocationProperties properties;

    public ResourceReader(Environment environment, TimetableResourceLocationProperties properties) {
        this.environment = environment;
        this.properties = properties;
    }

    public List<Resource> readResources() throws IOException {
        File resourceDirectory = new File(this.getClass().getResource(File.separator + properties.getFolder()).getFile());
        if (!resourceDirectory.isDirectory()) {
            throw new IllegalStateException("Must be a directory");
        }

        log.info("Reading resources from [{}]", resourceDirectory.getAbsolutePath());

        String[] activeProfiles = environment.getActiveProfiles();
        log.info("Active profiles [{}]", String.join(",", activeProfiles));
        return read(resourceDirectory, activeProfiles);
    }

    private List<Resource> read(File resourceDirectory, String[] profiles) throws IOException {
        List<Resource> resources = new ArrayList<>();
        List<String> activeProfilesFilenames = filenamesFromActiveProfiles(profiles);
        List<File> fileResources = filterFilesByExtension(resourceDirectory);
        if (profiles.length > 0) {
            fileResources = fileResources.stream()
                    .filter(file -> activeProfilesFilenames.contains(file.getName()))
                    .collect(Collectors.toList());
        }
        for (File file : fileResources) {
            List<UrlResource> urlResources = Arrays.stream(FileUtils.readFileToString(file, "UTF-8").split(System.lineSeparator()))
                    .filter(line -> !line.isEmpty() && !line.contains(properties.getCommentSign()))
                    .map(line -> {
                        try {
                            return new UrlResource(line);
                        } catch (MalformedURLException e) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
            resources.addAll(urlResources);
        }

        return resources;
    }

    private List<File> filterFilesByExtension(File resourceDirectory) {
        return Arrays.asList(resourceDirectory.listFiles(file -> file.getName().endsWith(properties.getFileExtension())));
    }

    private List<String> filenamesFromActiveProfiles(String[] profiles) {
        return Arrays.stream(profiles)
                .map(profile -> profile + "." + properties.getFileExtension())
                .collect(Collectors.toList());
    }
}
