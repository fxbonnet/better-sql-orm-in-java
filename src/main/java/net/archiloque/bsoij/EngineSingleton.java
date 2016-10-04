package net.archiloque.bsoij;

import org.jetbrains.annotations.NotNull;

/**
 * Holds the engine singleton
 */
public class EngineSingleton {

    private static Engine ENGINE;

    @NotNull
    public static Engine getEngine() {
        return ENGINE;
    }

    public static void setEngine(Engine engine) {
        EngineSingleton.ENGINE = engine;
    }
}
