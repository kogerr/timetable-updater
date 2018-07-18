package org.zenbot.timetableupdater.configuration

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.file.MultiResourceItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.zenbot.timetableupdater.batch.*
import org.zenbot.timetableupdater.dao.RouteRepository

@Configuration
@EnableBatchProcessing
open class BatchConfiguration(
        val jobBuilderFactory: JobBuilderFactory,
        val stepBuilderFactory: StepBuilderFactory,
        val routeRepository: RouteRepository,
        val resourceReader: ResourceReader,
        val environment: Environment
) {

    @Bean open fun importTimetableJob() = jobBuilderFactory
            .get("importTimetableJob")
            .listener(jobExecutionListener())
            .flow(importTimetableStep())
            .end()
            .build()

    @Bean open fun importTimetableStep() = stepBuilderFactory
            .get("importTimetableStep")
            .chunk<String, Timetable>(1)
            .reader(timetableReader())
            .processor(itemProcessor())
            .writer(itemWriter())
            .build()

    @Bean open fun timetableReader() : MultiResourceItemReader<String> {
        val resources = resourceReader.readResources()
        val multiResourceItemReader = MultiResourceItemReader<String>()
        multiResourceItemReader.setResources(resources.toTypedArray())
        multiResourceItemReader.setDelegate(timetableSimpleReader())
        return multiResourceItemReader
    }

    @Bean open fun timetableSimpleReader() = TimetableItemReader()

    @Bean open fun itemProcessor() = TimetableProcessor()

    @Bean open fun itemWriter() = TimetableItemWriter(routeRepository)

    @Bean open fun jobExecutionListener() = RemoveRouteLinesExecutionListener(routeRepository, environment)
}