package net.archiloque.bsoij.generator;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import net.archiloque.bsoij.schema.bean.Column;
import net.archiloque.bsoij.schema.bean.ForeignKey;
import net.archiloque.bsoij.schema.bean.Model;
import net.archiloque.bsoij.schema.bean.PrimaryKey;
import net.archiloque.bsoij.schema.bean.Schema;

import java.io.File;
import java.io.IOException;

/**
 * Entry point to generate the code.
 */
public class Main {

    private Main(String schemaFilePath, String generationPath) throws IOException, InvalidSchemaException {
        XStream xstream = createXStream();
        File schemaFile = new File(schemaFilePath);
        Schema schema = (Schema) xstream.fromXML(schemaFile);
        CodeGenerator codeGenerator = new CodeGenerator(new File(generationPath), schema);
        codeGenerator.initialize();
        codeGenerator.generate();
    }

    public static void main(String[] args) throws Exception {
        if(args.length !=2 ) {
            throw new RuntimeException("2 parameters : path to XML schema and path where to generate the code, currently we have [" + String.join(",", args) + "]");
        }
        new Main(args[0], args[1]);
    }

    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());
        xstream.processAnnotations(Column.class);
        xstream.processAnnotations(ForeignKey.class);
        xstream.processAnnotations(Model.class);
        xstream.processAnnotations(PrimaryKey.class);
        xstream.processAnnotations(Schema.class);
        xstream.alias("schema", Schema.class);
        return xstream;
    }

}
