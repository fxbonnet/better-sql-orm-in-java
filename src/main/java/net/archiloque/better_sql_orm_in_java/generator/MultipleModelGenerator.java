package net.archiloque.better_sql_orm_in_java.generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.better_sql_orm_in_java.generator.bean.ModelInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.MultipleModelInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.SchemaInfo;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Generate code for multiple models
 */
public class MultipleModelGenerator {

    private final Logger logger = Logger.getLogger(MultipleModelGenerator.class.getName());

    @NotNull
    private final File basePath;

    @NotNull
    private final File modelBasePath;

    @NotNull
    private final SchemaInfo schemaInfo;

    @NotNull
    private final MultipleModelInfo modelInfo;

    public MultipleModelGenerator(@NotNull File basePath, @NotNull File modelBasePath, @NotNull SchemaInfo schemaInfo, @NotNull MultipleModelInfo multipleModelInfo) {
        this.basePath = basePath;
        this.modelBasePath = modelBasePath;
        this.schemaInfo = schemaInfo;
        this.modelInfo = multipleModelInfo;
    }

    public void generate() throws IOException {
        String className = Arrays.
                stream(modelInfo.getModelInfos()).
                map(ModelInfo::getBaseClassName).
                collect(Collectors.joining("")) + "Model";
        File modelFile = new File(modelBasePath, modelInfo.getModelClass().simpleName() + ".java");
        logger.info("Generating Model for [" + className + "] at [" + modelFile.getAbsolutePath() + "]");

        TypeSpec.Builder modelTypeSpec = TypeSpec.classBuilder(modelInfo.getModelClass()).
                addModifiers(Modifier.PUBLIC, Modifier.FINAL).
                addJavadoc("This class has been generated, DO NOT EDIT IT MANUALLY !!\n").
                superclass(net.archiloque.better_sql_orm_in_java.base_classes.Model.class);

        // Write the class
        JavaFile.builder(schemaInfo.getModelPackage(), modelTypeSpec.build()).
                build().writeTo(basePath);
    }

}
