package net.archiloque.bsoij.generator.bean;

import net.archiloque.bsoij.schema.bean.ForeignKey;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class ForeignKeyInfo {

    @NotNull
    private final ForeignKey foreignKey;

    @NotNull
    private final SimpleModelInfo sourceModel;

    @NotNull
    private final SimpleModelInfo targetModel;

    @NotNull
    private final ColumnInfo columnInfo;

    public ForeignKeyInfo(
            @NotNull ForeignKey foreignKey,
            @NotNull SimpleModelInfo containReferenced,
            @NotNull SimpleModelInfo isReferenced,
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
    public SimpleModelInfo getSourceModel() {
        return sourceModel;
    }

    @NotNull
    public SimpleModelInfo getTargetModel() {
        return targetModel;
    }

    @NotNull
    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }
}
