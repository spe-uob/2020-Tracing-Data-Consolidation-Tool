package Group1.com.DataConsolidation.KnitController;


import Group1.com.DataConsolidation.DataProcessing.DataConsolidator;
import Group1.com.DataConsolidation.DataProcessing.WorkbookParseException;
import Group1.com.DataConsolidation.UploadHandlerController.UploadController;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.logging.Logger;

@RestController
@CrossOrigin(value = "http://localhost:3000")
public class Knitcontroller {
    private static final Logger logger = Logger.getLogger(Knitcontroller.class.getName());
    private final ApplicationEventPublisher applicationEventPublisher;

    private static String UPLOADED_FOLDER = "src/main/resources/UploadedFiles/";

    @Autowired
    public Knitcontroller(ApplicationEventPublisher applicationEventPublisher){
        this.applicationEventPublisher = applicationEventPublisher;
    }
    @PostMapping("/knit")
    public String knit(@RequestParam("action") String action){
        if(action.equals("")){
            throw new RuntimeException("No files for knitting");
        }

        logger.info("Knit received here"+ action);
        KnitEvent knitevent = new KnitEvent(this);
        applicationEventPublisher.publishEvent(knitevent);

        // Read excel file from X, parse, write excel file to Y


        return "knit received";
    }

}
