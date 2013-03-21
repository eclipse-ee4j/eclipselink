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
 *     10/15/2010-2.2 Guy Pelletier
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 *     04/01/2011-2.3 Guy Pelletier
 *       - 337323: Multi-tenant with shared schema support (part 2)
 *     05/24/2011-2.3 Guy Pelletier
 *       - 345962: Join fetch query when using tenant discriminator column fails.
 *     08/18/2011-2.3.1 Guy Pelletier
 *       - 355093: Add new 'includeCriteria' flag to Multitenant metadata
 *     09/09/2011-2.3.1 Guy Pelletier
 *       - 356197: Add new VPD type to MultitenantType
 *     08/01/2012-2.5 Chris Delahunt
 *       - 371950: JPA Metadata caching
 ******************************************************************************/
package org.eclipse.persistence.descriptors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.expressions.CompoundExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.SubSelectExpression;
import org.eclipse.persistence.internal.helper.ConcurrentFixedCache;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.JPAQueryBuilder;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.QueryResultsCachePolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.queries.WriteObjectQuery;

/**
 * <p><b>Purpose</b>: The query manager allows for the database operations that EclipseLink
 * performs to be customized by the application.  For each descriptor a query can be
 * given that controls how a operation will occur.  A common example is if the application
 * requires a stored procedure to be used to insert the object, it can override the SQL call
 * in the insert query that EclipseLink will use to insert the object.
 * Queries can be customized to extend EclipseLink behavior, access non-relational data or use stored
 * procedures or customized SQL calls.
 * <p>
 * The queries that can be customized include:
 * <ul>
 * <li> insertQuery - used to insert the object
 * <li> updateQuery - used to update the object
 * <li> readObjectQuery - used to read a single object by primary key
 * <li> readAllQuery - used to read all of the objects of the class
 * <li> doesExistQuery - used to determine whether an insert or update should occur
 * <li> deleteQuery - used to delete the object
 * </ul>
 *
 * @see ClassDescriptor
 */
public class DescriptorQueryManager implements Cloneable, Serializable {
    protected InsertObjectQuery insertQuery;
    protected UpdateObjectQuery updateQuery;
    protected ReadObjectQuery readObjectQuery;
    protected ReadAllQuery readAllQuery;
    protected DeleteObjectQuery deleteQuery;
    protected DoesExistQuery doesExistQuery;
    protected ClassDescriptor descriptor;
    protected boolean hasCustomMultipleTableJoinExpression;
    protected String additionalCriteria;
    protected transient Expression additionalJoinExpression;
    protected transient Expression multipleTableJoinExpression;
    protected Map<String, List<DatabaseQuery>> queries;
    protected transient Map<DatabaseTable, Expression> tablesJoinExpressions;
    /** PERF: Update call cache for avoiding regenerated update SQL. */
    protected transient ConcurrentFixedCache cachedUpdateCalls;
    /** PERF: Expression query call cache for avoiding regenerated dynamic query SQL. */
    protected transient ConcurrentFixedCache cachedExpressionQueries;

    /**
     * queryTimeout has three possible settings: DefaultTimeout, NoTimeout, and 1..N
     * This applies to both DatabaseQuery.queryTimeout and DescriptorQueryManager.queryTimeout
     *
     * DatabaseQuery.queryTimeout:
     * - DefaultTimeout: get queryTimeout from DescriptorQueryManager
     * - NoTimeout, 1..N: overrides queryTimeout in DescriptorQueryManager
     *
     * DescriptorQueryManager.queryTimeout:
     * - DefaultTimeout: get queryTimeout from parent DescriptorQueryManager. If there is no
     * parent, default to NoTimeout
     * - NoTimeout, 1..N: overrides parent queryTimeout
     */
    public static final int NoTimeout = 0;
    public static final int DefaultTimeout = -1;
    protected int queryTimeout;

    /**
     * INTERNAL:
     * Initialize the state of the descriptor query manager
     */
    public DescriptorQueryManager() {
        this.queries = new LinkedHashMap(5);
        setDoesExistQuery(new DoesExistQuery());// Always has a does exist.
        this.setQueryTimeout(DefaultTimeout);
    }

    /**
     * ADVANCED:
     * Set the max size of the expression query cache for avoiding regenerated dynamic query SQL.
     */
    public void setExpressionQueryCacheMaxSize(int maxSize) {
        this.cachedExpressionQueries = new ConcurrentFixedCache(maxSize);
    }

    /**
     * ADVANCED:
     * Return the max size of the expression query cache for avoiding regenerated dynamic query SQL.
     */
    public int getExpressionQueryCacheMaxSize() {
        return getCachedExpressionQueries().getMaxSize();
    }

    /**
     * PUBLIC:
     * Add the query to the descriptor queries with the given name
     * @param name This is the name of the query.  It will be set on the query and used to look it up.
     * @param query This is the query that will be added.  If the query being added has parameters, the
     * existing list of queries will be checked for matching queries.  If a matching query exists,
     * it will be replaced.
     */
    public void addQuery(String name, DatabaseQuery query) {
        query.setName(name);
        addQuery(query);
    }

    /**
     * PUBLIC:
     * Add the query to the session queries
     * @param query DatabaseQuery This is the query that will be added.  If the query being added has parameters, the
     * existing list of queries will be checked for matching queries.  If a matching query exists,
     * it will be replaced.
     */
    public synchronized void addQuery(DatabaseQuery query) {
        if (query instanceof ObjectLevelReadQuery && (((ObjectLevelReadQuery)query).getReferenceClassName() == null)) {
            ((ObjectLevelReadQuery)query).setReferenceClassName(getDescriptor().getJavaClassName());

            // try to set the reference ClassNotFoundException since it should only happen on the MW in which
            // case we will lazily initialize the reference class at a later point.
            try {
                ((ObjectLevelReadQuery)query).setReferenceClass(getDescriptor().getJavaClass());
            } catch (ConversionException exception) {
            }

            //this is an optimization
            query.setDescriptor(getDescriptor());
        }

        // Add query has been synchronized for bug 3355199.
        // Additionally code has been added to ensure that the same query is not added twice.
        Vector queriesByName = (Vector)getQueries().get(query.getName());
        if (queriesByName == null) {
            // lazily create Vector in Hashtable.
            queriesByName = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
            getQueries().put(query.getName(), queriesByName);
        } else {
            int argumentTypesSize = 0;
            if (query.getArguments() != null) {
                argumentTypesSize = query.getArguments().size();
            }
            List<String> argumentTypes = new ArrayList(argumentTypesSize);
            for (int i = 0; i < argumentTypesSize; i++) {
                argumentTypes.add(query.getArgumentTypeNames().get(i));
            }

            // Search for a query with the same parameters and replace it if one is found
            for (int i = 0; i < queriesByName.size(); i++) {
                DatabaseQuery currentQuery = (DatabaseQuery)queriesByName.get(i);

                // Here we are checking equality instead of assignability.  If you look at getQuery()
                // it is the other way around.
                // The reason we do this is we are replacing a query and we want to make sure we are
                // replacing the exact same one. - TW
                if (argumentTypes.equals(currentQuery.getArgumentTypeNames())) {
                    queriesByName.set(i, query);
                    return;
                }
            }
        }
        queriesByName.add(query);
    }

