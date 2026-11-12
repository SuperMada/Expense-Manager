package integration;   // src/test/java/support/TestDatabase.java

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.stream.Stream;

import org.sqlite.SQLiteConfig;

/** Builds a throwaway SQLite database from sql/schema.sql + sql/seed.sql. */
public final class TestDatabase implements AutoCloseable {
    // Surefire runs with the module folder (manager-app-java/) as the working directory.
    private static final Path SQL_DIR = Path.of("..", "sql");

    private final Path dir;
    private final Connection conn;

    private TestDatabase(Path dir, Connection conn) {
        this.dir = dir;
        this.conn = conn;
    }

    /** Creates the temp folder, the DB file, runs schema + seed, and opens a connection. */
    public static TestDatabase create() throws IOException, SQLException {
        Path dir = Files.createTempDirectory("em-test-");
        Connection conn = null;
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);   // same settings as the real app (8.1)
            config.setBusyTimeout(5000);
            conn = DriverManager.getConnection(
                "jdbc:sqlite:" + dir.resolve("test.db"), config.toProperties());

            runScript(conn, SQL_DIR.resolve("schema.sql"));
            runScript(conn, SQL_DIR.resolve("seed.sql"));
            return new TestDatabase(dir, conn);
        } catch (IOException | SQLException | RuntimeException e) {
            if (conn != null) conn.close();      // don't leak the file handle
            deleteRecursively(dir);
            throw e;
        }
    }

    public Connection connection() { return conn; }

    /** Closes the connection first (Windows can't delete an open file), then deletes the folder. */
    @Override
    public void close() throws IOException, SQLException {
        conn.close();
        deleteRecursively(dir);
    }

    // ---------- internals ----------

    private static void runScript(Connection conn, Path file) throws IOException, SQLException {
        StringBuilder cleaned = new StringBuilder();
        for (String line : Files.readAllLines(file)) {
            if (!line.strip().startsWith("--")) cleaned.append(line).append('\n');
        }
        try (Statement st = conn.createStatement()) {
            for (String stmt : cleaned.toString().split(";")) {
                if (!stmt.isBlank()) st.execute(stmt);
            }
        }
    }

    private static void deleteRecursively(Path dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }
}