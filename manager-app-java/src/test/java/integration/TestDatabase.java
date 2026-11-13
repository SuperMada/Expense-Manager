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

public final class TestDatabase implements AutoCloseable {
    private static final Path SQL_DIR = Path.of("..", "sql");

    private final Path dir;
    private final Connection conn;

    private TestDatabase(Path dir, Connection conn) {
        this.dir = dir;
        this.conn = conn;
    }

    //Creates the temp db
    public static TestDatabase create() throws IOException, SQLException {
        Path dir = Files.createTempDirectory("em-test-");
        Connection conn = null;
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            config.setBusyTimeout(5000);
            conn = DriverManager.getConnection(
                "jdbc:sqlite:" + dir.resolve("test.db"), config.toProperties());

            runScript(conn, SQL_DIR.resolve("schema.sql"));
            runScript(conn, SQL_DIR.resolve("seed.sql"));
            return new TestDatabase(dir, conn);
        } catch (IOException | SQLException | RuntimeException e) {
            if (conn != null) conn.close();
            deleteRecursively(dir);
            throw e;
        }
    }

    public Connection connection() { return conn; }

    //Deletes the db after every execution so db is new for every integration test
    @Override
    public void close() throws IOException, SQLException {
        conn.close();
        deleteRecursively(dir);
    }

    //Runs schema/seed
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

    //Deletes db
    private static void deleteRecursively(Path dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }
}