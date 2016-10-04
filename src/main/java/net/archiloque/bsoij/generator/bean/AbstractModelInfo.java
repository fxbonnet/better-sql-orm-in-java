package net.archiloque.bsoij.generator.bean;

import com.squareup.javapoet.ClassName;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public abstract class AbstractModelInfo {

    @NotNull
    public abstract ClassName getModelClass();

    @NotNull
    public abstract ClassName getSelectClass();

    @NotNull
    public abstract ClassName getShortSelectClass();

    @NotNull
    public String getSelectParam() {
        return WordUtils.uncapitalize(getShortSelectClass().simpleName());
    }
}
