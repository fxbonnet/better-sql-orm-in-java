package net.archiloque.better_sql_orm_in_java.generator.bean;

import net.archiloque.better_sql_orm_in_java.schema.bean.ForeignKey;

/**
 *
 */
public class ForeignKeyInfo {

    private final ForeignKey foreignKey;

    private final ModelInfo sourceModel;

    private final ModelInfo targetModel;

    public ForeignKeyInfo(ForeignKey foreignKey, ModelInfo containReferenced, ModelInfo isReferenced) {
        this.foreignKey = foreignKey;
        this.sourceModel = containReferenced;
        this.targetModel = isReferenced;
    }

    public ForeignKey getForeignKey() {
        return foreignKey;
    }

    public ModelInfo getSourceModel() {
        return sourceModel;
    }

    public ModelInfo getTargetModel() {
        return targetModel;
    }
}
