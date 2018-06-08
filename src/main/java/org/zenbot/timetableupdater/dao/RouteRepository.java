package org.zenbot.timetableupdater.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.zenbot.timetableupdater.domain.BusRoute;

public interface RouteRepository extends MongoRepository<BusRoute, String> {

    BusRoute findByRoutename(String routename);
}
