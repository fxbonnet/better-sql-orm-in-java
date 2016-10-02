package net.archiloque.bsoij;

import net.archiloque.bsoij.db_specific.DbTranslator;
import net.archiloque.bsoij.schema.bean.Column;
import net.archiloque.bsoij.schema.bean.Model;
import net.archiloque.bsoij.schema.bean.PrimaryKey;
import net.archiloque.bsoij.schema.bean.Schema;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class ValidatorTest {

    private Engine engine;

    private DbTranslator dbTranslator;

    @NotNull
    private Model createModel(@NotNull String tableName) {
        Model model = new Model();
        model.setTableName(tableName);
        return model;
    }

    @NotNull
    private void addColumn(@NotNull Model model, @NotNull String name, Column.ColumnType columnType, boolean nullable) {
        Column c = new Column();
        c.setName(name);
        c.setNullable(nullable);
        c.setType(columnType);
        model.getColumns().add(c);
    }

    @NotNull
    private void addPrimaryKey(@NotNull Model model, @NotNull String columnName){
        PrimaryKey primaryKey = new PrimaryKey();
        primaryKey.setColumn(columnName);
        model.setPrimaryKey(primaryKey);
    }

    @Before
    public void initialize() throws Exception {
        engine = new Engine("org.h2.Driver", "jdbc:h2:mem:~/bsoij", "bsoij", "");
        engine.connect();
        dbTranslator = engine.getDbTranslator();
    }

    @After
    public void clean() throws SQLException {
        engine.disconnect();
    }

    private void createTable(Model model) throws Exception {
        String sql = "CREATE TABLE  " + dbTranslator.escapeTableName(model.getTableName()) + " (";
        sql += model.getColumns().stream().map(column -> {
            String columnType = dbTranslator.getColumnType(column.getType());
            return dbTranslator.escapeTableName(column.getName()) + " " + columnType + (column.isNullable() ? " not null" : "");
        }).collect(Collectors.joining(", "));
        if (model.getPrimaryKey() == null) {
            throw new RuntimeException("No primary key");
        }
        sql += ", PRIMARY KEY(" + dbTranslator.escapeTableName(model.getPrimaryKey().getColumn()) + "))";
        try (PreparedStatement preparedStatement = engine.getConnection().prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }

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
        addColumn(createdModel, "id", Column.ColumnType.Integer, false);
        createTable(createdModel);

        Schema schema = new Schema();
        Model model = createModel("tableName");
        schema.add(model);
        addPrimaryKey(model, "id");
        addColumn(model, "id", Column.ColumnType.Integer, false);

        new Validator(engine, schema).validate();
    }

    @Test
    public void testMissingColumn() throws Exception {
        Model createdModel = createModel("tableName");
        addPrimaryKey(createdModel, "id");
        addColumn(createdModel, "id", Column.ColumnType.Integer, false);
        createTable(createdModel);

        Schema schema = new Schema();
        Model model = createModel("tableName");
        addPrimaryKey(model, "id");
        addColumn(model, "id", Column.ColumnType.Integer, false);
        addColumn(model, "missingId", Column.ColumnType.Integer, false);
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
        addColumn(createdModel, "id", Column.ColumnType.Integer, false);
        addColumn(createdModel, "unknownColumn", Column.ColumnType.Integer, false);
        createTable(createdModel);

        Schema schema = new Schema();
        Model model = createModel("tableName");
        schema.add(model);
        addPrimaryKey(model, "id");
        addColumn(model, "id", Column.ColumnType.Integer, false);

        try {
            new Validator(engine, schema).validate();
            fail();
        } catch (Validator.UnknownColumnException e) {
            assertEquals("Unknown column [unknownColumn] in table [tableName]", e.getMessage());
        }
    }


}
