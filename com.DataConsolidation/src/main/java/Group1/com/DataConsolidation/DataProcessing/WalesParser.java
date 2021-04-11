package Group1.com.DataConsolidation.DataProcessing;

import org.apache.poi.ss.usermodel.*;
import org.springframework.data.util.Pair;

import java.util.*;

public class WalesParser extends Parser{

     public WalesParser(Sheet sheet, Progress progress, CPH outbreakSource) {
         super(sheet, progress, outbreakSource, "wales");
     }

     public Pair<ArrayList<MoveRecord>, ArrayList<MoveRecord>> parse() throws WorkbookParseException {
         Iterator<Row> rowIter = this.sheet.rowIterator();

         String[] headingNames = {
                 "Ref",
                 "Count",
                 "Species",
                 "Lot",
                 "Date",
                 "From CPH",
                 "To CPH",
                 "Created By",
         };

         parseHeadings(rowIter, headingNames);

         ArrayList<MoveRecord> outFrom = new ArrayList();
         ArrayList<MoveRecord> outTo = new ArrayList();

         while (rowIter.hasNext()) {
             Row row = rowIter.next();
             progress.incrementRowsProcessed();

             MoveRecord move = new MoveRecord();
             move.id = getCellData(row, "Ref");
             move.animalCount = getCellData(row, "Count");
             move.species = getCellData(row, "Species");
             move.lotID = getCellData(row, "Lot");
             move.locationFrom = new CPH(getCellData(row, "From CPH"));
             move.locationTo = new CPH(getCellData(row, "To CPH"));
             move.createdBy = getCellData(row, "Created By");
             move.departCountry = move.locationFrom.getCountry();
             move.arriveCountry = move.locationTo.getCountry();
             move.departDate = parseDate(getCellData(row, "Date"));
             move.arriveDate = move.departDate;

             if (!move.isEmpty()) {
                 move.originatingSheet = this.parserName;
                 if (move.isFromInfected(this.outbreakSource)) {
                     outFrom.add(move);
                 } else {
                     outTo.add(move);
                 }
             }
         }

         return Pair.of(outFrom, outTo);
     }
}
