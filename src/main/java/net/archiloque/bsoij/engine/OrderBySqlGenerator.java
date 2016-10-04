package net.archiloque.bsoij.engine;

import net.archiloque.bsoij.base_classes.Sort;
import net.archiloque.bsoij.db_specific.DbTranslator;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class OrderBySqlGenerator {

    @NotNull
    private final Sort sort;


    @NotNull
    private final DbTranslator dbTranslator;


    public OrderBySqlGenerator(@NotNull Sort sort, @NotNull DbTranslator dbTranslator) {
        this.sort = sort;
        this.dbTranslator = dbTranslator;
    }

    @NotNull
    public String generateSql() {
        return dbTranslator.escapeTableName(sort.getField().getTableName()) +
                "." +
                dbTranslator.escapeColumnName(sort.getField().getColumnName()) +
                " " +
                sort.getOrder().name();
    }
}
