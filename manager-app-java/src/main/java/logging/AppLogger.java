package logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public final class AppLogger {
    private static final String NAME = "expensemanager";
    private static final DateTimeFormatter TS =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    private static boolean configured = false;

    private AppLogger() {}

    //Init in main
    public static synchronized void init(Path logFile) throws IOException {
        if (configured) return;
        Files.createDirectories(logFile.toAbsolutePath().getParent());

        Logger root = Logger.getLogger(NAME);
        //DOnt print log to main
        root.setUseParentHandlers(false);
        //Output level
        root.setLevel(Level.INFO);

        FileHandler fh = new FileHandler(logFile.toString(), true);
        fh.setEncoding("UTF-8");
        fh.setFormatter(new Formatter() {
            @Override public String format(LogRecord r) {
                String line = TS.format(Instant.ofEpochMilli(r.getMillis()))
                    + " " + r.getLevel() + " " + r.getLoggerName() + " - " + formatMessage(r) + "\n";
                if (r.getThrown() != null) {
                    java.io.StringWriter sw = new java.io.StringWriter();
                    r.getThrown().printStackTrace(new java.io.PrintWriter(sw));
                    line += sw;
                }
                return line;
            }
        });
        root.addHandler(fh);
        configured = true;
    }

    //Logger knows what class its in and can output it to log
    public static Logger get(Class<?> c) {
        return Logger.getLogger(NAME + "." + c.getSimpleName());
    }
}