package net.archiloque.bsoij.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.bsoij.base_classes.criteria.Criteria;
import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.generator.bean.ColumnInfo;
import net.archiloque.bsoij.generator.bean.ColumnTypeInfo;
import net.archiloque.bsoij.generator.bean.SchemaInfo;
import net.archiloque.bsoij.generator.bean.SimpleModelInfo;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Generate simple models
 */
public class SimpleModelGenerator extends AbstractModelGenerator {

    @NotNull
    private final SimpleModelInfo modelInfo;

    public SimpleModelGenerator(
            @NotNull File basePath,
            @NotNull File modelBasePath,
            @NotNull SchemaInfo schemaInfo,
            @NotNull SimpleModelInfo modelInfo) {
        super(basePath, modelBasePath, schemaInfo);
        this.modelInfo = modelInfo;
    }

    public void generate() throws IOException {
        TypeSpec.Builder classBuilder = initializeClass(modelInfo);

        classBuilder.addMethod(generateSelect());

        // Generate fields
        generateFieldsAttributes().forEach(classBuilder::addField);

        // Generate fields
        generateGetters().forEach(classBuilder::addMethod);

        // Generate columns declarations
        generateColumnsConstants().forEach(classBuilder::addField);

        // Generate the helpers for the columns types
        generateColumnTypes().forEach(classBuilder::addType);

        // Generate foreign keys fetch
        generateForeignKeysFetch().forEach(classBuilder::addMethod);
        generateForeignedKeysFetch().forEach(classBuilder::addMethod);

        // Write the class
        writeClass(classBuilder);
    }

    @NotNull
    private Stream<MethodSpec> generateForeignKeysFetch() {
        return modelInfo.getForeignKeyInfos().stream().map(foreignKeyInfo -> {
            // generate something like
            //   public CustomerModel fetchCustomer() {
            //      return CustomerModel.select().where(CustomerModel.CUSTOMER_ID, Criteria.integerEquals(customerId)).fetchFirst();
            // }

            String methodName = "fetch" + WordUtils.capitalize(foreignKeyInfo.getForeignKey().getName());

            ClassName targetModelClassName = foreignKeyInfo.getTargetModel().getModelClass();
            ColumnInfo targetPrimaryKeyColumnInfo = foreignKeyInfo.getTargetModel().getPrimaryKeyInfo().getColumnInfo();
            boolean nullableReturn = targetPrimaryKeyColumnInfo.getColumn().isNullable();
            TypeName returnType =  nullableReturn ?
                    ParameterizedTypeName.get(ClassName.get(Optional.class), targetModelClassName) : targetModelClassName;

            String statement = "return $T." +
                    "select().where($T." + targetPrimaryKeyColumnInfo.getColumnConstantName() + "," +
                    "$T." + ColumnTypeInfo.getCriteriaEquals(targetPrimaryKeyColumnInfo.getColumnTypeInfo().getColumnType()) + "(" +
                    foreignKeyInfo.getColumnInfo().getColumnFieldName() + ")).fetchFirst()";

            if(! nullableReturn) {
                statement+= ".get()";
            }

            return MethodSpec.methodBuilder(methodName).
                    addModifiers(Modifier.PUBLIC).
                    addAnnotation(NotNull.class).
                    returns(returnType).
                    addStatement(
                            statement,
                            targetModelClassName,
                            targetModelClassName,
                            Criteria.class).
                    build();
        });
    }

    @NotNull
    private Stream<MethodSpec> generateForeignedKeysFetch() {
        return modelInfo.getForeignKeyedInfos().stream().map(foreignKeyInfo -> {
            // generate something like
            //   public Stream<OrderModel> fetchOrders() {
            //      return OrderModel.select().where(OrderModel.CUSTOMER_ID, Criteria.integerEquals(customer_id)).fetch();
            // }

            String methodName = "fetch" + WordUtils.capitalize(foreignKeyInfo.getForeignKey().getReverseName());
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
                addAnnotation(NotNull.class).
                returns(modelInfo.getSelectClass()).
                addStatement("return new $T()", modelInfo.getSelectClass()).
                build();
    }

    @NotNull
    private Stream<FieldSpec> generateColumnsConstants() {
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
    private Stream<MethodSpec> generateGetters() {
        return modelInfo.getColumnInfos().stream().map(columnInfo -> {
            Class valueClass = ColumnTypeInfo.getValueClass(columnInfo.getColumnTypeInfo().getColumnType());
            return createGetter(columnInfo.getColumnFieldName(), valueClass);
        });
    }

    @NotNull
    private Stream<FieldSpec> generateFieldsAttributes() {
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
