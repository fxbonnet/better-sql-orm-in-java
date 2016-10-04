package net.archiloque.bsoij.base_classes;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * All kinds of criteria.
 */
@SuppressWarnings("unused")
public class Criteria<T> {

    @NotNull
    private final ColumnType columnType;

    private final boolean nullable;

    @NotNull
    private final String type;

    @NotNull
    private final Object[] parameters;

    private Criteria(
            @NotNull ColumnType columnType,
            boolean nullable,
            @NotNull String type,
            @NotNull Object... parameters) {
        this.columnType = columnType;
        this.nullable = nullable;
        this.type = type;
        this.parameters = parameters;
    }

    @NotNull
    public ColumnType getColumnType() {
        return columnType;
    }

    public boolean isNullable() {
        return nullable;
    }

    @NotNull
    public String getType() {
        return type;
    }

    @NotNull
    public Object[] getParameters() {
        return parameters;
    }


    public static class IntegerCriteria extends Criteria<Integer> {

        public enum Type {
            EQUALS,
            NOT_EQUALS,
            STRICTLY_LESS,
            LESS_OR_EQUALS,
            STRICTLY_MORE,
            MORE_OR_EQUALS
        }


        private IntegerCriteria(String type, Object... parameters) {
            super(ColumnType.Integer, false, type, parameters);
        }
    }

    @NotNull
    public static IntegerCriteria integerEquals(int value) {
        return new IntegerCriteria(IntegerCriteria.Type.EQUALS.name(), value);
    }

    @NotNull
    public static IntegerCriteria integerNotEquals(int value) {
        return new IntegerCriteria(IntegerCriteria.Type.NOT_EQUALS.name(), value);
    }

    @NotNull
    public static IntegerCriteria integerStrictlyLess(int value) {
        return new IntegerCriteria(IntegerCriteria.Type.STRICTLY_LESS.name(), value);
    }

    @NotNull
    public static IntegerCriteria integerLessOrEquals(int value) {
        return new IntegerCriteria(IntegerCriteria.Type.LESS_OR_EQUALS.name(), value);
    }

    @NotNull
    public static IntegerCriteria integerStrictlyMore(int value) {
        return new IntegerCriteria(IntegerCriteria.Type.STRICTLY_MORE.name(), value);
    }

    @NotNull
    public static IntegerCriteria integerMoreOrEquals(int value) {
        return new IntegerCriteria(IntegerCriteria.Type.MORE_OR_EQUALS.name(), value);
    }

    public static class NullableIntegerCriteria extends Criteria<Integer> {

        public enum Type {
            IS_NULL,
            IS_NOT_NULL,
            EQUALS_OR_NULL,
            NOT_EQUALS_OR_NULL,
            STRICTLY_LESS_OR_NULL,
            LESS_OR_EQUALS_OR_NULL,
            STRICTLY_MORE_OR_NULL,
            MORE_OR_EQUALS_OR_NULL;
        }

        private NullableIntegerCriteria(String type, Object... parameters) {
            super(ColumnType.Integer, true, type, parameters);
        }
    }

