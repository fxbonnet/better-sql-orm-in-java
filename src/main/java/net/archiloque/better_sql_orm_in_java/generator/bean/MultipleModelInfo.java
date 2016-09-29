package net.archiloque.better_sql_orm_in_java.generator.bean;

import org.jetbrains.annotations.NotNull;

/**
 * Infos for multiple models
 */
public class MultipleModelInfo {

    @NotNull
    private final ModelInfo[] modelInfos;

    public MultipleModelInfo(@NotNull ModelInfo[] modelInfos) {
        this.modelInfos = modelInfos;
    }

    @NotNull
    public ModelInfo[] getModelInfos() {
        return modelInfos;
    }
}
