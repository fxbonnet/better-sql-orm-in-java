package net.archiloque.better_sql_orm_in_java.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.better_sql_orm_in_java.base_classes.criteria.Criteria;
import net.archiloque.better_sql_orm_in_java.base_classes.field.Field;
import net.archiloque.better_sql_orm_in_java.generator.bean.ColumnInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.ColumnTypeInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.ModelInfo;
import net.archiloque.better_sql_orm_in_java.generator.bean.SchemaInfo;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Generate simple models
 */
public class SimpleModelGenerator {

    @NotNull
    private final Logger logger = Logger.getLogger(SimpleModelGenerator.class.getName());

    @NotNull
    private final File basePath;

    @NotNull
    private final File modelBasePath;

    @NotNull
    private final SchemaInfo schemaInfo;

    @NotNull
    private final ModelInfo modelInfo;

    public SimpleModelGenerator(@NotNull File basePath, @NotNull File modelBasePath, @NotNull SchemaInfo schemaInfo, @NotNull ModelInfo modelInfo) {
        this.basePath = basePath;
        this.modelBasePath = modelBasePath;
        this.schemaInfo = schemaInfo;
        this.modelInfo = modelInfo;
    }

    public void generate() throws IOException {
        File modelFile = new File(modelBasePath, modelInfo.getModelClassName() + ".java");
        logger.info("Generating Model for [" + modelInfo.getModel().getId() + "] at [" + modelFile.getAbsolutePath() + "]");

        TypeSpec.Builder modelTypeSpec = TypeSpec.classBuilder(modelInfo.getModelClassName()).
                addModifiers(Modifier.PUBLIC, Modifier.FINAL).
                addJavadoc("This class has been generated, DO NOT EDIT IT MANUALLY !!\n").
                superclass(net.archiloque.better_sql_orm_in_java.base_classes.Model.class);

        modelTypeSpec.addMethod(generateSelect());

        // Generate fields
        generateFieldsForModel().forEach(modelTypeSpec::addField);

        // Generate fields
        generateGettersForModel().forEach(modelTypeSpec::addMethod);

        // Generate columns declarations
        generateColumnsForModel().forEach(modelTypeSpec::addField);

        // Generate the helpers for the columns types
        generateColumnTypes().forEach(modelTypeSpec::addType);

        // Generate foreign keys
        generateForeignKeys().forEach(modelTypeSpec::addMethod);
        generateForeignedKeys().forEach(modelTypeSpec::addMethod);

        // Write the class
        JavaFile.builder(schemaInfo.getModelPackage(), modelTypeSpec.build()).
                build().writeTo(basePath);
    }

    @NotNull
    private Stream<MethodSpec> generateForeignKeys() {
        return modelInfo.getForeignKeyInfos().stream().map(foreignKeyInfo -> {
            // generate something like
            //   public CustomerModel fetchCustomer() {
            //      return CustomerModel.select().where(CustomerModel.CUSTOMER_ID, Criteria.integerEquals(customerId)).fetchFirst();
            // }

            String methodName = "fetch" + WordUtils.capitalize(foreignKeyInfo.getSourceModel().getModel().getId());

            ClassName targetModelClassName = foreignKeyInfo.getTargetModel().getModelClass();
            ColumnInfo targetPrimaryKeyColumnInfo = foreignKeyInfo.getTargetModel().getPrimaryKeyInfo().getColumnInfo();
            String statement = "return $T." +
                    "select().where($T." + targetPrimaryKeyColumnInfo.getColumnConstantName() + "," +
                    "$T." + ColumnTypeInfo.getCriteriaEquals(targetPrimaryKeyColumnInfo.getColumnTypeInfo().getColumnType()) + "(" +
                    foreignKeyInfo.getColumnInfo().getColumnFieldName() + ")).fetchFirst()";

            return MethodSpec.methodBuilder(methodName).
                    addModifiers(Modifier.PUBLIC).
                    addAnnotation(NotNull.class).
                    returns(targetModelClassName).
                    addStatement(
                            statement,
                            targetModelClassName,
                            targetModelClassName,
                            Criteria.class).
                    build();
        });
    }

