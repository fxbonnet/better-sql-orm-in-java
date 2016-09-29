package net.archiloque.better_sql_orm_in_java.generator;

import net.archiloque.better_sql_orm_in_java.generator.bean.ModelInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.MultipleModelInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.SchemaInfo;

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

    private final File basePath;
    private final File modelBasePath;
    private final SchemaInfo schemaInfo;
    private final MultipleModelInfo multipleModelInfo;

    public MultipleModelGenerator(File basePath, File modelBasePath, SchemaInfo schemaInfo, MultipleModelInfo multipleModelInfo) {
        this.basePath = basePath;
        this.modelBasePath = modelBasePath;
        this.schemaInfo = schemaInfo;
        this.multipleModelInfo = multipleModelInfo;
    }

    public void generate() throws IOException {
        String className = Arrays.
                stream(multipleModelInfo.getModelInfos()).
                map(ModelInfo::getBaseClassName).
                collect(Collectors.joining(""))  + "Model";
        File modelFile = new File(modelBasePath, className + ".java");
        logger.info("Generating Model for [" + className + "] at [" + modelFile.getAbsolutePath() + "]");

    }

}
