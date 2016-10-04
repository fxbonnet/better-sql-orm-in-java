package net.archiloque.bsoij;

import net.archiloque.bsoij.base_classes.Filter;
import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.base_classes.model.Model;
import net.archiloque.bsoij.base_classes.model.MultipleModel;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import net.archiloque.bsoij.base_classes.select.MultipleSelect;
import net.archiloque.bsoij.base_classes.select.Select;
import net.archiloque.bsoij.base_classes.select.SimpleSelect;
import net.archiloque.bsoij.db_specific.DbTranslator;
import net.archiloque.bsoij.db_specific.H2Translator;
import net.archiloque.bsoij.engine.FilterSqlGenerator;
import net.archiloque.bsoij.engine.JoinSqlGenerator;
import net.archiloque.bsoij.engine.MultipleModelMapper;
import net.archiloque.bsoij.engine.OrderBySqlGenerator;
import net.archiloque.bsoij.engine.ResultSetIterator;
import net.archiloque.bsoij.engine.SimpleModelMapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Use a singleton.
 */
public class Engine {

    @NotNull
    private final String driverClass;

    @NotNull
    private final String connectionUrl;

    @NotNull
    private final String user;

    @NotNull
    private final String password;

    private Connection connection;

    private final DbTranslator dbTranslator;

