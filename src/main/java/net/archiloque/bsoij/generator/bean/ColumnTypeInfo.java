package net.archiloque.bsoij.generator.bean;

import com.squareup.javapoet.ClassName;
import net.archiloque.bsoij.base_classes.ColumnType;
import net.archiloque.bsoij.base_classes.Criteria;
import net.archiloque.bsoij.base_classes.field.DateField;
import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.base_classes.field.IntegerField;
import net.archiloque.bsoij.base_classes.field.LongField;
import net.archiloque.bsoij.base_classes.field.NullableDateField;
import net.archiloque.bsoij.base_classes.field.NullableIntegerField;
import net.archiloque.bsoij.base_classes.field.NullableLongField;
import net.archiloque.bsoij.base_classes.field.NullableStringField;
import net.archiloque.bsoij.base_classes.field.StringField;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * A type for a column.
 */
public final class ColumnTypeInfo {

    @NotNull
    private final SchemaInfo schemaInfo;

    @NotNull
    private final SimpleModelInfo modelInfo;

    @NotNull
    private final ColumnType columnType;

    private final boolean nullable;

    public ColumnTypeInfo(@NotNull SchemaInfo schemaInfo, @NotNull SimpleModelInfo modelInfo, @NotNull ColumnType columnType, boolean nullable) {
        this.schemaInfo = schemaInfo;
        this.modelInfo = modelInfo;
        this.columnType = columnType;
        this.nullable = nullable;
    }

    @NotNull
    public ColumnType getColumnType() {
        return columnType;
    }

    public boolean isNullable() {
        return nullable;
    }

    @NotNull
    public Class<? extends Field> getFieldType() {
        return getFieldType(columnType, nullable);
    }

    @NotNull
    public ClassName getShortClassName() {
        return ClassName.get("", modelInfo.getModelClass().simpleName() + getFieldType().getSimpleName());
    }

    @NotNull
    public ClassName getClassName() {
        return getClassName(schemaInfo, modelInfo, getFieldType());
    }

    @NotNull
    public static ClassName getClassName(SchemaInfo schemaInfo, SimpleModelInfo modelInfo, Class<? extends Field> fieldType) {
        return ClassName.get(schemaInfo.getModelPackage(), modelInfo.getModelClass().simpleName() + fieldType.getSimpleName());
    }

    @NotNull
    public static Class<? extends Field> getFieldType(ColumnType columnType, boolean nullable) {
        switch (columnType) {
            case String:
                return nullable ? NullableStringField.class : StringField.class;
            case Date:
                return nullable ? NullableDateField.class : DateField.class;
            case Integer:
                return nullable ? NullableIntegerField.class : IntegerField.class;
            case Long:
                return nullable ? NullableLongField.class : LongField.class;
            default:
                throw new RuntimeException("Unknown type [" + columnType + "]");
        }
    }

    @NotNull
    public static Class<? extends Criteria> getCriteria(ColumnType columnType, boolean nullable) {
        switch (columnType) {
            case String:
                return nullable ? Criteria.NullableStringCriteria.class : Criteria.StringCriteria.class;
            case Date:
                return nullable ? Criteria.NullableDateCriteria.class : Criteria.DateCriteria.class;
            case Integer:
                return nullable ? Criteria.NullableIntegerCriteria.class : Criteria.IntegerCriteria.class;
            case Long:
                return nullable ? Criteria.NullableLongCriteria.class : Criteria.LongCriteria.class;
            default:
                throw new RuntimeException("Unknown type [" + columnType + "]");
        }
    }

    @NotNull
    public static String getCriteriaEquals(ColumnType columnType) {
        switch (columnType) {
            case String:
                return "stringEquals";
            case Date:
                return "dateEquals";
            case Integer:
                return "integerEquals";
            case Long:
                return "longEquals";
            default:
                throw new RuntimeException("Unknown type [" + columnType + "]");
        }
    }

    @NotNull
    public static Class getValueClass(ColumnType columnType) {
        switch (columnType) {
            case String:
                return String.class;
            case Date:
                return Date.class;
            case Integer:
                return Integer.class;
            case Long:
                return Long.class;
            default:
                throw new RuntimeException("Unknown type [" + columnType + "]");
        }
    }

}
