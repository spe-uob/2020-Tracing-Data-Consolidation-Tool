package Group1.com.DataConsolidation.UploadHandlerController;


import Group1.com.DataConsolidation.DataProcessing.DataConsolidator;
import Group1.com.DataConsolidation.DataProcessing.Location;
import Group1.com.DataConsolidation.DataProcessing.Progress;
import Group1.com.DataConsolidation.DataProcessing.WorkbookParseException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.logging.Logger;

@RestController
@CrossOrigin(value = "http://localhost:3000")
public class UploadController {
    private static String UPLOADED_FOLDER = "src/main/resources/UploadedFiles/";
    private static final Logger logger = Logger.getLogger(UploadController.class.getName());
    @Autowired
    public Progress Progress;
    @ResponseBody
    @PostMapping("/upload") // Handle Post Request sent by the React Client (save Uploaded files into resources)
    public CurrentJob uploadData(@RequestParam("file") MultipartFile file) throws Exception {
        if (file == null) {
            throw new RuntimeException("You must select the a file for uploading");
        }
        Progress.reset(); // make progress scope Request.
        CurrentJob currentJob = GenerateRandomJob();


        InputStream inStream = file.getInputStream();
        var outFile = new File("src/main/resources/ProcessedFiles/processed" + currentJob.getJobId() + ".xlsx");
        outFile.createNewFile();
        OutputStream outStream = new FileOutputStream(outFile);



        try {
            Workbook wbIn = WorkbookFactory.create(inStream);
            Location tempOutbreakSource = new Location("08/548/4000"); // TODO: Hook this value up to the frontend
            XSSFWorkbook wbOut = new DataConsolidator(wbIn,Progress).parse(tempOutbreakSource);
            wbOut.write(outStream);
            logger.info("Processing done");
            outStream.close();
            inStream.close();
        } catch (IOException | WorkbookParseException e) {
            outStream.close();
            inStream.close();
            DeleteCorruptedFiles(currentJob);
            currentJob.setError(e.toString());
            e.printStackTrace();
            System.out.println("error has occured");
            System.out.println(e);
        }



        //Logging information into the console. (just for Debugging)
        String originalName = file.getOriginalFilename();
        String name = file.getName();
        String contentType = file.getContentType();
        long size = file.getSize();
        logger.info("inputStream: " + inStream);
        logger.info("originalName: " + originalName);
        logger.info("name: " + name);
        logger.info("contentType: " + contentType);
        logger.info("size: " + size);
        // Do processing with uploaded file data in Service layer
        return currentJob;
    }

    private void DeleteCorruptedFiles(CurrentJob currentjob) {
        File ProcessedFiletoDelete = new File("src/main/resources/ProcessedFiles/processed" + currentjob.getJobId() + ".xlsx");
        if(ProcessedFiletoDelete.exists()) System.out.println(ProcessedFiletoDelete.delete());
    }

    private CurrentJob GenerateRandomJob() throws NoSuchProviderException, NoSuchAlgorithmException {
        SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG", "SUN");
        int jobId = Math.abs(secureRandomGenerator.nextInt());
        return new CurrentJob(jobId,"");
    }



}