    public Engine(
            @NotNull String driverClass,
            @NotNull String connectionUrl,
            @NotNull String user,
            @NotNull String password) {
        this.driverClass = driverClass;
        this.connectionUrl = connectionUrl;
        this.user = user;
        this.password = password;
        dbTranslator = new H2Translator();
    }

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName(driverClass);
        connection = DriverManager.
                getConnection(connectionUrl, user, password);
    }

    public void disconnect() throws SQLException {
        if (connection == null) {
            throw new RuntimeException("Can't disconnect before connecting");
        }
        connection.close();
    }

    @NotNull
    public Connection getConnection() {
        if (connection == null) {
            throw new RuntimeException("Can't disconnect before connecting");
        }
        return connection;
    }

    public DbTranslator getDbTranslator() {
        return dbTranslator;
    }

    /**
     * Insert a model instance in the database
     *
     * @param model the model to insert
     * @throws SQLException if something goes wrong.
     */
    public void insert(@NotNull SimpleModel model) throws SQLException {
        String statementContent = "INSERT INTO " + escapeTableName(model.getTableName()) + "(";
        statementContent += Arrays.stream(model.getFields()).
                map(field -> getDbTranslator().escapeColumnName(field.getColumnName())).
                collect(Collectors.joining(", "));
        statementContent += ") VALUES (" + StringUtils.join(Collections.nCopies(model.getFields().length, "?"), ", ") + ")";
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(
                             statementContent,
                             Statement.RETURN_GENERATED_KEYS)
        ) {
            // set the values
            for (int fieldIndex = 0; fieldIndex < model.getFields().length; fieldIndex++) {
                Field currentField = model.getFields()[fieldIndex];
                setValueInStatement(currentField.getGetter().apply(model), currentField, preparedStatement, fieldIndex);
            }
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    model.getPrimaryKeyField().getSetter().accept(model, id);
                } else {
                    throw new SQLException("No generated keys value");
                }
            }

        }
    }

    private static void setValueInStatement(
            Object value,
            @NotNull Field field,
            @NotNull PreparedStatement preparedStatement,
            int index) throws SQLException {
        if (value != null) {
            switch (field.getColumnType()) {
                case String:
                    preparedStatement.setNString(index + 1, (String) value);
                    break;
                case Date:
                    preparedStatement.setDate(index + 1, new java.sql.Date(((java.util.Date) value).getTime()));
                    break;
                case Integer:
                    preparedStatement.setInt(index + 1, (Integer) value);
                    break;
                case Long:
                    preparedStatement.setLong(index + 1, (Long) value);
                    break;
                default:
                    throw new RuntimeException("Unknown type [" + field.getColumnType() + "]");
            }
        } else {
            switch (field.getColumnType()) {
                case String:
                    preparedStatement.setNull(index + 1, JDBCType.VARCHAR.getVendorTypeNumber());
                    break;
                case Date:
                    preparedStatement.setNull(index + 1, JDBCType.DATE.getVendorTypeNumber());
                    break;
                case Integer:
                    preparedStatement.setNull(index + 1, JDBCType.INTEGER.getVendorTypeNumber());
                    break;
                case Long:
                    preparedStatement.setNull(index + 1, JDBCType.BIGINT.getVendorTypeNumber());
                    break;
                default:
                    throw new RuntimeException("Unknown type [" + field.getColumnType() + "]");
            }

        }
    }

    public <T extends SimpleModel> Stream<T> fetch(SimpleSelect<T> select) {
        return fetch(select, new SimpleModelMapper<T>(select), false);
    }

    public <S extends SimpleModel> Optional<S> fetchFirst(SimpleSelect<S> select) {
        return fetch(select, new SimpleModelMapper<S>(select), true).findFirst();
    }

    public <T extends MultipleModel> Stream<T> fetch(MultipleSelect<T> select) {
        return fetch(select, new MultipleModelMapper<T>(select), false);
    }

    public <T extends MultipleModel> Optional<T> fetchFirst(MultipleSelect<T> select) {
        return fetch(select, new MultipleModelMapper<T>(select), true).findFirst();
    }


    private <T extends Model> Stream<T> fetch(Select<T> select, Function<ResultSet, T> mapper, boolean fetchFirst) {
        String statement = createFetchStatement(select);
        if (fetchFirst) {
            statement += " limit 1";
        }

        ResultSetIterator<? extends Model> resultSetIterator =
                new ResultSetIterator<T>(statement, mapper);
        try {
            resultSetIterator.initialize(getConnection(), preparedStatement -> {
                ListIterator<Filter> filterListIterator = select.getFilters().listIterator();
                while (filterListIterator.hasNext()) {
                    Filter filter = filterListIterator.next();
                    Object[] parameters = filter.getCriteria().getParameters();
                    if (parameters.length == 0) {

                    } else if (parameters.length == 1) {
                        try {
                            setValueInStatement(parameters[0], filter.getField(), preparedStatement, filterListIterator.previousIndex());
                        } catch (SQLException e) {
                            throw new RuntimeSqlException(e);
                        }
                    } else {
                        throw new RuntimeException("Unknown number of parameters " + parameters.length);
                    }

                }
            });
        } catch (SQLException e) {
            try {
                resultSetIterator.close();
            } catch (IOException e1) {
                throw new RuntimeSqlException(e);
            }
            throw new RuntimeSqlException(e);
        }
        Iterable<T> iterable = () -> resultSetIterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    @NotNull
    private String createFetchStatement(Select<? extends Model> select) {
        String statementContent = "SELECT ";
        List<String> fields = new ArrayList<>();
        for (int fieldIndex = 0; fieldIndex < select.getFields().length; fieldIndex++) {
            Field field = select.getFields()[fieldIndex];
            fields.add(
                    escapeTableName(field.getTableName()) +
                            '.' +
                            escapeColumnName(field.getColumnName()) +
                            " as column_" + fieldIndex);
        }
        statementContent += String.join(", ", fields) + " from ";

        statementContent += Arrays.
                stream(select.getTablesNames()).
                map(dbTranslator::escapeTableName).
                collect(Collectors.joining(", "));

        if (!(select.getFilters().isEmpty() && (select.getJoins().length == 0))) {
            statementContent += " WHERE " +
                    Stream.concat(select.
                                    getFilters().
                                    stream().
                                    map(filter -> new FilterSqlGenerator(filter, dbTranslator).generateSql()),
                            Arrays.
                                    stream(select.getJoins()).
                                    map(join -> new JoinSqlGenerator(join, dbTranslator).generateSql())).
                            collect(Collectors.joining(", "));
        }

        if (!select.getSorts().isEmpty()) {
            statementContent += " ORDER BY " +
                    select.
                            getSorts().
                            stream().
                            map(sort -> new OrderBySqlGenerator(sort, dbTranslator).generateSql()).
                            collect(Collectors.joining(", "));
        }
        return statementContent;
    }

    @NotNull
    private String escapeTableName(@NotNull String tableName) {
        return dbTranslator.escapeTableName(tableName);
    }

    @NotNull
    private String escapeColumnName(@NotNull String columnName) {
        return dbTranslator.escapeColumnName(columnName);
    }

}
