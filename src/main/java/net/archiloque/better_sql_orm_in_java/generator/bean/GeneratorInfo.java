package net.archiloque.better_sql_orm_in_java.generator.bean;

import net.archiloque.better_sql_orm_in_java.schema.bean.Schema;

/**
 * Info for generation
 */
public final class GeneratorInfo {
    private final Schema schema;
    private final String modelPackage;
    private final String selectPackage;

    public GeneratorInfo(Schema schema) {
        this.schema = schema;
        modelPackage = schema.getTargetPackage() + ".model";
        selectPackage = schema.getTargetPackage() + ".select";
    }

    public String getSelectPackage() {
        return selectPackage;
    }

    public String getModelPackage() {
        return modelPackage;
    }

}
