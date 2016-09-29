package net.archiloque.better_sql_orm_in_java.generator.bean;

import com.squareup.javapoet.ClassName;
import net.archiloque.better_sql_orm_in_java.base_classes.field.Field;
import net.archiloque.better_sql_orm_in_java.generator.InvalidSchemaException;
import net.archiloque.better_sql_orm_in_java.schema.bean.Column;
import net.archiloque.better_sql_orm_in_java.schema.bean.ForeignKey;
import net.archiloque.better_sql_orm_in_java.schema.bean.Model;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Info for a model
 */
public final class ModelInfo {

    private final Model model;

    private final SchemaInfo schemaInfo;

    private final String baseClassName;

    private final String modelClassName;

    private final String selectClassName;

    private final ClassName modelClass;

    private final ClassName selectClass;

    private final ClassName shortSelectClass;

    private final List<ColumnInfo> columnInfos = new ArrayList<>();

    private final List<ColumnTypeAndNullable> columnsTypes;

    private final List<ForeignKeyInfo> foreignKeyInfos = new ArrayList<>();

    private final List<ForeignKeyInfo> foreignKeyedInfos = new ArrayList<>();

    public ModelInfo(Model model, SchemaInfo schemaInfo) {
        this.model = model;
        this.schemaInfo = schemaInfo;
        baseClassName = WordUtils.capitalize(model.getId());
        modelClassName = baseClassName + "Model";
        selectClassName = baseClassName + "Select";
        modelClass = ClassName.get(schemaInfo.getModelPackage(), modelClassName);
        selectClass = ClassName.get(schemaInfo.getSelectPackage(), selectClassName);
        shortSelectClass = ClassName.get("", selectClassName);

        // info on columns
        model.getColumns().forEach(column -> {
            columnInfos.add(
                    new ColumnInfo(
                            column, new
                            ColumnTypeInfo(schemaInfo, this, column.getType(), column.isNullable())
                    ));
        });

        // info on referenced types
        columnsTypes = columnInfos.stream().map(columnInfo -> {
            ColumnTypeInfo columnTypeInfo = columnInfo.getColumnTypeInfo();
            return new ColumnTypeAndNullable(columnTypeInfo.getColumnType(), columnTypeInfo.isNullable());
        }).distinct().collect(Collectors.toList());

    }

    public void processSecondPass() throws InvalidSchemaException {
        for (ForeignKey foreignKey : model.getForeignKeys()) {
            ModelInfo targetModelInfo = schemaInfo.getModelInfoMap().get(foreignKey.getReferences());
            if(targetModelInfo == null) {
                throw new InvalidSchemaException("Unknown reference [" + foreignKey.getReferences() + "] in model [" + model.getId() + "]");
            }
            ForeignKeyInfo foreignKeyInfo = new ForeignKeyInfo(foreignKey, this, targetModelInfo);
            foreignKeyInfos.add(foreignKeyInfo);
            targetModelInfo.addForeignKeyed(foreignKeyInfo);
        };
    }

    private void addForeignKeyed(ForeignKeyInfo foreignKeyInfo) {
        foreignKeyedInfos.add(foreignKeyInfo);
    }

    public List<ColumnInfo> getColumnInfos() {
        return columnInfos;
    }

    public Model getModel() {
        return model;
    }

    public String getBaseClassName() {
        return baseClassName;
    }

    public String getModelClassName() {
        return modelClassName;
    }

    public String getSelectClassName() {
        return selectClassName;
    }

    public ClassName getModelClass() {
        return modelClass;
    }

    public ClassName getSelectClass() {
        return selectClass;
    }

    public ClassName getShortSelectClass() {
        return shortSelectClass;
    }

    public List<ColumnTypeAndNullable> getColumnsTypes() {
        return columnsTypes;
    }

    public List<ForeignKeyInfo> getForeignKeyInfos() {
        return foreignKeyInfos;
    }

    public List<ForeignKeyInfo> getForeignKeyedInfos() {
        return foreignKeyedInfos;
    }

    /**
     * A column type and if it's nullable.
     * Handy for deduplication.
     */
    public final static class ColumnTypeAndNullable {

        private final Column.ColumnType columnType;

        private final boolean nullable;

        private ColumnTypeAndNullable(Column.ColumnType columnType, boolean nullable) {
            this.columnType = columnType;
            this.nullable = nullable;
        }


        public Column.ColumnType getColumnType() {
            return columnType;
        }

        public boolean isNullable() {
            return nullable;
        }

        public Class<? extends Field> getFieldType(){
            return ColumnTypeInfo.getFieldType(columnType, nullable);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || (getClass() != o.getClass())) {
                return false;
            }
            ColumnTypeAndNullable that = (ColumnTypeAndNullable) o;
            return (nullable == that.nullable) && (columnType == that.columnType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(columnType, nullable);
        }
    }
}
