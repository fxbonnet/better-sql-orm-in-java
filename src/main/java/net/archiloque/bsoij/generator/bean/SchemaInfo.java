package net.archiloque.bsoij.generator.bean;

import net.archiloque.bsoij.generator.InvalidSchemaException;
import net.archiloque.bsoij.schema.bean.Model;
import net.archiloque.bsoij.schema.bean.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final Map<String, SimpleModelInfo> modelInfoMap = new HashMap<>();

    @NotNull
    private final List<MultipleModelInfo> multipleModelsInfos = new ArrayList<>();

    public SchemaInfo(@NotNull Schema schema) {
        this.schema = schema;
        modelPackage = schema.getTargetPackage() + ".model";
        selectPackage = schema.getTargetPackage() + ".select";
    }

    public void process() throws InvalidSchemaException {
        for (Model model : schema.getModels()) {
            SimpleModelInfo modelInfo = new SimpleModelInfo(model, this);
            modelInfoMap.put(model.getId(), modelInfo);
        }
        for (SimpleModelInfo modelInfo : modelInfoMap.values()) {
            modelInfo.processSecondPass();
        }

        // @TODO : simplistic implementation
        for (SimpleModelInfo modelInfo : modelInfoMap.values()) {
            for (ForeignKeyInfo foreignKeyInfo : modelInfo.getForeignKeyInfos()) {
                SimpleModelInfo[] modelInfos = {foreignKeyInfo.getSourceModel(), foreignKeyInfo.getTargetModel()};
                Arrays.sort(modelInfos);
                multipleModelsInfos.add(new MultipleModelInfo(modelInfos, this, new ForeignKeyInfo[]{foreignKeyInfo}));
            }
        }
    }

    public MultipleModelInfo getMultipleModelInfo(SimpleModelInfo[] modelInfos) {
        Arrays.sort(modelInfos);
        Optional<MultipleModelInfo> any = multipleModelsInfos.
                stream().
                filter(multipleModelInfo -> Arrays.equals(multipleModelInfo.getModelInfos(), modelInfos)).
                findAny();
        if (!any.isPresent()) {
            throw new RuntimeException("Multiple model info not found for " + Arrays.toString(modelInfos));
        } else {
            return any.get();
        }
    }

    ;

    @NotNull
    public List<MultipleModelInfo> getMultipleModelsInfos() {
        return multipleModelsInfos;
    }

    @NotNull
    public Map<String, SimpleModelInfo> getModelInfoMap() {
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
