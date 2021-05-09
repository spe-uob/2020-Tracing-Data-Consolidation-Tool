package Group1.com.DataConsolidation.UploadHandlerController;

import Group1.com.DataConsolidation.DataProcessing.Location;
import Group1.com.DataConsolidation.DataProcessing.DataConsolidator;
import Group1.com.DataConsolidation.DataProcessing.Progress;
import Group1.com.DataConsolidation.DataProcessing.WorkbookParseException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.logging.Logger;

class ParseThread implements Runnable {
        private static String UPLOADED_FOLDER = "src/main/resources/UploadedFiles/";
        private static final Logger logger = Logger.getLogger(ParseThread.class.getName());
        Thread thread;
        private String threadname;
        private Progress progress;
        private CurrentJob currentJob;

        public ParseThread(String threadname, Progress progress, CurrentJob currentJob) {
            this.threadname = threadname;
            this.progress = progress;
            this.currentJob = currentJob;
        }

        @Override
        public void run() {
            try (InputStream inStream = new FileInputStream(UPLOADED_FOLDER + "targetFile" + currentJob.getJobId() + ".xlsx")) {
                var outFile = new File("src/main/resources/ProcessedFiles/processed" + currentJob.getJobId() + ".xlsx");
                assert(!outFile.exists());

                Workbook wbIn = WorkbookFactory.create(inStream);
                Location tempOutbreakSource = new Location("08/548/4000"); // TODO: Hook this value up to the frontend
                XSSFWorkbook wbOut = new DataConsolidator(wbIn, progress).parse(tempOutbreakSource);

                outFile.createNewFile();
                OutputStream outStream = new FileOutputStream(outFile);
                wbOut.write(outStream);

                logger.info("Processing done");
                outStream.close();
            } catch (IOException | WorkbookParseException e) {
                e.printStackTrace();
            }
        }
        public void start() {
            if (thread  == null) {
                thread = new Thread(this, threadname);
                thread.start();
            }
            logger.info("Thread started");
        }

}