    @NotNull
    private Stream<MethodSpec> generateForeignedKeys() {
        return modelInfo.getForeignKeyedInfos().stream().map(foreignKeyInfo -> {
            // generate something like
            //   public Stream<OrderModel> fetchOrders() {
            //      return OrderModel.select().where(OrderModel.CUSTOMER_ID, Criteria.integerEquals(customer_id)).fetch();
            // }

            String methodName = "fetch" + WordUtils.capitalize(foreignKeyInfo.getSourceModel().getModel().getId() + "s");
            ClassName sourceModelClassName = foreignKeyInfo.getSourceModel().getModelClass();
            ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
            ColumnInfo localKeyColumnInfo = modelInfo.getPrimaryKeyInfo().getColumnInfo();
            String statement = "return $T." +
                    "select().where($T." + foreignKeyColumnInfo.getColumnConstantName() + ", " +
                    "$T." + ColumnTypeInfo.getCriteriaEquals(foreignKeyColumnInfo.getColumnTypeInfo().getColumnType()) + "(" +
                    localKeyColumnInfo.getColumnFieldName() + ")).fetch()";

            return MethodSpec.methodBuilder(methodName).
                    addModifiers(Modifier.PUBLIC).
                    addAnnotation(NotNull.class).
                    returns(ParameterizedTypeName.get(ClassName.get(Stream.class), sourceModelClassName)).
                    addStatement(
                            statement,
                            sourceModelClassName,
                            sourceModelClassName,
                            Criteria.class).
                    build();
        });
    }

    @NotNull
    private MethodSpec generateSelect() {
        return MethodSpec.methodBuilder("select").
                addModifiers(Modifier.PUBLIC, Modifier.STATIC).
                returns(modelInfo.getSelectClass()).
                addStatement("return new " + modelInfo.getSelectClassName() + "()").
                build();
    }

    @NotNull
    private Stream<FieldSpec> generateColumnsForModel() {
        return modelInfo.getColumnInfos().stream().map(columnInfo -> {
            ColumnTypeInfo columnTypeInfo = columnInfo.getColumnTypeInfo();
            String columnName = columnInfo.getColumn().getName();
            return FieldSpec.
                    builder(columnTypeInfo.getShortClassName(), columnInfo.getColumnConstantName()).
                    addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).
                    initializer("new " + columnTypeInfo.getClassName().simpleName() + "($S)", columnName).
                    build();
        });
    }

    @NotNull
    private Stream<MethodSpec> generateGettersForModel() {
        return modelInfo.getColumnInfos().stream().map(columnInfo -> {
            Class valueClass = ColumnTypeInfo.getValueClass(columnInfo.getColumnTypeInfo().getColumnType());
            String getterMethodName = "get" + WordUtils.capitalize(columnInfo.getColumnFieldName());
            return MethodSpec.
                    methodBuilder(getterMethodName).
                    addModifiers(Modifier.PUBLIC).
                    returns(valueClass).
                    addStatement("return " + columnInfo.getColumnFieldName()).
                    build();
        });
    }

    @NotNull
    private Stream<FieldSpec> generateFieldsForModel() {
        return modelInfo.getColumnInfos().stream().map(columnInfo -> {
            ColumnTypeInfo columnTypeInfo = columnInfo.getColumnTypeInfo();
            Class valueClass = ColumnTypeInfo.getValueClass(columnTypeInfo.getColumnType());
            return FieldSpec.
                    builder(valueClass, columnInfo.getColumnFieldName()).
                    addModifiers(Modifier.PRIVATE).
                    build();
        });
    }

    @NotNull
    private Stream<TypeSpec> generateColumnTypes() {
        return modelInfo.getColumnsTypes().stream().map(columnTypeAndNullable -> {
            Class<? extends Field> fieldType = columnTypeAndNullable.getFieldType();
            ClassName className = ColumnTypeInfo.getClassName(schemaInfo, modelInfo, fieldType);
            MethodSpec columnTypeConstructor = MethodSpec.
                    constructorBuilder().
                    addModifiers(Modifier.PRIVATE).
                    addParameter(String.class, "columnName").
                    addStatement("super(columnName)").
                    build();

            return TypeSpec.
                    classBuilder(className).
                    addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).
                    superclass(fieldType).
                    addMethod(columnTypeConstructor).
                    build();
        });
    }

}
