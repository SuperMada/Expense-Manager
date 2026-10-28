import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_FILE = "app.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;

    public static Connection init() throws SQLException, IOException {
        // Check BEFORE connecting, because connecting creates the file
        boolean isNew = Files.notExists(Path.of(DB_FILE));

        Connection conn = DriverManager.getConnection(URL);

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