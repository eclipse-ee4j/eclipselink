/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.queries;


import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import java.util.HashMap;
import org.eclipse.persistence.internal.jpa.base.EJBQueryImpl;
import org.eclipse.persistence.sessions.Session;


/**
 * <b>Purpose</b>: 
 * A EJB3 placeholder Query object to store EJBQL strings so that processing the string is delayed 
 *  until Login<p>
 *
 * @author Chris Delahunt
 * @since TopLink Java Essentials
 */

public class EJBQLPlaceHolderQuery extends DatabaseQuery  {

    private String ejbQLString;
    private Boolean flushOnExecute;
    private HashMap hints;
    
    public EJBQLPlaceHolderQuery() {
    }
    public EJBQLPlaceHolderQuery(String ejbQLString) {
        this.ejbQLString=ejbQLString;
    }
    //buildEJBQLDatabaseQuery(queryString, session, hints, m_loader)
    public EJBQLPlaceHolderQuery(String name, String ejbql, HashMap hints) {
        this.name=name;
        this.ejbQLString=ejbql;
        this.flushOnExecute=null;
        this.hints=hints;
    }  
    
    public EJBQLPlaceHolderQuery(String name, String ejbql,  Boolean flushOnExecute, HashMap hints) {
        this.name=name;
        this.ejbQLString=ejbql;
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
    public String getEJBQLString(){
        return ejbQLString;
    }
    public void setEJBQLString(String ejbQLString){
        this.ejbQLString = ejbQLString;
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
    
    
    public DatabaseQuery processEjbQLQuery(Session session){
        ClassLoader classloader = session.getDatasourcePlatform().getConversionManager().getLoader();
        DatabaseQuery ejbquery = EJBQueryImpl.buildEJBQLDatabaseQuery(
            this.getName(), ejbQLString,  flushOnExecute, session, hints, classloader);
        ejbquery.setName(this.getName());
        return ejbquery;
    }
    
    
    
    /**
     * INTERNAL:
     * This should never be called and is only here because it is needed as an extension
     * to DatabaseQuery.  An exception should be thrown to warn users, but for now
     * it will process the EJBQL and execute the resulting query instead.
     *
     * @exception  DatabaseException - an error has occurred on the database.
     * @exception  OptimisticLockException - an error has occurred using the optimistic lock feature.
     * @return - the result of executing the query.
     */
    public Object executeDatabaseQuery() throws DatabaseException, OptimisticLockException{
        DatabaseQuery ejbquery = processEjbQLQuery(this.getSession());
        return ejbquery.executeDatabaseQuery();
    }
}
