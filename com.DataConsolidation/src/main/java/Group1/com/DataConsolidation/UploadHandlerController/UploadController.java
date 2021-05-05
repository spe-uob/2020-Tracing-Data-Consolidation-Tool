package Group1.com.DataConsolidation.UploadHandlerController;


import Group1.com.DataConsolidation.DataProcessing.Progress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@RestController
@CrossOrigin(value = "http://localhost:3000")
public class UploadController {
    private static String UPLOADED_FOLDER = "src/main/resources/UploadedFiles/";
    private static final Logger logger = Logger.getLogger(UploadController.class.getName());
    private static String MessageToShow = "";
    @Autowired
    public Progress Progress;
    @PostMapping("/upload") // Handle Post Request sent by the React Client (save Uploaded files into resources)
    public ResponseEntity<String> uploadData(@RequestParam("file") MultipartFile file) throws Exception {
        if (file == null) {
            throw new RuntimeException("You must select the a file for uploading");
        }
        Progress.reset();

        InputStream inputStream = file.getInputStream();
        byte[] buffer = new byte[inputStream.available()];
        //Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
        inputStream.read(buffer);
        File targetFile = new File(UPLOADED_FOLDER + "targetFile.xlsx");
        try (OutputStream outStream = new FileOutputStream(targetFile)) {
            outStream.write(buffer);
        }

        ParseThread parsethread = new ParseThread("parsethread", Progress);
        parsethread.start();


        //Logging information into the console. (just for Debugging)
        String originalName = file.getOriginalFilename();
        String name = file.getName();
        String contentType = file.getContentType();
        long size = file.getSize();
        logger.info("inputStream: " + inputStream);
        logger.info("originalName: " + originalName);
        logger.info("name: " + name);
        logger.info("contentType: " + contentType);
        logger.info("size: " + size);
        MessageToShow = originalName + " size:" + size + " Content Type: "+ contentType;
        // Do processing with uploaded file data in Service layer
        return new ResponseEntity<String>(originalName, HttpStatus.OK);
    }
    @GetMapping("/upload")
    @ResponseBody
    public String showdata(){
        return MessageToShow;
    }


}
