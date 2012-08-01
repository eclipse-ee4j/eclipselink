/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     08/01/2012-2.5 Chris Delahunt
 *       - 371950: Metadata caching 
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa;


import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
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
        //this.resultClassName = resultClassName;
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
        //this.resultClassName = resultClassName;
        this.name = queryName;
        this.call = call;
        this.flushOnExecute = null;
        this.hints = hints;
        this.lockMode = null;
    }
    
    public void setResultClassName(String className){
        this.resultClassName = className;
    }
    
    public void addResultSetMapping(String resultSetMapping){
        if (resultSetMappingNames == null) {
            resultSetMappingNames = new ArrayList<String>();
        }
        this.resultSetMappingNames.add(resultSetMapping);
    }
    
    public void setResultSetMappings(List<String> resultSetMappings){
        this.resultSetMappingNames = resultSetMappings;
    }
    
    public void addResultClassNames(String className) {
        if (resultClassNames == null) {
            resultClassNames = new ArrayList<String>();
        }
        this.resultClassNames.add(className);
    }

    /**
     * Return the JPQL string.
     */
    public String getJPQLString(){
        return jpqlString;
    }
    public void setJPQLString(String jpqlString){
        this.jpqlString = jpqlString;
    }
    
    /**
     * Return the JPA query hints.
     */
    public Map<String, Object> getHints(){
        return hints;
    }
    public void setHints(Map<String, Object> hints){
        this.hints = hints;
    }
    
    public DatabaseQuery getDatabaseQuery() {
        return (DatabaseQuery)getProperty("databasequery");
    }
    public void setDatabaseQuery(DatabaseQuery databaseQuery) {
        setProperty("databasequery", databaseQuery);
    }
    
    /**
     * INTERNAL:
     * Generate the DatabaseQuery query from the JPA named query.
     */
    public void prepare() {
        DatabaseQuery query = null;
        if (sqlString!=null) {
            query = processSQLQuery(getSession());
        } else if (jpqlString!=null) {
            query = processJPQLQuery(getSession());
        } else if (call!=null) {
            query = processStoredProcedureQuery(getSession());
        }
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
        DatabaseQuery ejbquery = EJBQueryImpl.buildEJBQLDatabaseQuery(
            this.getName(), this.jpqlString, (AbstractSession)session, lockModeEnum, this.hints, classloader);
        ejbquery.setName(this.getName());
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
            // Neither a resultClass or resultSetMapping is specified so place in a temp query on the session
            query = StoredProcedureQueryImpl.buildStoredProcedureQuery(call, hints, loader, (AbstractSession)session);
        }
        query.setName(this.getName());
        return query;
    }

    /**
     * INTERNAL:
     * This should never be called and is only here because it is needed as an extension
     * to DatabaseQuery.  Perhaps exception should be thrown to warn users, but for now
     * it will execute the resulting query instead, this allows JPA style queries to be executed
     * on a normal EclipseLink Session.
     */
    public Object executeDatabaseQuery() throws DatabaseException, OptimisticLockException{
        return getSession().executeQuery(getDatabaseQuery());
    }
}
