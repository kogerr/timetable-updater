package org.zenbot.timetableupdater.batch

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor

open class TimetableProcessor : ItemProcessor<Document, Timetable> {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val ROUTE_NAME_SELECTOR = "#art-main > div > div > div > div > div > div > div.art-postcontent > table > tbody > tr > td > center > table:nth-child(2) > tbody > tr > td > font > b"
    private val FROM_SELECTOR = "#art-main > div > div > div > div > div > div > div.art-postcontent > table > tbody > tr > td > center > table.stations > tbody > tr:nth-child(2) > td:nth-child(3) > a"
    private val ACTUALSTOP_SELECTOR = "#art-main > div > div > div > div > div > div > div.art-postcontent > table > tbody > tr > td > center > table.schedule > tbody > tr:nth-child(1) > th > font"
    private val TIME_TABLE_SELECTOR = "#art-main > div > div > div > div > div > div > div.art-postcontent > table > tbody > tr > td > center > table.schedule"
    val STATIONS_SELECTOR = "#art-main > div > div.art-layout-wrapper > div > div > div > div > div.art-postcontent > table > tbody > tr > td > center > table.stations"
    private val TABLE_ROW_SELECTOR = "tr"
    private val TABLE_COLUMN_SELECTOR = "td"

    private val WEEKDAY_KEY = "weekday"
    private val SATURDAY_KEY = "saturday"
    private val SUNDAY_KEY = "sunday"

    @Throws(Exception::class)
    override fun process(htmlDocument: Document): Timetable {
        val timetable = Timetable()
        setRoutename(htmlDocument, timetable)
        setStartBusStopName(htmlDocument, timetable)
        setEndBusStopName(htmlDocument, timetable)
        setActiveBusStopName(htmlDocument, timetable)
        setTimetable(htmlDocument, timetable)
        replaceInvalidChars(timetable)

        log.info("Process html with routename [#{}] and timetable for [{}] stop", timetable.routeName, timetable.activeStopName)
        return timetable
    }

    private fun replaceInvalidChars(timetable: Timetable) {
        timetable.startBusStopName = replaceInvalidChars(timetable.startBusStopName)
        timetable.endBusStopName = replaceInvalidChars(timetable.endBusStopName)
        timetable.activeStopName = replaceInvalidChars(timetable.activeStopName)
    }

    private fun replaceInvalidChars(string: String): String {
        var result = string
        result = result.replace("û", "ű")
        result = result.replace("õ", "ő")
        return result
    }

    private fun setTimetable(htmlDocument: Document, timetable: Timetable) {
        for (table in htmlDocument.select(TIME_TABLE_SELECTOR)) {
            for (row in table.select(TABLE_ROW_SELECTOR)) {
                val tds = row.select(TABLE_COLUMN_SELECTOR)
                if (tds.size == 4) {
                    val hour = tds.get(0).text()
                    val weekdayArrivals = tds.get(1).text().replace(" ".toRegex(), "")
                    val saturdayArrivals = tds.get(2).text().replace(" ".toRegex(), "")
                    val sundayArrivals = tds.get(3).text().replace(" ".toRegex(), "")
                    timetable.addRow(Integer.valueOf(hour), mapOf(
                            WEEKDAY_KEY to weekdayArrivals,
                            SATURDAY_KEY to saturdayArrivals,
                            SUNDAY_KEY to sundayArrivals))
                }
            }
        }
    }

    private fun setActiveBusStopName(htmlDocument: Document, timetable: Timetable) {
        var actualStop = getHtmlText(htmlDocument, ACTUALSTOP_SELECTOR)
        actualStop = actualStop.subSequence(actualStop.indexOf("(") + 1, actualStop.indexOf(")")) as String
        if (actualStop.contains("(")) {
            actualStop += ")"
        }
        if (actualStop.endsWith(".")) {
            actualStop = actualStop.substring(0, actualStop.length - 1)
        }
        timetable.activeStopName = actualStop
    }

    private fun setEndBusStopName(htmlDocument: Document, timetable: Timetable) {
        val stationsTable = htmlDocument.select(STATIONS_SELECTOR)
        stationsTable.select(TABLE_ROW_SELECTOR).size
        val indexOfEndBusStop = stationsTable.select(TABLE_ROW_SELECTOR).size - 2
        val rows = stationsTable.select(TABLE_ROW_SELECTOR)
        val to = rows[indexOfEndBusStop].select(TABLE_COLUMN_SELECTOR)[2].text()
        timetable.endBusStopName = to
    }

    private fun setStartBusStopName(htmlDocument: Document, timetable: Timetable) {
        val from = getHtmlText(htmlDocument, FROM_SELECTOR)
        timetable.startBusStopName = from
    }

    private fun setRoutename(htmlDocument: Document, timetable: Timetable) {
        val routename = getHtmlText(htmlDocument, ROUTE_NAME_SELECTOR)
        timetable.routeName = routename
    }

    private fun getHtmlText(htmlDocument: Document, s: String): String {
        return htmlDocument.select(s).text()
    }
}
