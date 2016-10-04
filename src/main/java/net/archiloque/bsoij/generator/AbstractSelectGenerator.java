package net.archiloque.bsoij.generator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import net.archiloque.bsoij.EngineSingleton;
import net.archiloque.bsoij.base_classes.Criteria;
import net.archiloque.bsoij.base_classes.Filter;
import net.archiloque.bsoij.base_classes.Sort;
import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.base_classes.select.Select;
import net.archiloque.bsoij.generator.bean.AbstractModelInfo;
import net.archiloque.bsoij.generator.bean.ColumnTypeInfo;
import net.archiloque.bsoij.generator.bean.ForeignKeyInfo;
import net.archiloque.bsoij.generator.bean.SchemaInfo;
import net.archiloque.bsoij.generator.bean.SimpleModelInfo;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static net.archiloque.bsoij.generator.AbstractModelGenerator.FIELDS_CONSTANT;
import static net.archiloque.bsoij.generator.AbstractModelGenerator.TABLE_NAME_CONSTANT;

/**
 *
 */
public abstract class AbstractSelectGenerator {

    static final String TABLES_NAME_CONSTANT = "TABLES_NAME";
    static final String JOINS_CONSTANT = "JOINS";

    private final Logger logger = Logger.getLogger(AbstractSelectGenerator.class.getName());

    @NotNull
    final File basePath;

    @NotNull
    final File selectBasePath;

    @NotNull
    final SchemaInfo schemaInfo;

    public AbstractSelectGenerator(
            @NotNull File basePath,
            @NotNull File selectBasePath,
            @NotNull SchemaInfo schemaInfo) {
        this.basePath = basePath;
        this.selectBasePath = selectBasePath;
        this.schemaInfo = schemaInfo;
    }

    @NotNull
    TypeSpec.Builder initiatilizeClass(
            @NotNull Class<? extends Select> parentClass,
            @NotNull AbstractModelInfo modelInfo,
            @NotNull SimpleModelInfo[] modelInfos,
            @NotNull ForeignKeyInfo[] foreignKeyInfos) {
        File selectFile = new File(selectBasePath, modelInfo.getSelectClass().simpleName() + ".java");
        logger.info("Generating Select for [" + modelInfo.getSelectClass().simpleName() + "] at [" + selectFile.getAbsolutePath() + "]");

        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(modelInfo.getSelectClass()).
                addModifiers(Modifier.PUBLIC, Modifier.FINAL).
                addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unused").build()).
                addJavadoc("This class has been generated, DO NOT EDIT IT MANUALLY !!\n").
                superclass(ParameterizedTypeName.get(ClassName.get(parentClass), modelInfo.getModelClass()));

        typeBuilder.addField(createTablesNamesConstant(modelInfos));
        typeBuilder.addMethod(createTableNameMethod());
        typeBuilder.addMethod(createFieldsMethod(modelInfo));
        typeBuilder.addField(createJoinsConstant(foreignKeyInfos));
        typeBuilder.addMethod(
                MethodSpec.
                        constructorBuilder().
                        addModifiers(Modifier.PUBLIC).
                        addStatement("super(" + TABLES_NAME_CONSTANT + ", " + JOINS_CONSTANT + ")").
                        build()
        );

        typeBuilder.addMethod(
                MethodSpec.
                        constructorBuilder().
                        addParameter(modelInfo.getSelectClass(), modelInfo.getSelectParam()).
                        addParameter(Sort.class, "sort").
                        addModifiers(Modifier.PRIVATE).
                        addStatement("super(" + modelInfo.getSelectParam() + ", sort)").
                        build()
        );

        typeBuilder.addMethod(
                MethodSpec.
                        constructorBuilder().
                        addParameter(modelInfo.getSelectClass(), modelInfo.getSelectParam()).
                        addParameter(Filter.class, "filter").
                        addModifiers(Modifier.PRIVATE).
                        addStatement("super(" + modelInfo.getSelectParam() + ", filter)").
                        build()
        );
        typeBuilder.addMethod(createInstanciateModelMethod(modelInfo));

        return typeBuilder;
    }

