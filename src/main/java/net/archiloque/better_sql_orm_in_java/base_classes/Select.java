package net.archiloque.better_sql_orm_in_java.base_classes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 *
 */
public abstract class Select<T extends Model> {

    /**
     * Fetch the select.
     *
     * @return a Stream with the result.
     */
    @NotNull
    public abstract Stream<T> fetch();

    /**
     * Fetch the firest element of a select.
     *
     * @return a result.
     */
    @Nullable
    public abstract T fetchFirst();
}
