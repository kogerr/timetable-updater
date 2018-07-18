package org.zenbot.timetableupdater.configuration

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@EnableMongoRepositories("org.zenbot.timetableupdater.dao")
open class MongoConfiguration