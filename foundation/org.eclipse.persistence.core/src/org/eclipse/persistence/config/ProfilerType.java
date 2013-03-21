/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
 
 package org.eclipse.persistence.config;


/**
 * Profiler type persistence property values.
 * 
 * <p>JPA persistence property Usage:
 * 
 * <p><code>properties.add(PersistenceUnitProperties.PROFILER, ProfilerType.PerformanceProfiler);</code>
 * <p>Property values are case-insensitive.
 * 
 * @see QueryMonitor
 * @see PerformanceProfiler
 */
public class ProfilerType {
    //A tool used to provide high level performance profiling information
    public static final String PerformanceProfiler = "PerformanceProfiler";
    public static final String QueryMonitor = "QueryMonitor";
    public static final String PerformanceMonitor = "PerformanceMonitor";
    public static final String DMSProfiler = "DMSProfiler";
    public static final String NoProfiler = "NoProfiler";

    public static final String DEFAULT = NoProfiler;
    
    public static final String DMSProfilerClassName = "org.eclipse.persistence.tools.profiler.oracle.DMSPerformanceProfiler";
}

