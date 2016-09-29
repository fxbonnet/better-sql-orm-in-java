package net.archiloque.better_sql_orm_in_java.generator.bean;

import com.squareup.javapoet.ClassName;
import net.archiloque.better_sql_orm_in_java.base_classes.field.Field;
import net.archiloque.better_sql_orm_in_java.schema.bean.Column;
import net.archiloque.better_sql_orm_in_java.schema.bean.Model;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Info for a model
 */
public final class ModelInfo {

    private final Model model;

    private final String baseClassName;

    private final String modelClassName;

    private final String selectClassName;

    private final ClassName modelClass;

    private final ClassName selectClass;

    private final ClassName shortSelectClass;

    private final List<ColumnInfo> columnInfos = new ArrayList<>();

    private final List<ColumnTypeInfo> columnTypeInfos = new ArrayList<>();

    private final List<ColumnTypeAndNullable> columnsTypes;

    public ModelInfo(Model model, GeneratorInfo generatorInfo) {
        this.model = model;
        baseClassName = WordUtils.capitalize(model.getId());
        modelClassName = baseClassName + "Model";
        selectClassName = baseClassName + "Select";
        modelClass = ClassName.get(generatorInfo.getModelPackage(), modelClassName);
        selectClass = ClassName.get(generatorInfo.getSelectPackage(), selectClassName);
        shortSelectClass = ClassName.get("", selectClassName);

        Map<Column.ColumnType, Boolean> columnsTypesMap = new HashMap<>();
        model.getColumns().forEach(column -> {
            Column.ColumnType columnType = column.getType();
            if (columnsTypesMap.containsKey(columnType)) {
                if (column.isNullable()) {
                    columnsTypesMap.put(columnType, true);
                }
            } else {
                columnsTypesMap.put(columnType, column.isNullable());
            }
        });

        model.getColumns().forEach(column -> {
            columnInfos.add(
                    new ColumnInfo(
                            column, new
                            ColumnTypeInfo(generatorInfo, this, column.getType(), column.isNullable())
                    ));
        });

        columnsTypesMap.forEach((columnType, nullable) -> {
            columnTypeInfos.add(new ColumnTypeInfo(generatorInfo, this, columnType, nullable));
        });

        columnsTypes = columnInfos.stream().map(columnInfo -> {
            ColumnTypeInfo columnTypeInfo = columnInfo.getColumnTypeInfo();
            return new ColumnTypeAndNullable(columnTypeInfo.getColumnType(), columnTypeInfo.isNullable());
        }).distinct().collect(Collectors.toList());

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

    public List<ColumnTypeInfo> getColumnTypeInfos() {
        return columnTypeInfos;
    }

    public ClassName getShortSelectClass() {
        return shortSelectClass;
    }

    public List<ColumnTypeAndNullable> getColumnsTypes() {
        return columnsTypes;
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
