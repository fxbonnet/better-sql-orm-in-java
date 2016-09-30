package net.archiloque.bsoij.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.bsoij.generator.bean.AbstractModelInfo;
import net.archiloque.bsoij.generator.bean.SchemaInfo;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

abstract class AbstractModelGenerator {

    private final Logger logger = Logger.getLogger(AbstractModelGenerator.class.getName());

    @NotNull
    final File basePath;

    @NotNull
    final File modelBasePath;

    @NotNull
    final SchemaInfo schemaInfo;

    AbstractModelGenerator(
            @NotNull File basePath,
            @NotNull File modelBasePath,
            @NotNull SchemaInfo schemaInfo) {
        this.basePath = basePath;
        this.modelBasePath = modelBasePath;
        this.schemaInfo = schemaInfo;
    }


    @NotNull
    MethodSpec createGetter(@NotNull String fieldName, @NotNull Class clazz){
        String getterMethodName = "get" + WordUtils.capitalize(fieldName);
        return MethodSpec.
                methodBuilder(getterMethodName).
                addModifiers(Modifier.PUBLIC).
                returns(clazz).
                addStatement("return " + fieldName).
                build();
    }

    @NotNull
    MethodSpec createGetter(@NotNull String fieldName, @NotNull ClassName clazz){
        String getterMethodName = "get" + WordUtils.capitalize(fieldName);
        return MethodSpec.
                methodBuilder(getterMethodName).
                addModifiers(Modifier.PUBLIC).
                returns(clazz).
                addStatement("return " + fieldName).
                build();
    }

    TypeSpec.Builder initializeClass(AbstractModelInfo modelInfo){
        File modelFile = new File(modelBasePath, modelInfo.getModelClass().simpleName() + ".java");
        logger.info("Generating Model for [" + modelInfo.getModelClass().simpleName() + "] at [" + modelFile.getAbsolutePath() + "]");

        return TypeSpec.classBuilder(modelInfo.getModelClass()).
                addModifiers(Modifier.PUBLIC, Modifier.FINAL).
                addJavadoc("This class has been generated, DO NOT EDIT IT MANUALLY !!\n").
                superclass(net.archiloque.bsoij.base_classes.Model.class);
    }

    void writeClass(TypeSpec.Builder classBuilder) throws IOException {
        // Write the class
        JavaFile.builder(schemaInfo.getModelPackage(), classBuilder.build()).
                build().writeTo(basePath);

    }

}
