package net.archiloque.better_sql_orm_in_java.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.better_sql_orm_in_java.base_classes.field.Field;
import net.archiloque.better_sql_orm_in_java.generator.bean.ColumnTypeInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.MultipleModelInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.SchemaInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.SimpleModelInfo;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Generate select
 */
public class SimpleSelectGenerator extends AbstractSelectGenerator {

    @NotNull
    private final Logger logger = Logger.getLogger(SimpleSelectGenerator.class.getName());

    @NotNull
    private final SimpleModelInfo modelInfo;

    public SimpleSelectGenerator(
            @NotNull File basePath,
            @NotNull File selectBasePath,
            @NotNull SchemaInfo schemaInfo,
            @NotNull SimpleModelInfo modelInfo) {
        super(basePath, selectBasePath, schemaInfo);
        this.modelInfo = modelInfo;
    }

    @NotNull
    private MethodSpec generateWhere(ClassName realFieldClassName, Class criteriaClass) {
        return MethodSpec.methodBuilder("where").
                addParameter(realFieldClassName, "field").
                addParameter(criteriaClass, "criteria").
                addModifiers(Modifier.PUBLIC).
                returns(modelInfo.getShortSelectClass()).
                addStatement("return this").
                build();
    }

    @NotNull
    private MethodSpec generateJoin(MultipleModelInfo multipleModelInfo, String name){
        return MethodSpec.methodBuilder("join" + WordUtils.capitalize(name)).
                addModifiers(Modifier.PUBLIC).
                returns(multipleModelInfo.getSelectClass()).
                addStatement("return null").
                build();
    }

    @NotNull
    private Stream<MethodSpec> generateJoins() {
        return Stream.concat(
                modelInfo.getForeignKeyedInfos().stream().map(foreignKeyInfo -> {
                    SimpleModelInfo[] modelList = new SimpleModelInfo[]{modelInfo, foreignKeyInfo.getSourceModel()};
                    MultipleModelInfo multipleModelInfo = schemaInfo.getMultipleModelInfo(modelList);
                    return generateJoin(multipleModelInfo, foreignKeyInfo.getForeignKey().getReverseName());
                }), modelInfo.getForeignKeyInfos().stream().map(foreignKeyInfo -> {
                    SimpleModelInfo[] modelList = new SimpleModelInfo[]{modelInfo, foreignKeyInfo.getTargetModel()};
                    MultipleModelInfo multipleModelInfo = schemaInfo.getMultipleModelInfo(modelList);
                    return generateJoin(multipleModelInfo, foreignKeyInfo.getForeignKey().getName());
                }));
    }

    @NotNull
    private Stream<MethodSpec> generateWheres() {
        return modelInfo.getColumnsTypes().stream().map(columnTypeAndNullable -> {
            Class<? extends Field> fieldType = columnTypeAndNullable.getFieldType();
            ClassName fieldClassName = ColumnTypeInfo.getClassName(schemaInfo, modelInfo, fieldType);
            ClassName realFieldClassName = ClassName.get("", modelInfo.getModelClass().simpleName(), fieldClassName.simpleName());

            // if it's nullable there's two methods :
            // one with the nullable criteria and one with the non nullable one
            List<MethodSpec> result = new ArrayList<>();

            result.add(generateWhere(
                    realFieldClassName,
                    ColumnTypeInfo.getCriteria(columnTypeAndNullable.getColumnType(), columnTypeAndNullable.isNullable())
            ));
            if (columnTypeAndNullable.isNullable()) {
                result.add(generateWhere(
                        realFieldClassName,
                        ColumnTypeInfo.getCriteria(columnTypeAndNullable.getColumnType(), false)
                ));
            }

            return result;
        }).flatMap(Collection::stream);
    }

    public void generate() throws IOException {
        TypeSpec.Builder classBuilder = initiatilizeClass(modelInfo);
        generateWheres().forEach(classBuilder::addMethod);
        generateJoins().forEach(classBuilder::addMethod);
        classBuilder.addMethod(generateFetch(modelInfo));
        classBuilder.addMethod(generateFetchFirst(modelInfo));
        writeClass(classBuilder);
    }

}