    /**
     * PUBLIC:
     * Assume that if the objects primary key does not include null then it must exist.
     * This may be used if the application guarantees or does not care about the existence check.
     */
    public void assumeExistenceForDoesExist() {
        getDoesExistQuery().assumeExistenceForDoesExist();
    }

    /**
     * PUBLIC:
     * Assume that the object does not exist. This may be used if the application guarantees or
     * does not care about the existence check.  This will always force an insert to be called.
     */
    public void assumeNonExistenceForDoesExist() {
        getDoesExistQuery().assumeNonExistenceForDoesExist();
    }

    /**
     * PUBLIC:
     * Default behavior.
     * Assume that if the objects primary key does not include null and it
     * is in the cache, then is must exist.
     */
    public void checkCacheForDoesExist() {
        getDoesExistQuery().checkCacheForDoesExist();
    }

    /**
     * PUBLIC:
     * Perform does exist check on the database
     */
    public void checkDatabaseForDoesExist() {
        getDoesExistQuery().checkDatabaseForDoesExist();
    }

    /**
     * INTERNAL:
     * Clone the query manager
     */
	public Object clone() {
        DescriptorQueryManager manager = null;
        try {
            manager = (DescriptorQueryManager)super.clone();
        } catch (Exception exception) {
            ;
        }

        // Bug 3037701 - clone the queries
        manager.setQueries(new LinkedHashMap(getQueries().size()));//bug5677655
        Iterator iterator = queries.values().iterator();
        while (iterator.hasNext()) {
            Iterator queriesForKey = ((Vector)iterator.next()).iterator();
            while (queriesForKey.hasNext()) {
                DatabaseQuery initialQuery = (DatabaseQuery)queriesForKey.next();
                DatabaseQuery clonedQuery = (DatabaseQuery)initialQuery.clone();
                clonedQuery.setDescriptor(manager.getDescriptor());
                manager.addQuery(clonedQuery);
            }
        }
        manager.setDoesExistQuery((DoesExistQuery)getDoesExistQuery().clone());
        if (getReadAllQuery() != null) {
            manager.setReadAllQuery((ReadAllQuery)getReadAllQuery().clone());
        }
        if (getReadObjectQuery() != null) {
            manager.setReadObjectQuery((ReadObjectQuery)getReadObjectQuery().clone());
        }
        if (getUpdateQuery() != null) {
            manager.setUpdateQuery((UpdateObjectQuery)getUpdateQuery().clone());
        }
        if (getInsertQuery() != null) {
            manager.setInsertQuery((InsertObjectQuery)getInsertQuery().clone());
        }
        if (getDeleteQuery() != null) {
            manager.setDeleteQuery((DeleteObjectQuery)getDeleteQuery().clone());
        }

        return manager;
    }

    /**
     * PUBLIC:
     * Return true if the query is defined on the session
     */
    public boolean containsQuery(String queryName) {
        return queries.containsKey(queryName);
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this Query Manager to actual class-based
     * settings
     * This method is implemented by subclasses as necessary.
     * @param classLoader
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        Iterator queryVectors = getQueries().values().iterator();
        while (queryVectors.hasNext()){
            Iterator queries = ((Vector)queryVectors.next()).iterator();;
            while (queries.hasNext()){
                ((DatabaseQuery)queries.next()).convertClassNamesToClasses(classLoader);
            }
        }
        if (getReadObjectQuery() != null) {
            getReadObjectQuery().convertClassNamesToClasses(classLoader);
        }
        if (getReadAllQuery() != null) {
            getReadAllQuery().convertClassNamesToClasses(classLoader);
        }
    };

    /**
     * ADVANCED:
     * Returns the join expression that should be appended to all of the descriptors expressions
     * Contains any multiple table or inheritance dependencies
     */
    public Expression getAdditionalJoinExpression() {
        return additionalJoinExpression;
    }

    /**
     * ADVANCED:
     * Return the receiver's delete query.
     * This should be an instance of a valid subclass of DeleteObjectQuery.
     * If specified this is used by the descriptor to delete itself and its private parts from the database.
     * This gives the user the ability to define exactly how to delete the data from the database,
     * or access data external from the database or from some other framework.
     */
    public DeleteObjectQuery getDeleteQuery() {
        return deleteQuery;
    }

    /**
     * ADVANCED:
     * Return the receiver's delete SQL string.
     * This allows the user to override the SQL generated by EclipseLink, with their own SQL or procedure call.
     * The arguments are translated from the fields of the source row,
     * through replacing the field names marked by '#' with the values for those fields.
     * <p>
     *    Example, "delete from EMPLOYEE where EMPLOYEE_ID = #EMPLOYEE_ID".
     */
    public String getDeleteSQLString() {
        if (getDeleteQuery() == null) {
            return null;
        }

        return getDeleteQuery().getSQLString();
    }

    /**
     * INTERNAL:
     * Return the descriptor associated with this descriptor query manager
     */
    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * ADVANCED:
     * Return the receiver's  does exist query.
     * This should be an instance of a valid subclass of DoesExistQuery.
     * If specified this is used by the descriptor to query existence of an object in the database.
     * This gives the user the ability to define exactly how to query existence from the database,
     * or access data external from the database or from some other framework.
     */
    public DoesExistQuery getDoesExistQuery() {
        return doesExistQuery;
    }

    /**

     * ADVANCED:
     * Return the receiver's does exist SQL string.
     * This allows the user to override the SQL generated by EclipseLink, with there own SQL or procedure call.
     * The arguments are translated from the fields of the source row, through replacing the field names marked by '#'
     * with the values for those fields.
     * This must return null if the object does not exist, otherwise return a database row.
     * <p>
     * Example, "select EMPLOYEE_ID from EMPLOYEE where EMPLOYEE_ID = #EMPLOYEE_ID".
     */
    public String getDoesExistSQLString() {
        if (getDoesExistQuery() == null) {
            return null;
        }

        return getDoesExistQuery().getSQLString();
    }

    /**
     * INTERNAL:
     * This method is explicitly used by the Builder only.
     */
    public String getExistenceCheck() {
        if (getDoesExistQuery().shouldAssumeExistenceForDoesExist()) {
            return "Assume existence";
        } else if (getDoesExistQuery().shouldAssumeNonExistenceForDoesExist()) {
            return "Assume non-existence";
        } else if (getDoesExistQuery().shouldCheckCacheForDoesExist()) {
            return "Check cache";
        } else if (getDoesExistQuery().shouldCheckDatabaseForDoesExist()) {
            return "Check database";
        } else {
            // Default.
            return "Check cache";
        }
    }

    /**
      * ADVANCED:
      * Return the receiver's insert query.
      * This should be an instance of a valid subclass of InsertObjectQuery.
      * If specified this is used by the descriptor to insert itself into the database.
      * If the receiver uses sequence numbers, this query must return the updated sequence value.
      * This gives the user the ability to define exactly how to insert the data into the database,
      * or access data external from the database or from some other framework.
      */
    public InsertObjectQuery getInsertQuery() {
        return insertQuery;
    }

    /**
     * ADVANCED:
     * Return the receiver's insert SQL string.
     * This allows the user to override the SQL generated by EclipseLink, with their own SQL or procedure call.
     * The arguments are translated from the fields of the source row,
     * through replacing the field names marked by '#' with the values for those fields.
     * <p>
     * Example, "insert into EMPLOYEE (F_NAME, L_NAME) values (#F_NAME, #L_NAME)".
     */
    public String getInsertSQLString() {
        if (getInsertQuery() == null) {
            return null;
        }

        return getInsertQuery().getSQLString();
    }

