/*******************************************************************************
 * Copyright (c) 1998, 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - added to allow pluggage archive factory
 ******************************************************************************/  
package org.eclipse.persistence.config;

/**
 * This class provides the list of System properties that are recognized by EclipseLink.
 * @author tware
 *
 */
public class SystemProperties {
    
    /**
     * Configures the factory class we use to produce instances of org.eclispe.persistence.jpa.Archive
     * These instances are used to examine persistence units and the files within them and are used for discovery
     * of classes in the persistence unit
     * Allows user-provided ArchiveFactory and Archive
     */
    public static final String ARCHIVE_FACTORY = "eclipselink.archive.factory";
    
    /**
     * This property is used to debug weaving issues.   When it is set, weaved classes will be
     * output to the given path as they are weaved
     */
    public static final String WEAVING_OUTPUT_PATH = "eclipselink.weaving.output.path";

    /**
     * This property is used in conjunction with WEAVING_OUTPUT_PATH.  By default, existing classes
     * on the path provided to WEAVING_OUTPUT_PATH will not be overridden.  If this is set to true, they will be
     */
    public static final String WEAVING_SHOULD_OVERWRITE = "eclipselink.weaving.overwrite.existing";
    
    /**
     * This property can be used to tell EclipseLink to process classes in the ASM Default manner.  The fix for bug
     * 370975 changes EclipseLink's weaving support to use ASM itself to examine class hierarchies.  Setting this flag to
     * true will cause us to use the default reflection mechanism again.  This flag provides a means to workaround any issues encountered with
     * the ASM-based weaving introspection
     */
    public static final String WEAVING_REFLECTIVE_INTROSPECTION = "eclipselink.weaving.reflective-introspection";

    /**
     * This property is used in conjunction with
     * org.eclipse.persistence.sessions.IdentityMapAccessor.printIdentityMapLocks().
     * Setting this property will cause EclipseLink to record the stack trace of
     * the lock acquisition and print it along with the identity map locks. This
     * should only be set if the thread that owns a lock is not 'stuck' but
     * still owns the lock when a normal printIdentityMapLocks is done.
     * 
     * This can also be set in code statically through ConcurrencyManager.setShouldTrackStack(true)
     */
    public static final String RECORD_STACK_ON_LOCK = "eclipselink.cache.record-stack-on-lock";
    
    /**
     * This property can be set to disable processing of X-Many relationship
     * attributes for Query By Example objects. In previous versions of
     * EclipseLink these attributes would have been ignored but as of this
     * release they will be processed into the expression.
     */
    public static final String DO_NOT_PROCESS_XTOMANY_FOR_QBE = "eclipselink.query.query-by-example.ignore-xtomany";

    /**
     * This system property can be set to override target server platform set by the Java EE container
     * with the one either set in persistence.xml or auto detected.
     */
    public static final String ENFORCE_TARGET_SERVER = "eclipselink.target-server.enforce";
    
    /**
     * This system property is used by the IndirectCollectionsFactory to configure if the Java SE 7 API-specific  
     * indirect collection implementation classes should be instantiated at runtime. Configure this system property 
     * to true if the Java SE 7 API-specific indirect collection classes should be used instead of the 
     * Java SE 8 API-specific indirect collection classes.
     * 
     * If the Java SE 8 API-specific indirect collection classes should be used at runtime when running in a 
     * Java SE 8 JVM (the default IndirectCollectionsFactory behavior), or when running in a Java SE 7 JVM, 
     * do not configure this system property, and the default behavior will be used.
     * 
     * Note: This (deprecated) API is specific to EclipseLink 2.6 only.
     */
    @Deprecated
    public static final String JAVASE7_INDIRECT_COLLECTIONS = "eclipselink.indirection.javase7-indirect-collections";
    
}
