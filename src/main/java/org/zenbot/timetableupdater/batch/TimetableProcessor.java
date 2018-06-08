package org.zenbot.timetableupdater.batch;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TimetableProcessor implements ItemProcessor<String, Timetable> {

    private static final String ROUTE_NAME_SELECTOR = "#art-main > div > div > div > div > div > div > div.art-postcontent > table > tbody > tr > td > center > table:nth-child(2) > tbody > tr > td > font > b";
    private static final String FROM_SELECTOR = "#art-main > div > div > div > div > div > div > div.art-postcontent > table > tbody > tr > td > center > table.stations > tbody > tr:nth-child(2) > td:nth-child(3) > a";
    private static final String ACTUALSTOP_SELECTOR = "#art-main > div > div > div > div > div > div > div.art-postcontent > table > tbody > tr > td > center > table.schedule > tbody > tr:nth-child(1) > th > font";
    private static final String TIME_TABLE_SELECTOR = "#art-main > div > div > div > div > div > div > div.art-postcontent > table > tbody > tr > td > center > table.schedule";
    private static final String TABLE_ROW_SELECTOR = "tr";
    private static final String TABLE_COLUMN_SELECTOR = "td";

    public static final String WEEKDAY_KEY = "weekday";
    public static final String SATURDAY_KEY = "saturday";
    public static final String SUNDAY_KEY = "sunday";
    public static final String STATIONS_SELECTOR = "#art-main > div > div.art-layout-wrapper > div > div > div > div > div.art-postcontent > table > tbody > tr > td > center > table.stations";

    @Override
    public Timetable process(String html) throws Exception {
        Document htmlDocument = Jsoup.parse(html);
        Timetable timetable = new Timetable();
        setRoutename(htmlDocument, timetable);
        setStartBusStopName(htmlDocument, timetable);
        setEndBusStopName(htmlDocument, timetable);
        setActiveBusStopName(htmlDocument, timetable);
        setTimetable(htmlDocument, timetable);

        log.info("Process html with routename [#{}] and timetable for [{}] stop", timetable.getRouteName(), timetable.getActiveStopName());
        return timetable;
    }

    private void setTimetable(Document htmlDocument, Timetable timetable) {
        for (Element table : htmlDocument.select(TIME_TABLE_SELECTOR)) {
            for (Element row : table.select(TABLE_ROW_SELECTOR)) {
                Elements tds = row.select(TABLE_COLUMN_SELECTOR);
                if (tds.size() == 4) {
                    String hour = tds.get(0).text();
                    String weekdayArrivals = tds.get(1).text().replaceAll(" ", "");
                    String saturdayArrivals = tds.get(2).text().replaceAll(" ", "");
                    String sundayArrivals = tds.get(3).text().replaceAll(" ", "");
                    timetable.addRow(Integer.valueOf(hour), ImmutableMap.<String, String>builder()
                            .put(WEEKDAY_KEY, weekdayArrivals)
                            .put(SATURDAY_KEY, saturdayArrivals)
                            .put(SUNDAY_KEY, sundayArrivals)
                            .build());
                }
            }
        }
    }

    private void setActiveBusStopName(Document htmlDocument, Timetable timetable) {
        String actualStop = getHtmlText(htmlDocument, ACTUALSTOP_SELECTOR);
        actualStop = (String) actualStop.subSequence(actualStop.indexOf("(") + 1, actualStop.indexOf(")"));
        if (actualStop.indexOf("(") > -1) {
            actualStop += ")";
        }
        timetable.setActiveStopName(actualStop);
    }

    private void setEndBusStopName(Document htmlDocument, Timetable timetable) {
        Elements stationsTable = htmlDocument.select(STATIONS_SELECTOR);
        stationsTable.select(TABLE_ROW_SELECTOR).size();
        int indexOfEndBusStop = stationsTable.select(TABLE_ROW_SELECTOR).size() - 2;
        Elements rows = stationsTable.select(TABLE_ROW_SELECTOR);
        String to = rows.get(indexOfEndBusStop).select(TABLE_COLUMN_SELECTOR).get(2).text();
        timetable.setEndBusStopName(to);
    }

    private void setStartBusStopName(Document htmlDocument, Timetable timetable) {
        String from = getHtmlText(htmlDocument, FROM_SELECTOR);
        timetable.setStartBusStopName(from);
    }

    private void setRoutename(Document htmlDocument, Timetable timetable) {
        String routename = getHtmlText(htmlDocument, ROUTE_NAME_SELECTOR);
        timetable.setRouteName(routename);
    }

    private String getHtmlText(Document htmlDocument, String s) {
        return htmlDocument.select(s).text();
    }
}
