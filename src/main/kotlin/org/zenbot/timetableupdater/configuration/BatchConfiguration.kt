package org.zenbot.timetableupdater.configuration

import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.MultiResourceItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zenbot.timetableupdater.batch.Timetable
import org.zenbot.timetableupdater.batch.TimetableReader

@Configuration
@EnableBatchProcessing
class BatchConfiguration(
        val jobBuilderFactory: JobBuilderFactory,
        val stepBuilderFactory: StepBuilderFactory,
        val itemProcessor: ItemProcessor<String, Timetable>,
        val itemWriter: ItemWriter<Timetable>,
        val jobExecutionListener: JobExecutionListener,
        val resourceReader: ResourceReader
) {

    @Bean fun importTimetableJob() = jobBuilderFactory
            .get("importTimetableJob")
            .listener(jobExecutionListener)
            .flow(importTimetableStep())
            .end()
            .build()

    @Bean fun importTimetableStep() = stepBuilderFactory
            .get("importTimetableStep")
            .chunk<String, Timetable>(1)
            .reader(timetableReader())
            .processor(itemProcessor)
            .writer(itemWriter)
            .build()

    @Bean fun timetableReader() : MultiResourceItemReader<String> {
        val resources = resourceReader.readResources()
        val multiResourceItemReader = MultiResourceItemReader<String>()
        multiResourceItemReader.setResources(resources.toTypedArray())
        multiResourceItemReader.setDelegate(timetableSimpleReader())
        return multiResourceItemReader
    }

    @Bean fun timetableSimpleReader() = TimetableReader()
}