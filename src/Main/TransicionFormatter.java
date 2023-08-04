package Main;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class TransicionFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return record.getMessage() + "-";
    }

}
