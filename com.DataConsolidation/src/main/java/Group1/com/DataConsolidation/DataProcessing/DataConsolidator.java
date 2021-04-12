package Group1.com.DataConsolidation.DataProcessing;

import org.apache.poi.sl.draw.binding.CTColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class DataConsolidator {

    private final Progress progress;
    private final Workbook inWb;
    private final XSSFWorkbook outWb;
    private final XSSFCellStyle headingStyle;
    private final XSSFCellStyle evenRowStyle;
    private final XSSFCellStyle oddRowStyle;
    private final XSSFCellStyle warningRowStyle;

    public DataConsolidator(Workbook inWb, Progress progress) {
        Assert.notNull(inWb, "workbook was null");
        this.inWb = inWb;
        this.progress = progress;

        this.outWb = new XSSFWorkbook();
        Font boldFont = outWb.createFont();
        boldFont.setBold(true);

        this.headingStyle = outWb.createCellStyle();
        this.headingStyle.setFont(boldFont);

        this.evenRowStyle = outWb.createCellStyle();
        this.oddRowStyle = outWb.createCellStyle();
        this.warningRowStyle = outWb.createCellStyle();

        var colorMap = new DefaultIndexedColorMap();
        XSSFColor oddFillColor = new XSSFColor(
                new byte[] {(byte)235, (byte)235, (byte)235},
                colorMap
        );
        this.oddRowStyle.setFillForegroundColor(oddFillColor);
        this.oddRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFColor warningFillColor = new XSSFColor(
                new byte[] {(byte)235, (byte)186, (byte)52},
                colorMap
        );
        this.warningRowStyle.setFillForegroundColor(warningFillColor);
        this.warningRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    public XSSFWorkbook parse(CPH outbreakSource) throws WorkbookParseException {
        if (inWb.getNumberOfSheets() == 0) {
            throw new WorkbookParseException("empty workbook (no sheets)");
        }

        ArrayList<MoveRecord> movesFrom = new ArrayList<>();
        ArrayList<MoveRecord> movesTo = new ArrayList<>();
        progress.reset();

        Sheet arams = inWb.getSheetAt(0); // TODO: Will this always correspond to ARAMS?
        ARAMSParser aramsParser = new ARAMSParser(arams, progress, outbreakSource);
        var aramsMoves = aramsParser.parse();
        movesFrom.addAll(aramsMoves.getFirst());
        movesTo.addAll(aramsMoves.getSecond());

        Sheet scotlandFrom = inWb.getSheetAt(1);
        SCOTEIDParser scoteidParserFrom = new SCOTEIDParser(scotlandFrom, progress, outbreakSource);
        movesFrom.addAll(scoteidParserFrom.parse().getFirst());

        Sheet scotlandTo = inWb.getSheetAt(2);
        SCOTEIDParser scoteidParserTo = new SCOTEIDParser(scotlandTo, progress, outbreakSource);
        movesTo.addAll(scoteidParserTo.parse().getFirst());

        Sheet wales = inWb.getSheetAt(3);
        WalesParser walesParser = new WalesParser(wales, progress, outbreakSource);
        var walesMoves = walesParser.parse();
        movesFrom.addAll(walesMoves.getFirst());
        movesTo.addAll(walesMoves.getSecond());

        // First sort by location, then by date
        movesFrom.sort(
                Comparator.comparing((MoveRecord m) -> m.locationTo.number, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing((MoveRecord m) -> m.arriveDate, Comparator.nullsLast(Comparator.naturalOrder()))
        );
        movesTo.sort(
                Comparator.comparing((MoveRecord m) -> m.locationFrom.number, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing((MoveRecord m) -> m.departDate, Comparator.nullsLast(Comparator.naturalOrder()))
        );

        deduplicate(movesFrom);
        deduplicate(movesTo);

        Sheet sheetFrom = outWb.createSheet("Moves Off");
        Sheet sheetTo = outWb.createSheet("Moves On");
        createResultSheet(sheetFrom, movesFrom, false);
        createResultSheet(sheetTo, movesTo, true);
        return outWb;
    }

    private ArrayList<ColumnInfo> getOutputColumns(boolean movesOn) throws WorkbookParseException {
        var columnOrder = new ArrayList<ColumnInfo>();

        var from = new ColumnInfo("locationFrom", "Location From", 1500);
        var to = new ColumnInfo("locationTo", "Location To", 1500);
        if (movesOn) {
            columnOrder.add(from);
        } else {
            columnOrder.add(to);
        }

        columnOrder.add(new ColumnInfo("departDate", "Depart Date", 1500));
        columnOrder.add(new ColumnInfo("arriveDate", "Arrive Date", 1500));
        columnOrder.add(new ColumnInfo("id", "Move ID", 1500));

        if (movesOn) {
            columnOrder.add(new ColumnInfo("activityFrom", "Activity From", 3000));
        } else {
            columnOrder.add(new ColumnInfo("activityTo", "Activity To", 3000));
        }

        columnOrder.add(new ColumnInfo("animalCount", "Animal Count", 1500));
        columnOrder.add(new ColumnInfo("departCountry", "Departure Country", 2000));
        columnOrder.add(new ColumnInfo("arriveCountry", "Arrival Country", 2000));
        columnOrder.add(new ColumnInfo("originatingSheet", "Originating Sheet", 2500));
        return columnOrder;
    }

    private void createResultSheet(Sheet sh, ArrayList<MoveRecord> moves, boolean movesOn)
            throws WorkbookParseException {
        var outputColumns = getOutputColumns(movesOn);

        // Print the headings
        Row headingsRow = sh.createRow(0);
        outputRow(outputColumns, headingsRow, Optional.empty());

        // Widen the columns to fit the headings
        // We could use autoSizeColumns here, but it is unacceptably slow on large sheets
        for (int i = 0; i < outputColumns.size(); i++) {
            sh.setColumnWidth(i, sh.getColumnWidth(i) + outputColumns.get(i).extraWidth);
        }

        // Print the MoveRecords
        int rowIndex = 1;
        for (MoveRecord m : moves) {
            Row r = sh.createRow(rowIndex);
            outputRow(outputColumns, r, Optional.of(m));
            rowIndex += 1;
        }
    }

    private void outputRow(ArrayList<ColumnInfo> columns, Row row, Optional<MoveRecord> move)
            throws WorkbookParseException {
        if (move.isEmpty()) {
            // Print the headings row
            for (int i = 0; i < columns.size(); i++) {
                var cell = row.createCell(i);
                cell.setCellStyle(this.headingStyle);
                cell.setCellValue(columns.get(i).friendlyName);
            }
        } else {
            // Print a data row
            for (int i = 0; i < columns.size(); i++) {
                String cellValue = move.get().fieldValue(columns.get(i).field);

                var cell = row.createCell(i);
                cell.setCellValue(cellValue);

                if (move.get().isMissingData()) {
                    cell.setCellStyle(this.warningRowStyle);
                } else if (row.getRowNum() % 2 == 0) {
                    cell.setCellStyle(this.evenRowStyle);
                } else {
                    cell.setCellStyle(this.oddRowStyle);
                }
            }
        }
    }

    // We assume, following the sorting we perform, that duplicates will be listed consecutively
    private void deduplicate(ArrayList<MoveRecord> moves) {
        for (int i = 1; i < moves.size(); i++) {
            MoveComparison comparison = moves.get(i).compareTo(moves.get(i - 1));
            if (comparison == MoveComparison.Equal) {
                // We have a duplicate
                // TODO: Merge the records rather than just deleting whichever one appears second
                moves.remove(i);
                i--;
            } else if (comparison == MoveComparison.ApproxEqual) {
                // TODO: Do something like highlight the rows to indicate that there was an approximate match
            }
        }
    }
}
