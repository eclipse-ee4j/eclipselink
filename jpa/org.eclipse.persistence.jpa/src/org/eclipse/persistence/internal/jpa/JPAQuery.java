/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import java.util.HashMap;

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.Session;


/**
 * <b>Purpose</b>: 
 * A EJB3 placeholder Query object to store JPQL strings so that processing the string is delayed 
 *  until Login<p>
 *
 * @author Chris Delahunt
 * @since TopLink Java Essentials
 */

public class JPAQuery extends DatabaseQuery  {

    private String jpqlString;
    private Boolean flushOnExecute;
    private HashMap hints;
    
    public JPAQuery() {
    }
    public JPAQuery(String jpqlString) {
        this.jpqlString=jpqlString;
    }
    //buildEJBQLDatabaseQuery(queryString, session, hints, m_loader)
    public JPAQuery(String name, String jpqlString, HashMap hints) {
        this.name=name;
        this.jpqlString=jpqlString;
        this.flushOnExecute=null;
        this.hints=hints;
    }  
    
    public JPAQuery(String name, String jpqlString,  Boolean flushOnExecute, HashMap hints) {
        this.name=name;
        this.jpqlString=jpqlString;
        this.flushOnExecute=flushOnExecute;
        this.hints=hints;
    }    

    /**
     * INTERNAL:
     * Add the expression value to be included in the result.
     * EXAMPLE: reportQuery.addItem("name", expBuilder.get("firstName").toUpperCase());
     * The resultType can be specified to support EJBQL that adheres to the
     * EJB 3.0 spec.
     */
    public String getJPQLString(){
        return jpqlString;
    }
    public void setJPQLString(String jpqlString){
        this.jpqlString = jpqlString;
    }
    
    /**
     * INTERNAL:
     * Accessor methods for hints that would be added to the EJBQuery class and 
     * applied to the TopLink query.
     */
    public HashMap getHints(){
        return hints;
    }
    public void setHints(HashMap hints){
        this.hints = hints;
    }
    
    
    public DatabaseQuery processJPQLQuery(Session session){
        ClassLoader classloader = session.getDatasourcePlatform().getConversionManager().getLoader();
        DatabaseQuery ejbquery = EJBQueryImpl.buildEJBQLDatabaseQuery(
            this.getName(), jpqlString,  flushOnExecute, session, hints, classloader);
        ejbquery.setName(this.getName());
        return ejbquery;
    }
    
    
    
    /**
     * INTERNAL:
     * This should never be called and is only here because it is needed as an extension
     * to DatabaseQuery.  An exception should be thrown to warn users, but for now
     * it will process the JPQL and execute the resulting query instead.
     *
     * @exception  DatabaseException - an error has occurred on the database.
     * @exception  OptimisticLockException - an error has occurred using the optimistic lock feature.
     * @return - the result of executing the query.
     */
    public Object executeDatabaseQuery() throws DatabaseException, OptimisticLockException{
        DatabaseQuery ejbquery = processJPQLQuery(this.getSession());
        return ejbquery.executeDatabaseQuery();
    }
}
