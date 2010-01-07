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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa;


import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import java.util.Map;

import javax.persistence.LockModeType;

import org.eclipse.persistence.queries.DatabaseQuery;
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
    private Map<String, Object> hints;
    
    public JPAQuery() {
    }
    
    public JPAQuery(String jpqlString) {
        this.jpqlString=jpqlString;
    }
    
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
        setDatabaseQuery(processJPQLQuery(getSession()));
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
            this.getName(), this.jpqlString, session, lockModeEnum, this.hints, classloader);
        ejbquery.setName(this.getName());
        return ejbquery;
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
