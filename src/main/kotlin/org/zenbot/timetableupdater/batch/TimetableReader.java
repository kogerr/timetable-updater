package org.zenbot.timetableupdater.batch;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TimetableReader implements ResourceAwareItemReaderItemStream<String> {
    private Resource resource;
    private int count = -1;
    private boolean readNext = false;

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String read() throws Exception {
        count++;
        if (readNext) {
            return null;
        }
        log.info("Reading html file [{}]", resource.getURL().toString());
        return Jsoup.connect(resource.getURL().toString()).get().html();
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        readNext = false;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        int resouceIndex = (Integer) executionContext.get("MultiResourceItemReader.resourceIndex");
        if (resouceIndex <= count) {
            readNext = true;
        }
    }

    @Override
    public void close() throws ItemStreamException {
        readNext = true;
    }
}
