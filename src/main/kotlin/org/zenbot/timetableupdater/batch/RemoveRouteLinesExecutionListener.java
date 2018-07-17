package org.zenbot.timetableupdater.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.zenbot.timetableupdater.dao.RouteRepository;
import org.zenbot.timetableupdater.domain.BusRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RemoveRouteLinesExecutionListener implements JobExecutionListener {

    private final RouteRepository routeRepository;
    private final Environment environment;

    public RemoveRouteLinesExecutionListener(RouteRepository routeRepository, Environment environment) {
        this.routeRepository = routeRepository;
        this.environment = environment;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        List<String> activeProfilesList = Arrays.asList(environment.getActiveProfiles());
        List<BusRoute> routes = routeRepository.findAll();
        if (!activeProfilesList.isEmpty()) {
            log.info("Removing routelines [{}]", String.join(",", activeProfilesList));
            routes.forEach(route -> {
                if (activeProfilesList.contains(route.getRoutename())) {
                    route.setBusRouteLines(new ArrayList<>());
                }
            });
        } else {
            log.info("Removing all routelines from database");
            routes.forEach(route -> route.setBusRouteLines(new ArrayList<>()));
        }
        routeRepository.saveAll(routes);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
    }
}
