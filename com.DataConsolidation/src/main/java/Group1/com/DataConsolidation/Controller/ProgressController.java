package Group1.com.DataConsolidation.Controller;

import Group1.com.DataConsolidation.DataProcessing.Progress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.Duration;
@RestController
public class ProgressController {
    final Progress parseProgress;

    @Autowired
    public ProgressController(Progress parseProgress) {
        this.parseProgress = parseProgress;
    }

    @CrossOrigin(value = "http://localhost:3000")
    @GetMapping(value = "/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<Boolean> getProgress(@RequestParam(name = "jobId") int jobId){
        File file = new File("src/main/resources/ProcessedFiles/processed" + jobId + ".xlsx");
        //System.out.println(file.exists());
        return Flux.interval(Duration.ofMillis(200)).map(it -> file.exists());
    }
}
