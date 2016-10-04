package net.archiloque.bsoij.generator;

import net.archiloque.bsoij.schema.SchemaReader;
import net.archiloque.bsoij.schema.bean.Schema;

import java.io.File;
import java.io.IOException;

/**
 * Entry point to generate the code.
 */
public class Main {

    private Main(String schemaFilePath, String generationPath) throws IOException, InvalidSchemaException {
        Schema schema = new SchemaReader(schemaFilePath).read();
        CodeGenerator codeGenerator = new CodeGenerator(new File(generationPath), schema);
        codeGenerator.initialize();
        codeGenerator.generate();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new RuntimeException("2 parameters : path to XML schema and path where to generate the code, currently we have [" + String.join(",", args) + "]");
        }
        new Main(args[0], args[1]);
    }

}
