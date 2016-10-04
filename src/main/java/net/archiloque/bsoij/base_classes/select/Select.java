package net.archiloque.bsoij.base_classes.select;

import net.archiloque.bsoij.base_classes.Filter;
import net.archiloque.bsoij.base_classes.Sort;
import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.base_classes.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 */
public abstract class Select<T extends Model> {

    /**
     * Data to write the JOIN SQL
     */
    @NotNull
    private final Join[] joins;

    /**
     * Criterias to be applied.
     */
    @NotNull
    private final List<Filter> filters;

    /**
     * Sorts to be applied.
     */
    @NotNull
    private final List<Sort> sorts;

    public Select(@NotNull String[] tablesNames, @NotNull Join[] joins) {
        this.joins = joins;
        this.filters = new ArrayList<>();
        this.sorts = new ArrayList<>();
    }

    /**
     * Create a new select and add a filter
     */
    public Select(@NotNull Select<T> select, @NotNull Filter filter) {
        this.joins = select.getJoins();
        this.filters = new ArrayList<>(select.getFilters());
        this.sorts = new ArrayList<>(select.getSorts());
        this.filters.add(filter);
    }

    /**
     * Create a new select and add a sort.
     */
    public Select(@NotNull Select<T> select, @NotNull Sort sort) {
        this.joins = select.getJoins();
        this.filters = new ArrayList<>(select.getFilters());
        this.sorts = new ArrayList<>(select.getSorts());
        this.sorts.add(sort);
    }

    /**
     * Create a new select from a select
     */
    public Select(@NotNull String[] tablesNames, @NotNull Join[] joins, @NotNull Select select) {
        this.joins = joins;
        this.filters = new ArrayList<>(select.getFilters());
        this.sorts = new ArrayList<>(select.getSorts());
    }


    @NotNull
    public abstract String[] getTablesNames();

    @NotNull
    public abstract Field[] getFields();

    @NotNull
    public Join[] getJoins() {
        return joins;
    }

    /**
     * Fetch the select.
     *
     * @return a Stream with the result.
     */
    @NotNull
    public abstract Stream<T> fetch();

    /**
     * Fetch the first element of a select.
     *
     * @return a result.
     */
    @NotNull
    public abstract Optional<T> fetchFirst();

    @NotNull
    public abstract T instantiateModel();

    @NotNull
    public List<Filter> getFilters() {
        return filters;
    }

    @NotNull
    public List<Sort> getSorts() {
        return sorts;
    }

    public static class Join {

        @NotNull
        private final Field from;

        @NotNull
        private final Field to;

        public Join(@NotNull Field from, @NotNull Field to) {
            this.from = from;
            this.to = to;
        }

        @NotNull
        public Field getFrom() {
            return from;
        }

        @NotNull
        public Field getTo() {
            return to;
        }
    }


}
