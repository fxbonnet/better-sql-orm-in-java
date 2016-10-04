package net.archiloque.bsoij.engine;

import net.archiloque.bsoij.base_classes.ColumnType;
import net.archiloque.bsoij.base_classes.Criteria;
import net.archiloque.bsoij.base_classes.Filter;
import net.archiloque.bsoij.db_specific.DbTranslator;
import org.jetbrains.annotations.NotNull;

/**
 * Generate SQL for a filter.
 */
public class FilterSqlGenerator {

    @NotNull
    private final Filter filter;

    @NotNull
    private final DbTranslator dbTranslator;

    @NotNull
    private final String fi;

    public FilterSqlGenerator(@NotNull Filter filter, @NotNull DbTranslator dbTranslator) {
        this.filter = filter;
        this.dbTranslator = dbTranslator;
        fi = dbTranslator.escapeTableName(filter.getField().getTableName()) +
                "." +
                dbTranslator.escapeColumnName(filter.getField().getColumnName());
    }

    @NotNull
    public String generateSql() {
        Criteria criteria = filter.getCriteria();
        ColumnType columnType = criteria.getColumnType();
        if (criteria.isNullable()) {
            switch (columnType) {
                case String:
                    return generateSqlNullableString();
                case Date:
                    return generateSqlNullableDate();
                case Integer:
                    return generateSqlNullableInteger();
                case Long:
                    return generateSqlNullableLong();
                default:
                    throw new RuntimeException("Unknown type [" + columnType + "]");
            }
        } else {
            switch (columnType) {
                case String:
                    return generateSqlString();
                case Date:
                    return generateSqlDate();
                case Integer:
                    return generateSqlInteger();
                case Long:
                    return generateSqlLong();
                default:
                    throw new RuntimeException("Unknown type [" + columnType + "]");
            }
        }
    }

    @NotNull
    private String generateSqlNullableString(){
        Criteria.NullableStringCriteria.Type type = Criteria.NullableStringCriteria.Type.valueOf(filter.getCriteria().getType());
        switch (type) {
            case EQUALS_OR_NULL:
                return sqlEqualsOrNull();
            case NOT_EQUALS_OR_NULL:
                return sqlNotEqualsOrNull();
            case IS_NULL:
                return sqlIsNull();
            case IS_NOT_NULL:
                return sqlIsNotNull();
            default:
                throw new RuntimeException("Unknown type [" + type + "]");
        }
    }

    @NotNull
    private String generateSqlString(){
        Criteria.StringCriteria.Type type = Criteria.StringCriteria.Type.valueOf(filter.getCriteria().getType());
        switch (type) {
            case EQUALS:
                return sqlEquals();
            case NOT_EQUALS:
                return sqlNotEquals();
            default:
                throw new RuntimeException("Unknown type [" + type + "]");
        }
    }

    @NotNull
    private String generateSqlNullableInteger(){
        Criteria.NullableIntegerCriteria.Type type = Criteria.NullableIntegerCriteria.Type.valueOf(filter.getCriteria().getType());
        switch (type) {
            case EQUALS_OR_NULL:
                return sqlEqualsOrNull();
            case NOT_EQUALS_OR_NULL:
                return sqlNotEqualsOrNull();
            case IS_NULL:
                return sqlIsNull();
            case IS_NOT_NULL:
                return sqlIsNotNull();
            case STRICTLY_LESS_OR_NULL:
                return sqlStrictlyLessOrNull();
            case LESS_OR_EQUALS_OR_NULL:
                return sqlLessOrEqualsOrNull();
            case STRICTLY_MORE_OR_NULL:
                return sqlStrictlyMoreOrNull();
            case MORE_OR_EQUALS_OR_NULL:
                return sqlMoreOrEqualsOrNull();
            default:
                throw new RuntimeException("Unknown type [" + type + "]");
        }
    }

    @NotNull
    private String generateSqlInteger(){
        Criteria.IntegerCriteria.Type type = Criteria.IntegerCriteria.Type.valueOf(filter.getCriteria().getType());
        switch (type) {
            case EQUALS:
                return sqlEquals();
            case NOT_EQUALS:
                return sqlNotEquals();
            case STRICTLY_LESS:
                return sqlStrictlyLess();
            case LESS_OR_EQUALS:
                return sqlLessOrEquals();
            case STRICTLY_MORE:
                return sqlStrictlyMore();
            case MORE_OR_EQUALS:
                return sqlMoreOrEquals();
            default:
                throw new RuntimeException("Unknown type [" + type + "]");
        }
    }

