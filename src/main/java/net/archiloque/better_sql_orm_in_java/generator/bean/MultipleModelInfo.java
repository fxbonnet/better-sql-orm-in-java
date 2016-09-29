package net.archiloque.better_sql_orm_in_java.generator.bean;

import com.squareup.javapoet.ClassName;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Infos for multiple models
 */
public class MultipleModelInfo extends AbstractModelInfo {

    @NotNull
    private final SimpleModelInfo[] modelInfos;

    @NotNull
    private final ClassName modelClass;

    @NotNull
    private final ClassName selectClass;

    public MultipleModelInfo(@NotNull SimpleModelInfo[] modelInfos, @NotNull SchemaInfo schemaInfo) {
        this.modelInfos = modelInfos;

        String baseClass = Arrays.
                stream(modelInfos).
                map(SimpleModelInfo::getBaseClassName).
                collect(Collectors.joining(""));
        modelClass = ClassName.get(schemaInfo.getModelPackage(), baseClass + "Model" );
        selectClass = ClassName.get(schemaInfo.getSelectPackage(), baseClass + "Select" );
    }

    @NotNull
    @Override
    public ClassName getModelClass() {
        return modelClass;
    }

    @NotNull
    public SimpleModelInfo[] getModelInfos() {
        return modelInfos;
    }

    @NotNull
    @Override
    public ClassName getSelectClass() {
        return selectClass;
    }
}
