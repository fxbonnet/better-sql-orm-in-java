package net.archiloque.bsoij.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.bsoij.base_classes.Select;
import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.generator.bean.AbstractModelInfo;
import net.archiloque.bsoij.generator.bean.ColumnTypeInfo;
import net.archiloque.bsoij.generator.bean.SchemaInfo;
import net.archiloque.bsoij.generator.bean.SimpleModelInfo;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 */
public abstract class AbstractSelectGenerator {

    private final Logger logger = Logger.getLogger(AbstractSelectGenerator.class.getName());

    @NotNull
    final File basePath;

    @NotNull
    final File selectBasePath;

    @NotNull
    final SchemaInfo schemaInfo;

    public AbstractSelectGenerator(
            @NotNull File basePath,
            @NotNull File selectBasePath,
            @NotNull SchemaInfo schemaInfo) {
        this.basePath = basePath;
        this.selectBasePath = selectBasePath;
        this.schemaInfo = schemaInfo;
    }

    @NotNull
    TypeSpec.Builder initiatilizeClass(AbstractModelInfo modelInfo) {
        File selectFile = new File(selectBasePath, modelInfo.getSelectClass().simpleName() + ".java");
        logger.info("Generating Select for [" + modelInfo.getSelectClass().simpleName() + "] at [" + selectFile.getAbsolutePath() + "]");

        return TypeSpec.classBuilder(modelInfo.getSelectClass()).
                addModifiers(Modifier.PUBLIC, Modifier.FINAL).
                addJavadoc("This class has been generated, DO NOT EDIT IT MANUALLY !!\n").
                superclass(ParameterizedTypeName.get(ClassName.get(Select.class), modelInfo.getModelClass()));
    }

    void writeClass(TypeSpec.Builder classBuilder) throws IOException {
        JavaFile.
                builder(schemaInfo.getSelectPackage(), classBuilder.build()).
                build().writeTo(basePath);
    }


    @NotNull
    MethodSpec generateFetch(AbstractModelInfo modelInfo) {
        return MethodSpec.methodBuilder("fetch").
                addModifiers(Modifier.PUBLIC).
                addAnnotation(Override.class).
                addAnnotation(NotNull.class).
                returns(ParameterizedTypeName.get(ClassName.get(Stream.class), modelInfo.getModelClass())).
                addStatement("return null").
                build();
    }

    @NotNull
    MethodSpec generateFetchFirst(AbstractModelInfo modelInfo) {
        return MethodSpec.methodBuilder("fetchFirst").
                addModifiers(Modifier.PUBLIC).
                addAnnotation(Override.class).
                addAnnotation(NotNull.class).
                returns(ParameterizedTypeName.get(ClassName.get(Optional.class), modelInfo.getModelClass())).
                addStatement("return null").
                build();
    }


    @NotNull
    private MethodSpec generateWhere(ClassName realFieldClassName, Class criteriaClass, AbstractModelInfo returnModelInfo) {
        return MethodSpec.methodBuilder("where").
                addParameter(realFieldClassName, "field").
                addParameter(criteriaClass, "criteria").
                addModifiers(Modifier.PUBLIC).
                addAnnotation(NotNull.class).
                returns(returnModelInfo.getShortSelectClass()).
                addStatement("return this").
                build();
    }

    @NotNull
    Stream<MethodSpec> generateWheres(SimpleModelInfo modelInfo, AbstractModelInfo returnModelInfo) {
        return modelInfo.getColumnsTypes().stream().map(columnTypeAndNullable -> {
            Class<? extends Field> fieldType = columnTypeAndNullable.getFieldType();
            ClassName fieldClassName = ColumnTypeInfo.getClassName(schemaInfo, modelInfo, fieldType);
            ClassName realFieldClassName = ClassName.get(schemaInfo.getModelPackage(), modelInfo.getModelClass().simpleName(), fieldClassName.simpleName());

            // if it's nullable there's two methods :
            // one with the nullable criteria and one with the non nullable one
            List<MethodSpec> result = new ArrayList<>();

            result.add(generateWhere(
                    realFieldClassName,
                    ColumnTypeInfo.getCriteria(columnTypeAndNullable.getColumnType(), columnTypeAndNullable.isNullable())
                    , returnModelInfo
            ));
            if (columnTypeAndNullable.isNullable()) {
                result.add(generateWhere(
                        realFieldClassName,
                        ColumnTypeInfo.getCriteria(columnTypeAndNullable.getColumnType(), false)
                        , returnModelInfo
                ));
            }

            return result;
        }).flatMap(Collection::stream);
    }


}
