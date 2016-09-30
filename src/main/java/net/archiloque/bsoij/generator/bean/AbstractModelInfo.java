package net.archiloque.bsoij.generator.bean;

import com.squareup.javapoet.ClassName;

/**
 *
 */
public abstract class AbstractModelInfo {

    public abstract ClassName getModelClass();

    public abstract  ClassName getSelectClass();

    public abstract ClassName getShortSelectClass();
}
