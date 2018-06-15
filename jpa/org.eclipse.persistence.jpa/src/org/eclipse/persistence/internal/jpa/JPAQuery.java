/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     08/01/2012-2.5 Chris Delahunt
//       - 371950: Metadata caching
//     08/24/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/11/2012-2.5 Guy Pelletier
//       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
package org.eclipse.persistence.internal.jpa;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.EntityResult;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.Session;

/**
 * <b>Purpose</b>:
 * A JPA placeholder Query object to store JPQL strings so that processing the string is delayed
 * until Login.<p>
 *
 * @author Chris Delahunt
 * @since TopLink Essentials
 */
public class JPAQuery extends DatabaseQuery  {
    private String lockMode;
    private String jpqlString;
    private String sqlString;
    private StoredProcedureCall call;
    private String resultClassName;
    private List<String> resultClassNames;
    private List<String> resultSetMappingNames;
    private Map<String, Object> hints;

    public JPAQuery() {
    }

    public JPAQuery(String jpqlString) {
        this.jpqlString=jpqlString;
    }

    /**
     * JPQL
     * @param name
     * @param jpqlString
     * @param lockMode
     * @param hints
     */
    public JPAQuery(String name, String jpqlString, String lockMode, Map<String, Object> hints) {
        this.name = name;
        this.jpqlString = jpqlString;
        this.flushOnExecute = null;
        this.hints = hints;
        this.lockMode = lockMode;
        if (lockMode == null) {
            this.lockMode = "NONE";
        }
    }

    /*
     * SQL returning an entity
     */
    public JPAQuery(String queryName, String sqlString, Map<String, Object> hints) {
        this.name = queryName;
        this.sqlString = sqlString;
        this.flushOnExecute = null;
        this.hints = hints;
        this.lockMode = null;
    }

    /*
     * Stored Proc returning an Entity
     */
    public JPAQuery(String queryName, StoredProcedureCall call, Map<String, Object> hints) {
        this.name = queryName;
        this.call = call;
        this.flushOnExecute = null;
        this.hints = hints;
        this.lockMode = null;
    }

    public void addResultClassNames(String className) {
        if (resultClassNames == null) {
            resultClassNames = new ArrayList<String>();
        }
        this.resultClassNames.add(className);
    }

    public void addResultSetMapping(String resultSetMapping){
        if (resultSetMappingNames == null) {
            resultSetMappingNames = new ArrayList<String>();
        }
        this.resultSetMappingNames.add(resultSetMapping);
    }

    /**
     * INTERNAL:
     * This should never be called and is only here because it is needed as an extension
     * to DatabaseQuery.  Perhaps exception should be thrown to warn users, but for now
     * it will execute the resulting query instead, this allows JPA style queries to be executed
     * on a normal EclipseLink Session.
     */
    @Override
    public Object executeDatabaseQuery() throws DatabaseException, OptimisticLockException{
        return getSession().executeQuery(getDatabaseQuery());
    }

    public DatabaseQuery getDatabaseQuery() {
        return (DatabaseQuery)getProperty("databasequery");
    }

    /**
     * INTERNAL:
     * For table per tenant queries the descriptor list will extracted from
     * parsing the jpql query and cached here.
     */
    @Override
    public List<ClassDescriptor> getDescriptors() {
        return descriptors;
    }

    /**
     * Return the JPA query hints.
     */
    public Map<String, Object> getHints(){
        return hints;
    }

    /**
     * Return the JPQL string.
     */
    @Override
    public String getJPQLString(){
        return jpqlString;
    }

    /**
     * Return true if this query is a jpql query.
     */
    public boolean isJPQLQuery() {
        return jpqlString != null;
    }

    /**
     * Return true if this query is an sql query.
     */
    public boolean isSQLQuery() {
        return sqlString != null;
    }

    /**
     * INTERNAL:
     * Generate the DatabaseQuery query from the JPA named query.
     */
    @Override
    public void prepare() {
        DatabaseQuery query = null;
        ClassLoader loader = session.getDatasourcePlatform().getConversionManager().getLoader();

        if (isSQLQuery()) {
            query = processSQLQuery(getSession());
        } else if (isJPQLQuery()) {
            query = processJPQLQuery(getSession());
        } else if (call != null) {
            query = processStoredProcedureQuery(getSession());
            if (call.hasParameters() ) {
                //convert the type in the parameters;  query.convertClassNamesToClasses does not cascade to the call
                for (Object value: call.getParameters()) {
                    if (value instanceof Object[]) {
                        //must be inout type, and the out portion is a DatabaseField
                        ((DatabaseField) ((Object[])value)[1]).convertClassNamesToClasses(loader);
                        value =  ((Object[])value)[0];
                    }
                    if (value instanceof DatabaseField) {
                        ((DatabaseField) value).convertClassNamesToClasses(loader);
                    }
                }
            }
        }

        // Make sure all class names have been converted.
        query.convertClassNamesToClasses(loader);

        setDatabaseQuery(query);
    }

