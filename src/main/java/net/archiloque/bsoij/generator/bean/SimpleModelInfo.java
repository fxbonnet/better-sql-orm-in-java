package net.archiloque.bsoij.generator.bean;

import com.squareup.javapoet.ClassName;
import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.generator.InvalidSchemaException;
import net.archiloque.bsoij.schema.bean.Column;
import net.archiloque.bsoij.schema.bean.ForeignKey;
import net.archiloque.bsoij.schema.bean.Model;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Info for a model
 */
public final class SimpleModelInfo extends AbstractModelInfo implements Comparable<SimpleModelInfo> {

    @NotNull
    private final Model model;

    @NotNull
    private final SchemaInfo schemaInfo;

    @NotNull
    private final String baseClassName;

    @NotNull
    private final ClassName modelClass;

    @NotNull
    private final ClassName selectClass;

    @NotNull
    private final ClassName shortSelectClass;

    @NotNull
    private final List<ColumnInfo> columnInfos = new ArrayList<>();

    @NotNull
    private final List<ColumnTypeAndNullable> columnsTypes;

    @NotNull
    private final List<ForeignKeyInfo> foreignKeyInfos = new ArrayList<>();

    @NotNull
    private final List<ForeignKeyInfo> foreignKeyedInfos = new ArrayList<>();

    @NotNull
    private PrimaryKeyInfo primaryKeyInfo;

    public SimpleModelInfo(@NotNull Model model, @NotNull SchemaInfo schemaInfo) {
        this.model = model;
        this.schemaInfo = schemaInfo;
        baseClassName = WordUtils.capitalize(model.getId());
        String selectClassName = baseClassName + "Select";
        modelClass = ClassName.get(schemaInfo.getModelPackage(), baseClassName + "Model");
        selectClass = ClassName.get(schemaInfo.getSelectPackage(), baseClassName + "Select");
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
        String primaryKeyColumnName = model.getPrimaryKey().getColumn();
        Optional<ColumnInfo> primaryKeyColumnInfo = columnInfos.
                stream().
                filter(ci -> ci.getColumn().getName().equals(primaryKeyColumnName)).
                findAny();
        if (!primaryKeyColumnInfo.isPresent()) {
            throw new InvalidSchemaException("Unknown column in primary key [" + primaryKeyColumnName + "] in model [" + model.getId() + "]");
        }
        primaryKeyInfo = new PrimaryKeyInfo(model.getPrimaryKey(), primaryKeyColumnInfo.get());


        for (ForeignKey foreignKey : model.getForeignKeys()) {
            SimpleModelInfo targetModelInfo = schemaInfo.getModelInfoMap().get(foreignKey.getReferences());
            if (targetModelInfo == null) {
                throw new InvalidSchemaException("Unknown reference [" + foreignKey.getReferences() + "] in model [" + model.getId() + "]");
            }
            String columnName = foreignKey.getColumn();
            Optional<ColumnInfo> foreignKeyColumnInfo = columnInfos.
                    stream().
                    filter(ci -> ci.getColumn().getName().equals(columnName)).
                    findAny();
            if (!foreignKeyColumnInfo.isPresent()) {
                throw new InvalidSchemaException("Unknown column in foreign key [" + foreignKey.getColumn() + "] in model [" + model.getId() + "]");
            }
            ForeignKeyInfo foreignKeyInfo = new ForeignKeyInfo(foreignKey, this, targetModelInfo, foreignKeyColumnInfo.get());
            foreignKeyInfos.add(foreignKeyInfo);
            targetModelInfo.addForeignKeyed(foreignKeyInfo);
        }
        ;
    }

    private void addForeignKeyed(@NotNull ForeignKeyInfo foreignKeyInfo) {
        foreignKeyedInfos.add(foreignKeyInfo);
    }

    @NotNull
    public List<ColumnInfo> getColumnInfos() {
        return columnInfos;
    }

    @NotNull
    public Model getModel() {
        return model;
    }

    @NotNull
    public String getBaseClassName() {
        return baseClassName;
    }

    @NotNull
    @Override
    public ClassName getModelClass() {
        return modelClass;
    }

    @NotNull
    @Override
    public ClassName getSelectClass() {
        return selectClass;
    }

    @NotNull
    @Override
    public ClassName getShortSelectClass() {
        return shortSelectClass;
    }

    @NotNull
    public List<ColumnTypeAndNullable> getColumnsTypes() {
        return columnsTypes;
    }

    @NotNull
    public List<ForeignKeyInfo> getForeignKeyInfos() {
        return foreignKeyInfos;
    }

    @Override
    public String toString() {
        return "SimpleModelInfo{" +
                "model=" + model +
                '}';
    }

    @NotNull
    public List<ForeignKeyInfo> getForeignKeyedInfos() {
        return foreignKeyedInfos;
    }

    @NotNull
    public PrimaryKeyInfo getPrimaryKeyInfo() {
        return primaryKeyInfo;
    }

    @Override
    public int compareTo(@NotNull SimpleModelInfo o) {
        return getModel().getId().compareTo(o.getModel().getId());
    }

    /**
     * A column type and if it's nullable.
     * Handy for deduplication.
     */
    public final static class ColumnTypeAndNullable {

        @NotNull
        private final Column.ColumnType columnType;

        private final boolean nullable;

        private ColumnTypeAndNullable(@NotNull Column.ColumnType columnType, boolean nullable) {
            this.columnType = columnType;
            this.nullable = nullable;
        }

        @NotNull
        public Column.ColumnType getColumnType() {
            return columnType;
        }

        public boolean isNullable() {
            return nullable;
        }

        @NotNull
        public Class<? extends Field> getFieldType() {
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
