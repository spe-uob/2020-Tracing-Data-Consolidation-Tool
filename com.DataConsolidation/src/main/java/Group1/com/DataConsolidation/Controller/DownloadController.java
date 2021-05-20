package Group1.com.DataConsolidation.Controller;


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
        byte[] error = new byte[0];

        try(InputStream inputStream = new FileInputStream("src/main/resources/ProcessedFiles/processed" + jobId + ".xlsx")){
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            File ProcessedFiletoDelete = new File("src/main/resources/ProcessedFiles/processed" + jobId + ".xlsx");
            File UploadedFiletoDelete  = new File("src/main/resources/UploadedFiles/targetFile" + jobId + ".xlsx");
            if(ProcessedFiletoDelete.exists()) ProcessedFiletoDelete.delete();
            if(UploadedFiletoDelete.exists()) UploadedFiletoDelete.delete();
            return buffer;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return error;
    }


}