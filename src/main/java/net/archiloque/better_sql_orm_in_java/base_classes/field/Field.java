package net.archiloque.better_sql_orm_in_java.base_classes.field;

/**
 *
 */
public class Field<T> {

    private final String columnName;

    public Field(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
