package net.archiloque.bsoij;

import net.archiloque.bsoij.schema.bean.Model;
import net.archiloque.bsoij.schema.bean.Schema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class ValidatorTest {

    private Engine engine;

    @Before
    public void initialize() throws SQLException, ClassNotFoundException {
        engine = new Engine("org.h2.Driver", "jdbc:h2:~/bsoij", "bsoij", "");
        engine.connect();
    }

    @After
    public void clean() throws SQLException {
        engine.disconnect();
    }

    @Test
    public void testMissingTable() throws SQLException, ClassNotFoundException, Validator.ValidationException {
        Schema schema = new Schema();
        Model model = new Model();
        model.setTableName("tableName");
        schema.add(model);
        try {
            new Validator(engine, schema).validate();
            fail();
        } catch (Validator.MissingTableException e) {
            assertEquals("Table missing [tableName]", e.getMessage());
        }
    }

}