    /**
     * ADVANCED:
     * This is normally generated for descriptors that have multiple tables.
     * However, if the additional table does not reference the primary tables primary key,
     * this expression may be set directly.
     */
    public Expression getMultipleTableJoinExpression() {
        return multipleTableJoinExpression;
    }

    /**
     * PUBLIC:
     * Return the pre-defined queries for the descriptor.
     * The Map returned contains Lists of queries.
     *
     * @see #getAllQueries()
     */
    public Map<String, List<DatabaseQuery>> getQueries() {
        return queries;
    }

    /**
     * PUBLIC:
     * Return the pre-defined queries for the descriptor.  The Vector returned
     * contains all queries for this descriptor.
     *
     * @see #getQueries()
     */
    public Vector getAllQueries() {
        Vector allQueries = new Vector();
        for (Iterator vectors = getQueries().values().iterator(); vectors.hasNext();) {
            allQueries.addAll((Vector)vectors.next());
        }
        return allQueries;
    }

    /**
     * INTERNAL:
     * Set pre-defined queries for the descriptor.  Converts the Vector to a hashtable
     */
    public void setAllQueries(Vector vector) {
        for (Enumeration enumtr = vector.elements(); enumtr.hasMoreElements();) {
            addQuery((DatabaseQuery)enumtr.nextElement());
        }
    }

    /**
     * PUBLIC:
     * set the pre-defined queries for the descriptor.  Used to write out deployment XML
     */
    public void setQueries(Map map) {
        queries = map;
    }

    /**
     * PUBLIC:
     * Return the query name from the set of pre-defined queries
     * If only one query exists with this name, it will be returned.
     * If there are multiple queries of this name, this method will search for a query
     * with no arguments and return the first one it finds.
     *
     * @see #getQuery(String, Vector)
     */
    public DatabaseQuery getQuery(String queryName) {
        return getQuery(queryName, null);
    }

    /**
     * PUBLIC:
     * Return the query from the set of pre-defined queries with the given name and argument types.
     * This allows for common queries to be pre-defined, reused and executed by name.
     * This method should be used if the Session has multiple queries with the same name but
     * different arguments.
     * If only one query exists, it will be returned regardless of the arguments.
     * If multiple queries exist, the first query that has corresponding argument types will be returned
     *
     * @see #getQuery(String)
     */
    public DatabaseQuery getQuery(String name, Vector arguments) {
        DatabaseQuery query = getLocalQuery(name, arguments);

        // CR#3711: Check if a query with the same name exists for this descriptor.
        // If not, recursively check descriptors of parent classes.  If nothing is
        // found in parents, return null.
        if (query == null) {
            DatabaseQuery parentQuery = getQueryFromParent(name, arguments);
            if ((parentQuery != null) && parentQuery.isReadQuery()) {
                parentQuery = (DatabaseQuery)parentQuery.clone();
                ((ObjectLevelReadQuery)parentQuery).setReferenceClass(this.descriptor.getJavaClass());
                addQuery(name, parentQuery);
            }
            return parentQuery;
        }
        return query;
    }

    /**
     * INTENAL:
     * Return the query from the set of pre-defined queries with the given name and argument types.
     * This allows for common queries to be pre-defined, reused and executed by name.
     * Only returns those queries locally defined, not superclass's queries
     * If only one query exists, it will be returned regardless of the arguments.
     * If multiple queries exist, the first query that has corresponding argument types will be returned
     *
     * @see #getQuery(String)
     */
    public DatabaseQuery getLocalQuery(String name, Vector arguments) {
        Vector queries = (Vector)getQueries().get(name);

        if (queries == null) {
            return null;
        }

        // Short circuit the simple, most common case of only one query.
        if (queries.size() == 1) {
            return (DatabaseQuery)queries.firstElement();
        }

        // CR#3754; Predrag; mar 19/2002;
        // We allow multiple named queries with the same name but
        // different argument set; we can have only one query with
        // no arguments; Vector queries is not sorted;
        // When asked for the query with no parameters the
        // old version did return the first query - wrong:
        // return (DatabaseQuery) queries.firstElement();
        int argumentTypesSize = 0;
        if (arguments != null) {
            argumentTypesSize = arguments.size();
        }
        Vector argumentTypes = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(argumentTypesSize);
        for (int i = 0; i < argumentTypesSize; i++) {
            argumentTypes.addElement(arguments.elementAt(i).getClass());
        }
        return getLocalQueryByArgumentTypes(name, argumentTypes);

    }

    /**
     * INTERNAL:
     * Return the query from the set of pre-defined queries with the given name and argument types.
     * This allows for common queries to be pre-defined, reused and executed by name.
     * Only returns those queries locally defined, not superclass's queries
     * If only one query exists, it will be returned regardless of the arguments.
     * If multiple queries exist, the first query that has corresponding argument types will be returned
     *
     * @see #getQuery(String)
     */
    public DatabaseQuery getLocalQueryByArgumentTypes(String name, List argumentTypes) {
        List<DatabaseQuery> queries = getQueries().get(name);

        if (queries == null) {
            return null;
        }

        // Short circuit the simple, most common case of only one query.
        if (queries.size() == 1) {
            return queries.get(0);
        }

        for (DatabaseQuery query : queries) {
            // BUG#2698755
            // This check was backward, we default the type to Object
            // Was checking Object is descendant of String not other way.
            if (Helper.areTypesAssignable(query.getArgumentTypes(), argumentTypes)) {
                return query;
            }
        }

        return null;

    }

    /**
     * INTERNAL:
     * CR#3711: Check if the class for this descriptor has a parent class.
     * Then search this parent's descriptor for a query with the same name
     * and arguments.  If nothing found, return null.
     *
     * This method should only be used recursively by getQuery().
     */
    protected DatabaseQuery getQueryFromParent(String name, Vector arguments) {
        ClassDescriptor descriptor = this.descriptor;
        if (descriptor.hasInheritance()) {
            InheritancePolicy inheritancePolicy = descriptor.getInheritancePolicy();
            ClassDescriptor parent = inheritancePolicy.getParentDescriptor();

            // if parent exists, check for the query
            if (parent != null) {
                return parent.getQueryManager().getQuery(name, arguments);
            }
        }
        return null;
    }

    /**
      * ADVANCED:
      * Return the receiver's read query.
      * This should be an instance of a valid subclass of ReadAllQuery.
      */
    public ReadAllQuery getReadAllQuery() {
        return readAllQuery;
    }

    /**
     * ADVANCED:
     * Return the receiver's read SQL string.
     * This allows the user to override the SQL generated by EclipseLink, with their own SQL or procedure call.
     * The arguments are translated from the fields of the read arguments row,
     * through replacing the field names marked by '#' with the values for those fields.
     * Note that this is only used on readAllObjects(Class), and not when an expression is provided.
     * <p>
     * Example, "select * from EMPLOYEE"
     */
    public String getReadAllSQLString() {
        if (getReadAllQuery() == null) {
            return null;
        }

        return getReadAllQuery().getSQLString();
    }

