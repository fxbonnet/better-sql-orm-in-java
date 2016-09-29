package net.archiloque.better_sql_orm_in_java.generator.bean;

import net.archiloque.better_sql_orm_in_java.generator.InvalidSchemaException;
import net.archiloque.better_sql_orm_in_java.schema.bean.Model;
import net.archiloque.better_sql_orm_in_java.schema.bean.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Info for generation
 */
public final class SchemaInfo {

    private final Schema schema;
    private final String modelPackage;
    private final String selectPackage;
    private final Map<String, ModelInfo> modelInfoMap = new HashMap<>();

    public SchemaInfo(Schema schema) {
        this.schema = schema;
        modelPackage = schema.getTargetPackage() + ".model";
        selectPackage = schema.getTargetPackage() + ".select";
    }

    public void process() throws InvalidSchemaException {
        for (Model model : schema.getModels()) {
            ModelInfo modelInfo = new ModelInfo(model, this);
            modelInfoMap.put(model.getId(), modelInfo);
        }
        for (ModelInfo modelInfo : modelInfoMap.values()) {
            modelInfo.processSecondPass();
        }
    }

    public Map<String, ModelInfo> getModelInfoMap() {
        return modelInfoMap;
    }

    public String getSelectPackage() {
        return selectPackage;
    }

    public String getModelPackage() {
        return modelPackage;
    }

}
