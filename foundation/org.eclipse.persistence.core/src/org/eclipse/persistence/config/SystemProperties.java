/*******************************************************************************
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
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
     * This property can be set to <code>false</code> to enable UPDATE call to set
     * foreign key value in the target row in unidirectional 1-Many mapping
     * with not nullable FK. In previous versions of EclipseLink this was
     * the default behaviour.
     * Allowed values are: true/false.
     */
    public static final String ONETOMANY_DEFER_INSERTS = "eclipselink.mapping.onetomany.defer-inserts";

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

    /**
     * This system property in milliseconds can control thread management in org.eclipse.persistence.internal.helper.ConcurrencyManager.
     * It control how much time loop wait before it try acquire lock for current thread again. It value is set above above 0 dead lock detection
     * mechanism and related extended logging will be activated.
     * Default value is 0 (unit is ms). Allowed values are: long
     */
    public static final String CONCURRENCY_MANAGER_ACQUIRE_WAIT_TIME = "eclipselink.concurrency.manager.waittime";

    /**
     * This system property in milliseconds can control thread management in org.eclipse.persistence.internal.helper.ConcurrencyManager.
     * It control how long we are willing to wait before firing up an exception
     * Default value is 40000 (unit is ms). Allowed values are: long
     */
    public static final String CONCURRENCY_MANAGER_MAX_SLEEP_TIME  = "eclipselink.concurrency.manager.maxsleeptime";

    /**
     * This system property in milliseconds can control thread management in org.eclipse.persistence.internal.helper.ConcurrencyManager and org.eclipse.persistence.internal.helper.ConcurrencyUtil.
     * It control how frequently the tiny dump log message is created.
     * Default value is 40000 (unit is ms). Allowed values are: long
     */
    public static final String CONCURRENCY_MANAGER_MAX_FREQUENCY_DUMP_TINY_MESSAGE = "eclipselink.concurrency.manager.maxfrequencytodumptinymessage";

    /**
     * This system property in milliseconds can control thread management in org.eclipse.persistence.internal.helper.ConcurrencyManager and org.eclipse.persistence.internal.helper.ConcurrencyUtil.
     * It control how frequently the massive dump log message is created.
     * Default value is 60000 (unit is ms). Allowed values are: long
     */
    public static final String CONCURRENCY_MANAGER_MAX_FREQUENCY_DUMP_MASSIVE_MESSAGE  = "eclipselink.concurrency.manager.maxfrequencytodumpmassivemessage";

    /**
     * true - if we want the to fire up an exception to try to get the current thread to release all of its acquired locks and allow other
     * threads to progress.
     * false - if aborting frozen thread is not effective it is preferable to not fire the interrupted exception let the system
     * In the places where use this property normally if a thread is stuck it is because it is doing object building.
     * Blowing the threads ups is not that dangerous. It can be very dangerous for production if the dead lock ends up
     * not being resolved because the productive business transactions will become cancelled if the application has a
     * limited number of retries to for example process an MDB. However, the code spots where we use this constant are
     * not as sensible as when the write lock manager is starving to run commit.
     */
    public static final String CONCURRENCY_MANAGER_ALLOW_INTERRUPTED_EXCEPTION  = "eclipselink.concurrency.manager.allow.interruptedexception";

    /**
     * true - if we want the to fire up an exception to try to get the current thread to realease all of its acquired locks and allow other
     * threads to progress.
     * false - if aborting frozen thread is not effective it is preferable to not fire the concurrency exception let the system
     * freeze and die and force the administration to kill the server. This is preferable to aborting the transactions
     * multiple times without success in resolving the dead lock and having business critical messages that after 3 JMS
     * retries are discarded out. Failing to resolve a dead lock can have terrible impact in system recovery unless we
     * have infinite retries for the business transactions.
     * Allowed values are: true/false.
     */
    public static final String CONCURRENCY_MANAGER_ALLOW_CONCURRENCY_EXCEPTION  = "eclipselink.concurrency.manager.allow.concurrency.exception";

    /**
     * true - collect debug/trace information during ReadLock acquisition
     * false - don't collect debug/trace information during ReadLock acquisition
     * Allowed values are: true/false.
     */
    public static final String CONCURRENCY_MANAGER_ALLOW_STACK_TRACE_READ_LOCK = "eclipselink.concurrency.manager.allow.readlockstacktrace";
}
