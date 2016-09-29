package net.archiloque.better_sql_orm_in_java.base_classes;

import java.util.stream.Stream;

/**
 *
 */
public abstract class Select<T extends Model> {

    /**
     * Fetch the select.
     * @return a stream with the result.
     */
    public abstract Stream<T> fetch();
}
