/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.sessions.server.*;

/**
 * <b>Purpose</b>: Maintain all of the TopLink configuration information for a system.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Project options and defaults
 * <li> Database login information
 * <li> Descriptors
 * <li> Validate Descriptors
 * <li> Maintain sequencing information & other project options
 * </ul>
 *
 * @see DatabaseLogin
 */
public class Project implements Serializable, Cloneable {
    protected String name;
    protected Login datasourceLogin;
    protected Map descriptors;
    protected Vector orderedDescriptors;

    /** Holds the default set of read-only classes that apply to each UnitOfWork. */
    protected Vector defaultReadOnlyClasses;

    /** Cache the EJBQL descriptor aliases. */
    protected Map aliasDescriptors;

    /** Cache if any descriptor is isolated. (set during initialization) */
    protected boolean hasIsolatedClasses;
    /** Cache if any descriptor has history. (set during initialization) */
    protected boolean hasGenericHistorySupport;
    /** Cache if any descriptor is using ProxyIndirection. (set during initialization */
    protected boolean hasProxyIndirection;
    /** Cache if all descriptors are CMP1/2 */
    protected boolean isPureCMP2Project;
    
    /** This a collection of 'maps' that allow users to map custom SQL to query results */
    protected Map sqlResultSetMappings;

    /** PERF: Provide an JPQL parse cache to optimize dynamic JPQL. */
    protected transient ConcurrentFixedCache jpqlParseCache;

    /** Define the default setting for configuring if dates and calendars are mutable. */
    protected boolean defaultTemporalMutable = false;
    
    /**
     * PUBLIC:
     * Create a new project.
     */
    public Project() {
        this.name = "";
        this.descriptors = new HashMap();
        this.defaultReadOnlyClasses = NonSynchronizedVector.newInstance();
        this.orderedDescriptors = NonSynchronizedVector.newInstance();
        this.hasIsolatedClasses = false;
        this.hasGenericHistorySupport = false;
        this.isPureCMP2Project = false;
        this.hasProxyIndirection = false;
        this.jpqlParseCache = new ConcurrentFixedCache(200);
    }

    /**
     * PUBLIC:
     * Create a new project that will connect through the login information.
     * This method can be used if the project is being create in code instead of being read from a file.
     */
    public Project(Login login) {
        this();
        this.datasourceLogin = login;
    }

    /**
     * PUBLIC:
     * Create a new project that will connect through JDBC using the login information.
     * This method can be used if the project is being create in code instead of being read from a file.
     */
    public Project(DatabaseLogin login) {
        this();
        this.datasourceLogin = login;
    }

    /**
     * PUBLIC:
     * Return the default setting for configuring if dates and calendars are mutable.
     * Mutable means that changes to the date's year/month/day are detected.
     * By default they are treated as not mutable.
     */
    public boolean getDefaultTemporalMutable() {
        return defaultTemporalMutable;
    }

    /**
     * PUBLIC:
     * Set the default setting for configuring if dates and calendars are mutable.
     * Mutable means that changes to the date's year/month/day are detected.
     * By default they are treated as not mutable.
     */
    public void setDefaultTemporalMutable(boolean defaultTemporalMutable) {
        this.defaultTemporalMutable = defaultTemporalMutable;
    }
    
    /**
     * INTERNAL:
     * Return the JPQL parse cache.
     * This is used to optimize dynamic JPQL.
     */
    public ConcurrentFixedCache getJPQLParseCache() {
        return jpqlParseCache;
    }

    /**
     * ADVANCED:
     * Set the JPQL parse cache max size.
     * This is used to optimize dynamic JPQL.
     */
    public void setJPQLParseCacheMaxSize(int maxSize) {
        setJPQLParseCache(new ConcurrentFixedCache(maxSize));
    }

    /**
     * ADVANCED:
     * Return the JPQL parse cache max size.
     * This is used to optimize dynamic JPQL.
     */
    public int getJPQLParseCacheMaxSize() {
        return getJPQLParseCache().getMaxSize();
    }

    /**
     * INTERNAL:
     * Set the JPQL parse cache.
     * This is used to optimize dynamic JPQL.
     */
    protected void setJPQLParseCache(ConcurrentFixedCache jpqlParseCache) {
        this.jpqlParseCache = jpqlParseCache;
    }

    /**
     * PUBLIC:
     * Add the read-only class which apply to each UnitOfWork created by default.
     */
    public void addDefaultReadOnlyClass(Class readOnlyClass) {
        getDefaultReadOnlyClasses().addElement(readOnlyClass);
    }

