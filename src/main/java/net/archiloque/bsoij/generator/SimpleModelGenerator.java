package net.archiloque.bsoij.generator;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.bsoij.base_classes.ColumnType;
import net.archiloque.bsoij.base_classes.Criteria;
import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import net.archiloque.bsoij.generator.bean.ColumnInfo;
import net.archiloque.bsoij.generator.bean.ColumnTypeInfo;
import net.archiloque.bsoij.generator.bean.SchemaInfo;
import net.archiloque.bsoij.generator.bean.SimpleModelInfo;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * create simple models
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

    public void create() throws IOException {
        TypeSpec.Builder classBuilder = initializeClass(modelInfo, SimpleModel.class);

        classBuilder.addMethod(createSelect());
        classBuilder.addField(createTableNameConstant());

        createFieldsAttributes().forEach(classBuilder::addField);

        createGetters().forEach(classBuilder::addMethod);
        createSetters().forEach(classBuilder::addMethod);

        createFieldsConstants().forEach(classBuilder::addField);

        classBuilder.addField(createFieldsConstant());
        classBuilder.addMethod(createFieldsMethod());
        classBuilder.addMethod(createTableNameMethod());
        classBuilder.addMethod(createPrimaryKeyMethod());

        createColumnTypes().forEach(classBuilder::addType);

        createForeignKeysFetch().forEach(classBuilder::addMethod);
        createForeignedKeysFetchMethods().forEach(classBuilder::addMethod);
        createForeignKeySet().forEach(classBuilder::addMethod);

        // Write the class
        writeClass(classBuilder);
    }

    @NotNull
    private FieldSpec createTableNameConstant() {
        return FieldSpec.
                builder(String.class, TABLE_NAME_CONSTANT).
                addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).
                initializer("$S", modelInfo.getModel().getTableName()).
                build();
    }

    @NotNull
    private MethodSpec createTableNameMethod() {
        return MethodSpec.
                methodBuilder("getTableName").
                addModifiers(Modifier.PUBLIC).
                returns(String.class).
                addAnnotation(NotNull.class).
                addAnnotation(Override.class).
                addStatement("return " + TABLE_NAME_CONSTANT).
                build();
    }

    @NotNull
    private FieldSpec createFieldsConstant() {
        String statement = "new $T[]{\n";
        List<Object> statementParams = new ArrayList<>();
        statementParams.add(Field.class);
        statement += modelInfo.
                getColumnInfos().
                stream().
                map(ColumnInfo::getColumnConstantName).
                collect(Collectors.joining(", "));
        statement += "}";

        return FieldSpec.
                builder(ArrayTypeName.of(Field.class), FIELDS_CONSTANT).
                addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).
                initializer(statement, statementParams.toArray()).
                build();
    }


    @NotNull
    private Stream<MethodSpec> createForeignKeysFetch() {
        return modelInfo.getForeignKeyInfos().stream().map(foreignKeyInfo -> {
            // create something like
            //   public CustomerModel fetchCustomer() {
            //      return CustomerModel.select().where(CustomerModel.CUSTOMER_ID, Criteria.integerEquals(customerId)).fetchFirst();
            // }

            String methodName = "fetch" + WordUtils.capitalize(foreignKeyInfo.getForeignKey().getName());

            ClassName targetModelClassName = foreignKeyInfo.getTargetModel().getModelClass();
            ColumnInfo targetPrimaryKeyColumnInfo = foreignKeyInfo.getTargetModel().getPrimaryKeyInfo().getColumnInfo();
            boolean nullableReturn = targetPrimaryKeyColumnInfo.getColumn().isNullable();
            TypeName returnType = nullableReturn ?
                    ParameterizedTypeName.get(ClassName.get(Optional.class), targetModelClassName) : targetModelClassName;

            String statement = "return $T." +
                    "select().where($T." + targetPrimaryKeyColumnInfo.getColumnConstantName() + "," +
                    "$T." + ColumnTypeInfo.getCriteriaEquals(targetPrimaryKeyColumnInfo.getColumnTypeInfo().getColumnType()) + "(" +
                    foreignKeyInfo.getColumnInfo().getColumnFieldName() + ")).fetchFirst()";

            if (!nullableReturn) {
                statement += ".get()";
            }

            return MethodSpec.
                    methodBuilder(methodName).
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
    
    @NotNull Stream<MethodSpec> createForeignKeySet(){
        return modelInfo.getForeignKeyInfos().stream().map(foreignKeyInfo -> {
            String methodName = "set" + WordUtils.capitalize(foreignKeyInfo.getForeignKey().getName());
            ParameterSpec parameterSpec = ParameterSpec.builder(
                    foreignKeyInfo.getTargetModel().getModelClass(),
                    foreignKeyInfo.getForeignKey().getName()).
                    addAnnotation(foreignKeyInfo.getColumnInfo().getColumnTypeInfo().isNullable() ? Nullable.class : NotNull.class).
                    build();
            return MethodSpec.
                    methodBuilder(methodName).
                    addModifiers(Modifier.PUBLIC).
                    addAnnotation(NotNull.class).
                    addParameter(parameterSpec).
                    addStatement(
                            "this." +
                                    foreignKeyInfo.getColumnInfo().getColumnFieldName() +
                                    " = " +
                                    foreignKeyInfo.getForeignKey().getName() +
                                    "." +
                                    foreignKeyInfo.getTargetModel().getPrimaryKeyInfo().getColumnInfo().getGetterName() +
                                    "()"
                    ).
                    build();
        });
    }

    @NotNull
    private Stream<MethodSpec> createForeignedKeysFetchMethods() {
        return modelInfo.getForeignKeyedInfos().stream().map(foreignKeyInfo -> {
            // create something like
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

            return MethodSpec.
                    methodBuilder(methodName).
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
    private MethodSpec createSelect() {
        return MethodSpec.
                methodBuilder("select").
                addModifiers(Modifier.PUBLIC, Modifier.STATIC).
                addAnnotation(NotNull.class).
                returns(modelInfo.getSelectClass()).
                addStatement("return new $T()", modelInfo.getSelectClass()).
                build();
    }

    @NotNull
    private Stream<FieldSpec> createFieldsConstants() {
        return modelInfo.getColumnInfos().stream().map(columnInfo -> {
            ColumnTypeInfo columnTypeInfo = columnInfo.getColumnTypeInfo();
            String columnName = columnInfo.getColumn().getName();
            return FieldSpec.
                    builder(columnTypeInfo.getShortClassName(), columnInfo.getColumnConstantName()).
                    addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).
                    initializer(
                            "new " + columnTypeInfo.getClassName().simpleName() + "($S, $T::" + columnInfo.getGetterName() + ", $T::" + columnInfo.getSetterName() + ")",
                            columnName,
                            modelInfo.getModelClass(),
                            modelInfo.getModelClass()
                    ).
                    build();
        });
    }

    @NotNull
    private MethodSpec createPrimaryKeyMethod() {
        ColumnInfo columnInfo = modelInfo.getPrimaryKeyInfo().getColumnInfo();

        return MethodSpec.
                methodBuilder("getPrimaryKeyField").
                addModifiers(Modifier.PUBLIC).
                returns(columnInfo.getColumnTypeInfo().getShortClassName()).
                addAnnotation(NotNull.class).
                addAnnotation(Override.class).
                addStatement("return " + columnInfo.getColumnConstantName()).
                build();
    }

    @NotNull
    private Stream<MethodSpec> createGetters() {
        return modelInfo.getColumnInfos().stream().map(columnInfo -> {
            Class valueClass = ColumnTypeInfo.getValueClass(columnInfo.getColumnTypeInfo().getColumnType());
            return MethodSpec.
                    methodBuilder(columnInfo.getGetterName()).
                    addModifiers(Modifier.PUBLIC).
                    returns(valueClass).
                    addStatement("return " + columnInfo.getColumnFieldName()).
                    build();
        });
    }

    @NotNull
    private Stream<MethodSpec> createSetters() {
        return modelInfo.getColumnInfos().stream().map(columnInfo -> {
            Class valueClass = ColumnTypeInfo.getValueClass(columnInfo.getColumnTypeInfo().getColumnType());
            String columnFieldName = columnInfo.getColumnFieldName();
            return MethodSpec.
                    methodBuilder(columnInfo.getSetterName()).
                    addModifiers(Modifier.PUBLIC).
                    addParameter(valueClass, columnFieldName).
                    addStatement("this." + columnFieldName + " = " + columnFieldName).
                    build();
        });
    }

    @NotNull
    private Stream<FieldSpec> createFieldsAttributes() {
        return modelInfo.getColumnInfos().stream().map(columnInfo -> {
            Class valueClass = ColumnTypeInfo.getValueClass(columnInfo.getColumnTypeInfo().getColumnType());
            return FieldSpec.
                    builder(valueClass, columnInfo.getColumnFieldName()).
                    addModifiers(Modifier.PRIVATE).
                    build();
        });
    }

    @NotNull
    private Stream<TypeSpec> createColumnTypes() {
        return modelInfo.getColumnsTypes().stream().map(columnTypeAndNullable -> {
            Class<? extends Field> fieldType = columnTypeAndNullable.getFieldType();
            ClassName className = ColumnTypeInfo.getClassName(schemaInfo, modelInfo, fieldType);
            MethodSpec columnTypeConstructor = MethodSpec.
                    constructorBuilder().
                    addModifiers(Modifier.PRIVATE).
                    addParameter(String.class, "columnName").
                    addParameter(createGetterFunctionType(columnTypeAndNullable.getColumnType()), "getter").
                    addParameter(createSetterFunctionType(columnTypeAndNullable.getColumnType()), "setter").
                    addStatement("super($S, columnName, getter, setter)", modelInfo.getModel().getTableName()).
                    build();

            return TypeSpec.
                    classBuilder(className).
                    addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).
                    superclass(ParameterizedTypeName.get(ClassName.get(fieldType), modelInfo.getModelClass())).
                    addMethod(columnTypeConstructor).
                    build();
        });
    }

    @NotNull
    private TypeName createGetterFunctionType(@NotNull ColumnType columnType) {
        return ParameterizedTypeName.get(
                ClassName.get(Function.class),
                modelInfo.getModelClass(),
                ClassName.get(ColumnTypeInfo.getValueClass(columnType))
        );
    }

    @NotNull
    private TypeName createSetterFunctionType(@NotNull ColumnType columnType) {
        return ParameterizedTypeName.get(
                ClassName.get(BiConsumer.class),
                modelInfo.getModelClass(),
                ClassName.get(ColumnTypeInfo.getValueClass(columnType))
        );
    }


}
