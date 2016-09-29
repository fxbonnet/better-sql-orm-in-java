package net.archiloque.better_sql_orm_in_java.generator.bean;

import com.squareup.javapoet.ClassName;

/**
 *
 */
public abstract class AbstractModelInfo {

    public abstract ClassName getModelClass();

    public abstract  ClassName getSelectClass();
}
