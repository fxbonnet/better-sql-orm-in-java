package net.archiloque.better_sql_orm_in_java.generator;

import net.archiloque.better_sql_orm_in_java.generator.bean.SimpleModelInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.MultipleModelInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.SchemaInfo;
import net.archiloque.better_sql_orm_in_java.schema.bean.Schema;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 */
public class CodeGenerator {

    @NotNull
    private final Logger logger = Logger.getLogger(CodeGenerator.class.getName());

    @NotNull
    private final File basePath;

    private final Schema schema;

    @NotNull
    private File codeBasePath;

    @NotNull
    private SchemaInfo schemaInfo;

    public CodeGenerator(@NotNull File basePath, Schema schema) {
        this.basePath = basePath;
        this.schema = schema;
    }

    public void initialize() throws IOException, InvalidSchemaException {
        logger.info("Base generation path is [" + basePath.getAbsolutePath() + "]");
        if (basePath.exists()) {
            if (basePath.isDirectory()) {
                FileUtils.cleanDirectory(basePath);
            } else {
                throw new IOException("[" + basePath.getAbsolutePath() + "] is not a directory");
            }
        } else {
            basePath.mkdirs();
        }
        codeBasePath = new File(basePath, schema.getTargetPackage().replace('.', ('/')));
        logger.info("Java classes will be generated in [" + codeBasePath.getAbsolutePath() + "] with package [" + schema.getTargetPackage() + "]");
        codeBasePath.mkdirs();
        schemaInfo = new SchemaInfo(schema);
        schemaInfo.process();
    }

    public void generate() throws IOException {
        generateModels();
        generateSelects();
    }

    private void generateModels() throws IOException {
        File modelBasePath = new File(basePath, "model");
        for (SimpleModelInfo modelInfo : schemaInfo.getModelInfoMap().values()) {
            new SimpleModelGenerator(basePath, modelBasePath, schemaInfo, modelInfo).generate();
        }
        for (MultipleModelInfo multipleModelInfo : schemaInfo.getMultipleModelsInfos()) {
            new MultipleModelGenerator(basePath, modelBasePath, schemaInfo, multipleModelInfo).generate();
        }
    }

    private void generateSelects() throws IOException {
        File selectBasePath = new File(basePath, "select");
        for (SimpleModelInfo modelInfo : schemaInfo.getModelInfoMap().values()) {
            new SimpleSelectGenerator(basePath, selectBasePath, schemaInfo, modelInfo).generate();
        }
        for (MultipleModelInfo multipleModelInfo : schemaInfo.getMultipleModelsInfos()) {
            new MultipleSelectGenerator(basePath, selectBasePath, schemaInfo, multipleModelInfo).generate();
        }

    }

}
