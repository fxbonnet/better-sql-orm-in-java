package net.archiloque.better_sql_orm_in_java.generator.bean;

import net.archiloque.better_sql_orm_in_java.schema.bean.ForeignKey;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class ForeignKeyInfo {

    @NotNull
    private final ForeignKey foreignKey;

    @NotNull
    private final ModelInfo sourceModel;

    @NotNull
    private final ModelInfo targetModel;

    @NotNull
    private final ColumnInfo columnInfo;

    public ForeignKeyInfo(
            @NotNull ForeignKey foreignKey,
            @NotNull ModelInfo containReferenced,
            @NotNull ModelInfo isReferenced,
            @NotNull ColumnInfo columnInfo) {
        this.foreignKey = foreignKey;
        this.sourceModel = containReferenced;
        this.targetModel = isReferenced;
        this.columnInfo = columnInfo;
    }

    @NotNull
    public ForeignKey getForeignKey() {
        return foreignKey;
    }

    @NotNull
    public ModelInfo getSourceModel() {
        return sourceModel;
    }

    @NotNull
    public ModelInfo getTargetModel() {
        return targetModel;
    }

    @NotNull
    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }
}
