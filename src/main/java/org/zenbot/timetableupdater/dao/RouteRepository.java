package org.zenbot.timetableupdater.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.zenbot.timetableupdater.domain.Route;

public interface RouteRepository extends MongoRepository<Route, String> {

    Route findByRoutename(String routename);
}
