package org.zenbot.timetableupdater.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import org.zenbot.timetableupdater.dao.RouteRepository;
import org.zenbot.timetableupdater.domain.BusRoute;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RemoveRouteLinesExecutionListener implements JobExecutionListener {

    private final RouteRepository routeRepository;

    public RemoveRouteLinesExecutionListener(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Removing all routelines from database");
        List<BusRoute> routes = routeRepository.findAll();
        routes.forEach(route -> route.setBusRouteLines(new ArrayList<>()));
        routeRepository.saveAll(routes);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
    }
}
