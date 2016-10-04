package net.archiloque.bsoij.generator;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import net.archiloque.bsoij.base_classes.TableAndModel;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import net.archiloque.bsoij.base_classes.select.MultipleSelect;
import net.archiloque.bsoij.generator.bean.MultipleModelInfo;
import net.archiloque.bsoij.generator.bean.SchemaInfo;
import net.archiloque.bsoij.generator.bean.SimpleModelInfo;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.archiloque.bsoij.generator.AbstractModelGenerator.TABLE_NAME_CONSTANT;

/**
 * create code for multiple select
 */
public class MultipleSelectGenerator extends AbstractSelectGenerator {

    private static final String TABLES_AND_MODELS_CONSTANT = "TABLES_AND_MODELS";

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

    public void create() throws IOException {
        TypeSpec.Builder classBuilder = initiatilizeClass(
                MultipleSelect.class,
                modelInfo,
                modelInfo.getModelInfos(),
                modelInfo.getForeignKeyInfos()
        );
        classBuilder.addMethod(createFetch(modelInfo));
        classBuilder.addMethod(createFetchFirst(modelInfo));
        Arrays.stream(modelInfo.getModelInfos()).forEach(simpleModelInfo -> {
            classBuilder.addMethod(createConstructor(simpleModelInfo));
            createWheres(simpleModelInfo, modelInfo).forEach(classBuilder::addMethod);
            createOrdersMethods(simpleModelInfo, modelInfo).forEach(classBuilder::addMethod);
        });
        classBuilder.addField(createGetTablesAndModelsConstant());
        classBuilder.addMethod(createGetTablesAndModelsMethod());


        writeClass(classBuilder);
    }

    @NotNull
    private MethodSpec createConstructor(@NotNull SimpleModelInfo modelInfo) {
        return MethodSpec.
                constructorBuilder().
                addParameter(modelInfo.getShortSelectClass(), modelInfo.getSelectParam()).
                addStatement("super(" + TABLES_NAME_CONSTANT + ", " + JOINS_CONSTANT + ", " + modelInfo.getSelectParam() + ")").
                build();
    }


    /**
     * Constants containing the list of tables
     * private static final String[] TABLES_NAMES = {"customer", "order"};
     */
    @NotNull
    private FieldSpec createGetTablesAndModelsConstant() {
        List<Object> statementParams = new ArrayList<>();
        String statement = "new $T[]{\n";
        statementParams.add(TableAndModel.class);
        statement += Arrays.stream(modelInfo.getModelInfos()).map(simpleModelInfo -> {
            statementParams.add(TableAndModel.class);
            statementParams.add(modelInfo.getModelClass());
            statementParams.add(simpleModelInfo.getModelClass());
            statementParams.add(simpleModelInfo.getModelClass());
            statementParams.add(simpleModelInfo.getModelClass());
            statementParams.add(modelInfo.getModelClass());
            return "  new $T<$T,$T>($T." + TABLE_NAME_CONSTANT + ", $T.class, $T::" + "get" + simpleModelInfo.getBaseClassName() + ")";
        }).collect(Collectors.joining(",\n"));
        statement += "\n}";

        return FieldSpec.
                builder(getTablesAndModelsType(), TABLES_AND_MODELS_CONSTANT).
                addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC).
                initializer(
                        statement,
                        statementParams.toArray()
                ).
                build();
    }

    @NotNull
    private MethodSpec createGetTablesAndModelsMethod() {
        return MethodSpec.methodBuilder("getTablesAndModels").
                addModifiers(Modifier.PUBLIC).
                addAnnotation(Override.class).
                addAnnotation(NotNull.class).
                returns(getTablesAndModelsType()).
                addStatement("return " + TABLES_AND_MODELS_CONSTANT).
                build();
    }

    @NotNull
    private ArrayTypeName getTablesAndModelsType() {
        return ArrayTypeName.of(
                ParameterizedTypeName.get(
                        ClassName.get(TableAndModel.class),
                        modelInfo.getModelClass(),
                        WildcardTypeName.subtypeOf(SimpleModel.class)

                )
        );
    }

}