    /**
     * ADVANCED:
     * Return the receiver's read query.
     * This should be an instance of a valid subclass of ReadObjectQuery.
     * If specified this is used by the descriptor to read itself from the database.
     * The read arguments must be the primary key of the object only.
     * This gives the user the ability to define exactly how to read the object from the database,
     * or access data external from the database or from some other framework.
     */
    public ReadObjectQuery getReadObjectQuery() {
        return readObjectQuery;
    }

    /**
     * ADVANCED:
     * Return the receiver's read SQL string.
     * This allows the user to override the SQL generated by EclipseLink, with their own SQL or procedure call.
     * The arguments are translated from the fields of the read arguments row,
     * through replacing the field names marked by '#' with the values for those fields.
     * This must accept only the primary key of the object as arguments.
     * <p>
     * Example, "select * from EMPLOYEE where EMPLOYEE_ID = #EMPLOYEE_ID"
     */
    public String getReadObjectSQLString() {
        if (getReadObjectQuery() == null) {
            return null;
        }

        return getReadObjectQuery().getSQLString();
    }

    /**
      * ADVANCED:
      * Return the receiver's update query.
      * This should be an instance of a valid subclass of UpdateObjectQuery.
      * If specified this is used by the descriptor to insert itself into the database.
      * If the receiver uses optimistic locking this must raise an error on optimistic lock failure.
      * This gives the user the ability to define exactly how to update the data into the database,
      * or access data external from the database or from some other framework.
      */
    public UpdateObjectQuery getUpdateQuery() {
        return updateQuery;
    }

    /**
     * ADVANCED:
     * Return the receiver's update SQL string.
     * This allows the user to override the SQL generated by EclipseLink, with there own SQL or procedure call.
     * The arguments are translated from the fields of the source row,
     * through replacing the field names marked by '#' with the values for those fields.
     * This must check the optimistic lock field and raise an error on optimistic lock failure.
     * <p>
     * Example, "update EMPLOYEE set F_NAME to #F_NAME, L_NAME to #L_NAME where EMPLOYEE_ID = #EMPLOYEE_ID".
     */
    public String getUpdateSQLString() {
        if (getUpdateQuery() == null) {
            return null;
        }

        return getUpdateQuery().getSQLString();
    }

    /**
     * ADVANCED:
     * Return true if an additional criteria has been set on this query manager.
     */
    public boolean hasAdditionalCriteria() {
        return additionalCriteria != null;
    }

    /**
     * INTERNAL:
     * Return if a custom join expression is used.
     */
    public boolean hasCustomMultipleTableJoinExpression() {
        return hasCustomMultipleTableJoinExpression;
    }

    /**
     * INTERNAL:
     * Flag that specifies if a delete query is available
     */
    public boolean hasDeleteQuery() {
        return (deleteQuery != null);
    }

    /**
     * INTERNAL:
     * Flag that specifies if a does exist query is available
     */
    public boolean hasDoesExistQuery() {
        return (doesExistQuery != null);
    }

    /**
     * INTERNAL:
     * Flag that specifies if a insert query is available
     */
    public boolean hasInsertQuery() {
        return (insertQuery != null);
    }

    /**
     * INTERNAL:
     * Flag that specifies if a read all query is available
     */
    public boolean hasReadAllQuery() {
        return (readAllQuery != null);
    }

    /**
     * INTERNAL:
     * Flag that specifies if a read object  query is available
     */
    public boolean hasReadObjectQuery() {
        return (readObjectQuery != null);
    }

    /**
     * INTERNAL:
     * Flag that specifies if a update query is available
     */
    public boolean hasUpdateQuery() {
        return (updateQuery != null);
    }

    /**
     * INTERNAL:
     * populate the queries with the descriptor.
     */
    private void populateQueries() {

        /* CR2260
         * Description:
         *   NullPointerException accessing null descriptor
         * Fix:
         *   Initialize queries with an instantiated descriptor at this point
         */
        if (getInsertQuery() != null) {
            getInsertQuery().setDescriptor(descriptor);
        }
        if (getUpdateQuery() != null) {
            getUpdateQuery().setDescriptor(descriptor);
        }
        if (getReadObjectQuery() != null) {
            getReadObjectQuery().setReferenceClass(getDescriptor().getJavaClass());
            getReadObjectQuery().setDescriptor(descriptor);
        }
        if (getDeleteQuery() != null) {
            getDeleteQuery().setDescriptor(descriptor);
        }
        if (getReadAllQuery() != null) {
            getReadAllQuery().setReferenceClass(getDescriptor().getJavaClass());
            getReadAllQuery().setDescriptor(descriptor);
        }
        for (Iterator it = getAllQueries().iterator(); it.hasNext();) {
            ((DatabaseQuery)it.next()).setDescriptor(descriptor);
        }
    }

    /**
     * INTERNAL:
     * Post initialize the mappings
     */
    public void initialize(AbstractSession session) {
        this.initializeQueryTimeout(session);

        if (getDescriptor().isAggregateDescriptor()) {
            return;
        }

        if (getMultipleTableJoinExpression() != null) {
            // Combine new multiple table expression to additional join expression
            setAdditionalJoinExpression(getMultipleTableJoinExpression().and(getAdditionalJoinExpression()));
        }

        if (getDescriptor().isAggregateCollectionDescriptor()) {
            return;
        }
        
        // Configure default query cache for all named queries.
        QueryResultsCachePolicy defaultQueryCachePolicy = session.getProject().getDefaultQueryResultsCachePolicy();
        if (defaultQueryCachePolicy != null && !getDescriptor().getCachePolicy().isIsolated()) {
            for (List<DatabaseQuery> queries : getQueries().values()) {
                for (DatabaseQuery query : queries) {
                    if (query.isReadQuery()) {
                        ReadQuery readQuery = (ReadQuery)query;
                        if (!readQuery.shouldCacheQueryResults()) {
                            readQuery.setQueryResultsCachePolicy(defaultQueryCachePolicy.clone());
                        }
                    }
                }
            }
        }

        getDescriptor().initialize(this, session);
    }

    /**
     * INTERNAL:
     * Initialize the queryTimeout to:
     *
     * NoTimeout: If queryTimeout is DefaultTimeout, either directly or via inheritance.
     * Parent's Timeout: If queryTimeout is something other than DefaultTimeout via my parent.
     */
    public void initializeQueryTimeout(AbstractSession session) {
        //if queryTimeout is DefaultTimeout, try to get my parent's queryTimeout
        if (getQueryTimeout() == DefaultTimeout) {
            if (getDescriptor().hasInheritance() && (this.getDescriptor().getInheritancePolicy().getParentDescriptor() != null)) {
                setQueryTimeout(this.getParentDescriptorQueryManager().getQueryTimeout());
            }
        }

        //if I have DefaultTimeout (via parent or not), set to NoTimeout
        if (getQueryTimeout() == DefaultTimeout) {
            setQueryTimeout(NoTimeout);
        }
    }

    /**
     * INTERNAL:
     * Get the parent DescriptorQueryManager.
     * Caution must be used in using this method as it expects the descriptor
     * to have inheritance.
     * Calling this when the descriptor that does not use inheritance will cause problems, #hasInheritance() must
     * always first be called.
     */
    public DescriptorQueryManager getParentDescriptorQueryManager() {
        return this.descriptor.getInheritancePolicy().getParentDescriptor().getQueryManager();
    }

