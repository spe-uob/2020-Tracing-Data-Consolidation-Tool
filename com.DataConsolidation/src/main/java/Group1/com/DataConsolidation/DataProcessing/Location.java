package Group1.com.DataConsolidation.DataProcessing;

// Represents a single CPH number
public class Location {
    public String number;

    public Location(String number) {
        if (!number.contains("/") && !number.isEmpty()) {
            // Prepend 'SH' to FSA numbers
            number = "SH" + number;
        }

        this.number = number;
    }

    public String getCountry() {
        int digits;
        try {
            // The country can be identified by looking at the first two digits
            digits = Integer.parseUnsignedInt(this.number, 0, 2, 10);
        } catch (Exception e) {
            // It is important to return empty strings here so that empty records get treated as empty
            return "";
        }

        // 1  - 51: England
        // 52 - 66: Wales
        // 67+    : Scotland
        if (1 <= digits && digits <= 51) {
            return "England";
        } else if (52 <= digits && digits <= 66) {
            return "Wales";
        } else if (67 <= digits && digits <= 99) {
            return "Scotland";
        } else {
            return "";
        }
    }

    public boolean equals(Location other) {
        if (this == other) {
            return true;
        } else {
            // TODO: Make sure that whitespace doesn't mess up this comparison
            return this.number.equals(other.number);
        }
    }
}
