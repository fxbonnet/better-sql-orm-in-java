package net.archiloque.bsoij.schema.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * The full Schema
 */
@XStreamAlias("schema")
public class Schema {

    @XStreamAsAttribute
    private String targetPackage;

    @XStreamImplicit(itemFieldName = "model")
    private List<Model> models = new ArrayList<>();

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

    public void add(Model model) {
        models.add(model);
    }

    public void validate() {
        for (Model model : models) {
            model.validate();
        }
    }
}
