package net.archiloque.bsoij.generator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.base_classes.model.Model;
import net.archiloque.bsoij.generator.bean.AbstractModelInfo;
import net.archiloque.bsoij.generator.bean.SchemaInfo;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

abstract class AbstractModelGenerator {

    static final String TABLE_NAME_CONSTANT = "TABLE_NAME";

    static final String FIELDS_CONSTANT = "FIELDS";

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
    TypeSpec.Builder initializeClass(@NotNull AbstractModelInfo modelInfo, @NotNull Class<? extends Model> parentClass) {
        File modelFile = new File(modelBasePath, modelInfo.getModelClass().simpleName() + ".java");
        logger.info("Generating Model for [" + modelInfo.getModelClass().simpleName() + "] at [" + modelFile.getAbsolutePath() + "]");


        TypeSpec.Builder clazz = TypeSpec.classBuilder(modelInfo.getModelClass()).
                addModifiers(Modifier.PUBLIC, Modifier.FINAL).
                addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unused").build()).
                addJavadoc("This class has been generated, DO NOT EDIT IT MANUALLY !!\n").
                superclass(parentClass);
        return clazz;
    }

    void writeClass(@NotNull TypeSpec.Builder classBuilder) throws IOException {
        // Write the class
        JavaFile.builder(schemaInfo.getModelPackage(), classBuilder.build()).
                build().writeTo(basePath);

    }

    @NotNull
    MethodSpec createFieldsMethod() {
        return MethodSpec.
                methodBuilder("getFields").
                addModifiers(Modifier.PUBLIC).
                returns(ArrayTypeName.of(Field.class)).
                addAnnotation(NotNull.class).
                addAnnotation(Override.class).
                addStatement("return " + FIELDS_CONSTANT).
                build();
    }


}
