package org.zenbot.timetableupdater.dao

import org.springframework.data.repository.CrudRepository
import org.zenbot.timetableupdater.domain.BusRoute

interface RouteRepository : CrudRepository<BusRoute, String> {
    fun findByRoutename(routename: String): BusRoute
}