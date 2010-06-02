/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     
 *     04/30/2009-2.0 Michael O'Brien 
 *       - 266912: JPA 2.0 Metamodel API (part of Criteria API)
 *         Add Set<RelationalDescriptor> mappedSuperclassDescriptors 
 *         to support the Metamodel API 
 *     06/17/2009-2.0 Michael O'Brien 
 *       - 266912: change mappedSuperclassDescriptors Set to a Map
 *          keyed on MetadataClass - avoiding the use of a hashCode/equals
 *          override on RelationalDescriptor, but requiring a contains check prior to a put
 *     09/23/2009-2.0 Michael O'Brien 
 *       - 266912: Add metamodelIdClassMap to store IdClass types for exclusive 
 *         use by the IdentifiableTypeImpl class in the JPA 2.0 Metamodel API     
 ******************************************************************************/  
package org.eclipse.persistence.sessions;

import java.util.*;
import java.io.*;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.AbstractIdentityMap;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.sessions.server.*;

/**
 * <b>Purpose</b>: Maintain all of the EclipseLink configuration information for a system.
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
    /** Cache if all descriptors are isolated in the unit of work. (set during initialization) */
    protected boolean hasNonIsolatedUOWClasses;
    /** Cache if any descriptor has history. (set during initialization) */
    protected boolean hasGenericHistorySupport;
    /** Cache if any descriptor is using ProxyIndirection. (set during initialization */
    protected boolean hasProxyIndirection;
    
    /** This a collection of 'maps' that allow users to map custom SQL to query results */
    protected Map sqlResultSetMappings;

    /** PERF: Provide an JPQL parse cache to optimize dynamic JPQL. */
    protected transient ConcurrentFixedCache jpqlParseCache;

    /** Define the default setting for configuring if dates and calendars are mutable. */
    protected boolean defaultTemporalMutable = false;
    
    /** Indicates whether there is at least one descriptor that has at least on mapping that
     *  require a call on deleted objects to update change sets. 
     */
    protected transient boolean hasMappingsPostCalculateChangesOnDeleted = false;
    
    /** Default value for ClassDescriptor.identityMapClass. */
    protected Class defaultIdentityMapClass = AbstractIdentityMap.getDefaultIdentityMapClass();
    
    /** Default value for ClassDescriptor.identityMapSize. */
    protected int defaultIdentityMapSize = 100;
    
    /** Default value for ClassDescriptor.isIsolated. */
    protected boolean defaultIsIsolated = false;
    
    /** Default value for ClassDescriptor.idValidation. */
    protected IdValidation defaultIdValidation = null;
    
    
    /** List of queries - once Project is initialized, these are copied to the Session. */
    protected transient List<DatabaseQuery> queries = null;
    
    /**
     * Mapped Superclasses (JPA 2) collection of parent non-relational descriptors keyed on MetadataClass
     * without creating a compile time dependency on JPA.
     * The descriptor values of this map must not be replaced by a put() so that the 
     * mappings on the initial descriptor are not overwritten.<p>
     * These descriptors are only to be used by Metamodel generation.
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation
     */
    protected Map<Object, RelationalDescriptor> mappedSuperclassDescriptors;

    /**
     * Store the IdClass Id attributes for exclusive use by the Metamodel API
     * Keyed on the fully qualified accessible object owner class name.
     * Value is a List of the fully qualified id class name or id attribute name.
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation 
     */
    protected Map<String, List<String>> metamodelIdClassMap;

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
        this.hasProxyIndirection = false;
        this.jpqlParseCache = new ConcurrentFixedCache(200);
        this.queries = new ArrayList<DatabaseQuery>();
        this.mappedSuperclassDescriptors = new HashMap<Object, RelationalDescriptor>(2);
        this.metamodelIdClassMap = new HashMap<String, List<String>>();
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
     * INTERNAL:
     * List of queries that upon initialization are copied over to the session
     */
    public List<DatabaseQuery> getQueries() {
        return queries;
    }
    /**
     * INTERNAL:
     * @param queries
     */
    public void setQueries(List<DatabaseQuery> queries) {
        this.queries = queries;
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
     * This method allows for a batch of descriptors to be added at once so that EclipseLink
     * can resolve the dependencies between the descriptors and perform initialization optimally.
     */
    public void addDescriptors(Collection descriptors, DatabaseSessionImpl session) {
        for (Iterator enumeration = descriptors.iterator(); enumeration.hasNext();) {
            ClassDescriptor descriptor = (ClassDescriptor)enumeration.next();
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
     * This method allows for a batch of descriptors to be added at once so that EclipseLink
     * can resolve the dependencies between the descriptors and perform initialization optimally.
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
     * <br>
     * By default the ServerSession has a single shared read/write connection pool
     * with 32 min/max connections and an initial of 1 connection.
     */
    public Server createServerSession() {
        return new ServerSession(this);
    }

    /**
     * PUBLIC:
     * Factory method to create a server session.
     * This returns an implementor of the Server interface, which can be used to login
     * and add descriptors from other projects, configure connection pooling and acquire client sessions.
     * Configure the min and max number of connections for the default shared read/write pool.
     */
    public Server createServerSession(int min, int max) {
        return new ServerSession(this, min, max);
    }

    /**
     * PUBLIC:
     * Factory method to create a server session.
     * This returns an implementor of the Server interface, which can be used to login
     * and add descriptors from other projects, configure connection pooling and acquire client sessions.
     * Configure the min and max number of connections for the default shared read/write pool.
     */
    public Server createServerSession(int initial, int min, int max) {
        return new ServerSession(this, initial, min, max);
    }

    /**
     * PUBLIC:
     * Factory method to create a server session.
     * This returns an implementor of the Server interface, which can be used to login
     * and add descriptors from other projects, configure connection pooling and acquire client sessions.
     * Configure the default connection policy to be used.
     * This policy is used on the "acquireClientSession()" protocol.
     * <br>
     * By default the ServerSession has a single shared read/write connection pool
     * with 32 min/max connections and an initial of 1 connection.
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
     * Return default value for descriptor cache type. 
     */
    public Class getDefaultIdentityMapClass() {
        return this.defaultIdentityMapClass;
    }
    
    /**
     * PUBLIC: 
     * Return default value descriptor cache size. 
     */
    public int getDefaultIdentityMapSize() {
        return this.defaultIdentityMapSize;
    }
    
    /** 
     * PUBLIC:
     * Return default value for whether descriptor should use isolated cache.
     */
    public boolean getDefaultIsIsolated() {
        return this.defaultIsIsolated;
    }
    
    /** 
     * PUBLIC:
     * Return default value for descriptor primary key validation.
     */
    public IdValidation getDefaultIdValidation() {
        return this.defaultIdValidation;
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
        return getDescriptors().get(theClass);
    }

    /**
     * PUBLIC:
     * Return the descriptors in a ClassDescriptors Map keyed on the Java class.
     */
    public Map<Class, ClassDescriptor> getDescriptors() {
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
     * Used to maintain consistent order in XML.
     */
    public Vector getOrderedDescriptors() {
        return orderedDescriptors;
    }

    /**
     * INTERNAL:
     * Set the descriptors order.
     * Used to maintain consistent order in XML.
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
    public void setDefaultReadOnlyClasses(Collection newValue) {
        this.defaultReadOnlyClasses = new Vector(newValue);
    }

    /** 
     * PUBLIC:
     * Set default value for descriptor cache type. 
     */
    public void setDefaultIdentityMapClass(Class defaultIdentityMapClass) {
        this.defaultIdentityMapClass = defaultIdentityMapClass;
    }
    
    /**
     * PUBLIC: 
     * Set default value descriptor cache size. 
     */
    public void setDefaultIdentityMapSize(int defaultIdentityMapSize) {
        this.defaultIdentityMapSize = defaultIdentityMapSize;
    }
    
    /** 
     * PUBLIC:
     * Set default value for whether descriptor should use isolated cache.
     */
    public void setDefaultIsIsolated(boolean defaultIsIsolated) {
        this.defaultIsIsolated = defaultIsIsolated;
    }
    
    /** 
     * PUBLIC:
     * Set default value for descriptor primary key validation.
     */
    public void setDefaultIdValidation(IdValidation defaultIdValidation) {
        this.defaultIdValidation = defaultIdValidation;
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
     * Set to true during descriptor initialize if any descriptor has history.
     */
    public void setHasGenericHistorySupport(boolean hasGenericHistorySupport) {
        this.hasGenericHistorySupport = hasGenericHistorySupport;
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
     * Return if any descriptors are not isolated to the unit of work.
     * Set to true during descriptor initialize if any descriptor is not isolated.
     * Allows uow merge to be bypassed.
     */
    public boolean hasNonIsolatedUOWClasses() {
        return hasNonIsolatedUOWClasses;
    }
    
    /**
     * INTERNAL:
     * Set if any descriptors are not isolated to the unit of work.
     * Set to true during descriptor initialize if any descriptor is not isolated.
     * Allows uow merge to be bypassed.
     */
    public void setHasNonIsolatedUOWClasses(boolean hasNonIsolatedUOWClasses) {
        this.hasNonIsolatedUOWClasses = hasNonIsolatedUOWClasses;
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
     * PUBLIC:
     * Return true if the sql result set mapping name exists.
     */
    public boolean hasSQLResultSetMapping(String sqlResultSetMapping) {
        return sqlResultSetMappings.containsKey(sqlResultSetMapping);
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
    
    /**
     * INTERNAL:
     * Indicates whether there is at least one descriptor that has at least on mapping that
     * require a call on deleted objects to update change sets. 
     */
    public boolean hasMappingsPostCalculateChangesOnDeleted() {
        return hasMappingsPostCalculateChangesOnDeleted;
    }
    
    /**
     * INTERNAL:
     * Indicates whether there is at least one descriptor that has at least on mapping that
     * require a call on deleted objects to update change sets. 
     */
    public void setHasMappingsPostCalculateChangesOnDeleted(boolean hasMappingsPostCalculateChangesOnDeleted) {
        this.hasMappingsPostCalculateChangesOnDeleted = hasMappingsPostCalculateChangesOnDeleted;
    }
    
    /**
     * INTERNAL:
     * Return whether there any mappings that are mapped superclasses.
     * @return
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation 
     */
    public boolean hasMappedSuperclasses() {
        return (null != this.mappedSuperclassDescriptors && !this.mappedSuperclassDescriptors.isEmpty());
    }
    
    /**
     * INTERNAL:
     * 266912: Add a descriptor to the Map of mappedSuperclass descriptors
     * @param key (Metadata class) 
     * @param value (RelationalDescriptor)
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation 
     */
    public void addMappedSuperclass(Object key, RelationalDescriptor value) {
        // Lazy initialization of the mappedSuperclassDescriptors field.
        if(null == this.mappedSuperclassDescriptors) {
            this.mappedSuperclassDescriptors = new HashMap<Object, RelationalDescriptor>(2);
        }
        // Avoid replacing the current RelationalDescriptor that may have mappings set
        if(!this.mappedSuperclassDescriptors.containsKey(key)) {
            this.mappedSuperclassDescriptors.put(key, value);
        }
    }

    /**
     * INTERNAL:
     * Use the Metadata key parameter to lookup the 
     * Descriptor from the Map of mappedSuperclass descriptors
     * @param key - theMetadata class
     * @return
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation 
     */
    public RelationalDescriptor getMappedSuperclass(Object key) {
        // TODO: this implementation may have side effects when we have the same class 
        // in different class loaders - however currently there is only one classLoader per project
        // Lazy initialization of the mappedSuperclassDescriptors field.
        if(null == this.mappedSuperclassDescriptors) {
            this.mappedSuperclassDescriptors = new HashMap<Object, RelationalDescriptor>(2);
            return null;
        }
        // iterate the set of mappedSuperclasses and return the value that has a matching javaClass key
        return this.mappedSuperclassDescriptors.get(key);
    }

    /**
     * INTERNAL:
     * Return the Map of RelationalDescriptor objects representing mapped superclass parents
     * keyed by className of the metadata class.
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation 
     * @return Map
     */
    public Map<Object, RelationalDescriptor> getMappedSuperclassDescriptors() {        
        // Lazy initialization of the mappedSuperclassDescriptors field.
        if(null == this.mappedSuperclassDescriptors) {
            this.mappedSuperclassDescriptors = new HashMap<Object, RelationalDescriptor>(2);
        }
        return this.mappedSuperclassDescriptors;
    }
    
    /**
     * INTERNAL:
     * Add an IdClass entry to the map of ids for a particular owner
     * This function is used exclusively by the Metamodel API. 
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation 
     * @param idMap
     * @param ownerName
     * @param name
     */
    public void addMetamodelIdClassMapEntry(String ownerName, String name) {        
        // Add a possible composite key to the owner - this function will handle duplicates by overwriting the entry
        if(this.metamodelIdClassMap.containsKey(ownerName)) {
            // If we have a key entry then the list will always exist
            this.metamodelIdClassMap.get(ownerName).add(name);
        } else {
            List<String> ownerList = new ArrayList<String>();
            ownerList.add(name);
            this.metamodelIdClassMap.put(ownerName, ownerList);    
        }        
    }
    
    /**
     * INTERNAL: 
     * Return the Map of IdClass attribute lists keyed on owner class name.
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation 
     * @return
     */
    public Map<String, List<String>> getMetamodelIdClassMap() {
        return metamodelIdClassMap;
    }
}