    @NotNull
    private MethodSpec createInstanciateModelMethod(@NotNull AbstractModelInfo modelInfo) {
        return MethodSpec.
                methodBuilder("instantiateModel").
                addModifiers(Modifier.PUBLIC).
                returns(modelInfo.getModelClass()).
                addAnnotation(NotNull.class).
                addAnnotation(Override.class).
                addStatement("return new $T()", modelInfo.getModelClass()).
                build();
    }

    /**
     * Constants containing the list of tables
     * private static final String[] TABLES_NAMES = {"customer", "order"};
     */
    @NotNull
    private FieldSpec createTablesNamesConstant(@NotNull SimpleModelInfo[] modelInfos) {
        List<Object> statementParams = new ArrayList<>();
        String statement = "new $T[]{";
        statementParams.add(String.class);
        for (SimpleModelInfo info : modelInfos) {
            statementParams.add(info.getModelClass());
        }
        statement += String.join(", ", Collections.nCopies(modelInfos.length, "$T." + TABLE_NAME_CONSTANT));
        statement += "}";

        return FieldSpec.
                builder(ArrayTypeName.of(String.class), TABLES_NAME_CONSTANT).
                addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC).
                initializer(
                        statement,
                        statementParams.toArray()
                ).
                build();
    }

    @NotNull
    private MethodSpec createTableNameMethod() {
        return MethodSpec.
                methodBuilder("getTablesNames").
                addModifiers(Modifier.PUBLIC).
                returns(ArrayTypeName.of(String.class)).
                addAnnotation(NotNull.class).
                addAnnotation(Override.class).
                addStatement("return " + TABLES_NAME_CONSTANT).
                build();
    }

    /**
     * Constants containing the list of joins
     * private static final Join[] JOINS = {
     * new Join(
     * new TableAndField("order", OrderModel.CUSTOMER_ID),
     * new TableAndField("customer", CustomerModel.CUSTOMER_ID)
     * )};
     */
    @NotNull
    private FieldSpec createJoinsConstant(@NotNull ForeignKeyInfo[] foreignKeyInfos) {
        List<Object> statementParams = new ArrayList<>();
        String statement = "new $T[]{\n";
        statementParams.add(Select.Join.class);
        statement += stream(foreignKeyInfos).map(foreignKeyInfo -> {
            statementParams.add(Select.Join.class);

            statementParams.add(foreignKeyInfo.getSourceModel().getModelClass());
            String sourceConstantName = foreignKeyInfo.getColumnInfo().getColumnConstantName();

            statementParams.add(foreignKeyInfo.getTargetModel().getModelClass());
            String targetConstantName = foreignKeyInfo.getTargetModel().getPrimaryKeyInfo().getColumnInfo().getColumnConstantName();

            return "  new $T($T." + sourceConstantName + ", $T." + targetConstantName + ")";

        }).collect(Collectors.joining(",\n"));
        statement += "}";

        return FieldSpec.
                builder(ArrayTypeName.of(Select.Join.class), JOINS_CONSTANT).
                addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC).
                initializer(
                        statement,
                        statementParams.toArray()
                ).
                build();
    }

    void writeClass(TypeSpec.Builder classBuilder) throws IOException {
        JavaFile.
                builder(schemaInfo.getSelectPackage(), classBuilder.build()).
                build().writeTo(basePath);
    }


    @NotNull
    MethodSpec createFetch(AbstractModelInfo modelInfo) {
        return MethodSpec.methodBuilder("fetch").
                addModifiers(Modifier.PUBLIC).
                addAnnotation(Override.class).
                addAnnotation(NotNull.class).
                returns(ParameterizedTypeName.get(ClassName.get(Stream.class), modelInfo.getModelClass())).
                addStatement("return $T.getEngine().fetch(this)", EngineSingleton.class).
                build();
    }

    @NotNull
    MethodSpec createFetchFirst(AbstractModelInfo modelInfo) {
        return MethodSpec.methodBuilder("fetchFirst").
                addModifiers(Modifier.PUBLIC).
                addAnnotation(Override.class).
                addAnnotation(NotNull.class).
                returns(ParameterizedTypeName.get(ClassName.get(Optional.class), modelInfo.getModelClass())).
                addStatement("return $T.getEngine().fetchFirst(this)", EngineSingleton.class).
                build();
    }


    @NotNull
    Stream<MethodSpec> createWheres(
            @NotNull SimpleModelInfo modelInfo,
            @NotNull AbstractModelInfo returnModelInfo) {
        return modelInfo.getColumnsTypes().stream().map(columnTypeAndNullable -> {

            // if it's nullable there's two methods :
            // one with the nullable criteria and one with the non nullable one
            List<MethodSpec> result = new ArrayList<>();


            result.add(createWhere(
                    modelInfo,
                    columnTypeAndNullable,
                    returnModelInfo,
                    ColumnTypeInfo.getCriteria(columnTypeAndNullable.getColumnType(), columnTypeAndNullable.isNullable())
            ));
            if (columnTypeAndNullable.isNullable()) {
                result.add(createWhere(
                        modelInfo,
                        columnTypeAndNullable,
                        returnModelInfo,
                        ColumnTypeInfo.getCriteria(columnTypeAndNullable.getColumnType(), false)
                ));
            }

            return result;
        }).flatMap(Collection::stream);
    }

    @NotNull
    private MethodSpec createWhere(
            @NotNull SimpleModelInfo modelInfo,
            @NotNull SimpleModelInfo.ColumnTypeAndNullable columnTypeAndNullable,
            @NotNull AbstractModelInfo returnModelInfo,
            @NotNull Class<? extends Criteria> criteriaClass) {
        ClassName fieldClassName = ColumnTypeInfo.getClassName(schemaInfo, modelInfo, columnTypeAndNullable.getFieldType());
        ClassName realFieldClassName = ClassName.get(schemaInfo.getModelPackage(), modelInfo.getModelClass().simpleName(), fieldClassName.simpleName());

        String statement = "return new $T(this, new $T(criteria, field))";
        Object[] statementParams = new Object[]{
                returnModelInfo.getShortSelectClass(),
                ParameterizedTypeName.get(
                        ClassName.get(Filter.class),
                        modelInfo.getModelClass(),
                        ClassName.get(ColumnTypeInfo.getValueClass(columnTypeAndNullable.getColumnType()))
                )
        };

        return MethodSpec.methodBuilder("where").
                addParameter(realFieldClassName, "field").
                addParameter(criteriaClass, "criteria").
                addModifiers(Modifier.PUBLIC).
                addAnnotation(NotNull.class).
                returns(returnModelInfo.getShortSelectClass()).
                addStatement(statement, statementParams).
                build();
    }

    @NotNull
    Stream<MethodSpec> createOrdersMethods(SimpleModelInfo modelInfo, AbstractModelInfo returnModelInfo) {
        return modelInfo.getColumnsTypes().stream().map(columnTypeAndNullable -> {
            Class<? extends Field> fieldType = columnTypeAndNullable.getFieldType();
            ClassName fieldClassName = ColumnTypeInfo.getClassName(schemaInfo, modelInfo, fieldType);
            ClassName realFieldClassName = ClassName.get(schemaInfo.getModelPackage(), modelInfo.getModelClass().simpleName(), fieldClassName.simpleName());

            String statement = "return new $T(this, new $T(field, order))";
            Object[] statementParams = new Object[]{
                    returnModelInfo.getShortSelectClass(),
                    ParameterizedTypeName.get(
                            ClassName.get(Sort.class),
                            modelInfo.getModelClass(),
                            ClassName.get(ColumnTypeInfo.getValueClass(columnTypeAndNullable.getColumnType()))
                    )
            };
            return MethodSpec.methodBuilder("order").
                    addParameter(realFieldClassName, "field").
                    addParameter(Sort.Order.class, "order").
                    addModifiers(Modifier.PUBLIC).
                    addAnnotation(NotNull.class).
                    returns(returnModelInfo.getShortSelectClass()).
                    addStatement(statement, statementParams).
                    build();
        });
    }


    @NotNull
    private MethodSpec createFieldsMethod(@NotNull AbstractModelInfo modelInfo) {
        return MethodSpec.
                methodBuilder("getFields").
                addModifiers(Modifier.PUBLIC).
                returns(ArrayTypeName.of(Field.class)).
                addAnnotation(NotNull.class).
                addAnnotation(Override.class).
                addStatement("return $T." + FIELDS_CONSTANT, modelInfo.getModelClass()).
                build();
    }
}
