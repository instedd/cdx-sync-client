package org.instedd.cdx.app;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class LogFormatter extends Formatter {
  private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
  private static final String lineSeparator = System.getProperty("line.separator");

  @Override
  public String format(LogRecord record) {
    StringBuilder builder = new StringBuilder();
    builder.append(df.format(new Date(record.getMillis())));
    builder.append(" - ");
    builder.append(record.getLevel());
    builder.append(" - ");
    builder.append(formatMessage(record));

    if (record.getThrown() != null) {
      builder.append(" - ");
      builder.append(record.getThrown().toString());
    }

    builder.append(lineSeparator);
    return builder.toString();
  }

}
