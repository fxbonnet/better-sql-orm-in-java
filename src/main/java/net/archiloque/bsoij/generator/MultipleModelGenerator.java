package net.archiloque.bsoij.generator;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.base_classes.model.MultipleModel;
import net.archiloque.bsoij.generator.bean.MultipleModelInfo;
import net.archiloque.bsoij.generator.bean.SchemaInfo;
import net.archiloque.bsoij.generator.bean.SimpleModelInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * create code for multiple models
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
    private Stream<FieldSpec> createAttributes() {
        return Arrays.stream(modelInfo.getModelInfos()).map(modelInfo ->
                FieldSpec.
                        builder(modelInfo.getModelClass(), getAttributeName(modelInfo)).
                        addModifiers(Modifier.PRIVATE, Modifier.FINAL).
                        build()
        );
    }

    @NotNull
    private Stream<MethodSpec> createGetters() {
        return Arrays.stream(modelInfo.getModelInfos()).map(modelInfo ->
                createGetter(
                        getAttributeName(modelInfo),
                        modelInfo.getModelClass())
        );
    }

    @NotNull
    private MethodSpec createConstructor() {
        MethodSpec.Builder constructor = MethodSpec.
                constructorBuilder().
                addModifiers(Modifier.PUBLIC);
        for (SimpleModelInfo info : modelInfo.getModelInfos()) {
            String attributeName = getAttributeName(info);
            constructor.addStatement("this." + attributeName + " = new $T()", info.getModelClass());
        }
        return constructor.build();
    }

    @NotNull
    private MethodSpec createGetter(@NotNull String fieldName, @NotNull ClassName clazz) {
        String getterMethodName = "get" + WordUtils.capitalize(fieldName);
        return MethodSpec.
                methodBuilder(getterMethodName).
                addModifiers(Modifier.PUBLIC).
                returns(clazz).
                addStatement("return " + fieldName).
                build();
    }

    @NotNull
    private FieldSpec createFieldsConstant() {
        String statement = "$T.addAll(";
        List<Object> statementParams = new ArrayList<>();
        statementParams.add(ArrayUtils.class);
        statement += Arrays.stream(modelInfo.getModelInfos()).map(simpleModelInfo -> {
            statementParams.add(simpleModelInfo.getModelClass());
            return "$T." + FIELDS_CONSTANT;
        }).collect(Collectors.joining(", "));
        statement += ")";
        return FieldSpec.
                builder(ArrayTypeName.of(Field.class), FIELDS_CONSTANT).
                addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).
                initializer(statement, statementParams.toArray()).
                build();
    }

    public void create() throws IOException {
        TypeSpec.Builder classBuilder = initializeClass(modelInfo, MultipleModel.class);
        classBuilder.addField(createFieldsConstant());
        classBuilder.addMethod(createConstructor());
        createAttributes().forEach(classBuilder::addField);
        createGetters().forEach(classBuilder::addMethod);
        classBuilder.addMethod(createFieldsMethod());

        // Write the class
        writeClass(classBuilder);
    }

}
