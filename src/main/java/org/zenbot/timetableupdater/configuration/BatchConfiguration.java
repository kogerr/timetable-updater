package org.zenbot.timetableupdater.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.zenbot.timetableupdater.batch.Timetable;
import org.zenbot.timetableupdater.batch.TimetableReader;

import java.net.MalformedURLException;

@Slf4j
@Configuration
@EnableBatchProcessing
@EnableConfigurationProperties(TimetableResourceLocationProperties.class)
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ItemProcessor<String, Timetable> itemProcessor;
    private final ItemWriter<Timetable> itemWriter;
    private final TimetableResourceLocationProperties resourceLocationProperties;

    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ItemProcessor<String, Timetable> itemProcessor, ItemWriter<Timetable> itemWriter, TimetableResourceLocationProperties resourceLocationProperties) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.itemProcessor = itemProcessor;
        this.itemWriter = itemWriter;
        this.resourceLocationProperties = resourceLocationProperties;
    }

    @Bean
    public ItemReader<String> timetableReader() {
        Resource[] resources = resourceLocationProperties.getLocataions()
                .stream()
                .map(location -> {
                    try {
                        return new UrlResource(location);
                    } catch (MalformedURLException e) {
                        log.error("Invalid html resouce location [{}]", location);
                        return null;
                    }
                })
                .filter(resouce -> resouce != null)
                .toArray(Resource[]::new);
        MultiResourceItemReader<String> multiResourceItemReader = new MultiResourceItemReader<>();
        multiResourceItemReader.setResources(resources);
        multiResourceItemReader.setDelegate(new TimetableReader());
        return multiResourceItemReader;
    }

    @Bean
    public Job importTimetableJob() {
        return jobBuilderFactory
                .get("importTimetableJob")
                .flow(importTimetableStep())
                .end()
                .build();
    }

    @Bean
    public Step importTimetableStep() {
        return stepBuilderFactory
                .get("importTimetableStep")
                .<String, Timetable> chunk(1)
                .reader(timetableReader())
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }
}
