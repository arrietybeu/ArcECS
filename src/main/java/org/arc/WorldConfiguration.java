package org.arc;

import lombok.Getter;

/**
 * Configuration class for World instances.
 * Allows customization of world behavior and performance characteristics.
 *
 * @author Arriety
 */
public class WorldConfiguration {

    /**
     * -- GETTER --
     *  Gets the expected entity count.
     *
     * @return the expected entity count
     */
    @Getter
    private int expectedEntityCount = 100;
    /**
     * -- GETTER --
     *  Gets the expected component count.
     *
     * @return the expected component count
     */
    @Getter
    private int expectedComponentCount = 200;
    private boolean enableEntityCaching = true;
    private boolean enableComponentPooling = false;
    /**
     * -- GETTER --
     *  Gets the fixed time step.
     *
     * @return the fixed time step in seconds
     */
    @Getter
    private float fixedTimeStep = 0.016f; // 60 FPS
    private boolean enableProfiling = false;

    /**
     * Creates a new world configuration with default settings.
     */
    public WorldConfiguration() {
        // Default configuration
    }

    /**
     * Thiết lập số lượng entity dự kiến để tối ưu hóa bộ nhớ.
     *
     * @param expectedEntityCount số lượng entity dự kiến
     * @return trả về cấu hình hiện tại để có thể gọi nối tiếp phương thức
     */

    public WorldConfiguration setExpectedEntityCount(int expectedEntityCount) {
        this.expectedEntityCount = Math.max(1, expectedEntityCount);
        return this;
    }

    /**
     * Sets the expected number of components for memory optimization.
     *
     * @param expectedComponentCount the expected component count
     * @return this configuration for method chaining
     */
    public WorldConfiguration setExpectedComponentCount(int expectedComponentCount) {
        this.expectedComponentCount = Math.max(1, expectedComponentCount);
        return this;
    }

    /**
     * Enables or disables entity caching for better performance.
     *
     * @param enableEntityCaching true to enable caching, false to disable
     * @return this configuration for method chaining
     */
    public WorldConfiguration setEntityCaching(boolean enableEntityCaching) {
        this.enableEntityCaching = enableEntityCaching;
        return this;
    }

    /**
     * Checks if entity caching is enabled.
     *
     * @return true if caching is enabled, false otherwise
     */
    public boolean isEntityCachingEnabled() {
        return enableEntityCaching;
    }

    /**
     * Enables or disables component pooling for memory efficiency.
     *
     * @param enableComponentPooling true to enable pooling, false to disable
     * @return this configuration for method chaining
     */
    public WorldConfiguration setComponentPooling(boolean enableComponentPooling) {
        this.enableComponentPooling = enableComponentPooling;
        return this;
    }

    /**
     * Checks if component pooling is enabled.
     *
     * @return true if pooling is enabled, false otherwise
     */
    public boolean isComponentPoolingEnabled() {
        return enableComponentPooling;
    }

    /**
     * Sets the fixed time step for deterministic updates.
     *
     * @param fixedTimeStep the fixed time step in seconds
     * @return this configuration for method chaining
     */
    public WorldConfiguration setFixedTimeStep(float fixedTimeStep) {
        this.fixedTimeStep = Math.max(0.001f, fixedTimeStep);
        return this;
    }

    /**
     * Enables or disables performance profiling.
     *
     * @param enableProfiling true to enable profiling, false to disable
     * @return this configuration for method chaining
     */
    public WorldConfiguration setProfiling(boolean enableProfiling) {
        this.enableProfiling = enableProfiling;
        return this;
    }

    /**
     * Checks if profiling is enabled.
     *
     * @return true if profiling is enabled, false otherwise
     */
    public boolean isProfilingEnabled() {
        return enableProfiling;
    }

    @Override
    public String toString() {
        return String.format(
                "WorldConfiguration{entities=%d, components=%d, caching=%b, pooling=%b, timeStep=%.3f, profiling=%b}",
                expectedEntityCount, expectedComponentCount, enableEntityCaching,
                enableComponentPooling, fixedTimeStep, enableProfiling
        );
    }
} 