package net.archiloque.better_sql_orm_in_java.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.better_sql_orm_in_java.base_classes.Select;
import net.archiloque.better_sql_orm_in_java.base_classes.field.Field;
import net.archiloque.better_sql_orm_in_java.generator.bean.ColumnTypeInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.SchemaInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.ModelInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Generate select
 */
public class SelectGenerator {

    @NotNull
    private final Logger logger = Logger.getLogger(SelectGenerator.class.getName());

    @NotNull
    private final File basePath;

    @NotNull
    private final File selectBasePath;

    @NotNull
    private final SchemaInfo schemaInfo;

    @NotNull
    private final ModelInfo modelInfo;

    public SelectGenerator(@NotNull File basePath, @NotNull File selectBasePath, @NotNull SchemaInfo schemaInfo, @NotNull ModelInfo modelInfo) {
        this.basePath = basePath;
        this.selectBasePath = selectBasePath;
        this.schemaInfo = schemaInfo;
        this.modelInfo = modelInfo;
    }

    @NotNull
    private MethodSpec generateFetch() {
        return MethodSpec.methodBuilder("fetch").
                addModifiers(Modifier.PUBLIC).
                addAnnotation(Override.class).
                addAnnotation(NotNull.class).
                returns(ParameterizedTypeName.get(ClassName.get(Stream.class), modelInfo.getModelClass())).
                addStatement("return null").
                build();
    }

    @NotNull
    private MethodSpec generateFetchFirst() {
        return MethodSpec.methodBuilder("fetchFirst").
                addModifiers(Modifier.PUBLIC).
                addAnnotation(Override.class).
                addAnnotation(Nullable.class).
                returns(modelInfo.getModelClass()).
                addStatement("return null").
                build();
    }

    @NotNull
    private MethodSpec generateWhere(ClassName realFieldClassName, Class criteriaClass) {
        return MethodSpec.methodBuilder("where").
                addParameter(realFieldClassName, "field").
                addParameter(criteriaClass, "criteria").
                addModifiers(Modifier.PUBLIC).
                returns(modelInfo.getShortSelectClass()).
                addStatement("return this").
                build();
    }

    @NotNull
    private Stream<MethodSpec> generateWheres() {
        return modelInfo.getColumnsTypes().stream().map(columnTypeAndNullable -> {
            Class<? extends Field> fieldType = columnTypeAndNullable.getFieldType();
            ClassName fieldClassName = ColumnTypeInfo.getClassName(schemaInfo, modelInfo, fieldType);
            ClassName realFieldClassName = ClassName.get("", modelInfo.getModelClass().simpleName(), fieldClassName.simpleName());

            // if it's nullable there's two method :
            // one with the nullable criteria and one with the non nullable one
            List<MethodSpec> result = new ArrayList<>();

            result.add(generateWhere(
                    realFieldClassName,
                    ColumnTypeInfo.getCriteria(columnTypeAndNullable.getColumnType(), columnTypeAndNullable.isNullable())
            ));
            if (columnTypeAndNullable.isNullable()) {
                result.add(generateWhere(
                        realFieldClassName,
                        ColumnTypeInfo.getCriteria(columnTypeAndNullable.getColumnType(), false)
                ));
            }

            return result;
        }).flatMap(Collection::stream);
    }

    public void generate() throws IOException {
        File selectFile = new File(selectBasePath, modelInfo.getSelectClass().simpleName() + ".java");
        logger.info("Generating Select for [" + modelInfo.getModel().getId() + "] at [" + selectFile.getAbsolutePath() + "]");

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(modelInfo.getSelectClass()).
                addModifiers(Modifier.PUBLIC, Modifier.FINAL).
                addJavadoc("This class has been generated, DO NOT EDIT IT MANUALLY !!\n").
                superclass(ParameterizedTypeName.get(ClassName.get(Select.class), modelInfo.getModelClass()));

        generateWheres().forEach(classBuilder::addMethod);

        classBuilder.addMethod(generateFetch());
        classBuilder.addMethod(generateFetchFirst());

        JavaFile.
                builder(schemaInfo.getSelectPackage(), classBuilder.build()).
                build().writeTo(basePath);
    }
}
