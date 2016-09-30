package net.archiloque.bsoij.generator;

import com.squareup.javapoet.TypeSpec;
import net.archiloque.bsoij.generator.bean.MultipleModelInfo;
import net.archiloque.bsoij.generator.bean.SchemaInfo;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Generate code for multiple select
 */
public class MultipleSelectGenerator extends AbstractSelectGenerator {

    @NotNull
    private final MultipleModelInfo modelInfo;

    public MultipleSelectGenerator(
            @NotNull File basePath,
            @NotNull File selectBasePath,
            @NotNull SchemaInfo schemaInfo,
            @NotNull MultipleModelInfo multipleModelInfo) {
        super(basePath, selectBasePath, schemaInfo);
        this.modelInfo = multipleModelInfo;
    }

    public void generate() throws IOException {
        TypeSpec.Builder classBuilder = initiatilizeClass(modelInfo);
        classBuilder.addMethod(generateFetch(modelInfo));
        classBuilder.addMethod(generateFetchFirst(modelInfo));
        Arrays.stream(modelInfo.getModelInfos()).forEach(simpleModelInfo -> {
            generateWheres(simpleModelInfo, modelInfo).forEach(classBuilder::addMethod);
        });
        writeClass(classBuilder);
    }

}
