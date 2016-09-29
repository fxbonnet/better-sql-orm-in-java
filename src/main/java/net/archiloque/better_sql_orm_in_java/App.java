package net.archiloque.better_sql_orm_in_java;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import net.archiloque.better_sql_orm_in_java.generator.CodeGenerator;
import net.archiloque.better_sql_orm_in_java.schema.bean.Column;
import net.archiloque.better_sql_orm_in_java.schema.bean.ForeignKey;
import net.archiloque.better_sql_orm_in_java.schema.bean.Model;
import net.archiloque.better_sql_orm_in_java.schema.bean.PrimaryKey;
import net.archiloque.better_sql_orm_in_java.schema.bean.Schema;

import java.io.File;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        XStream xstream = createXStream();
        File file = new File("example/schema.xml");
        Schema schema = (Schema) xstream.fromXML(file);
        CodeGenerator codeGenerator = new CodeGenerator(new File("target/generation"), schema);
        codeGenerator.initialize();
        codeGenerator.generate();
    }

    private static XStream createXStream() {
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
