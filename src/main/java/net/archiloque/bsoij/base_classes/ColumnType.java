package net.archiloque.bsoij.base_classes;

import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.enums.EnumToStringConverter;

/**
 * A Column type
 */
@XStreamConverter(EnumToStringConverter.class)
public enum ColumnType {
    String, Date, Integer, Long
}
