package org.zenbot.timetableupdater.batch

import org.slf4j.LoggerFactory
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.core.env.Environment
import org.zenbot.timetableupdater.dao.RouteRepository

open class RemoveRouteLinesExecutionListener(val routeRepository: RouteRepository, val environment: Environment): JobExecutionListener {

    val log = LoggerFactory.getLogger(this::class.java)

    override fun beforeJob(jobExecution: JobExecution?) {
        val routes = routeRepository.findAll()
        val activeProfiles = environment.activeProfiles
        if (!activeProfiles.isEmpty()) {
            log.info("Removing routelines [{}]", activeProfiles.joinToString(", "))
            routes.forEach {
                busRoute -> if (activeProfiles.contains(busRoute.routename)) busRoute.busRouteLines = ArrayList()
            }
        } else {
            routes.forEach{busRoute -> busRoute.busRouteLines = ArrayList()}

        }
        routeRepository.saveAll(routes)
    }

    override fun afterJob(jobExecution: JobExecution?){}
}