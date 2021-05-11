package Group1.com.DataConsolidation.DataProcessing;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;

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

    public XSSFWorkbook parse(Location outbreakSource) throws WorkbookParseException {
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
        SCOTEIDParser scoteidParserFrom = new SCOTEIDParser(scotlandFrom, progress, outbreakSource, true);
        movesFrom.addAll(scoteidParserFrom.parse().getFirst());

        Sheet scotlandTo = inWb.getSheetAt(2);
        SCOTEIDParser scoteidParserTo = new SCOTEIDParser(scotlandTo, progress, outbreakSource, false);
        movesTo.addAll(scoteidParserTo.parse().getFirst());

        Sheet wales = inWb.getSheetAt(3);
        WalesParser walesParser = new WalesParser(wales, progress, outbreakSource);
        var walesMoves = walesParser.parse();
        movesFrom.addAll(walesMoves.getFirst());
        movesTo.addAll(walesMoves.getSecond());

        // First sort by location, then by originating sheet, then by date
        movesFrom.sort(
            Comparator.comparing((MoveRecord m) -> m.locationTo.number, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing((MoveRecord m) -> m.originatingSheet.split(":")[0], Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing((MoveRecord m) -> m.arriveDate, Comparator.nullsLast(Comparator.naturalOrder()))
        );
        movesTo.sort(
            Comparator.comparing((MoveRecord m) -> m.locationFrom.number, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing((MoveRecord m) -> m.originatingSheet.split(":")[0], Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing((MoveRecord m) -> m.departDate, Comparator.nullsLast(Comparator.naturalOrder()))
        );

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
        outputHeadingRow(outputColumns, sh);

        // Widen the columns to fit the headings
        // We could use autoSizeColumns here, but it is unacceptably slow on large sheets
        for (int i = 0; i < outputColumns.size(); i++) {
            sh.setColumnWidth(i, sh.getColumnWidth(i) + outputColumns.get(i).extraWidth);
        }

        // Set the width for the 'Animal No' column
        sh.setColumnWidth(outputColumns.size(), sh.getColumnWidth(outputColumns.size()) + 3000);
        // TODO: Center horizontally?

        // Print the MoveRecords
        int rowIndex = 1;
        for (int i = 0; i < moves.size(); i++) {
            int numCreated = outputDataRow(outputColumns, sh, rowIndex, moves.get(i), i % 2 == 1);
            rowIndex += numCreated;
        }
    }

    private void outputHeadingRow(ArrayList<ColumnInfo> columns, Sheet sh) {
        // Print the headings row
        Row row = sh.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            var cell = row.createCell(i);
            cell.setCellStyle(this.headingStyle);
            cell.setCellValue(columns.get(i).friendlyName);
        }
    }

    private int outputDataRow(ArrayList<ColumnInfo> columns, Sheet sh, int rowIndex, MoveRecord move, boolean evenRow)
            throws WorkbookParseException {
        // Print a data row
        Row row = sh.createRow(rowIndex);
        XSSFCellStyle style = evenRow ? this.evenRowStyle : this.oddRowStyle;
        if (move.isWarning()) {
            style = this.warningRowStyle;
        }

        for (int i = 0; i < columns.size(); i++) {
            String cellValue = move.fieldValue(columns.get(i).field);

            var cell = row.createCell(i);
            cell.setCellValue(cellValue);
            cell.setCellStyle(style);
        }

        // Print all the animal IDs
        int numRowsCreated = 1;

        if (!move.animalIDs.isEmpty()) {
            Row r = sh.createRow(rowIndex + numRowsCreated);
            var cell = r.createCell(1);
            cell.setCellValue("Animal IDs:");
            cell.setCellStyle(style);
            numRowsCreated++;
        }

        for (var animal : move.animalIDs) {
            if (animal.isEmpty())
                continue;

            Row r = sh.createRow(rowIndex + numRowsCreated);
            var cell = r.createCell(1);
            cell.setCellValue(animal);
            cell.setCellStyle(style);
            numRowsCreated++;
        }

        return numRowsCreated;
    }
}
