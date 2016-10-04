package net.archiloque.bsoij.schema.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A model
 */
@XStreamAlias("model")
public class Model {

    @XStreamAsAttribute
    private String id;

    @XStreamImplicit
    @NotNull
    private List<Column> columns = new ArrayList<>();

    @NotNull
    private PrimaryKey primaryKey;

    @NotNull
    @XStreamAsAttribute
    private String tableName;

    @XStreamImplicit
    private List<ForeignKey> foreignKeys = new ArrayList<>();

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<ForeignKey> getForeignKeys() {
        if (foreignKeys == null) {
            foreignKeys = new ArrayList<>();
        }
        return foreignKeys;
    }

    public void setForeignKeys(List<ForeignKey> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void validate() {
        primaryKey.validate();
        for (Column column : columns) {
            column.validate();
        }
        for (ForeignKey foreignKey : foreignKeys) {
            foreignKey.validate();
        }
    }

    @Override
    public String toString() {
        return "Model{" +
                "id='" + id + '\'' +
                "tableName='" + tableName + '\'' +
                '}';
    }

    @NotNull
    public String getTableName() {
        return tableName;
    }

    public void setTableName(@NotNull String tableName) {
        this.tableName = tableName;
    }
}
