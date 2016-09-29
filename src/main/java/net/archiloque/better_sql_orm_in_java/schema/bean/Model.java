package net.archiloque.better_sql_orm_in_java.schema.bean;

import com.sun.istack.internal.NotNull;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

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
        if(foreignKeys == null) {
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
}
