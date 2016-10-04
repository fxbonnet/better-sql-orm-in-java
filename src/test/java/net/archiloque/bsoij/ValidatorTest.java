package net.archiloque.bsoij;

import net.archiloque.bsoij.base_classes.ColumnType;
import net.archiloque.bsoij.schema.bean.Model;
import net.archiloque.bsoij.schema.bean.Schema;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class ValidatorTest extends AbstractBSOIJTest {

    @Test
    public void testMissingTable() throws Exception {
        Schema schema = new Schema();
        schema.add(createModel("tableName"));
        try {
            new Validator(engine, schema).validate();
            fail();
        } catch (Validator.MissingTableException e) {
            assertEquals("Table missing [tableName]", e.getMessage());
        }
    }

    @Test
    public void testFound() throws Exception {
        Model createdModel = createModel("tableName");
        addPrimaryKey(createdModel, "id");
        addColumn(createdModel, "id", ColumnType.Integer, false);
        createTable(createdModel);

        Schema schema = new Schema();
        Model model = createModel("tableName");
        schema.add(model);
        addPrimaryKey(model, "id");
        addColumn(model, "id", ColumnType.Integer, false);

        new Validator(engine, schema).validate();
    }

    @Test
    public void testMissingColumn() throws Exception {
        Model createdModel = createModel("tableName");
        addPrimaryKey(createdModel, "id");
        addColumn(createdModel, "id", ColumnType.Integer, false);
        createTable(createdModel);

        Schema schema = new Schema();
        Model model = createModel("tableName");
        addPrimaryKey(model, "id");
        addColumn(model, "id", ColumnType.Integer, false);
        addColumn(model, "missingId", ColumnType.Integer, false);
        schema.add(model);
        try {
            new Validator(engine, schema).validate();
            fail();
        } catch (Validator.MissingColumnException e) {
            assertEquals("Column missing [missingId] in table [tableName]", e.getMessage());
        }
    }


    @Test
    public void testUnknownColumn() throws Exception {
        Model createdModel = createModel("tableName");
        addPrimaryKey(createdModel, "id");
        addColumn(createdModel, "id", ColumnType.Integer, false);
        addColumn(createdModel, "unknownColumn", ColumnType.Integer, false);
        createTable(createdModel);

        Schema schema = new Schema();
        Model model = createModel("tableName");
        schema.add(model);
        addPrimaryKey(model, "id");
        addColumn(model, "id", ColumnType.Integer, false);

        try {
            new Validator(engine, schema).validate();
            fail();
        } catch (Validator.UnknownColumnException e) {
            assertEquals("Unknown column [unknownColumn] in table [tableName]", e.getMessage());
        }
    }

}