    /**
     * PUBLIC:
     * Add the descriptor to the project.
     */
    public void addDescriptor(ClassDescriptor descriptor) {
        getOrderedDescriptors().add(descriptor);
        String alias = descriptor.getAlias();
        if (alias != null) {
            addAlias(alias, descriptor);
        }

        // Avoid loading class definition at this point if we haven't done so yet.
        if ((descriptors != null) && !descriptors.isEmpty()) {
            getDescriptors().put(descriptor.getJavaClass(), descriptor);
        }
    }

    /**
     * INTERNAL: Used by the BuilderInterface when reading a Project from INI files.
     */
    public void addDescriptor(ClassDescriptor descriptor, DatabaseSessionImpl session) {
        getOrderedDescriptors().add(descriptor);
        String alias = descriptor.getAlias();
        if (alias != null) {
            addAlias(alias, descriptor);
        }

        // Avoid loading class definition at this point if we haven't done so yet.
        if ((descriptors != null) && !descriptors.isEmpty()) {
            getDescriptors().put(descriptor.getJavaClass(), descriptor);
        }
        session.initializeDescriptorIfSessionAlive(descriptor);
    }

    /**
     * INTERNAL:
     * Add the descriptors to the session.
     * All persistent classes must have a descriptor registered for them with the session.
     * This method allows for a batch of descriptors to be added at once so that TopLink
     * can resolve the dependancies between the descriptors and perform initialization optimally.
     */
    public void addDescriptors(Vector descriptors, DatabaseSessionImpl session) {
        for (Enumeration enumeration = descriptors.elements(); enumeration.hasMoreElements();) {
            ClassDescriptor descriptor = (ClassDescriptor)enumeration.nextElement();
            getDescriptors().put(descriptor.getJavaClass(), descriptor);
            String alias = descriptor.getAlias();
            if (alias != null) {
                addAlias(alias, descriptor);
            }
        }

        if (session.isConnected()) {
            session.initializeDescriptors(descriptors);
            // The commit order must be maintain whenever new descriptors are added.
            session.getCommitManager().initializeCommitOrder();
        }

        getOrderedDescriptors().addAll(descriptors);
    }

    /**
     * PUBLIC:
     * Merge the descriptors from another project into this one.
     * All persistent classes must have a descriptor registered for them with the session.
     * This method allows for a batch of descriptors to be added at once so that TopLink
     * can resolve the dependancies between the descriptors and perform initialization optimially.
     */
    public void addDescriptors(Project project, DatabaseSessionImpl session) {
        Iterator descriptors = project.getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            getDescriptors().put(descriptor.getJavaClass(), descriptor);
            String alias = descriptor.getAlias();
            if (alias != null) {
                addAlias(alias, descriptor);
            }
        }

        if (session.isConnected()) {
            session.initializeDescriptors(project.getDescriptors());
            // The commit order must be maintained whenever new descriptors are added.
            session.getCommitManager().initializeCommitOrder();
        }

