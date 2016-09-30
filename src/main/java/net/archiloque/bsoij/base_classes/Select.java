package net.archiloque.bsoij.base_classes;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
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
    @NotNull
    public abstract Optional<T> fetchFirst();
}
