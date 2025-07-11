/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2023 IBM Corporation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     10/15/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
//     05/24/2011-2.3 Guy Pelletier
//       - 345962: Join fetch query when using tenant discriminator column fails.
//     06/30/2011-2.3.1 Guy Pelletier
//       - 341940: Add disable/enable allowing native queries
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     02/06/2013-2.5 Guy Pelletier
//       - 382503: Use of @ConstructorResult with createNativeQuery(sqlString, resultSetMapping) results in NullPointerException
//     09/24/2014-2.6 Rick Curtis
//       - 443762 : Misc message cleanup.
//     12/18/2014-2.6 Rick Curtis
//       - 454189 : Misc message cleanup.#2
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL: English ResourceBundle for QueryException messages.
 *
 * @author Xi Chen
 */
public final class QueryExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
       { "6001", "Cursored SQL queries must provide an additional query to retrieve the size of the result set." },
       { "6002", "Aggregated objects cannot be written/deleted/queried independently from their owners. {1}Descriptor: [{0}]" },
       { "6003", "The number of arguments provided to the query for execution does not match the number of arguments in the query definition." },
       { "6004", "The object [{0}], of class [{1}], with identity hashcode (System.identityHashCode()) [{2}], {3} is not from this UnitOfWork object space, but the parent session''s. " +
               "The object was never registered in this UnitOfWork, {3} but read from the parent session and related to an object registered in the UnitOfWork. " +
               "Ensure that you are correctly {3} registering your objects. " +
               "If you are still having problems, you can use the UnitOfWork.validateObjectSpace() method to {3} help debug where the error occurred. " +
               "For more information, see the manual or FAQ." },
       { "6005", "The object [{0}], of class [{1}], with identity hashcode (System.identityHashCode()) [{2}], {3} is the original to a registered new object. " +
               "The UnitOfWork clones registered new objects, so you must ensure that an object {3} is registered before it is referenced by another object. " +
               "If you do not want the new object to be cloned, use the {3}UnitOfWork.registerNewObject(Object) API. " +
               "If you are still having problems, you can use the UnitOfWork.validateObjectSpace() {3} method to help debug where the error occurred. " +
               "For more information, see the manual or FAQ." },
       { "6006", "The mapping [{0}] does not support batch reading." },
       { "6007", "Missing descriptor for [{0}]." },
       { "6008", "Missing descriptor for [{0}] for query named [{1}]." },
       { "6013", "Incorrect size query given to CursoredStream." },
       { "6014", "Objects cannot be written during a UnitOfWork, they must be registered." },
       { "6015", "Invalid query key [{0}] in expression." },
       { "6016", "Objects or the database cannot be changed through a ServerSession.  All changes must be done through a ClientSession''s UnitOfWork." },
       { "6020", "No concrete class indicated for the type in the row [{0}]." },
       { "6021", "Cursors are not supported for interface descriptors, or abstract class multiple table descriptors using expressions.  Consider using custom SQL or multiple queries." },
       { "6023", "The list of fields to insert into the table [{0}] is empty.  You must define at least one mapping for this table." },
       { "6024", "Modify queries require an object to modify." },
       { "6026", "Query named [{0}] is not defined. Domain class: [{1}]" },
       { "6027", "Query sent to a unactivated UnitOfWork." },
       { "6028", "An attempt to read beyond the end of stream occurred." },
       { "6029", "A reference class must be provided." },
       { "6030", "Refreshing is not possible if caching is not enabled." },
       { "6031", "size() is only supported on expression queries, unless a size query is given." },
       { "6032", "The SQL statement has not been properly set." },
       { "6034", "Invalid query item expression [{0}]." },
       { "6041", "The selection object passed to a ReadObjectQuery was null." },
       { "6042", "A session name must be specified for non-object-level queries.  See the setSessionName(String) method." },
       { "6043", "ReportQueries without primary keys cannot use readObject(). {1}ReportQueryResult: [{0}]." },
       { "6044", "The primary key read from the row [{0}] during the execution of the query was detected to be null.  Primary keys must not contain null." },
       { "6045", "The subclass [{0}], indicated in the row while building the object, has no descriptor defined for it." },
       { "6046", "Cannot delete an object of a read-only class.  The class [{0}] is declared as read-only in this UnitOfWork." },
       { "6047", "Invalid operator [{0}] in expression." },
       { "6048", "Illegal use of getField() [{0}] in expression." },
       { "6049", "Illegal use of getTable() [{0}] in expression." },
       { "6050", "ReportQuery result size mismatch.  Expecting [{0}], but retrieved [{1}]" },
       { "6051", "Partial object queries are not allowed to maintain the cache or be edited.  You must use dontMaintainCache()." },
       { "6052", "An outer join (getAllowingNull or anyOfAllowingNone) is only valid for OneToOne, OneToMany, ManyToMany, AggregateCollection and DirectCollection Mappings, and cannot be used for the mapping [{0}]." },
       { "6054", "Cannot add the object [{0}], of class [{1}], to container class [{2}] using policy [{3}]." },
       { "6055", "The method invocation of the method [{0}] on the object [{1}], of class [{2}], triggered an exception." },
       { "6056", "Cannot create a clone of object [{0}], of class [{1}], using [{2}]." },
       { "6057", "The method [{0}] is not a valid method to call on object [{1}]." },
       { "6058", "The method [{0}] was not found in class [{1}]." },
       { "6059", "The class [{0}] cannot be used as the container for the results of a query because it cannot be instantiated." },
       { "6060", "Could not use object [{0}] of type [{1}] as a key into [{2}] of type [{3}].  The key cannot be compared with the keys currently in the Map." },
       { "6061", "Cannot reflectively access the method [{0}] for object [{1}], of class [{2}]." },
       { "6062", "The method [{0}], called reflectively on object [{1}], of class [{2}], triggered an exception." },
       { "6063", "Invalid operation [{0}] on cursor." },
       { "6064", "Cannot remove the object [{0}], of class [{1}], from container class [{2}] using policy [{3}]." },
       { "6065", "Cannot add the object [{0}], of class [{1}], to container [{2}]." },
       { "6066", "The object [{0}], of class [{1}], with identity hashcode (System.identityHashCode()) [{2}], {3}has been deleted, but still has references.  Deleted objects cannot be referenced after being deleted. {3}Ensure that you are correctly registering your objects.  If you are still having problems, you can use the UnitOfWork.validateObjectSpace() {3}method to help debug where the error occurred.  For more information, see the manual or FAQ." },
       { "6067", "Cannot reflectively access the field [{0}] for object [{1}], of class [{2}]." },
       { "6068", "Cannot compare table reference to [{0}] in expression." },
       { "6069", "The field [{0}] in this expression has an invalid table in this context." },
       { "6070", "Invalid use of a query key [{0}] representing a \"to-many\" relationship in an expression.  Use anyOf() rather than get()." },
       { "6071", "Invalid use of anyOf() for a query key [{0}] not representing a to-many relationship in an expression.  Use get() rather than anyOf()." },
       { "6072", "Querying across a VariableOneToOneMapping is not supported. {2}Descriptor: [{0}] {2}Mapping: [{1}]" },
       { "6073", "Malformed expression in query.  Attempting to print an object reference into an SQL statement for query key [{0}]." },
       { "6074", "This expression cannot determine if the object conforms in memory.  You must set the query to check the database." },
       { "6075", "Object comparisons can only use the equal() or notEqual() operators.  Other comparisons must be done through query keys or direct attribute level comparisons. {1}Expression: [{0}]" },
       { "6076", "Object comparisons can only be used with OneToOneMappings.  Other mapping comparisons must be done through query keys or direct attribute level comparisons. {2}Mapping: [{0}] {2}Expression: [{1}]" },
       { "6077", "Object comparisons cannot be used in parameter queries.  You must build the expression dynamically. {1}Expression: [{0}]" },
       { "6078", "The class of the argument for the object comparison is incorrect. {3}Expression: [{0}] {3}Mapping: [{1}] {3}Argument: [{2}]" },
       { "6079", "Object comparison to NULL cannot be used for target foreign key relationships.  Query on the source primary key instead. {3}Expression: [{0}] {3}Mapping: [{1}] {3}Argument: [{2}]" },
       { "6080", "Invalid database call [{0}].  The call must be an instance of DatabaseCall." },
       { "6081", "Invalid database accessor [{0}].  The accessor must be an instance of DatabaseAccessor." },
       { "6082", "The method [{0}] with argument types [{1}] cannot be invoked on Expression." },
       { "6083", "Queries using in() cannot be parameterized.  Disable either query preparation or binding." },
       { "6084", "The redirection query was not configured properly.  The class or method name was not set." },
       { "6085", "The redirection query''s method is not defined or defined with the wrong arguments.  It must be declared \"public static\" and have arguments (DatabaseQuery, Record, Session) or (Session, Vector). {2}Class: [{0}] {2}Method: [{1}]" },
       { "6086", "The redirection query''s method invocation triggered an exception." },
       { "6087", "The example object class [{0}] does not match the reference object class [{1}]." },
       { "6088", "There are no attributes for the ReportQuery." },
       { "6089", "The expression has not been initialized correctly.  Only a single ExpressionBuilder should be used for a query. {1}For parallel expressions, the query class must be provided to the ExpressionBuilder constructor, and the query''s ExpressionBuilder must {1}always be on the left side of the expression. {1}Expression: [{0}]" },
       { "6090", "Cannot set ReportQuery to \"check cache only\"." },
       { "6091", "The type of the constant [{0}], used for comparison in the expression, does not match the type of the attribute [{1}]." },
       { "6092", "Uninstantiated ValueHolder detected. You must instantiate the relevant Valueholders to perform this in-memory query." },
       { "6093", "Invalid Type Expression on [{0}].  The class does not have a descriptor, or a descriptor that does not use inheritance or uses a ClassExtractor for inheritance" },
       { "6094", "The parameter name [{0}] in the query''s selection criteria does not match any parameter name defined in the query." },
       { "6095", "Public clone method is required." },
       { "6096", "Clone method is inaccessible." },
       { "6097", "clone method threw an exception: {0}." },
       { "6098", "Unexpected Invocation Exception: {0}." },
       { "6099", "Joining across inheritance class with multiple table subclasses not supported: {0}, {1}" },
       { "6100", "Multiple values detected for single-object read query." },
       { "6101", "Executing this query could violate the integrity of the global session cache which must contain only the latest versions of objects. " +
               "In order to execute a query that returns objects as of a past time, try one of the following: Use a HistoricalSession (acquireSessionAsOf), " +
               "all objects read will be cached and automatically read as of the same time.  This will apply even to triggering object relationships. " +
               "Set shouldMaintainCache to false.  You may make any object expression as of a past time, " +
               "provided none of its fields are represented in the result set (i.e. used only in the where clause)." },
       { "6102", "At present historical queries only work with Oracle 9R2 or later databases, as it uses Oracle''s Flashback feature." },
       { "6103", "You may not execute a WriteQuery from inside a read-only HistoricalSession.  To restore past objects, try the following: read the same object as it is now with a UnitOfWork and commit the UnitOfWork." },
       { "6104", "The object, {0}, does not exist in the cache." },
       { "6105", "Query has to be reinitialized with a cursor stream policy." },
       { "6106", "The object of type [{0}] with primary key [{1}] does not exist in the cache." },
       { "6107", "Missing update statements on UpdateAllQuery." },
       { "6108", "Update all query does not support inheritance with multiple tables" },
       { "6109", "The named fetch group ({0}) is not defined at the descriptor level." },
       { "6110", "Read query cannot conform the unfetched attribute ({0}) of the partially fetched object in the unit of work identity map." },
       { "6111", "The fetch group attribute ({0}) is not defined or not mapped." },
       { "6112", "Fetch group cannot be set on report query." },
       { "6113", "Fetch group cannot be used along with partial attribute reading." },
       { "6114", "You must define a fetch group manager at descriptor ({0}) in order to set a fetch group on the query ({1})" },
       { "6115", "Queries on isolated classes, or queries set to use exclusive connections, must not be executed on a ServerSession or, in CMP, outside of a transaction." },
       { "6116", "No Call or Interaction was specified for the attempted operation." },
       { "6117", "Cannot set a query, that uses a cursored result, to cache query results." },
       { "6118", "A query on an Isolated class must not cache query results on the query." },
       { "6119", "The join expression {0} is not valid, or for a mapping type that does not support joining." },
       { "6120", "The partial attribute {0} is not a valid attribute of the class {1}." },
       { "6121", "The query has not been defined correctly, the expression builder is missing.  For sub and parallel queries ensure the queries builder is always on the left." },
       { "6122", "The expression is not a valid expression. {0}" },
       { "6123", "The container class specified [{0}] cannot be used because the container needs to implement {1}." },
       { "6124", "Required query of {0}, found {1}" },
       { "6125", "ReadQuery.clearQueryResults() can no longer be called. The call to clearQueryResults now requires that the session be provided. clearQueryResults(session) should be called." },
       { "6126", "A query is being executed that uses both conforming and cached query results.  These two settings are incompatible." },
       { "6127", "A reflective call failed on the EclipseLink class {0}, your environment must be set up to allow Java reflection." },
       { "6128", "Batch Reading is not supported on Queries using custom Calls."},
       { "6129", "Refreshing is not possible if the query does not go to the database." },
       { "6130", "Custom SQL failed to provide discriminator column : {0}, as defined in SQLResultSetMapping : {1}."},
       { "6131", "DeleteAllQuery that defines objects to be deleted using setObjects method with non-null argument must also define the corresponding selection criteria. {1}Objects: [{2}]{1}Descriptor: [{0}]" },
       { "6132", "Query argument {0} not found in list of parameters provided during query execution."},
       { "6133", "First argument of addUpdate method defines a field to be assigned a new value - it cannot be null."},
       { "6134", "Attribute name or expression passed as a first parameter to addUpdate method does not define a field. {1}Attribute name or Expression: [{2}]{1}Descriptor: [{0}]" },
       { "6135", "Attribute name or expression passed as a first parameter to addUpdate method defines a field from a table that is not mapped to query descriptor. {1}Attribute name or Expression: [{2}]{1}Wrong field: [{3}]{1}Descriptor: [{0}]" },
       { "6136", "Classes mapped with multi table inheritance cannot be ReportQuery items. Item: {0}, Expression: {1}."},
       { "6137", "An Exception was thrown while executing a ReportQuery with a constructor expression: {0}" },
       { "6138", "Query requires temporary storage, but {0} does not support temporary tables." },
       { "6139", "Problem finding mapping for {0} defined in field result named {1}" },
       { "6140", "You have attempted to assign join expressions to the Report Item {1} of type {0}.  Join expressions are only applicable on Items that return an Persistent Object."},
       { "6141", "A ClassCastException was thrown when trying to convert {0} to a class in a query hint."},
       { "6142", "The value {1} supplied to the query hint {0} navigated an illegal relationship.  The relationship {2} is not a OneToOne or a OneToMany relationship."},
       { "6143", "The value {1} supplied to the query hint {0} navigated a non-existent relationship.  The relationship {2} does not exist."},
       { "6144", "The value {1} supplied to the query hint {0} did not contain enough tokens.  The join must start with the identification variable of the query.  For instance, in the query \"SELECT x from X x\", to refer to a \"y\" belonging to \"x\", you should use the hint \"x.y\"."},
       { "6145", "Count distinct on a composite primary key class [{0}] is not supported. Descriptor [{1}] "},
       { "6146", "The value {1} supplied to the query hint {0} is not a valid value, valid values are Integer or Strings that can be parsed to int values."},
       { "6147", "The expression {0} is not valid for partial attribute reading." },
       { "6148", "Adding {0} to PLSQLStoredProcedureCall is not supported." },
       { "6149", "PLSQLStoredProcedureCall cannot use an unnamed argument." },
       { "6150", "A null value cannot be used as a key in a container of type [{1}]. Ensure your key values for the objects of type [{0}] cannot be null." },
       { "6151", "An exception occurred while attempting to set a Redirector {0} passed through a Jakarta Persistence Query Hint {1}.  Please verify that the provided Redirector implements org.eclipse.persistence.queries.QueryRedirector."},
       { "6152", "An exception occurred while attempting to instantiate the class {0} passed through a Jakarta Persistence Query Hint {1}.  Please verify that the class has a default constructor."},
       { "6153", "CompatibleType must be set on complex type: {0}."},
       { "6154", "TypeName must be set on complex type: {0}."},
       { "6155", "No relation table found in {0}. {2}joinCriteria Expression: [{1}]"},
       { "6156", "An exception occurred while attempting to set read the map key for [{0}]: [{1}]."},
       { "6157", "Element [{0}] is being added to a map without a key.  This generally means the database does not hold a key that is expected."},
       { "6158", "MapContainerPolicy has been asked to unwrap element [{0}] which is not a map element.  This means that the incorrect container policy is being used."},
       { "6159", "Cannot find mapping for MapEntryExpression with base: [{0}]."},
       { "6160", "MapEntryExpression with base: [{0}] refers to mapping [{1}] which is not a collection mapping.  Maps may only exist on colleciton mappings."},
       { "6161", "MapEntryExpression with base: [{0}] refers to mapping [{1}] which does not refer to a map."},
       { "6162", "List order column [{0}] contains wrong values:{2}{1}"},
       { "6163", "index() requires QueryKeyExpression, cannot be applied to [{0}]"},
       { "6164", "index() requires QueryKeyExpression with CollectionMapping that has non-null list order column. [{1}] does not meet this condition in [{0}]"},
       { "6165", "Batch fetch using IN requires singleton primary key."},
       { "6166", "An attempt was made to cast outside of an inheritance hierarchy.  [{0}] does not appear in the class heirarchy for [{1}]. Note: This exception could also indicate you are casting on a Table-Per-Class inheritance relationship which is not supported for casting."},
       { "6167", "A cast has been invoked on an expression that does not use inheritance: [{0}]."},
       { "6168", "Query failed to prepare, unexpected error occurred: [{0}]."},
       { "6169", "A mapping was configured to use IN batch fetching, but the original query was not configured to use IN batch fetching and must be: [{0}]."},
       { "6171", "Partitioning not support for the session type [{0}]. Only ServerSession and ClientSession are supported."},
       { "6172", "Missing connection pool for partitioning [{0}]."},
       { "6173", "Connection pool [{0}] failed to fail-over, all servers are dead."},
       { "6174", "No value was provided for the session property [{0}]. This exception is possible when using additional criteria or tenant discriminator columns without specifying the associated contextual property. These properties must be set through EntityManager, EntityManagerFactory or persistence unit properties. If using native EclipseLink, these properties should be set directly on the session."},
       { "6175", "Native SQL queries have been disabled. This is done either by setting the persistence unit property \"{0}\" to false or having at least one multitenant entity defined in your persistence unit. Check your persistence unit specification. To allow native sql queries, set this property to true. Alternatively, individual queries may bypass this setting by setting the \"{1}\" query hint to true."},
       { "6176", "An exception was thrown while initializing the constructor from the class [{0}]:  [{1}]"},
       { "6177", "The column result [{0}] was not found in the results of the query."},
       { "6178", "isResultSetAccessOptimizedQuery set to true conflicts with other query settings."},
       { "6179", "Failed to deserialize sopObject from [{0}] in [{1}]"},
       { "6180", "serialized sopObject is not found in [{0}] in [{1}]"},
       { "6181", "sopObject has a wrong version [{0}] in [{1}] in [{2}]"},
       { "6182", "sopObject has a wrong primary key [{0}] in [{1}] in [{2}]"},
       { "6183",  "The mapping type {1} for attribute {2} from {0} is not supported with Query By Example functionality.  If the attribute can safely be ignored then add it to the ignore list or set example validation to false in the policy."},
       { "6184", "Incorrect value type {0} is passed to the constructor expression. Complex types like arrays, collections, objects are not supported." },

    };

    /**
     * Default constructor.
     */
    public QueryExceptionResource() {
        // for reflection
    }

    /**
     * Return the lookup table.
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
