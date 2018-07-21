package org.zenbot.timetableupdater.batch

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream
import org.springframework.core.io.Resource
import java.net.URL

class TimetableItemReader: ResourceAwareItemReaderItemStream<Document> {

    val log = LoggerFactory.getLogger(this::class.java)

    private var resource: Resource? = null
    private var count = -1
    private var readNext = false

    constructor()

    override fun update(executionContext: ExecutionContext?) {
        val resouceIndex = executionContext!!.get("MultiResourceItemReader.resourceIndex") as Int
        if (resouceIndex <= count) {
            readNext = true
        }
    }

    override fun open(executionContext: ExecutionContext?) {
        readNext = false
    }

    override fun close() {
        readNext = true
    }

    override fun setResource(resource: Resource?) {
        this.resource = resource
    }

    override fun read(): Document? {
        count++
        if (readNext) {
            return null
        }
        log.info("Reading html file [{}]", resource!!.getURL().toString())
        return Jsoup.connect(resource!!.url.toString()).get()
    }
}