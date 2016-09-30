package net.archiloque.bsoij.schema.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * A foreign key.
 */
@XStreamAlias("foreignKey")
public class ForeignKey {

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    private String reverseName;

    @XStreamAsAttribute
    private String column;

    @XStreamAsAttribute
    private String references;

    @XStreamAsAttribute
    private boolean nullable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public void validate() {
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public void setReverseName(String reverseName) {
        this.reverseName = reverseName;
    }

    public String getReverseName() {
        return reverseName;
    }
}