        getOrderedDescriptors().addAll(project.getOrderedDescriptors());
    }

    /**
     * PUBLIC:
     * Add a named SQLResultSetMapping to this project.  These SQLResultSetMappings
     * can be later used by ResultSetMappingQueries to map Custom sql results to
     * results as defined by the SQLResultSetMappings.
     */
    public void addSQLResultSetMapping(SQLResultSetMapping sqlResultSetMapping){
        if (sqlResultSetMapping == null || sqlResultSetMapping.getName() == null){
            return;
        }
        if (this.sqlResultSetMappings == null){
            this.sqlResultSetMappings = new HashMap();
        }
        this.sqlResultSetMappings.put(sqlResultSetMapping.getName(), sqlResultSetMapping);
    }
    
    /**
     * PUBLIC:
     * Set all this project's descriptors to conform all read queries within the context of the unit of work.
     */
    public void conformAllDescriptors() {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.setShouldAlwaysConformResultsInUnitOfWork(true);
        }
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this project to actual class-based settings.
     * This will also reset any class references to the version of the class from the class loader.
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        Iterator ordered = orderedDescriptors.iterator();
        while (ordered.hasNext()){
            ClassDescriptor descriptor = (ClassDescriptor)ordered.next();
            descriptor.convertClassNamesToClasses(classLoader);
        }
        // Clear old descriptors to allow rehash on new classes.
        this.descriptors = new HashMap();
        // convert class names to classes for each SQLResultSetMapping
        if (sqlResultSetMappings != null) {
            for (Iterator mappingIt = sqlResultSetMappings.keySet().iterator(); mappingIt.hasNext();) {
                SQLResultSetMapping mapping = (SQLResultSetMapping) sqlResultSetMappings.get(mappingIt.next());
                mapping.convertClassNamesToClasses(classLoader);
            }
        }
    }

    /**
     * PUBLIC:
     * Switch all descriptors to assume existence for non-null primary keys.
     */
    public void assumeExistenceForDoesExist() {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.getQueryManager().assumeExistenceForDoesExist();
        }
    }

    /**
     * PUBLIC:
     * Switch all descriptors to check the cache for existence.
     */
    public void checkCacheForDoesExist() {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.getQueryManager().checkCacheForDoesExist();
        }
    }

    /**
     * PUBLIC:
     * Switch all descriptors to check the database for existence.
     */
    public void checkDatabaseForDoesExist() {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.getQueryManager().checkDatabaseForDoesExist();
        }
    }

    /**
     * INTERNAL:
     * Clones the descriptor
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * PUBLIC:
     * Factory method to create session.
     * This returns an implementor of the DatabaseSession interface, which can be used to login
     * and add descriptors from other projects.  The Session interface however should be used for
     * reading and writing once connected for complete portability.
     */
    public DatabaseSession createDatabaseSession() {
        return new DatabaseSessionImpl(this);
    }

    /**
     * PUBLIC:
     * Factory method to create a server session.
     * This returns an implementor of the Server interface, which can be used to login
     * and add descriptors from other projects, configure connection pooling and acquire client sessions.
     */
    public Server createServerSession() {
        return new ServerSession(this);
    }

    /**
     * PUBLIC:
     * Factory method to create a server session.
     * This returns an implementor of the Server interface, which can be used to login
     * and add descriptors from other projects, configure connection pooling and acquire client sessions.
     * Configure the min and max number of connections for the default pool.
     */
    public Server createServerSession(int min, int max) {
        return new ServerSession(this, min, max);
    }

    /**
     * PUBLIC:
     * Factory method to create a server session.
     * This returns an implementor of the Server interface, which can be used to login
     * and add descriptors from other projects, configure connection pooling and acquire client sessions.
     * Configure the default connection policy to be used.
     * This policy is used on the "acquireClientSession()" protocol.
     */
    public Server createServerSession(ConnectionPolicy defaultConnectionPolicy) {
        return new ServerSession(this, defaultConnectionPolicy);
    }

    /**
     * PUBLIC:
     * Returns the default set of read-only classes.
     */
    public Vector getDefaultReadOnlyClasses() {
        return defaultReadOnlyClasses;
    }

    /**
     * PUBLIC:
     * Return the descriptor specified for the class.
     * If the passed Class parameter is null, null will be returned.
     */
    public ClassDescriptor getClassDescriptor(Class theClass) {
        return getDescriptor(theClass);
    }

    /**
     * PUBLIC:
     * Return the descriptor specified for the class.
     */
    public ClassDescriptor getDescriptor(Class theClass) {
        if (theClass == null) {
            return null;
        }
        return (ClassDescriptor)getDescriptors().get(theClass);
    }

    /**
     * PUBLIC:
     * Return the descriptors.
     */
    public Map getDescriptors() {
        // Lazy initialize class references from orderedDescriptors when reading from XML.
        if (descriptors.isEmpty() && (!orderedDescriptors.isEmpty())) {
            for (Iterator iterator = orderedDescriptors.iterator(); iterator.hasNext();) {
                ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
                descriptors.put(descriptor.getJavaClass(), descriptor);
            }
        }
        return descriptors;
    }

    /**
     * INTERNAL:
     * Return the descriptors in the order added.
     * Used to maitain consistent order in XML.
     */
    public Vector getOrderedDescriptors() {
        return orderedDescriptors;
    }

    /**
     * INTERNAL:
     * Set the descriptors order.
     * Used to maitain consistent order in XML.
     */
    public void setOrderedDescriptors(Vector orderedDescriptors) {
        this.orderedDescriptors = orderedDescriptors;
        for (Enumeration e = orderedDescriptors.elements(); e.hasMoreElements();) {
            ClassDescriptor descriptor = (ClassDescriptor)e.nextElement();
            String alias = descriptor.getAlias();
            if (alias != null) {
                addAlias(alias, descriptor);
            }
        }
    }

    /**
     * OBSOLETE:
     * Return the login, the login holds any database connection information given.
     * This has been replaced by getDatasourceLogin to make use of the Login interface
     * to support non-relational datasources,
     * if DatabaseLogin API is required it will need to be cast.
     */
    public DatabaseLogin getLogin() {
        return (DatabaseLogin)datasourceLogin;
    }

    /**
     * PUBLIC:
     * Return the login, the login holds any database connection information given.
     * This return the Login interface and may need to be cast to the datasource specific implementation.
     */
    public Login getDatasourceLogin() {
        return datasourceLogin;
    }

    /**
     * PUBLIC:
     * get the name of the project.
     */
    public String getName() {
        return name;
    }

    /**
     * PUBLIC:
     * Get a named SQLResultSetMapping from this project.  These SQLResultSetMappings
     * can be used by ResultSetMappingQueries to map Custom sql results to
     * results as defined by the SQLResultSetMappings.
     */
    public SQLResultSetMapping getSQLResultSetMapping(String sqlResultSetMapping){
        if (sqlResultSetMapping == null || this.sqlResultSetMappings == null){
            return null;
        }
        return (SQLResultSetMapping)this.sqlResultSetMappings.get(sqlResultSetMapping);
    }
    
    /**
     * INTERNAL:
     * Answers if at least one Descriptor or Mapping had a HistoryPolicy at initialize time.
     */
    public boolean hasGenericHistorySupport() {
        return hasGenericHistorySupport;
    }

    /**
     * PUBLIC:
     * Set the read-only classes which apply to each UnitOfWork create by default.
     */
    public void setDefaultReadOnlyClasses(Vector newValue) {
        this.defaultReadOnlyClasses = (Vector)newValue.clone();
    }

    /**
     * INTERNAL:
     * Set the descriptors registered with this session.
     */
    public void setDescriptors(Map descriptors) {
        this.descriptors = descriptors;
        for (Iterator iterator = descriptors.values().iterator(); iterator.hasNext();) {
            ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
            String alias = descriptor.getAlias();
            if (alias != null) {
                addAlias(alias, descriptor);
            }
        }
    }

    /**
     * ADVANCED:
     * This method is a 'helper' method for updating all of the descriptors
     * within this project to have a particular deferral level.  The levels are
     * as follows
     *     ClassDescriptor.ALL_MODIFICATIONS - this is the default and recommended.
     *        The writing of all changes will be deferred until the end of the
     *       transaction
     *    ClassDescriptor.UPDATE_MODIFICATIONS - this will cause the update changes to
     *        be deferred and all other changes to be written immediately.
     *    ClassDescriptor.NONE - this will cause all changes to be written on each
     *        container call.
     */
    public void setDeferModificationsUntilCommit(int deferralLevel) {
        for (Iterator iterator = descriptors.values().iterator(); iterator.hasNext();) {
            ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
            if (descriptor.getCMPPolicy() != null) {
                descriptor.getCMPPolicy().setDeferModificationsUntilCommit(deferralLevel);
            }
        }
    }


    /**
     * INTERNAL:
     * Set to true during descriptor initialize if any descriptor has hsitory.
     */
    public void setHasGenericHistorySupport(boolean hasGenericHistorySupport) {
        this.hasGenericHistorySupport = hasGenericHistorySupport;
    }
    /**
     * INTERNAL:
     * Return if all descriptors are for CMP1 or CMP2 beans.
     * Set to true during descriptor initialize.
     * Allows certain optimizations to be made.
     */
    public boolean isPureCMP2Project() {
        return isPureCMP2Project;
    }
    /**
     * INTERNAL:
     * Set if all descriptors are for CMP1 or CMP2 beans.
     * Set to true during descriptor initialize.
     * Allows certain optimizations to be made.
     */
    public void setIsPureCMP2Project(boolean isPureCMP2Project) {
        this.isPureCMP2Project = isPureCMP2Project;
    }
    /**
     * INTERNAL:
     * Return if any descriptors are isolated.
     * Set to true during descriptor initialize if any descriptor is isolated.
     * Determines if an isolated client session is required.
     */
    public boolean hasIsolatedClasses() {
        return hasIsolatedClasses;
    }
    /**
     * INTERNAL:
     * Set to true during descriptor initialize if any descriptor is isolated.
     * Determines if an isolated client session is required.
     */
    public void setHasIsolatedClasses(boolean hasIsolatedClasses) {
        this.hasIsolatedClasses = hasIsolatedClasses;
    }

    /**
     * INTERNAL:
     * Return if any descriptors use ProxyIndirection.
     * Set to true during descriptor initialize if any descriptor uses ProxyIndirection
     * Determines if ProxyIndirectionPolicy.getValueFromProxy should be called.
     */
    public boolean hasProxyIndirection() {
        return this.hasProxyIndirection;
    }
    /**
     * INTERNAL:
     * Set to true during descriptor initialize if any descriptor uses ProxyIndirection
     * Determines if ProxyIndirectionPolicy.getValueFromProxy should be called.
     */
    public void setHasProxyIndirection(boolean hasProxyIndirection) {
        this.hasProxyIndirection = hasProxyIndirection;
    }
    /**
     * PUBLIC:
     * Set the login to be used to connect to the database for this project.
     */
    public void setLogin(DatabaseLogin datasourceLogin) {
        this.datasourceLogin = datasourceLogin;
    }

    /**
     * PUBLIC:
     * Set the login to be used to connect to the database for this project.
     */
    public void setLogin(Login datasourceLogin) {
        this.datasourceLogin = datasourceLogin;
    }

    /**
     * PUBLIC:
     * Set the login to be used to connect to the database for this project.
     */
    public void setDatasourceLogin(Login datasourceLogin) {
        this.datasourceLogin = datasourceLogin;
    }

    /**
     * PUBLIC:
     * Set the name of the project.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * INTERNAL:
     */
    public String toString() {
        return Helper.getShortClassName(getClass()) + "(" + getName() + ")";
    }

    /**
     * PUBLIC:
     * Switch all descriptors to use the cache identity map.
     */
    public void useCacheIdentityMap() {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.useCacheIdentityMap();
        }
    }

    /**
     * PUBLIC:
     * Switch all descriptors to use the cache identity map the size.
     */
    public void useCacheIdentityMap(int cacheSize) {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.useCacheIdentityMap();
            descriptor.setIdentityMapSize(cacheSize);
        }
    }

    /**
     * PUBLIC:
     * Switch all descriptors to use the full identity map.
     */
    public void useFullIdentityMap() {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.useFullIdentityMap();
        }
    }

    /**
     * PUBLIC:
     * Switch all descriptors to use the full identity map with initial cache size.
     */
    public void useFullIdentityMap(int initialCacheSize) {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.useFullIdentityMap();
            descriptor.setIdentityMapSize(initialCacheSize);
        }
    }

    /**
     * PUBLIC:
     * Switch all descriptors to use no identity map.
     */
    public void useNoIdentityMap() {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.useNoIdentityMap();
        }
    }

    /**
     * PUBLIC:
     * Switch all descriptors to use the soft cache weak identity map.
     */
    public void useSoftCacheWeakIdentityMap() {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.useSoftCacheWeakIdentityMap();
        }
    }

    /**
     * PUBLIC:
     * Switch all descriptors to use the soft cache weak identity map with soft cache size.
     */
    public void useSoftCacheWeakIdentityMap(int cacheSize) {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.useSoftCacheWeakIdentityMap();
            descriptor.setIdentityMapSize(cacheSize);
        }
    }

    /**
     * INTERNAL:
     * Asks each descriptor if is uses optimistic locking.
     */
    public boolean usesOptimisticLocking() {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            if (descriptor.usesOptimisticLocking()) {
                return true;
            }
        }
        return false;
    }

    /**
     * INTERNAL:
     * Asks each descriptor if is uses sequencing.
     */
    public boolean usesSequencing() {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            if (descriptor.usesSequenceNumbers()) {
                return true;
            }
        }
        return false;
    }

    /**
     * PUBLIC:
     * Switch all descriptors to use the weak identity map.
     */
    public void useWeakIdentityMap() {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.useWeakIdentityMap();
        }
    }

    /**
     * PUBLIC:
     * Switch all descriptors to use the weak identity map.
     */
    public void useWeakIdentityMap(int initialCacheSize) {
        Iterator descriptors = getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.useWeakIdentityMap();
            descriptor.setIdentityMapSize(initialCacheSize);
        }
    }

    /**
     * INTERNAL:
     * Default apply  login implementation.
     * Defined for generated subclasses that may not have a login.
     * BUG#2669342
     */
    public void applyLogin() {
        // Do nothing by default.
    }

    /**
     * INTERNAL:
     * Returns the alias descriptors hashtable.
     */
    public Map getAliasDescriptors() {
        return aliasDescriptors;
    }

    /**
     * PUBLIC:
     * Add an alias for the descriptor.
     */
    public void addAlias(String alias, ClassDescriptor descriptor) {
        if (aliasDescriptors == null) {
            aliasDescriptors = new HashMap(10);
        }
        aliasDescriptors.put(alias, descriptor);
    }

    /**
     * PUBLIC:
     * Return the descriptor for  the alias
     */
    public ClassDescriptor getDescriptorForAlias(String alias) {
        ClassDescriptor descriptor = null;
        if (aliasDescriptors != null) {
            descriptor = (ClassDescriptor)aliasDescriptors.get(alias);
        }
        return descriptor;
    }

    /**
     * INTERNAL:
     * Set the alias descriptors hashtable.
     */
    public void setAliasDescriptors(Map aHashtable) {
        aliasDescriptors = aHashtable;
    }
}
