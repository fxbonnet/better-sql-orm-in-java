package net.archiloque.bsoij.schema;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import net.archiloque.bsoij.schema.bean.Column;
import net.archiloque.bsoij.schema.bean.ForeignKey;
import net.archiloque.bsoij.schema.bean.Model;
import net.archiloque.bsoij.schema.bean.PrimaryKey;
import net.archiloque.bsoij.schema.bean.Schema;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Read the schema configuration file
 */
public class SchemaReader {

    private final @NotNull String schemaFilePath;

    public SchemaReader(String schemaFilePath) {
        this.schemaFilePath = schemaFilePath;
    }

    /**
     * Read the schema
     * @return
     */
    @NotNull
    public Schema read(){
        XStream xstream = createXStream();
        File schemaFile = new File(schemaFilePath);
        return (Schema) xstream.fromXML(schemaFile);
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