    @NotNull
    public static NullableIntegerCriteria integerIsNull() {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.IS_NULL.name());
    }

    @NotNull
    public static NullableIntegerCriteria integerIsNotNull() {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.IS_NOT_NULL.name());
    }

    @NotNull
    public static NullableIntegerCriteria integerEqualsOrNull(int value) {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.EQUALS_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableIntegerCriteria integerNotEqualsOrNull(int value) {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.NOT_EQUALS_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableIntegerCriteria integerStrictlyLessOrNull(int value) {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.STRICTLY_LESS_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableIntegerCriteria integerLessOrEqualsOrNull(int value) {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.LESS_OR_EQUALS_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableIntegerCriteria integerStrictlyMoreOrNull(int value) {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.STRICTLY_MORE_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableIntegerCriteria integerMoreOrEqualsOrNull(int value) {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.MORE_OR_EQUALS_OR_NULL.name(), value);
    }

    public static final class StringCriteria extends Criteria<String> {

        public enum Type {
            NOT_EQUALS,
            EQUALS
        }

        private StringCriteria(String type, Object... parameters) {
            super(ColumnType.String, false, type, parameters);
        }
    }

    @NotNull
    public static StringCriteria stringEquals(String value) {
        return new StringCriteria(StringCriteria.Type.EQUALS.name(), value);
    }

    @NotNull
    public static StringCriteria stringNotEquals(String value) {
        return new StringCriteria(StringCriteria.Type.NOT_EQUALS.name(), value);
    }

    public static final class NullableStringCriteria extends Criteria<String> {

        public enum Type {
            EQUALS_OR_NULL,
            NOT_EQUALS_OR_NULL,
            IS_NULL,
            IS_NOT_NULL;
        }

        private NullableStringCriteria(String type, Object... parameters) {
            super(ColumnType.String, true, type, parameters);
        }
    }

    @NotNull
    public static NullableStringCriteria stringEqualsOrNull(String value) {
        return new NullableStringCriteria(NullableStringCriteria.Type.EQUALS_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableStringCriteria stringNotEqualsOrNull(String value) {
        return new NullableStringCriteria(NullableStringCriteria.Type.NOT_EQUALS_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableStringCriteria stringIsNotNull() {
        return new NullableStringCriteria(NullableStringCriteria.Type.IS_NULL.name());
    }

    @NotNull
    public static NullableStringCriteria stringIsNull() {
        return new NullableStringCriteria(NullableStringCriteria.Type.IS_NOT_NULL.name());
    }

    public static class DateCriteria extends Criteria<Date> {

        public enum Type {
            BEFORE,
            AFTER,
            EQUALS,
            NOT_EQUALS
        }


        private DateCriteria(String type, Object... parameters) {
            super(ColumnType.Date, false, type, parameters);
        }
    }

    @NotNull
    public static DateCriteria dateBefore(Date value) {
        return new DateCriteria(DateCriteria.Type.BEFORE.name(), value);
    }

    @NotNull
    public static DateCriteria dateAfter(Date value) {
        return new DateCriteria(DateCriteria.Type.AFTER.name(), value);
    }

    @NotNull
    public static DateCriteria dateEquals(Date value) {
        return new DateCriteria(DateCriteria.Type.EQUALS.name(), value);
    }

    @NotNull
    public static DateCriteria dateNotEquals(Date value) {
        return new DateCriteria(DateCriteria.Type.NOT_EQUALS.name(), value);
    }

    public static final class NullableDateCriteria extends Criteria<Date> {

        public enum Type {
            EQUALS_OR_NULL,
            NOT_EQUALS_OR_NULL,
            BEFORE_OR_NULL,
            AFTER_OR_NULL,
            IS_NULL,
            IS_NOT_NULL;
        }

        private NullableDateCriteria(String type, Object... parameters) {
            super(ColumnType.Date, true, type, parameters);
        }
    }

    @NotNull
    public static NullableDateCriteria dateEqualsOrNull(Date value) {
        return new NullableDateCriteria(NullableDateCriteria.Type.EQUALS_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableDateCriteria dateNotEqualsOrNull(Date value) {
        return new NullableDateCriteria(NullableDateCriteria.Type.NOT_EQUALS_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableDateCriteria dateBeforeOrNull(Date value) {
        return new NullableDateCriteria(NullableDateCriteria.Type.BEFORE_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableDateCriteria dateAfterOrNull(Date value) {
        return new NullableDateCriteria(NullableDateCriteria.Type.BEFORE_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableDateCriteria dateIsNotNull() {
        return new NullableDateCriteria(NullableDateCriteria.Type.IS_NULL.name());
    }

    @NotNull
    public static NullableDateCriteria dateIsNull() {
        return new NullableDateCriteria(NullableDateCriteria.Type.IS_NOT_NULL.name());
    }

    public static class LongCriteria extends Criteria<Long> {

        public enum Type {
            EQUALS,
            NOT_EQUALS,
            STRICTLY_LESS,
            LESS_OR_EQUALS,
            STRICTLY_MORE,
            MORE_OR_EQUALS
        }


        private LongCriteria(String type, Object... parameters) {
            super(ColumnType.Long, false, type, parameters);
        }
    }

    @NotNull
    public static LongCriteria longEquals(long value) {
        return new LongCriteria(LongCriteria.Type.EQUALS.name(), value);
    }

    @NotNull
    public static LongCriteria longNotEquals(long value) {
        return new LongCriteria(LongCriteria.Type.NOT_EQUALS.name(), value);
    }

    @NotNull
    public static LongCriteria longStrictlyLess(long value) {
        return new LongCriteria(LongCriteria.Type.STRICTLY_LESS.name(), value);
    }

    @NotNull
    public static LongCriteria longLessOrEquals(long value) {
        return new LongCriteria(LongCriteria.Type.LESS_OR_EQUALS.name(), value);
    }

    @NotNull
    public static LongCriteria longStrictlyMore(long value) {
        return new LongCriteria(LongCriteria.Type.STRICTLY_MORE.name(), value);
    }

    @NotNull
    public static LongCriteria longMoreOrEquals(long value) {
        return new LongCriteria(LongCriteria.Type.MORE_OR_EQUALS.name(), value);
    }

    public static class NullableLongCriteria extends Criteria<Long> {

        public enum Type {
            IS_NULL,
            IS_NOT_NULL,
            EQUALS_OR_NULL,
            NOT_EQUALS_OR_NULL,
            STRICTLY_LESS_OR_NULL,
            LESS_OR_EQUALS_OR_NULL,
            STRICTLY_MORE_OR_NULL,
            MORE_OR_EQUALS_OR_NULL;
        }

        private NullableLongCriteria(String type, Object... parameters) {
            super(ColumnType.Long, true, type, parameters);
        }
    }

    @NotNull
    public static NullableLongCriteria longIsNull() {
        return new NullableLongCriteria(NullableLongCriteria.Type.IS_NULL.name());
    }

    @NotNull
    public static NullableLongCriteria longIsNotNull() {
        return new NullableLongCriteria(NullableLongCriteria.Type.IS_NOT_NULL.name());
    }

    @NotNull
    public static NullableLongCriteria longEqualsOrNull(long value) {
        return new NullableLongCriteria(NullableLongCriteria.Type.EQUALS_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableLongCriteria longNotEqualsOrNull(long value) {
        return new NullableLongCriteria(NullableLongCriteria.Type.NOT_EQUALS_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableLongCriteria longStrictlyLessOrNull(long value) {
        return new NullableLongCriteria(NullableLongCriteria.Type.STRICTLY_LESS_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableLongCriteria longLessOrEqualsOrNull(long value) {
        return new NullableLongCriteria(NullableLongCriteria.Type.LESS_OR_EQUALS_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableLongCriteria longStrictlyMoreOrNull(long value) {
        return new NullableLongCriteria(NullableLongCriteria.Type.STRICTLY_MORE_OR_NULL.name(), value);
    }

    @NotNull
    public static NullableLongCriteria longMoreOrEqualsOrNull(long value) {
        return new NullableLongCriteria(NullableLongCriteria.Type.MORE_OR_EQUALS_OR_NULL.name(), value);
    }


}
