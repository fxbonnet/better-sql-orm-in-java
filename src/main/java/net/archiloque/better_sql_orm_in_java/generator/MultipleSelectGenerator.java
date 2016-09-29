package net.archiloque.better_sql_orm_in_java.generator;

import com.squareup.javapoet.TypeSpec;
import net.archiloque.better_sql_orm_in_java.generator.bean.MultipleModelInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.SchemaInfo;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Generate code for multiple select
 */
public class MultipleSelectGenerator extends AbstractSelectGenerator {

    private final Logger logger = Logger.getLogger(MultipleSelectGenerator.class.getName());

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
        writeClass(classBuilder);
    }

}
