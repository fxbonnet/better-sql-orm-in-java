package net.archiloque.bsoij.engine;

import net.archiloque.bsoij.RuntimeSqlException;
import net.archiloque.bsoij.base_classes.model.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Create an Iterator iterating from ResultSet.
 *
 * @param <T> the model that will be created.
 */
public final class ResultSetIterator<T extends Model> implements Iterator, AutoCloseable {

    @NotNull
    private final String sqlStatement;

    @NotNull
    private final Function<ResultSet, T> resultCallback;

    private PreparedStatement preparedStatement;

    private boolean hasNext = true;

    private ResultSet resultSet;

    public ResultSetIterator(
            @NotNull String sqlStatement,
            @NotNull Function<ResultSet, T> resultCallback) {
        this.sqlStatement = sqlStatement;
        this.resultCallback = resultCallback;
    }

    public void initialize(
            @NotNull Connection connection,
            @Nullable Consumer<PreparedStatement> preparedStatementCallback) throws SQLException {
        preparedStatement = connection.prepareStatement(sqlStatement);
        if (preparedStatementCallback != null) {
            preparedStatementCallback.accept(preparedStatement);
        }
        resultSet = preparedStatement.executeQuery();
        nextStep();
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public T next() throws NoSuchElementException {
        if (hasNext) {
            T value = resultCallback.apply(resultSet);
            nextStep();
            return value;
        } else {
            throw new NoSuchElementException();
        }
    }

    private void nextStep() {
        try {
            hasNext = resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                closePreparedStatement();
                throw new IOException(e);
            }
        }
        closePreparedStatement();
    }

    private void closePreparedStatement() throws IOException {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                throw new IOException(e);
            }
        }
    }
}
