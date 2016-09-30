package net.archiloque.bsoij.generator.bean;

import net.archiloque.bsoij.schema.bean.PrimaryKey;

/**
 *
 */
public class PrimaryKeyInfo {

    private final PrimaryKey primaryKey;

    private final ColumnInfo columnInfo;

    public PrimaryKeyInfo(PrimaryKey primaryKey, ColumnInfo columnInfo) {
        this.primaryKey = primaryKey;
        this.columnInfo = columnInfo;
    }

    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }
}
