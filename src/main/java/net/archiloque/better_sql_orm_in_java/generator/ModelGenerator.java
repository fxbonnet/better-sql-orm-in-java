package net.archiloque.better_sql_orm_in_java.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.better_sql_orm_in_java.base_classes.field.Field;
import net.archiloque.better_sql_orm_in_java.generator.bean.ColumnTypeInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.GeneratorInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.ModelInfo;
import org.apache.commons.lang3.text.WordUtils;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Generate models
 */
public class ModelGenerator {

    private final Logger logger = Logger.getLogger(ModelGenerator.class.getName());

    private final File basePath;
    private final File modelBasePath;
    private final GeneratorInfo generatorInfo;
    private final ModelInfo modelInfo;

    public ModelGenerator(File basePath, File modelBasePath, GeneratorInfo generatorInfo, ModelInfo modelInfo) {
        this.basePath = basePath;
        this.modelBasePath = modelBasePath;
        this.generatorInfo = generatorInfo;
        this.modelInfo = modelInfo;
    }

    public void generate() throws IOException {
        File modelFile = new File(modelBasePath, modelInfo.getModelClassName() + ".java");
        logger.info("Generating Model for [" + modelInfo.getModel().getId() + "] at [" + modelFile.getAbsolutePath() + "]");

        TypeSpec.Builder modelTypeSpec = TypeSpec.classBuilder(modelInfo.getModelClassName()).
                addModifiers(Modifier.PUBLIC, Modifier.FINAL).
                addJavadoc("This class has been generated, DO NOT EDIT IT MANUALLY !!\n").
                superclass(net.archiloque.better_sql_orm_in_java.base_classes.Model.class);

        modelTypeSpec.addMethod(generateSelect());

        // Generate fields
        generateFieldsForModel().forEach(modelTypeSpec::addField);

        // Generate fields
        generateGettersForModel().forEach(modelTypeSpec::addMethod);

        // Generate columns declarations
        generateColumnsForModel().forEach(modelTypeSpec::addField);

        // Generate the helpers for the columns types
        generateColumnTypes().forEach(modelTypeSpec::addType);

        // Write the class
        JavaFile.builder(generatorInfo.getModelPackage(), modelTypeSpec.build()).
                build().writeTo(basePath);
    }

    private MethodSpec generateSelect() {
        return MethodSpec.methodBuilder("select").
                addModifiers(Modifier.PUBLIC, Modifier.STATIC).
                returns(modelInfo.getSelectClass()).
                addStatement("return new " + modelInfo.getSelectClassName() + "()").
                build();
    }

    private Stream<FieldSpec> generateColumnsForModel() {
        return modelInfo.getColumnInfos().stream().map(columnInfo -> {
            ColumnTypeInfo columnTypeInfo = columnInfo.getColumnTypeInfo();
            String columnName = columnInfo.getColumn().getName();
            String fieldName = columnName.toUpperCase();
            return FieldSpec.
                    builder(columnTypeInfo.getShortClassName(), fieldName).
                    addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).
                    initializer("new " + columnTypeInfo.getClassName().simpleName() + "($S)", columnName).
                    build();
        });
    }

    private Stream<MethodSpec> generateGettersForModel() {
        return modelInfo.getColumnInfos().stream().map(columnInfo -> {
            Class valueClass = ColumnTypeInfo.getValueClass(columnInfo.getColumnTypeInfo().getColumnType());
            String getterMethodName = "get" + WordUtils.capitalize(columnInfo.getColumnFieldName());
            return MethodSpec.
                    methodBuilder(getterMethodName).
                    addModifiers(Modifier.PUBLIC).
                    returns(valueClass).
                    addStatement("return " + columnInfo.getColumnFieldName()).
                    build();
        });
    }

    private Stream<FieldSpec> generateFieldsForModel() {
        return modelInfo.getColumnInfos().stream().map(columnInfo -> {
            ColumnTypeInfo columnTypeInfo = columnInfo.getColumnTypeInfo();
            Class valueClass = ColumnTypeInfo.getValueClass(columnTypeInfo.getColumnType());
            return FieldSpec.
                    builder(valueClass, columnInfo.getColumnFieldName()).
                    addModifiers(Modifier.PRIVATE).
                    build();
        });
    }

    private Stream<TypeSpec> generateColumnTypes() {
        return modelInfo.getColumnsTypes().stream().map(columnTypeAndNullable -> {
            Class<? extends Field> fieldType = columnTypeAndNullable.getFieldType();
            ClassName className = ColumnTypeInfo.getClassName(generatorInfo, modelInfo, fieldType);
            MethodSpec columnTypeConstructor = MethodSpec.
                    constructorBuilder().
                    addModifiers(Modifier.PRIVATE).
                    addParameter(String.class, "columnName").
                    addStatement("super(columnName)").
                    build();

            return TypeSpec.
                    classBuilder(className).
                    addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).
                    superclass(fieldType).
                    addMethod(columnTypeConstructor).
                    build();
        });
    }

}
