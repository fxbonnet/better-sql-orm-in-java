package net.archiloque.bsoij.base_classes.select;

import net.archiloque.bsoij.base_classes.Filter;
import net.archiloque.bsoij.base_classes.Sort;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public abstract class SimpleSelect<S extends SimpleModel> extends Select<S> {

    public SimpleSelect(@NotNull String[] tablesNames, @NotNull Join[] joins) {
        super(tablesNames, joins);
    }

    public SimpleSelect(@NotNull Select<S> select, @NotNull Filter filter) {
        super(select, filter);
    }

    public SimpleSelect(@NotNull Select<S> select, @NotNull Sort sort) {
        super(select, sort);
    }

    public SimpleSelect(@NotNull String[] tablesNames, @NotNull Join[] joins, @NotNull Select select) {
        super(tablesNames, joins, select);
    }

}