    /**
     * INTERNAL:
     * Execute the post delete operation for the query
     */
    public void postDelete(DeleteObjectQuery query) {
        ObjectBuilder builder = this.descriptor.getObjectBuilder();
        // PERF: Only process relationships.
        if (!builder.isSimple()) {
            List<DatabaseMapping> mappings = builder.getRelationshipMappings();
            int size = mappings.size();
            for (int index = 0; index < size; index++) {
                mappings.get(index).postDelete(query);
            }
        }
    }

    /**
     * INTERNAL:
     * Post initializations after mappings are initialized.
     */
    public void postInitialize(AbstractSession session) throws DescriptorException {
        // If the additional criteria is specified, append it to the additional
        // join expression. We do this in postInitialize after all the mappings
        // have been fully initialized.
        if (additionalCriteria != null) {
            if (getDescriptor().hasInheritance() && getDescriptor().getInheritancePolicy().hasView()) {
                throw DescriptorException.additionalCriteriaNotSupportedWithInheritanceViews(getDescriptor());
            }

            JPAQueryBuilder queryBuilder = session.getQueryBuilder();
            Expression selectionCriteria = queryBuilder.buildSelectionCriteria(
               getDescriptor().getAlias(),
               additionalCriteria,
               session
            );

            updatePropertyParameterExpression(selectionCriteria);
            additionalJoinExpression = selectionCriteria.and(additionalJoinExpression);
        }

        if (additionalJoinExpression != null) {
            // The make sure the additional join expression has the correct
            // context, rebuild the additional join expression on a new
            // expression builder.
            additionalJoinExpression = additionalJoinExpression.rebuildOn(new ExpressionBuilder());
        }
    }

    /**
     * INTERNAL:
     * This method will walk the given expression and mark any parameter
     * expressions as property expressions. This is done when additional
     * criteria has been specified and parameter values must be resolved
     * through session properties.
     *
     * @see postInitialize
     */
    protected void updatePropertyParameterExpression(Expression exp) {
        if (exp.isCompoundExpression()) {
            updatePropertyParameterExpression(((CompoundExpression) exp).getFirstChild());
            updatePropertyParameterExpression(((CompoundExpression) exp).getSecondChild());
        } else if (exp.isFunctionExpression()) {
            for (Expression e : (Vector<Expression>) ((FunctionExpression) exp).getChildren()) {
                updatePropertyParameterExpression(e);
            }
        } else if (exp.isSubSelectExpression()) {
            ReportQuery subSelectQuery = ((SubSelectExpression) exp).getSubQuery();
            for (ReportItem item : subSelectQuery.getItems()) {
                updatePropertyParameterExpression(item.getAttributeExpression());
            }
        }

        if (exp.isParameterExpression()) {
            ((ParameterExpression) exp).setIsProperty(true);
        }
    }

    /**
     * INTERNAL:
     * Execute the post insert operation for the query
     */
    public void postInsert(WriteObjectQuery query) {
        ObjectBuilder builder = this.descriptor.getObjectBuilder();
        // PERF: Only process relationships.
        if (!builder.isSimple()) {
            List<DatabaseMapping> mappings = builder.getRelationshipMappings();
            int size = mappings.size();
            for (int index = 0; index < size; index++) {
                mappings.get(index).postInsert(query);
            }
        }
    }

