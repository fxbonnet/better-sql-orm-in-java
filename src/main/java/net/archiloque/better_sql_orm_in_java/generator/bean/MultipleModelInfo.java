package net.archiloque.better_sql_orm_in_java.generator.bean;

import com.squareup.javapoet.ClassName;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Infos for multiple models
 */
public class MultipleModelInfo {

    @NotNull
    private final ModelInfo[] modelInfos;

    @NotNull
    private final ClassName modelClass;


    public MultipleModelInfo(@NotNull ModelInfo[] modelInfos, @NotNull SchemaInfo schemaInfo) {
        this.modelInfos = modelInfos;

        String className = Arrays.
                stream(modelInfos).
                map(ModelInfo::getBaseClassName).
                collect(Collectors.joining("")) + "Model";
        modelClass = ClassName.get(schemaInfo.getModelPackage(), className + "Model");
    }

    @NotNull
    public ClassName getModelClass() {
        return modelClass;
    }

    @NotNull
    public ModelInfo[] getModelInfos() {
        return modelInfos;
    }
}
