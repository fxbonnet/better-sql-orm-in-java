package net.archiloque.bsoij.engine;

import net.archiloque.bsoij.base_classes.select.Select;
import net.archiloque.bsoij.db_specific.DbTranslator;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class JoinSqlGenerator {

    @NotNull
    private final Select.Join join;

    @NotNull
    private final DbTranslator dbTranslator;

    public JoinSqlGenerator(@NotNull Select.Join join, @NotNull DbTranslator dbTranslator) {
        this.join = join;
        this.dbTranslator = dbTranslator;
    }


    @NotNull
    public String generateSql() {
        return dbTranslator.escapeTableName(join.getFrom().getTableName()) +
                "." +
                dbTranslator.escapeColumnName(join.getFrom().getColumnName()) +
                " = " +
                dbTranslator.escapeTableName(join.getTo().getTableName()) +
                "." +
                dbTranslator.escapeColumnName(join.getTo().getColumnName());
    }
}