    @NotNull
    private String generateSqlNullableLong(){
        Criteria.NullableLongCriteria.Type type = Criteria.NullableLongCriteria.Type.valueOf(filter.getCriteria().getType());
        switch (type) {
            case EQUALS_OR_NULL:
                return sqlEqualsOrNull();
            case NOT_EQUALS_OR_NULL:
                return sqlNotEqualsOrNull();
            case IS_NULL:
                return sqlIsNull();
            case IS_NOT_NULL:
                return sqlIsNotNull();
            case STRICTLY_LESS_OR_NULL:
                return sqlStrictlyLessOrNull();
            case LESS_OR_EQUALS_OR_NULL:
                return sqlLessOrEqualsOrNull();
            case STRICTLY_MORE_OR_NULL:
                return sqlStrictlyMoreOrNull();
            case MORE_OR_EQUALS_OR_NULL:
                return sqlMoreOrEqualsOrNull();
            default:
                throw new RuntimeException("Unknown type [" + type + "]");
        }
    }

    @NotNull
    private String generateSqlLong(){
        Criteria.LongCriteria.Type type = Criteria.LongCriteria.Type.valueOf(filter.getCriteria().getType());
        switch (type) {
            case EQUALS:
                return sqlEquals();
            case NOT_EQUALS:
                return sqlNotEquals();
            case STRICTLY_LESS:
                return sqlStrictlyLess();
            case LESS_OR_EQUALS:
                return sqlLessOrEquals();
            case STRICTLY_MORE:
                return sqlStrictlyMore();
            case MORE_OR_EQUALS:
                return sqlMoreOrEquals();
            default:
                throw new RuntimeException("Unknown type [" + type + "]");
        }
    }

    @NotNull
    private String generateSqlNullableDate(){
        Criteria.NullableDateCriteria.Type type = Criteria.NullableDateCriteria.Type.valueOf(filter.getCriteria().getType());
        switch (type) {
            case EQUALS_OR_NULL:
                return sqlEqualsOrNull();
            case NOT_EQUALS_OR_NULL:
                return sqlEqualsOrNull();
            case BEFORE_OR_NULL:
                return sqlStrictlyLessOrNull();
            case AFTER_OR_NULL:
                return sqlStrictlyMoreOrNull();
            case IS_NULL:
                return sqlIsNull();
            case IS_NOT_NULL:
                return sqlIsNotNull();
            default:
                throw new RuntimeException("Unknown type [" + type + "]");
        }
    }

    @NotNull
    private String generateSqlDate(){
        Criteria.DateCriteria.Type type = Criteria.DateCriteria.Type.valueOf(filter.getCriteria().getType());
        switch (type) {
            case BEFORE:
                return sqlStrictlyLess();
            case AFTER:
                return sqlStrictlyMore();
            case EQUALS:
                return sqlEquals();
            case NOT_EQUALS:
                return sqlNotEquals();
            default:
                throw new RuntimeException("Unknown type [" + type + "]");
        }
    }

    @NotNull
    private String sqlEqualsOrNull(){
        return "((" + fi + " = ?) OR (" + fi + " IS NULL))";
    }

    @NotNull
    private String sqlEquals(){
        return "(" + fi + " = ?)";
    }

    @NotNull
    private String sqlIsNotNull() {
        return "(" + fi + " IS NOT NULL)";
    }

    @NotNull
    private String sqlIsNull() {
        return "(" + fi + " IS NULL)";
    }

    @NotNull
    private String sqlNotEqualsOrNull() {
        return "((" + fi + " != ?) OR (" + fi + " IS NULL))";
    }

    @NotNull
    private String sqlNotEquals() {
        return "(" + fi + " != ?)";
    }

    @NotNull
    private String sqlStrictlyLess() {
        return "(" + fi + " > ?)";
    }

    @NotNull
    private String sqlStrictlyMore() {
        return "(" + fi + " < ?)";
    }

    @NotNull
    private String sqlStrictlyLessOrNull() {
        return "((" + fi + " > ?) OR (" + fi + " IS NULL))";
    }

    @NotNull
    private String sqlStrictlyMoreOrNull() {
        return "((" + fi + " < ?) OR (" + fi + " IS NULL))";
    }

    @NotNull
    private String sqlLessOrEquals() {
        return "(" + fi + " >= ?)";
    }

    @NotNull
    private String sqlMoreOrEquals() {
        return "(" + fi + " <= ?)";
    }

    @NotNull
    private String sqlLessOrEqualsOrNull() {
        return "((" + fi + " >= ?) OR (" + fi + " IS NULL))";
    }

    @NotNull
    private String sqlMoreOrEqualsOrNull() {
        return "((" + fi + " <= ?) OR (" + fi + " IS NULL))";
    }
}
