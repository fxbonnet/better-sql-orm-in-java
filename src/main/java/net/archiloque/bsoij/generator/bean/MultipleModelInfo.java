package net.archiloque.bsoij.generator.bean;

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

    @NotNull
    private final ClassName shortSelectClass;

    public MultipleModelInfo(@NotNull SimpleModelInfo[] modelInfos, @NotNull SchemaInfo schemaInfo) {
        this.modelInfos = modelInfos;

        String baseClass = Arrays.
                stream(modelInfos).
                map(SimpleModelInfo::getBaseClassName).
                collect(Collectors.joining(""));
        modelClass = ClassName.get(schemaInfo.getModelPackage(), baseClass + "Model" );
        String selectClassName = baseClass + "Select";
        selectClass = ClassName.get(schemaInfo.getSelectPackage(), selectClassName);
        shortSelectClass = ClassName.get("", selectClassName);
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

    @Override
    @NotNull
    public ClassName getShortSelectClass() {
        return shortSelectClass;
    }
}
