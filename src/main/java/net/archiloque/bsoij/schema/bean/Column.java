package net.archiloque.bsoij.schema.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.enums.EnumToStringConverter;
import org.jetbrains.annotations.NotNull;

/**
 * A column
 */
@XStreamAlias("column")
public class Column {

    @XStreamAsAttribute
    @NotNull
    private String name;

    @XStreamAsAttribute
    @NotNull
    private ColumnType type;

    @XStreamAsAttribute
    @NotNull
    private Boolean nullable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public void validate() {

    }

    @XStreamConverter(EnumToStringConverter.class)
    public enum ColumnType {
        String, Date, Integer
    }
}
