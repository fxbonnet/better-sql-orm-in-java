package net.archiloque.better_sql_orm_in_java.generator;

import net.archiloque.better_sql_orm_in_java.generator.bean.GeneratorInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.ModelInfo;
import net.archiloque.better_sql_orm_in_java.schema.bean.Model;
import net.archiloque.better_sql_orm_in_java.schema.bean.Schema;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 */
public class CodeGenerator {

    private final Logger logger = Logger.getLogger(CodeGenerator.class.getName());

    private final File basePath;

    private final Schema schema;

    private File codeBasePath;

    private GeneratorInfo generatorInfo;

    private final List<ModelInfo> modelsInfos = new ArrayList<>();
    
    public CodeGenerator(File basePath, Schema schema) {
        this.basePath = basePath;
        this.schema = schema;
    }

    public void initialize() throws IOException {
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
        generatorInfo = new GeneratorInfo(schema);

        for (Model model : schema.getModels()) {
            ModelInfo modelInfo = new ModelInfo(model, generatorInfo);
            modelsInfos.add(modelInfo);
        }
    }

    public void generate() throws IOException {
        generateModels();
        generateSelects();
    }


    private void generateModels() throws IOException {
        File modelBasePath = new File(basePath, "model");
        for (ModelInfo modelInfo : modelsInfos) {
            new ModelGenerator(basePath, modelBasePath, generatorInfo, modelInfo).generate();
        }
    }

    private void generateSelects() throws IOException {
        File selectBasePath = new File(basePath, "select");
        for (ModelInfo modelInfo : modelsInfos) {
            new SelectGenerator(basePath, selectBasePath, generatorInfo, modelInfo).generate();
        }
    }

}