    /**
     * INTERNAL:
     * Convert the JPA query into a DatabaseQuery.
     */
    public DatabaseQuery processJPQLQuery(Session session){
        ClassLoader classloader = session.getDatasourcePlatform().getConversionManager().getLoader();
        LockModeType lockModeEnum = null;

        // Must handle errors if a JPA 2.0 option is used in JPA 1.0.
        try {
            lockModeEnum = LockModeType.valueOf(lockMode);
        } catch (Exception ignore) {
            // Ignore JPA 2.0 in JPA 1.0, reverts to no lock.
        }

        DatabaseQuery ejbquery = EJBQueryImpl.buildEJBQLDatabaseQuery(getName(), jpqlString, (AbstractSession) session, lockModeEnum, hints, classloader);
        ejbquery.setName(getName());

        return ejbquery;
    }

    /**
     * INTERNAL:
     * Convert the SQL string into a DatabaseQuery.
     */
    public DatabaseQuery processSQLQuery(Session session){
        DatabaseQuery query = null;
        ClassLoader loader = session.getDatasourcePlatform().getConversionManager().getLoader();

        if (resultClassName != null) {
            Class clazz = session.getDatasourcePlatform().getConversionManager().convertClassNameToClass(resultClassName);
            query = EJBQueryImpl.buildSQLDatabaseQuery(clazz, sqlString, hints, loader, (AbstractSession)session);
        } else if (resultSetMappingNames != null) {
            query = EJBQueryImpl.buildSQLDatabaseQuery(resultSetMappingNames.get(0), sqlString, hints, loader, (AbstractSession)session);
        } else {
            // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
            query = EJBQueryImpl.buildSQLDatabaseQuery(sqlString, hints, loader, (AbstractSession)session);
        }

        query.setName(this.getName());
        return query;
    }

    /**
     * INTERNAL:
     * Convert the StoredProc call into a DatabaseQuery.
     */
    public DatabaseQuery processStoredProcedureQuery(Session session){
        DatabaseQuery query = null;
        ClassLoader loader = session.getDatasourcePlatform().getConversionManager().getLoader();

        if (resultClassNames != null) {
            List<SQLResultSetMapping> resultSetMappings = new ArrayList<SQLResultSetMapping>();

            for (String resultClass : resultClassNames) {
                SQLResultSetMapping mapping = new SQLResultSetMapping(resultClass);

                EntityResult entityResult = new EntityResult(resultClass);
                mapping.addResult(entityResult);

                resultSetMappings.add(mapping);
            }
            query = StoredProcedureQueryImpl.buildResultSetMappingQuery(resultSetMappings, call, hints, loader, (AbstractSession)session);
        } else if (resultSetMappingNames != null) {
            query = StoredProcedureQueryImpl.buildResultSetMappingNameQuery(resultSetMappingNames, call, hints, loader, (AbstractSession)session);
        } else if (resultClassName != null) {
            Class clazz = session.getDatasourcePlatform().getConversionManager().convertClassNameToClass(resultClassName);
            query = StoredProcedureQueryImpl.buildStoredProcedureQuery(clazz, call, hints, loader, (AbstractSession)session);
        } else {
            // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session.
            if (call.isStoredFunctionCall() || call.isStoredPLSQLProcedureCall()) {
                // If it is a function (plsql or not) or plsql procedure use the data read query.
                query = StoredProcedureQueryImpl.buildStoredProcedureQuery(call, hints, loader, (AbstractSession)session);
            } else {
                // Otherwise use a result set mapping query for stored procedure calls so users can use the execute
                // method on it (JPA 2.1 API). Will return the same result, that is, Object[] in this case.
                query = StoredProcedureQueryImpl.buildResultSetMappingQuery(new ArrayList<SQLResultSetMapping>(), call, hints, loader, (AbstractSession)session);
            }
        }
        query.setName(getName());

        return query;
    }

    public void setDatabaseQuery(DatabaseQuery databaseQuery) {
        setProperty("databasequery", databaseQuery);
    }

    /**
     * INTERNAL:
     * For table per tenant queries the descriptor list will extracted from
     * parsing the jpql query and cached here.
     */
    public void setDescriptors(List<ClassDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    public void setHints(Map<String, Object> hints){
        this.hints = hints;
    }

    @Override
    public void setJPQLString(String jpqlString){
        this.jpqlString = jpqlString;
    }

    public void setResultClassName(String className){
        this.resultClassName = className;
    }

    public void setResultSetMappings(List<String> resultSetMappings){
        this.resultSetMappingNames = resultSetMappings;
    }
}
