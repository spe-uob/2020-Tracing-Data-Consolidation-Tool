package Group1.com.DataConsolidation.DataProcessing;

import org.apache.poi.ss.usermodel.*;
import org.springframework.data.util.Pair;

import java.lang.reflect.Array;
import java.util.*;

public class SCOTEIDParser extends Parser {
    public SCOTEIDParser(Sheet sheet, Progress progress, CPH outbreakSource) {
        super(sheet, progress, outbreakSource, "SCOT EID");
    }

    public Pair<ArrayList<MoveRecord>, ArrayList<MoveRecord>> parse() throws WorkbookParseException {
        Iterator<Row> rowIter = this.sheet.rowIterator();

        String[] headingNames = {
                "Unique_Ref",
                "Sheep",
                "Reads",
                "%",
                "Move",
                "Lot Date",
                "Lot",
                "Depart. CPH",
                "Read Location",
                "Dest. CPH"
        };

        parseHeadings(rowIter, headingNames);

        ArrayList<MoveRecord> out = new ArrayList<>();

        while (rowIter.hasNext()) {
            Row row = rowIter.next();
            progress.incrementRowsProcessed();

            MoveRecord move = new MoveRecord();
            move.id = getCellData(row, "Unique_Ref");
            move.animalCount = getCellData(row, "Sheep");
            move.reads = getCellData(row, "Reads");
            move.percentage = getCellData(row, "%");
            move.moveMove = getCellData(row, "Move");
            move.lotDate = getCellData(row, "Lot Date");
            move.lotID = getCellData(row, "Lot");
            move.locationFrom = new CPH(getCellData(row, "Depart. CPH"));
            move.readLocation = getCellData(row, "Read Location");
            move.locationTo = new CPH(getCellData(row, "Dest. CPH"));
            move.departCountry = move.locationFrom.getCountry();
            move.arriveCountry = move.locationTo.getCountry();
            move.arriveDate = parseDate(move.lotDate); // TODO: Is this correct?
            move.departDate = parseDate(move.lotDate);

            if (!move.isEmpty()) {
                move.originatingSheet = this.parserName;
                out.add(move);
            }
        }

        // We know whether it was from or to the source based on which sheet is being parsed
        return Pair.of(out, new ArrayList<>());
    }
}
