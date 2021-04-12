package Group1.com.DataConsolidation.DataProcessing;

import java.lang.reflect.Field;

public class ColumnInfo {
    public Field field;
    public String friendlyName;
    public int extraWidth;

    ColumnInfo(String fieldName, String friendlyName, int width) throws WorkbookParseException {
        try {
            this.field = MoveRecord.class.getDeclaredField(fieldName);
            this.friendlyName = friendlyName;
            this.extraWidth = width;
        } catch (NoSuchFieldException e) {
            throw new WorkbookParseException("MoveRecord field " + fieldName + " not found: " + e.getMessage());
        }
    }

    ColumnInfo(String fieldName, String friendlyName) throws WorkbookParseException {
        this(fieldName, friendlyName, 0);
    }
}
