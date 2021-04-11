package Group1.com.DataConsolidation.KnitController;

import Group1.com.DataConsolidation.DataProcessing.Progress;
import Group1.com.DataConsolidation.DataProcessing.CPH;
import Group1.com.DataConsolidation.DataProcessing.DataConsolidator;
import Group1.com.DataConsolidation.DataProcessing.WorkbookParseException;
import Group1.com.DataConsolidation.UploadHandlerController.UploadController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class KnitListener implements ApplicationListener<KnitEvent> {

    private static final Logger logger = Logger.getLogger(KnitListener.class.getName());
    private static String UPLOADED_FOLDER = "src/main/resources/UploadedFiles/";

    @Autowired
    private Progress progress;

    @Override
    public void onApplicationEvent(KnitEvent event) {
        logger.info("event listened");

        ParseThread parsethread = new ParseThread("parsethread", progress);
        parsethread.start();
    }
}
