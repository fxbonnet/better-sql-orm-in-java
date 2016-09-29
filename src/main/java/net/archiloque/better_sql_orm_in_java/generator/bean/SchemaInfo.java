package net.archiloque.better_sql_orm_in_java.generator.bean;

import net.archiloque.better_sql_orm_in_java.generator.InvalidSchemaException;
import net.archiloque.better_sql_orm_in_java.schema.bean.Model;
import net.archiloque.better_sql_orm_in_java.schema.bean.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Info for generation
 */
public final class SchemaInfo {

    @NotNull
    private final Schema schema;

    @NotNull
    private final String modelPackage;

    @NotNull
    private final String selectPackage;

    @NotNull
    private final Map<String, ModelInfo> modelInfoMap = new HashMap<>();

    @NotNull
    private final List<MultipleModelInfo> multipleModelsInfos = new ArrayList<>();

    public SchemaInfo(@NotNull Schema schema) {
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

        // @TODO : simplistic implementation
        for (ModelInfo modelInfo : modelInfoMap.values()) {
            for (ForeignKeyInfo foreignKeyInfo : modelInfo.getForeignKeyInfos()) {
                ModelInfo[] modelInfos = {foreignKeyInfo.getSourceModel(), foreignKeyInfo.getTargetModel()};
                Arrays.sort(modelInfos, (o1, o2) -> o1.getModel().getId().compareTo(o2.getModel().getId()));
                multipleModelsInfos.add(new MultipleModelInfo(modelInfos, this));
            }
        }
    }

    @NotNull
    public List<MultipleModelInfo> getMultipleModelsInfos() {
        return multipleModelsInfos;
    }

    @NotNull
    public Map<String, ModelInfo> getModelInfoMap() {
        return modelInfoMap;
    }

    @NotNull
    public String getSelectPackage() {
        return selectPackage;
    }

    public String getModelPackage() {
        return modelPackage;
    }

}
