package Group1.com.DataConsolidation.DownloadController;


import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.spec.RSAOtherPrimeInfo;


//Taking shortcuts. Need more work an refactoring
@RestController
@CrossOrigin("http://localhost:3000")
public class DownloadController {
    @GetMapping(value = "/processed", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public byte[] getFile (@RequestParam("jobId") int jobId ) throws Exception {
        InputStream inputStream = new FileInputStream("src/main/resources/ProcessedFiles/processed" + jobId + ".xlsx");
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        inputStream.close();
        Path ProcessedFiletoDelete =  Paths.get("src/main/resources/ProcessedFiles/processed" + jobId + ".xlsx");
        Path UploadedFiletoDelete  = Paths.get("src/main/resources/UploadedFiles/targetFile" + jobId + ".xlsx");
        try {
            Files.delete(ProcessedFiletoDelete);
            Files.delete(UploadedFiletoDelete);
        }
        catch (Exception e){
            throw new Exception(e);
        }
        return buffer;
    }


}