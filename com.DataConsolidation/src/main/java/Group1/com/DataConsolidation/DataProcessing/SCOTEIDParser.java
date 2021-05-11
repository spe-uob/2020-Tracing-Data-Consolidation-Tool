package Group1.com.DataConsolidation.DataProcessing;

import org.apache.poi.ss.usermodel.*;
import org.springframework.data.util.Pair;

import java.util.*;

public class SCOTEIDParser extends Parser {
    private boolean fromData;

    public SCOTEIDParser(Sheet sheet, Progress progress, Location outbreakSource, boolean fromData) {
        super(sheet, progress, outbreakSource, "scoteid");
        this.fromData = fromData;
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

            MoveRecord move = new MoveRecord("scoteid", row.getRowNum());
            move.id = getCellData(row, "Unique_Ref");
            move.animalCount = getCellData(row, "Sheep");
            move.locationFrom = new Location(getCellData(row, "Depart. CPH"));
            move.locationTo = new Location(getCellData(row, "Dest. CPH"));
            move.activityTo = fromData ? getCellData(row, "Move") : null;
            move.activityFrom = fromData ? null: getCellData(row, "Move");
            move.departCountry = move.locationFrom.getCountry();
            move.arriveCountry = move.locationTo.getCountry();
            var lotDate = getCellData(row, "Lot Date");
            move.arriveDate = fromData ? null : parseDate(lotDate);
            move.departDate = fromData ? parseDate(lotDate) : null;

            // TODO: Why?
            if (!move.isEmpty()) {
                out.add(move);
            }
        }

        // We know whether it was from or to the source based on which sheet is being parsed
        return Pair.of(out, new ArrayList<>());
    }
}
