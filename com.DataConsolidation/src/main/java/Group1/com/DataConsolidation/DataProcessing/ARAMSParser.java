package Group1.com.DataConsolidation.DataProcessing;

import javassist.Loader;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ARAMSParser extends Parser {
    public ARAMSParser(Sheet sheet, Progress progress, CPH outbreakSource) {
        super(sheet, progress, outbreakSource, "arams");
    }

    public Pair<ArrayList<MoveRecord>, ArrayList<MoveRecord>> parse() throws WorkbookParseException {
        Iterator<Row> rowIter = this.sheet.rowIterator();

        String[] headingNames = {
                "Movement ID",
                "From Premises",
                "From Activity",
                "To Premises",
                "To Activity",
                "Departure Date",
                "Arrival Date",
                "Recorded Date",
                "Status",
                "Move Method",
                "Move Direction",
                "Species",
                "Animal No",
                "Herd Mark",
                "Animal Count",
                "Animal Description",
                "Dept Country",
                "Dest Country"
        };

        parseHeadings(rowIter, headingNames);

        ArrayList<MoveRecord> outFrom = new ArrayList<>();
        ArrayList<MoveRecord> outTo = new ArrayList<>();

        MoveRecord currentMove = null;
        int bulkCount = 0;

        while (rowIter.hasNext()) {
            Row row = rowIter.next();
            progress.incrementRowsProcessed();

            // ARAMS lists each individual animal within a move in a separate row. We consolidate
            // bulk movements into one row, under the assumption that the rows of a bulk movement
            // are listed consecutively (with the same movement ID).

            if (currentMove != null && currentMove.id.equals(getCellData(row, "Movement ID"))) {
                // This row is part of the current movement, so accumulate.
                bulkCount += 1; // TODO: Is this always going to be 1?
            } else {
                // This row is part of a different movement to the previous row. Commit the last move
                // and create a new one.

                if (currentMove != null && !currentMove.isEmpty()) {
                    currentMove.animalCount = Integer.toString(bulkCount);
                    if (currentMove.isFromInfected(this.outbreakSource)) {
                        outFrom.add(currentMove);
                    } else {
                        outTo.add(currentMove);
                    }
                }

                currentMove = new MoveRecord("arams", row.getRowNum());
                currentMove.id = getCellData(row, "Movement ID");
                currentMove.locationFrom = new CPH(getCellData(row, "From Premises"));
                currentMove.locationTo = new CPH(getCellData(row, "To Premises"));
                currentMove.activityFrom = getCellData(row, "From Activity");
                currentMove.activityTo = getCellData(row, "To Activity");
//                currentMove.recordedDate = getCellData(row, "Recorded Date");
//                currentMove.status = getCellData(row, "Status");
//                currentMove.moveMethod = getCellData(row, "Move Method");
//                currentMove.moveDirection = getCellData(row, "Move Direction");
//                currentMove.species = getCellData(row, "Species");
//                currentMove.animalDescription = getCellData(row, "Animal Description");
                currentMove.departCountry = getCellData(row, "Dept Country");
                currentMove.arriveCountry = getCellData(row, "Dest Country");
                currentMove.departDate = parseDate(getCellData(row, "Departure Date"));
                currentMove.arriveDate = parseDate(getCellData(row, "Arrival Date"));
                bulkCount = 1;
            }
        }

        return Pair.of(outFrom, outTo);
    }
}
