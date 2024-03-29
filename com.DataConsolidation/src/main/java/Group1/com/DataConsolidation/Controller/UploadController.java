package Group1.com.DataConsolidation.Controller;


import Group1.com.DataConsolidation.DataProcessing.DataConsolidator;
import Group1.com.DataConsolidation.DataProcessing.Location;
import Group1.com.DataConsolidation.DataProcessing.Progress;
import Group1.com.DataConsolidation.DataProcessing.WorkbookParseException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
    public Progress progress; // TODO why is this public? Could we not use this for sending progress udpates?
    @ResponseBody
    @PostMapping("/upload") // Handle Post Request sent by the React Client (save Uploaded files into resources)
    public CurrentJob uploadData(@RequestParam("file") MultipartFile file, @RequestParam("outbreakSource") String outbreakSource) throws Exception {

        progress.reset(); // make progress scope Request.
        CurrentJob currentJob = GenerateRandomJob();


        InputStream inStream = file.getInputStream();
        var outFile = new File("src/main/resources/ProcessedFiles/processed" + currentJob.getJobId() + ".xlsx");
        // TODO create the necessary folders if not already present?
        outFile.createNewFile(); // TODO creating a file immediately could cause problems when it comes to checking progress
        OutputStream outStream = new FileOutputStream(outFile);

        try {
            Workbook wbIn = WorkbookFactory.create(inStream);
            // TODO check outbreakSource is not an empty string?
            Location tempOutbreakSource = new Location(outbreakSource);
            XSSFWorkbook wbOut = new DataConsolidator(wbIn, progress).parse(tempOutbreakSource);
            wbOut.write(outStream);
            logger.info("Processing done");
            outStream.close();
            inStream.close();
        } catch (IOException | WorkbookParseException e) {
            outStream.close();
            inStream.close();
            DeleteCorruptedFiles(currentJob);
            // TODO do we really want to print IOException errors? Those would be bugs on our end, not issues for the user to fix
            currentJob.setError(e.getMessage());
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
