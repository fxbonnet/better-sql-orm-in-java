package net.archiloque.bsoij;

import org.junit.Test;

import java.sql.SQLException;

/**
 * Test the engine.
 */
public class EngineTest {

    @Test
    public void testConnection() throws SQLException, ClassNotFoundException {
        Engine engine = new Engine("org.h2.Driver", "jdbc:h2:~/bsoij", "bsoij", "");
        engine.connect();
    }

}
