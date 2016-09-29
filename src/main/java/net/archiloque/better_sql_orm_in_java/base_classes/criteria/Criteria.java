package net.archiloque.better_sql_orm_in_java.base_classes.criteria;

import java.util.Date;

/**
 *
 */
public class Criteria<T> {

    private final String type;

    private final Object[] parameters;

    private Criteria(String type, Object... parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public String getType() {
        return type;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public static class IntegerCriteria extends Criteria<Integer> {

        public enum Type {
            EQUALS,
            STRICTLY_LESS,
            LESS_OR_EQUALS,
            STRICTLY_MORE,
            MORE_OR_EQUALS
        }


        private IntegerCriteria(String type, Object... parameters) {
            super(type, parameters);
        }
    }

    public static IntegerCriteria integerEquals(int value) {
        return new IntegerCriteria(IntegerCriteria.Type.EQUALS.name(), value);
    }

    public static IntegerCriteria integerStrictlyLess(int value) {
        return new IntegerCriteria(IntegerCriteria.Type.STRICTLY_LESS.name(), value);
    }

    public static IntegerCriteria integerLessOrEquals(int value) {
        return new IntegerCriteria(IntegerCriteria.Type.LESS_OR_EQUALS.name(), value);
    }

    public static IntegerCriteria StrictlyMore(int value) {
        return new IntegerCriteria(IntegerCriteria.Type.STRICTLY_MORE.name(), value);
    }

    public static IntegerCriteria integerMoreOrEquals(int value) {
        return new IntegerCriteria(IntegerCriteria.Type.MORE_OR_EQUALS.name(), value);
    }

    public static class NullableIntegerCriteria extends Criteria<Integer> {

        public enum Type {
            IS_NULL,
            IS_NOT_NULL,
            EQUALS_OR_NULL,
            STRICTLY_LESS_OR_NULL,
            LESS_OR_EQUALS_OR_NULL,
            STRICTLY_MORE_OR_NULL,
            MORE_OR_EQUALS_OR_NULL;
        }

        private NullableIntegerCriteria(String type, Object... parameters) {
            super(type, parameters);
        }
    }

    public static NullableIntegerCriteria integerIsNull() {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.IS_NULL.name());
    }

    public static NullableIntegerCriteria integerIsNotNull() {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.IS_NOT_NULL.name());
    }

    public static NullableIntegerCriteria integerEqualsOrNull(int value) {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.EQUALS_OR_NULL.name(), value);
    }

    public static NullableIntegerCriteria integerStrictlyLessOrNull(int value) {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.STRICTLY_LESS_OR_NULL.name(), value);
    }

    public static NullableIntegerCriteria integerLessOrEqualsOrNull(int value) {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.LESS_OR_EQUALS_OR_NULL.name(), value);
    }

    public static NullableIntegerCriteria StrictlyMoreOrNull(int value) {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.STRICTLY_MORE_OR_NULL.name(), value);
    }

    public static NullableIntegerCriteria integerMoreOrEqualsOrNull(int value) {
        return new NullableIntegerCriteria(NullableIntegerCriteria.Type.MORE_OR_EQUALS_OR_NULL.name(), value);
    }

    public static final class StringCriteria extends Criteria<String> {

        public enum Type {
            EQUALS
        }

        private StringCriteria(String type, Object... parameters) {
            super(type, parameters);
        }
    }

    public static StringCriteria stringEquals(String value) {
        return new StringCriteria(StringCriteria.Type.EQUALS.name(), value);
    }

    public static final class NullableStringCriteria extends Criteria<String> {

        public enum Type {
            EQUALS_OR_NULL,
            IS_NULL,
            IS_NOT_NULL;
        }

        private NullableStringCriteria(String type, Object... parameters) {
            super(type, parameters);
        }
    }

    public static NullableStringCriteria stringEqualsOrNull(String value) {
        return new NullableStringCriteria(NullableStringCriteria.Type.EQUALS_OR_NULL.name(), value);
    }

    public static NullableStringCriteria stringIsNotNull(String value) {
        return new NullableStringCriteria(NullableStringCriteria.Type.IS_NULL.name(), value);
    }

    public static NullableStringCriteria stringIsNull(String value) {
        return new NullableStringCriteria(NullableStringCriteria.Type.IS_NOT_NULL.name(), value);
    }
    
    public static class DateCriteria extends Criteria<Date> {

        public enum Type {
            BEFORE,
            AFTER
        }


        private DateCriteria(String type, Object... parameters) {
            super(type, parameters);
        }
    }

    public static DateCriteria dateBefore(Date value) {
        return new DateCriteria(DateCriteria.Type.BEFORE.name(), value);
    }

    public static DateCriteria dateAfter(Date value) {
        return new DateCriteria(DateCriteria.Type.AFTER.name(), value);
    }

    public static final class NullableDateCriteria extends Criteria<Date> {

        public enum Type {
            BEFORE_OR_NULL,
            AFTER_OR_NULL,
            IS_NULL,
            IS_NOT_NULL;
        }

        private NullableDateCriteria(String type, Object... parameters) {
            super(type, parameters);
        }
    }

    public static NullableDateCriteria dateBeforeOrNull(Date value) {
        return new NullableDateCriteria(NullableDateCriteria.Type.BEFORE_OR_NULL.name(), value);
    }

    public static NullableDateCriteria dateAfterOrNull(Date value) {
        return new NullableDateCriteria(NullableDateCriteria.Type.BEFORE_OR_NULL.name(), value);
    }

    public static NullableDateCriteria dateIsNotNull(Date value) {
        return new NullableDateCriteria(NullableDateCriteria.Type.IS_NULL.name(), value);
    }

    public static NullableDateCriteria dateIsNull(Date value) {
        return new NullableDateCriteria(NullableDateCriteria.Type.IS_NOT_NULL.name(), value);
    }


}
