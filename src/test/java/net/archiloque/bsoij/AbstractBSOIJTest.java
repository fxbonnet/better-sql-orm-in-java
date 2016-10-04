package net.archiloque.bsoij;

import net.archiloque.bsoij.base_classes.ColumnType;
import net.archiloque.bsoij.db_specific.DbTranslator;
import net.archiloque.bsoij.schema.bean.Column;
import net.archiloque.bsoij.schema.bean.ForeignKey;
import net.archiloque.bsoij.schema.bean.Model;
import net.archiloque.bsoij.schema.bean.PrimaryKey;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
public abstract class AbstractBSOIJTest {

    protected Engine engine;

    protected DbTranslator dbTranslator;

    @Before
    public void initialize() throws Exception {
        engine = createEngine();
        engine.connect();
        dbTranslator = engine.getDbTranslator();
    }

    @After
    public void clean() throws SQLException {
        engine.disconnect();
        EngineSingleton.setEngine(null);
    }

    protected void createCustomersTable() throws Exception {
        Model createdModel = createModel("customer");
        addPrimaryKey(createdModel, "customer_id");
        addColumn(createdModel, "customer_id", ColumnType.Long, false);
        addColumn(createdModel, "name", ColumnType.String, false);
        addColumn(createdModel, "email", ColumnType.String, false);
        addColumn(createdModel, "birth", ColumnType.Date, false);
        createTable(createdModel);
    }

    protected void createOrdersTable() throws Exception {
        Model createdModel = createModel("order");
        addPrimaryKey(createdModel, "order_id");
        addColumn(createdModel, "order_id", ColumnType.Long, false);
        addColumn(createdModel, "date", ColumnType.Date, false);
        addColumn(createdModel, "delivery_date", ColumnType.Date, true);
        addColumn(createdModel, "amount", ColumnType.Integer, false);
        addColumn(createdModel, "customer_id", ColumnType.Long, false);
        addForeignKey(createdModel, "customer_id", "customer", "customer_id");
        createTable(createdModel);
    }

    protected void createTable(Model model) throws Exception {
        if (model.getPrimaryKey() == null) {
            throw new RuntimeException("No primary key");
        }
        String sql = "CREATE TABLE " + dbTranslator.escapeTableName(model.getTableName()) + " (";
        sql += model.getColumns().stream().map(column -> {
            String columnType = dbTranslator.getColumnType(column.getType());
            Optional<ForeignKey> optionalForeignKey = model.
                    getForeignKeys().
                    stream().
                    filter(fk -> fk.getColumn().equals(column.getName())).
                    findFirst();
            String statement = dbTranslator.escapeColumnName(column.getName()) + " " + columnType +
                    ((!column.isNullable()) ? " not null" : "") +
                    (column.getName().equals(model.getPrimaryKey().getColumn()) ? " AUTO_INCREMENT" : "");
            if (optionalForeignKey.isPresent()) {
                ForeignKey foreignKey = optionalForeignKey.get();
                statement += ", FOREIGN KEY (" +
                        dbTranslator.escapeColumnName(column.getName()) +
                        ") REFERENCES " +
                        dbTranslator.escapeTableName(foreignKey.getReferences()) +
                        "(" +
                        dbTranslator.escapeColumnName(foreignKey.getReverseName()) +
                        ")";
            }
            return statement;

        }).collect(Collectors.joining(", "));
        sql += ", PRIMARY KEY(" + dbTranslator.escapeTableName(model.getPrimaryKey().getColumn()) + "))";
        try (PreparedStatement preparedStatement = engine.getConnection().prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }

    @NotNull
    protected Model createModel(@NotNull String tableName) {
        Model model = new Model();
        model.setTableName(tableName);
        return model;
    }

    protected void addColumn(@NotNull Model model, @NotNull String name, ColumnType columnType, boolean nullable) {
        Column c = new Column();
        c.setName(name);
        c.setNullable(nullable);
        c.setType(columnType);
        model.getColumns().add(c);
    }

    protected void addPrimaryKey(@NotNull Model model, @NotNull String columnName) {
        PrimaryKey primaryKey = new PrimaryKey();
        primaryKey.setColumn(columnName);
        model.setPrimaryKey(primaryKey);
    }

    protected void addForeignKey(
            @NotNull Model model,
            @NotNull String column,
            @NotNull String targetTable,
            @NotNull String targetColumn) {
        ForeignKey foreignKey = new ForeignKey();
        foreignKey.setColumn(column);
        foreignKey.setReferences(targetTable);
        // hack : we store the column in the reverse name
        foreignKey.setReverseName(targetColumn);
        model.getForeignKeys().add(foreignKey);

    }


    @NotNull
    protected Engine createEngine() {
        return new Engine("org.h2.Driver", "jdbc:h2:mem:~/bsoij", "bsoij", "");
    }
}
