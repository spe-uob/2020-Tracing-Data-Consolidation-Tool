package Group1.com.DataConsolidation.DataProcessing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.atomic.AtomicInteger;

// TODO: Currently there is only one of these for the whole application.
// This will cause problems if two people are running a report at the exact same time.
// Ideally, we distinguish between clients based on a cookie or a query param, but for now
// we pretend that there is only ever one client. This is defined in the Application class.

public class Progress {
    AtomicInteger rowsProcessed;

    public Progress() {
        this.rowsProcessed = new AtomicInteger(0);
    }

    public void reset() {
        rowsProcessed.set(0);
    }

    void incrementRowsProcessed() {
        rowsProcessed.incrementAndGet();
    }

    public float getProgress() {
        return (float)rowsProcessed.get();
    }
}

