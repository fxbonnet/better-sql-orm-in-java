package net.archiloque.bsoij.base_classes.select;

import net.archiloque.bsoij.base_classes.Filter;
import net.archiloque.bsoij.base_classes.Sort;
import net.archiloque.bsoij.base_classes.TableAndModel;
import net.archiloque.bsoij.base_classes.model.MultipleModel;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public abstract class MultipleSelect<M extends MultipleModel> extends Select<M> {

    public MultipleSelect(@NotNull String[] tablesNames, @NotNull Join[] joins) {
        super(tablesNames, joins);
    }

    public MultipleSelect(@NotNull Select<M> select, @NotNull Filter filter) {
        super(select, filter);
    }

    public MultipleSelect(@NotNull Select<M> select, @NotNull Sort sort) {
        super(select, sort);
    }

    public MultipleSelect(@NotNull String[] tablesNames, @NotNull Join[] joins, @NotNull Select select) {
        super(tablesNames, joins, select);
    }

    @NotNull
    public abstract TableAndModel[] getTablesAndModels();

}
