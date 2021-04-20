package Group1.com.DataConsolidation;

import Group1.com.DataConsolidation.DataProcessing.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class DataProcessingTests{

    private final Location outbreakSource = new Location("08/548/4000");

    XSSFWorkbook loadExcelFile(String path) {
        InputStream data = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);
        return assertDoesNotThrow(() -> {
            assert data != null;
            return new XSSFWorkbook(data);
        }, "could not open " + path);
    }

    Progress dummyProgress() {
        return new Progress();
    }

    @Test
    void rejectsInvalidWorkbooks() {
        assertThrows(IllegalArgumentException.class,
                () -> new DataConsolidator(null, dummyProgress()));

        Exception e = assertThrows(WorkbookParseException.class,
                () -> new DataConsolidator(new XSSFWorkbook(), dummyProgress()).parse(outbreakSource));
        assertEquals("empty workbook (no sheets)", e.getMessage());

        // TODO: Test workbook with no data
        // TODO: Test workbook with some sheets missing
    }

    void testWorksheet(String prefix, String[] headingNames, Function<Sheet, Parser> createParser) {
        // Should reject workbook with no rows
        XSSFWorkbook wbEmpty = new XSSFWorkbook();
        Sheet sheet = wbEmpty.createSheet("test");
        Exception e = assertThrows(WorkbookParseException.class,
                () -> createParser.apply(sheet).parse());
        assertEquals(prefix + ": empty spreadsheet (no headings)", e.getMessage());

        // Should accept every sheet in this file
        XSSFWorkbook wbValid = assertDoesNotThrow(() -> loadExcelFile(prefix + "_valid.xlsx"));
        for (Sheet sh : wbValid) {
            String testName = sh.getSheetName();
            var p = createParser.apply(sh);
            assertDoesNotThrow(
                    p::parse,
                    "didn't accept valid " + prefix + " sheet: " + testName);
        }

        // Should reject every sheet in this file
        XSSFWorkbook wbInvalid = assertDoesNotThrow(() -> loadExcelFile("wales_invalid.xlsx"));
        for (Sheet sh : wbInvalid) {
            String testName = sh.getSheetName();
            Parser p = createParser.apply(sh);

            Iterator<Row> rowIter = sh.rowIterator();
            Map<String, Integer> headings = new HashMap<>();
            Row row = rowIter.next();

            // exceptValue == 0: no exception thrown
            // exceptValue == 1: duplicate heading
            // exceptValue == 2: missing column
            var exceptValue = 0;

            //check duplicate heading
            String valueName = "";
            for (Cell cell : row) {
                if (!cell.getCellType().equals(CellType.STRING)) {
                    continue;
                }
                for (String name : headingNames) {
                    String value = cell.getStringCellValue();
                    if (value.compareToIgnoreCase(name) == 0) {
                        Integer oldIndex = headings.putIfAbsent(value, cell.getColumnIndex());
                        if (!Objects.isNull(oldIndex)) {
                            valueName = value;
                            exceptValue = 1;
                        }
                    }

                }
            }

            ArrayList<String> missing = new ArrayList<>();
            if(exceptValue == 0) {
                if (headings.size() != headingNames.length) {
                    // Find which headings were missing from the sheet
                    ArrayList<String> missingHeadings = new ArrayList<>();
                    for (String heading : headingNames) {
                        if (!headings.containsKey(heading)) {
                            missingHeadings.add(heading);
                            exceptValue = 2;
                        }
                    }
                    missing = missingHeadings;
                }
            }

            String expect1 = String.format("%s: duplicate heading '%s'", prefix, valueName);
            String expect2 = String.format("%s: didn't find all headings - missing %s",
                    prefix, String.join(", ", missing));

            e = assertThrows(WorkbookParseException.class,
                    p::parse,
                    "didn't reject invalid " + prefix + " sheet: " + testName);
            System.out.println(e.getMessage());
            if(exceptValue == 1)
                assertEquals(expect1, e.getMessage());
            else if(exceptValue == 2)
                assertEquals(expect2, e.getMessage());
        }
    }

    @Test
    void walesParsing() {
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
        testWorksheet("wales", headingNames, sh -> new WalesParser(sh, dummyProgress(), outbreakSource));
    }

    @Test
    void aramsParsing(){
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
        testWorksheet("arams", headingNames, sh -> new ARAMSParser(sh, dummyProgress(), outbreakSource));
    }

    @Test
    void scoteidParsing() {
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
        testWorksheet("scoteid", headingNames, sh -> new SCOTEIDParser(sh, dummyProgress(), outbreakSource));
    }

    @Test
//    @Disabled
    void parsesTestData() {
        XSSFWorkbook inWb = assertDoesNotThrow(() -> loadExcelFile("test_data_cleaned.xlsx"));
        DataConsolidator cs = assertDoesNotThrow(() -> new DataConsolidator(inWb, dummyProgress()));

        var outWb = assertDoesNotThrow(() -> cs.parse(outbreakSource));

        // Check reasonable output
        for (Sheet sh : outWb) {
            assertTrue(sh.getSheetName().equals("Moves On") || sh.getSheetName().equals("Moves Off"));
        }

        assertDoesNotThrow(() -> outWb.write(new FileOutputStream("/tmp/parsed_data.xlsx")));
    }
}