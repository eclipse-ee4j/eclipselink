package org.eclipse.persistence.jpa.config;


/**
 * Profiler type persistence property values.
 * 
 * <p>JPA persistence property Usage:
 * 
 * <p><code>properties.add(PersistenceUnitProperties.ProfilerType, ProfilerType.PerformanceProfiler);</code>
 * <p>Property values are case-insensitive.
 * 
 * @see QueryMonitor
 * @see PerformanceProfiler
 */
public class ProfilerType {
    //A tool used to provide high level performance profiling information
    public static final String PerformanceProfiler = "PerformanceProfiler";
    public static final String QueryMonitor = "QueryMonitor";
    public static final String NoProfiler = "NoProfiler";

    public static final String DEFAULT = NoProfiler;
}

