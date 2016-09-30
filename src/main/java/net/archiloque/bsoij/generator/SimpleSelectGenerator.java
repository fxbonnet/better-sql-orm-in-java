package net.archiloque.bsoij.generator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.bsoij.generator.bean.MultipleModelInfo;
import net.archiloque.bsoij.generator.bean.SchemaInfo;
import net.archiloque.bsoij.generator.bean.SimpleModelInfo;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * Generate select
 */
public class SimpleSelectGenerator extends AbstractSelectGenerator {

    @NotNull
    private final SimpleModelInfo modelInfo;

    public SimpleSelectGenerator(
            @NotNull File basePath,
            @NotNull File selectBasePath,
            @NotNull SchemaInfo schemaInfo,
            @NotNull SimpleModelInfo modelInfo) {
        super(basePath, selectBasePath, schemaInfo);
        this.modelInfo = modelInfo;
    }

    @NotNull
    private MethodSpec generateJoin(MultipleModelInfo multipleModelInfo, String name) {
        return MethodSpec.methodBuilder("join" + WordUtils.capitalize(name)).
                addModifiers(Modifier.PUBLIC).
                addAnnotation(NotNull.class).
                returns(multipleModelInfo.getSelectClass()).
                addStatement("return null").
                build();
    }

    @NotNull
    private Stream<MethodSpec> generateJoins() {
        return Stream.concat(
                modelInfo.getForeignKeyedInfos().stream().map(foreignKeyInfo -> {
                    SimpleModelInfo[] modelList = new SimpleModelInfo[]{modelInfo, foreignKeyInfo.getSourceModel()};
                    MultipleModelInfo multipleModelInfo = schemaInfo.getMultipleModelInfo(modelList);
                    return generateJoin(multipleModelInfo, foreignKeyInfo.getForeignKey().getReverseName());
                }), modelInfo.getForeignKeyInfos().stream().map(foreignKeyInfo -> {
                    SimpleModelInfo[] modelList = new SimpleModelInfo[]{modelInfo, foreignKeyInfo.getTargetModel()};
                    MultipleModelInfo multipleModelInfo = schemaInfo.getMultipleModelInfo(modelList);
                    return generateJoin(multipleModelInfo, foreignKeyInfo.getForeignKey().getName());
                }));
    }


    public void generate() throws IOException {
        TypeSpec.Builder classBuilder = initiatilizeClass(modelInfo);
        generateWheres(modelInfo, modelInfo).forEach(classBuilder::addMethod);
        generateJoins().forEach(classBuilder::addMethod);
        classBuilder.addMethod(generateFetch(modelInfo));
        classBuilder.addMethod(generateFetchFirst(modelInfo));
        writeClass(classBuilder);
    }

}
