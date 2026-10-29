import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Database {

    private static final String ENV_VAR = "EM_DB_PATH";
    private static final Path ENV_FILE = Path.of("..", ".env");

    private static Path resolveDbPath() throws IOException {
        String value = null;

        if (Files.exists(ENV_FILE)) {
            for (String line : Files.readAllLines(ENV_FILE)) {
                line = line.strip();
                if (line.startsWith(ENV_VAR + "=")) {
                    value = line.substring(ENV_VAR.length() + 1).strip();
                    break;
                }
            }
        }

        if (value == null || value.isBlank()) {
            throw new IOException(ENV_VAR + " is not set in " + ENV_FILE.toAbsolutePath());
        }

        Path dbPath = Path.of(value);
        Path parent = dbPath.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        return dbPath;
    }

    public static Connection init() throws SQLException, IOException {
        // Check BEFORE connecting, because connecting creates the file
        Path url = resolveDbPath();
        boolean isNew = Files.notExists(url);

        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + url.toString());

        if (isNew) {
            runScript(conn, "schema.sql");
            runScript(conn, "seed.sql");
        }
        return conn;
    }


    //This creates the db if it does not already exist
    private static void runScript(Connection conn, String resource)
            throws SQLException, IOException {
        try (var in = Database.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IOException("Missing resource: " + resource);
            }
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            try (Statement st = conn.createStatement()) {
                for (String stmt : sql.split(";")) {
                    if (!stmt.isBlank()) {
                        st.execute(stmt);
                    }
                }
            }
        }
    }
}