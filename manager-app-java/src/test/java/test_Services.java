import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


//@ExtendWith(MockitoExtension.class)
public class test_Services {

    private AutoCloseable mocks;

    @BeforeEach
    void initMocks() {
        mocks = MockitoAnnotations.openMocks(this);
    }
 
    @AfterEach
    void closeMocks() throws Exception {
        mocks.close();
    }

    @Mock Connection conn;
    @Mock PreparedStatement ps;
    @Mock ResultSet rs;

    @Test 
    void test_manager_login_works() throws Exception{


        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);


        when(rs.next()).thenReturn(true);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("role")).thenReturn("manager");

        int id = Services.login(conn, "mgr_john", "Passw0rd!");

        assertEquals(1, id);
        verify(ps).setString(1, "mgr_john");
        verify(ps).setString(2, "Passw0rd!");
    }

    @Test 
    void test_get_name_from_id_works() throws Exception{
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("full_name")).thenReturn("John Doe");

        String name = Services.getNameFromID(conn, 1);

        assertEquals(name, "John Doe");
        verify(ps).setInt(1, 1);

    }
}
