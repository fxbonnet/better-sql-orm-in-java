package net.archiloque.better_sql_orm_in_java.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.better_sql_orm_in_java.generator.bean.MultipleModelInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.SchemaInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.SimpleModelInfo;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Generate code for multiple models
 */
public class MultipleModelGenerator extends AbstractModelGenerator {

    @NotNull
    private final MultipleModelInfo modelInfo;

    public MultipleModelGenerator(
            @NotNull File basePath,
            @NotNull File modelBasePath,
            @NotNull SchemaInfo schemaInfo,
            @NotNull MultipleModelInfo multipleModelInfo) {
        super(basePath, modelBasePath, schemaInfo);
        this.modelInfo = multipleModelInfo;
    }

    private String getAttributeName(SimpleModelInfo modelInfo) {
        return WordUtils.uncapitalize(modelInfo.getBaseClassName());
    }

    @NotNull
    private Stream<FieldSpec> generateAttributes() {
        return Arrays.stream(modelInfo.getModelInfos()).map(modelInfo ->
                FieldSpec.
                builder(modelInfo.getModelClass(), getAttributeName(modelInfo)).
                addModifiers(Modifier.PRIVATE, Modifier.FINAL).
                build()
        );
    }

    @NotNull
    private Stream<MethodSpec> generateGetters() {
        return Arrays.stream(modelInfo.getModelInfos()).map(modelInfo ->
                createGetter(
                        getAttributeName(modelInfo),
                        modelInfo.getModelClass())
        );
    }

    @NotNull
    private MethodSpec generateConstructor() {
        MethodSpec.Builder constructor = MethodSpec.
                constructorBuilder().
                addModifiers(Modifier.PUBLIC);
        for (SimpleModelInfo info : modelInfo.getModelInfos()) {
            String attributeName = getAttributeName(info);
            constructor.addParameter(info.getModelClass(), attributeName).
                    addStatement("this." + attributeName + " = " + attributeName);
        }
        return constructor.build();
    }


    public void generate() throws IOException {
        TypeSpec.Builder classBuilder = initializeClass(modelInfo);

        classBuilder.addMethod(generateConstructor());
        generateAttributes().forEach(classBuilder::addField);
        generateGetters().forEach(classBuilder::addMethod);

        // Write the class
        writeClass(classBuilder);

    }

}