    /**
     * INTERNAL:
     * Execute the post update operation for the query
     */
    public void postUpdate(WriteObjectQuery query) {
        ObjectBuilder builder = this.descriptor.getObjectBuilder();
        // PERF: Only process relationships.
        if (!builder.isSimple()) {
            // PERF: Only process changed mappings.
            ObjectChangeSet changeSet = query.getObjectChangeSet();
            if ((changeSet != null) && (!changeSet.isNew())) {
                List changeRecords = changeSet.getChanges();
                int size = changeRecords.size();
                for (int index = 0; index < size; index++) {
                    ChangeRecord record = (ChangeRecord)changeRecords.get(index);
                    record.getMapping().postUpdate(query);
                }
            } else {
                List<DatabaseMapping> mappings = builder.getRelationshipMappings();
                int size = mappings.size();
                for (int index = 0; index < size; index++) {
                    mappings.get(index).postUpdate(query);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Execute the pre delete operation for the query
     */
    public void preDelete(DeleteObjectQuery query) {
        ObjectBuilder builder = this.descriptor.getObjectBuilder();
        // PERF: Only process relationships.
        if (!builder.isSimple()) {
            List<DatabaseMapping> mappings = builder.getRelationshipMappings();
            int size = mappings.size();
            for (int index = 0; index < size; index++) {
                mappings.get(index).preDelete(query);
            }
        }
    }

    /**
     * INTERNAL:
     * Initialize the query manager.
     * Any custom queries must be inherited from the parent before any initialization.
     */
    public void preInitialize(AbstractSession session) {
        if (getDescriptor().isAggregateDescriptor()) {
            return;
        }

        // Must inherit parent query customization if not redefined.
        if (getDescriptor().isChildDescriptor()) {
            DescriptorQueryManager parentQueryManager = getDescriptor().getInheritancePolicy().getParentDescriptor().getQueryManager();

            if ((!hasInsertQuery()) && (parentQueryManager.hasInsertQuery())) {
                setInsertQuery((InsertObjectQuery)parentQueryManager.getInsertQuery().clone());
            }
            if ((!hasUpdateQuery()) && (parentQueryManager.hasUpdateQuery())) {
                setUpdateQuery((UpdateObjectQuery)parentQueryManager.getUpdateQuery().clone());
            }
            if ((!hasDeleteQuery()) && (parentQueryManager.hasDeleteQuery())) {
                setDeleteQuery((DeleteObjectQuery)parentQueryManager.getDeleteQuery().clone());
            }
            if ((!hasReadObjectQuery()) && (parentQueryManager.hasReadObjectQuery())) {
                setReadObjectQuery((ReadObjectQuery)parentQueryManager.getReadObjectQuery().clone());
            }
            if ((!hasReadAllQuery()) && (parentQueryManager.hasReadAllQuery())) {
                setReadAllQuery((ReadAllQuery)parentQueryManager.getReadAllQuery().clone());
            }
            if ((!getDoesExistQuery().isUserDefined()) && getDoesExistQuery().shouldCheckCacheForDoesExist()) {
                setDoesExistQuery(((DoesExistQuery)parentQueryManager.getDoesExistQuery().clone()));
            }
        }
    }

    /**
     * INTERNAL:
     * Execute the pre insert  operation for the query.
     */
    public void preInsert(WriteObjectQuery query) {
        ObjectBuilder builder = this.descriptor.getObjectBuilder();
        // PERF: Only process relationships.
        if (!builder.isSimple()) {
            List<DatabaseMapping> mappings = builder.getRelationshipMappings();
            int size = mappings.size();
            for (int index = 0; index < size; index++) {
                mappings.get(index).preInsert(query);
            }
        }
    }

    /**
     * INTERNAL:
     * Execute the pre update operation for the query
     */
    public void preUpdate(WriteObjectQuery query) {
        ObjectBuilder builder = this.descriptor.getObjectBuilder();
        // PERF: Only process relationships.
        if (!builder.isSimple()) {
            // PERF: Only process changed mappings.
            ObjectChangeSet changeSet = query.getObjectChangeSet();
            if ((changeSet != null) && (!changeSet.isNew())) {
                List changeRecords = changeSet.getChanges();
                int size = changeRecords.size();
                for (int index = 0; index < size; index++) {
                    ChangeRecord record = (ChangeRecord)changeRecords.get(index);
                    record.getMapping().preUpdate(query);
                }
            } else {
                List<DatabaseMapping> mappings = builder.getRelationshipMappings();
                int size = mappings.size();
                for (int index = 0; index < size; index++) {
                    mappings.get(index).preUpdate(query);
                }
            }
        }
    }

    /**
     * PUBLIC:
     * Remove all queries with the given query name from the set of pre-defined queries
     *
     * @see #removeQuery(String, Vector)
     */
    public void removeQuery(String queryName) {
        queries.remove(queryName);
    }

    /**
     * PUBLIC:
     * Remove the specific query with the given queryName and argumentTypes.
     *
     * @see #removeQuery(String)
     */
    public void removeQuery(String queryName, Vector argumentTypes) {
        Vector queries = (Vector)getQueries().get(queryName);
        if (queries == null) {
            return;
        } else {
            DatabaseQuery query = null;
            for (Enumeration enumtr = queries.elements(); enumtr.hasMoreElements();) {
                query = (DatabaseQuery)enumtr.nextElement();
                if (Helper.areTypesAssignable(argumentTypes, query.getArgumentTypes())) {
                    break;
                }
            }
            if (query != null) {
                queries.remove(query);
            }
        }
    }

    /**
     * ADVANCED:
     * Set the additional join criteria that will be used to form the additional
     * join expression. The additionalCriteria is a jpql fragment at this point.
     * @see setAdditionalJoinExpression
     */
    public void setAdditionalCriteria(String additionalCriteria) {
        this.additionalCriteria = additionalCriteria;
    }

    /**
     * ADVANCED:
     * Set the additional join expression. Used in conjunction with
     * multiple tables and inheritance relationships.
     * This can also be used if a sub-expression is always required to be
     * appended to all queries.  Such as tables that are shared based on a type field
     * without inheritance.
     */
    public void setAdditionalJoinExpression(Expression additionalJoinExpression) {
        this.additionalJoinExpression = additionalJoinExpression;
    }

    /**
     * ADVANCED:
     * Set the receiver's delete query.
     * This should be an instance of a valid subclass of DeleteObjectQuery.
     * If specified this is used by the descriptor to delete itself and its private parts from the database.
     * This gives the user the ability to define exactly how to delete the data from the database,
     * or access data external from the database or from some other framework.
     */
    public void setDeleteQuery(DeleteObjectQuery query) {
        this.deleteQuery = query;
        if (query == null) {
            return;
        }
        query.setIsUserDefined(true);
        query.setDescriptor(getDescriptor());
        if (query.isCallQuery()) {
            query.setIsFullRowRequired(true);
        }
    }

    /**
     * ADVANCED:
     * Set the receiver's delete SQL string.
     * This allows the user to override the SQL generated by EclipseLink, with their own SQL or procedure call.
     * The arguments are translated from the fields of the source row,
     * through replacing the field names marked by '#' with the values for those fields.
     * Warning: Allowing an unverified SQL string to be passed into this
     * method makes your application vulnerable to SQL injection attacks.
     * <p>
     *    Example, "delete from EMPLOYEE where EMPLOYEE_ID = #EMPLOYEE_ID".
     */
    public void setDeleteSQLString(String sqlString) {
        if (sqlString == null) {
            return;
        }

        DeleteObjectQuery query = new DeleteObjectQuery();
        query.setSQLString(sqlString);
        setDeleteQuery(query);
    }

    /**
     * ADVANCED:
     * Set the receiver's delete call.
     * This allows the user to override the delete operation.
     */
    public void setDeleteCall(Call call) {
        if (call == null) {
            return;
        }
        DeleteObjectQuery query = new DeleteObjectQuery();
        query.setCall(call);
        setDeleteQuery(query);
    }

    /**
     * INTERNAL:
     * Set the descriptor.
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
        //Gross alert: This is for the case when we are reading from XML, and
        //we have to compensate for no descriptor available at read time.  - JL
        populateQueries();

    }

    /**
     * ADVANCED:
     * Set the receiver's  does exist query.
     * This should be an instance of a valid subclass of DoesExistQuery.
     * If specified this is used by the descriptor to query existence of an object in the database.
     * This gives the user the ability to define exactly how to query existence from the database,
     * or access data external from the database or from some other framework.
     */
    public void setDoesExistQuery(DoesExistQuery query) {
        this.doesExistQuery = query;
        if (query == null) {
            return;
        }
        this.doesExistQuery.setIsUserDefined(true);
        this.doesExistQuery.setDescriptor(getDescriptor());
    }

    /**
     * ADVANCED:
     * Set the receiver's does exist SQL string.
     * This allows the user to override the SQL generated by EclipseLink, with there own SQL or procedure call.
     * The arguments are translated from the fields of the source row, through replacing the field names marked by '#'
     * with the values for those fields.
     * This must return null if the object does not exist, otherwise return a database row.
     * Warning: Allowing an unverified SQL string to be passed into this
     * method makes your application vulnerable to SQL injection attacks.
     * <p>
     * Example, "select EMPLOYEE_ID from EMPLOYEE where EMPLOYEE_ID = #EMPLOYEE_ID".
     */
    public void setDoesExistSQLString(String sqlString) {
        if (sqlString == null) {
            return;
        }
        getDoesExistQuery().setSQLString(sqlString);
        getDoesExistQuery().checkDatabaseForDoesExist();
    }

    /**
     * ADVANCED:
     * Set the receiver's does exist call.
     * This allows the user to override the does exist operation.
     */
    public void setDoesExistCall(Call call) {
        if (call == null) {
            return;
        }
        getDoesExistQuery().setCall(call);
    }

    /**
     * INTERNAL:
     * This method is explicitly used by the Builder only.
     */
    public void setExistenceCheck(String token) throws DescriptorException {
        if (token.equals("Check cache")) {
            checkCacheForDoesExist();
        } else if (token.equals("Check database")) {
            checkDatabaseForDoesExist();
        } else if (token.equals("Assume existence")) {
            assumeExistenceForDoesExist();
        } else if (token.equals("Assume non-existence")) {
            assumeNonExistenceForDoesExist();
        } else {
            throw DescriptorException.setExistenceCheckingNotUnderstood(token, getDescriptor());
        }
    }

    /**
     * INTENAL:
     * Set if a custom join expression is used.
     */
    protected void setHasCustomMultipleTableJoinExpression(boolean hasCustomMultipleTableJoinExpression) {
        this.hasCustomMultipleTableJoinExpression = hasCustomMultipleTableJoinExpression;
    }

    /**
     * ADVANCED:
     * Set the receiver's insert query.
     * This should be an instance of a valid subclass of InsertObjectQuery.
     * If specified this is used by the descriptor to insert itself into the database.
     * This gives the user the ability to define exactly how to insert the data into the database,
     * or access data external from the database or from some other framework.
     */
    public void setInsertQuery(InsertObjectQuery insertQuery) {
        this.insertQuery = insertQuery;
        if (insertQuery == null) {
            return;
        }
        this.insertQuery.setIsUserDefined(true);
        this.insertQuery.setDescriptor(getDescriptor());
    }

    /**
     * ADVANCED:
     * Set the receiver's insert call.
     * This allows the user to override the insert operation.
     */
    public void setInsertCall(Call call) {
        if (call == null) {
            return;
        }
        InsertObjectQuery query = new InsertObjectQuery();
        query.setCall(call);
        setInsertQuery(query);
    }

    /**
     * ADVANCED:
     * Set the receiver's insert SQL string.
     * This allows the user to override the SQL generated by EclipseLink, with their own SQL or procedure call.
     * The arguments are translated from the fields of the source row,
     * through replacing the field names marked by '#' with the values for those fields.
     * Warning: Allowing an unverified SQL string to be passed into this
     * method makes your application vulnerable to SQL injection attacks.
     * <p>
     * Example, "insert into EMPLOYEE (F_NAME, L_NAME) values (#F_NAME, #L_NAME)".
     */
    public void setInsertSQLString(String sqlString) {
        if (sqlString == null) {
            return;
        }

        InsertObjectQuery query = new InsertObjectQuery();
        query.setSQLString(sqlString);
        setInsertQuery(query);
    }

    /**
     * ADVANCED:
     * Return the receiver's insert call.
     * This allows the user to override the insert operation.
     */
    public Call getInsertCall() {
        if (getInsertQuery() == null) {
            return null;
        }
        return getInsertQuery().getDatasourceCall();
    }

    /**
     * ADVANCED:
     * Return the receiver's update call.
     * This allows the user to override the update operation.
     */
    public Call getUpdateCall() {
        if (getUpdateQuery() == null) {
            return null;
        }
        return getUpdateQuery().getDatasourceCall();
    }

    /**
     * ADVANCED:
     * Return the receiver's delete call.
     * This allows the user to override the delete operation.
     */
    public Call getDeleteCall() {
        if (getDeleteQuery() == null) {
            return null;
        }
        return getDeleteQuery().getDatasourceCall();
    }

    /**
     * ADVANCED:
     * Return the receiver's read-object call.
     * This allows the user to override the read-object operation.
     */
    public Call getReadObjectCall() {
        if (getReadObjectQuery() == null) {
            return null;
        }
        return getReadObjectQuery().getDatasourceCall();
    }

    /**
     * ADVANCED:
     * Return the receiver's read-all call.
     * This allows the user to override the read-all operation.
     */
    public Call getReadAllCall() {
        if (getReadAllQuery() == null) {
            return null;
        }
        return getReadAllQuery().getDatasourceCall();
    }

    /**
     * ADVANCED:
     * Return the receiver's does-exist call.
     * This allows the user to override the does-exist operation.
     */
    public Call getDoesExistCall() {
        if (getDoesExistQuery() == null) {
            return null;
        }
        return getDoesExistQuery().getDatasourceCall();
    }

    /**
     * INTERNAL:
     * Used in case descriptor has additional tables:
     * each additional table mapped to an expression joining it.
     */
    public Map<DatabaseTable, Expression> getTablesJoinExpressions() {
        if (tablesJoinExpressions == null) {
            tablesJoinExpressions = new HashMap<DatabaseTable, Expression>();
        }
        return tablesJoinExpressions;
    }

    /**
     * INTERNAL:
     * Used to set the multiple table join expression that was generated by EclipseLink as opposed
     * to a custom one supplied by the user.
     * @see #setMultipleTableJoinExpression(Expression)
     */
    public void setInternalMultipleTableJoinExpression(Expression multipleTableJoinExpression) {
        this.multipleTableJoinExpression = multipleTableJoinExpression;
    }

    /**
     * ADVANCED:
     * This is normally generated for descriptors that have multiple tables.
     * However, if the additional table does not reference the primary table's primary key,
     * this expression may be set directly.
     */
    public void setMultipleTableJoinExpression(Expression multipleTableJoinExpression) {
        this.multipleTableJoinExpression = multipleTableJoinExpression;
        setHasCustomMultipleTableJoinExpression(true);
    }

    /**
     * ADVANCED:
     * Set the receiver's read all query.
     * This should be an instance of a valid subclass of ReadAllQuery.
     * If specified this is used by the descriptor to read all instances of its class from the database.
     * This gives the user the ability to define exactly how to read all objects from the database,
     * or access data external from the database or from some other framework.
     * Note that this is only used on readAllObjects(Class), and not when an expression is provided.
     */
    public void setReadAllQuery(ReadAllQuery query) {
        this.readAllQuery = query;
        if (query == null) {
            return;
        }

        this.readAllQuery.setIsUserDefined(true);

        /* CR2260 - Steven Vo
         * Description:
         *  NullPointerException accessing null descriptor
         * Fix:
         *   Setting query's descriptor and reference class when descriptor is not null.
         *   Otherwise, wait until the descriptor is set.See populateQueries() that is
         *   called by setDescriptor()
         */
        if (this.getDescriptor() != null) {
            this.readAllQuery.setDescriptor(getDescriptor());
            this.readAllQuery.setReferenceClassName(getDescriptor().getJavaClassName());
            try {
                readAllQuery.setReferenceClass(getDescriptor().getJavaClass());
            } catch (ConversionException exception) {
            }
        }
    }

    /**
     * ADVANCED:
     * Set the receiver's read SQL string.
     * This allows the user to override the SQL generated by EclipseLink, with their own SQL or procedure call.
     * The arguments are translated from the fields of the read arguments row,
     * through replacing the field names marked by '#' with the values for those fields.
     * Note that this is only used on readAllObjects(Class), and not when an expression is provided.
     * Warning: Allowing an unverified SQL string to be passed into this
     * method makes your application vulnerable to SQL injection attacks.
     * <p>
     * Example, "select * from EMPLOYEE"
     */
    public void setReadAllSQLString(String sqlString) {
        if (sqlString == null) {
            return;
        }

        ReadAllQuery query = new ReadAllQuery();
        query.setSQLString(sqlString);
        setReadAllQuery(query);
    }

    /**
     * ADVANCED:
     * Set the receiver's read all call.
     * This allows the user to override the read all operation.
     * Note that this is only used on readAllObjects(Class), and not when an expression is provided.
     */
    public void setReadAllCall(Call call) {
        if (call == null) {
            return;
        }
        ReadAllQuery query = new ReadAllQuery();
        query.setCall(call);
        setReadAllQuery(query);
    }

    /**
     * ADVANCED:
     * Set the receiver's read query.
     * This should be an instance of a valid subclass of ReadObjectQuery>
     * If specified this is used by the descriptor to read itself from the database.
     * The read arguments must be the primary key of the object only.
     * This gives the user the ability to define exactly how to read the object from the database,
     * or access data external from the database or from some other framework.
     */
    public void setReadObjectQuery(ReadObjectQuery query) {
        this.readObjectQuery = query;
        if (query == null) {
            return;
        }
        this.readObjectQuery.setIsUserDefined(true);

        /* CR2260 - Steven Vo
         * Description:
         *  NullPointerException accessing null descriptor
         * Fix:
         *   Setting query's descriptor and reference class when descriptor is not null.
         *   Otherwise, wait until the descriptor is set.See populateQueries() that is
         *   called by setDescriptor()
         */
        if (this.getDescriptor() != null) {
            this.readObjectQuery.setDescriptor(getDescriptor());
            this.readObjectQuery.setReferenceClassName(getDescriptor().getJavaClassName());
            try {
                readObjectQuery.setReferenceClass(getDescriptor().getJavaClass());
            } catch (ConversionException exception) {
            }
        }
    }

    /**
     * ADVANCED:
     * Set the receiver's read SQL string.
     * This allows the user to override the SQL generated by EclipseLink, with their own SQL or procedure call.
     * The arguments are translated from the fields of the read arguments row,
     * through replacing the field names marked by '#' with the values for those fields.
     * This must accept only the primary key of the object as arguments.
     * Warning: Allowing an unverified SQL string to be passed into this
     * method makes your application vulnerable to SQL injection attacks.
     * <p>
     * Example, "select * from EMPLOYEE where EMPLOYEE_ID = #EMPLOYEE_ID"
     */
    public void setReadObjectSQLString(String sqlString) {
        if (sqlString == null) {
            return;
        }

        ReadObjectQuery query = new ReadObjectQuery();
        query.setSQLString(sqlString);
        setReadObjectQuery(query);
    }

    /**
     * ADVANCED:
     * Set the receiver's read object call.
     * This allows the user to override the read object operation.
     * This must accept only the primary key of the object as arguments.
     */
    public void setReadObjectCall(Call call) {
        if (call == null) {
            return;
        }
        ReadObjectQuery query = new ReadObjectQuery();
        query.setCall(call);
        setReadObjectQuery(query);
    }

    /**
     * ADVANCED:
     * Set the receiver's update query.
     * This should be an instance of a valid subclass of UpdateObjectQuery.
     * If specified this is used by the descriptor to update itself in the database.
     * If the receiver uses optimistic locking this must raise an error on optimistic lock failure.
     * This gives the user the ability to define exactly how to update the data into the database,
     * or access data external from the database or from some other framework.
     */
    public void setUpdateQuery(UpdateObjectQuery updateQuery) {
        this.updateQuery = updateQuery;
        if (updateQuery == null) {
            return;
        }
        this.updateQuery.setIsUserDefined(true);
        this.updateQuery.setDescriptor(getDescriptor());
    }

    /**
     * ADVANCED:
     * Set the receiver's update SQL string.
     * This allows the user to override the SQL generated by EclipseLink, with there own SQL or procedure call.
     * The arguments are translated from the fields of the source row,
     * through replacing the field names marked by '#' with the values for those fields.
     * This must check the optimistic lock field and raise an error on optimistic lock failure.
     * Warning: Allowing an unverified SQL string to be passed into this
     * method makes your application vulnerable to SQL injection attacks.
     * <p>
     * Example, "update EMPLOYEE set F_NAME to #F_NAME, L_NAME to #L_NAME where EMPLOYEE_ID = #EMPLOYEE_ID".
     */
    public void setUpdateSQLString(String sqlString) {
        if (sqlString == null) {
            return;
        }

        UpdateObjectQuery query = new UpdateObjectQuery();
        query.setSQLString(sqlString);
        setUpdateQuery(query);
    }

    /**
     * ADVANCED:
     * Set the receiver's update call.
     * This allows the user to override the update operation.
     */
    public void setUpdateCall(Call call) {
        if (call == null) {
            return;
        }
        UpdateObjectQuery query = new UpdateObjectQuery();
        query.setCall(call);
        setUpdateQuery(query);
    }

    /**
     * PUBLIC:
     * Return the number of seconds queries will wait for their Statement to execute.
     *
     * - DefaultTimeout: get queryTimeout from parent DescriptorQueryManager. If there is no
     * parent, default to NoTimeout
     * - NoTimeout, 1..N: overrides parent queryTimeout
     */
    public int getQueryTimeout() {
        return queryTimeout;
    }

    /**
     * PUBLIC:
     * Set the number of seconds that queries will wait for their Statement to execute.
     * If the limit is exceeded, a DatabaseException is thrown.
     *
     * - DefaultTimeout: get queryTimeout from parent DescriptorQueryManager. If there is no
     * parent, default to NoTimeout
     * - NoTimeout, 1..N: overrides parent queryTimeout
     */
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    /**
     * INTERNAL:
     * Returns the collection of cached Update calls.
     */
    private ConcurrentFixedCache getCachedUpdateCalls() {
        if (cachedUpdateCalls == null) {
            this.cachedUpdateCalls = new ConcurrentFixedCache(10);
        }
        return this.cachedUpdateCalls;
    }

    /**
     * INTERNAL:
     * Returns the collection of cached expression queries.
     */
    private ConcurrentFixedCache getCachedExpressionQueries() {
        if (cachedExpressionQueries == null) {
            this.cachedExpressionQueries = new ConcurrentFixedCache(20);
        }
        return this.cachedExpressionQueries;
    }

    /**
     * ADVANCED:
     * Return the size of the update call cache.
     * The update call cache is used to cache the update SQL to avoid regeneration.
     * Since every update with different fields produces different SQL,
     * this cache allows caching of the update SQL based on the fields being updated.
     * The default cache size is 10, the update call cache can be disabled through setting the size to 0.
     */
    public int getUpdateCallCacheSize() {
        return getCachedUpdateCalls().getMaxSize();
    }

    /**
     * ADVANCED:
     * Set the size of the update call cache.
     * The update call cache is used to cache the update SQL to avoid regeneration.
     * Since every update with different fields produces different SQL,
     * this cache allows caching of the update SQL based on the fields being updated.
     * The default cache size is 10, the update call cache can be disabled through setting the size to 0.
     */
    public void setUpdateCallCacheSize(int updateCallCacheSize) {
        getCachedUpdateCalls().setMaxSize(updateCallCacheSize);
    }

    /**
     * INTERNAL:
     * Return the cached update SQL call based on the updated fields.
     * PERF: Allow caching of the update SQL call to avoid regeneration.
     */
    public Vector getCachedUpdateCalls(Vector updateFields) {
        return (Vector) getCachedUpdateCalls().get(updateFields);
    }

    /**
     * INTERNAL:
     * Cache a clone of the update SQL calls based on the updated fields.
     * If the max size is reached, do not cache the call.
     * The call's query must be dereferenced in order to allow the GC of a related session.
     * PERF: Allow caching of the update SQL call to avoid regeneration.
     */
    public void putCachedUpdateCalls(Vector updateFields, Vector updateCalls) {
        Vector vectorToCache = updateCalls;
        if (!updateCalls.isEmpty()) {
            int updateCallsSize = updateCalls.size();
            vectorToCache = new NonSynchronizedVector(updateCallsSize);
            for (int i = 0; i < updateCallsSize; i++) {
                DatasourceCall updateCall = (DatasourceCall)updateCalls.get(i);
                // clone call and dereference query for DatasourceCall and EJBQLCall
                DatasourceCall clonedUpdateCall = (DatasourceCall) updateCall.clone();
                clonedUpdateCall.setQuery(null);
                vectorToCache.add(clonedUpdateCall);
            }
        }
        getCachedUpdateCalls().put(updateFields, vectorToCache);
    }

    /**
     * INTERNAL:
     * Return the cached SQL call for the expression query.
     * PERF: Allow caching of expression query SQL call to avoid regeneration.
     */
    public DatabaseQuery getCachedExpressionQuery(DatabaseQuery query) {
        return (DatabaseQuery) getCachedExpressionQueries().get(query);
    }

    /**
     * INTERNAL:
     * Set the cached SQL call for the expression query.
     * PERF: Allow caching of expression query SQL call to avoid regeneration.
     */
    public void putCachedExpressionQuery(DatabaseQuery query) {
        getCachedExpressionQueries().put(query, query);
    }
}
