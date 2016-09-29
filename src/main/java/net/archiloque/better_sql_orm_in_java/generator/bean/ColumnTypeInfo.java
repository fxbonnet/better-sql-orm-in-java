package net.archiloque.better_sql_orm_in_java.generator.bean;

import com.squareup.javapoet.ClassName;
import net.archiloque.better_sql_orm_in_java.base_classes.criteria.Criteria;
import net.archiloque.better_sql_orm_in_java.base_classes.field.DateField;
import net.archiloque.better_sql_orm_in_java.base_classes.field.Field;
import net.archiloque.better_sql_orm_in_java.base_classes.field.IntegerField;
import net.archiloque.better_sql_orm_in_java.base_classes.field.NullableDateField;
import net.archiloque.better_sql_orm_in_java.base_classes.field.NullableIntegerField;
import net.archiloque.better_sql_orm_in_java.base_classes.field.NullableStringField;
import net.archiloque.better_sql_orm_in_java.base_classes.field.StringField;
import net.archiloque.better_sql_orm_in_java.schema.bean.Column;

import java.util.Date;

/**
 * A type for a column.
 */
public final class ColumnTypeInfo {

    private final SchemaInfo schemaInfo;

    private final ModelInfo modelInfo;

    private final Column.ColumnType columnType;

    private final boolean nullable;

    public ColumnTypeInfo(SchemaInfo schemaInfo, ModelInfo modelInfo, Column.ColumnType columnType, boolean nullable) {
        this.schemaInfo = schemaInfo;
        this.modelInfo = modelInfo;
        this.columnType = columnType;
        this.nullable = nullable;
    }

    public Column.ColumnType getColumnType() {
        return columnType;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Class<? extends Field> getFieldType() {
        return getFieldType(columnType, nullable);
    }

    public ClassName getShortClassName(){
        return ClassName.get("", modelInfo.getModelClassName() + getFieldType().getSimpleName());
    }

    public ClassName getClassName(){
        return getClassName(schemaInfo, modelInfo, getFieldType());
    }

    public static ClassName getClassName(SchemaInfo schemaInfo, ModelInfo modelInfo, Class<? extends Field> fieldType){
        return ClassName.get(schemaInfo.getModelPackage(), modelInfo.getModelClassName() + fieldType.getSimpleName());
    }

    public static Class<? extends Field> getFieldType(Column.ColumnType columnType, boolean nullable) {
        switch (columnType) {
            case String:
                return nullable ? NullableStringField.class : StringField.class;
            case Date:
                return nullable ? NullableDateField.class : DateField.class;
            case Integer:
                return nullable ? NullableIntegerField.class : IntegerField.class;
            default:
                throw new RuntimeException("Unknown type [" + columnType + "]");
        }
    }

    public static Class<? extends Criteria> getCriteria(Column.ColumnType columnType, boolean nullable) {
        switch (columnType) {
            case String:
                return nullable ? Criteria.NullableStringCriteria.class : Criteria.StringCriteria.class;
            case Date:
                return nullable ? Criteria.NullableDateCriteria.class : Criteria.DateCriteria.class;
            case Integer:
                return nullable ? Criteria.NullableIntegerCriteria.class : Criteria.IntegerCriteria.class;
            default:
                throw new RuntimeException("Unknown type [" + columnType + "]");
        }
    }

    public static Class getValueClass(Column.ColumnType columnType) {
        switch (columnType) {
            case String:
                return String.class;
            case Date:
                return Date.class;
            case Integer:
                return Integer.class;
            default:
                throw new RuntimeException("Unknown type [" + columnType + "]");
        }
    }

}
