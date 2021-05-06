package Group1.com.DataConsolidation.DownloadController;


import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


//Taking shortcuts. Need more work an refactoring
@RestController
@CrossOrigin("http://localhost:3000")
public class Downloadcontroller {
    @GetMapping(value = "/Processed.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public byte[] getFile (@RequestParam("JobId") int JobId ) throws IOException {
        InputStream inputStream = new FileInputStream("src/main/resources/ProcessedFiles/processed" + JobId + ".xlsx");
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        File ProcessedFile =  new File("src/main/resources/ProcessedFiles/processed" + JobId + ".xlsx");
        ProcessedFile.delete();
        return buffer;
    }


}