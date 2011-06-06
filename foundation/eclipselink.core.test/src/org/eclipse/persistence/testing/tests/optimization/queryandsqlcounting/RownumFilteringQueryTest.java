/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;;


/**
 * This tests Oracle's Rownum filtering feature, using a query with MaxRows and FirstResults set
 */
public class RownumFilteringQueryTest extends TestCase{
    protected QuerySQLTracker tracker = null;
    protected int resultSize;
    
    protected int maxRows, firstResult;
    protected int expectedResultSize;
    protected String queryString = null;
    protected ReadQuery queryToUse;
    
    public RownumFilteringQueryTest() {
        firstResult = 1;
        maxRows = 2;
        expectedResultSize = 1;
    }
    
    public RownumFilteringQueryTest(Class classToQuery) {
        this();
        queryToUse = new ReadAllQuery(classToQuery);
    }
    
    public RownumFilteringQueryTest(int maxRows, int firstResult, int expectedResultSize) {
        this.firstResult = firstResult;
        this.maxRows = maxRows;
        this.expectedResultSize = expectedResultSize;
    }
    
    public void setup() {
        DatabaseSession session = (DatabaseSession)getSession();
        DatabasePlatform platform = getSession().getPlatform();

        if ( !platform.isOracle() ) {
            throw new TestWarningException("Oracle Pagination not supported on platform " + platform);
        }
        tracker = new QuerySQLTracker(session);
    }
    
    public void reset() {
        tracker.remove();
        queryString= null;
    }
    
    
    public void test() {
        queryString= null;
        resultSize = 0;
        ReadQuery query = (ReadQuery)getQuery().clone();
        //query.addJoinedAttribute("address");
        query.setMaxRows(maxRows);
        query.setFirstResult(firstResult);
        resultSize = ((java.util.Vector)getSession().executeQuery(query)).size();
        java.util.List alist = tracker.getSqlStatements();
        int size = alist.size();
        if (size>0){
            queryString = (String)alist.get(size-1);
        }
        
    }
    
    public void verify() {
        if ( resultSize != expectedResultSize){
            throw new TestErrorException("A ReadAllQuery with MaxRows="+maxRows+",FirstResult="+firstResult+" returned "+
                resultSize+" result(s) when "+expectedResultSize+" result(s) were expected.");
        }
        if ( queryString==null){
            throw new TestErrorException("A ReadAllQuery with MaxRows="+maxRows+",FirstResult="+firstResult+" did not generate an SQL string.");
        }
        int firstSelectIndex = queryString.indexOf("SELECT");
        int lastSelectIndex = queryString.lastIndexOf("SELECT");
        int firstRowNumIndex = queryString.indexOf("ROWNUM");
        int LastRowNumIndex = queryString.lastIndexOf("ROWNUM");
        if ( (firstSelectIndex == lastSelectIndex) || (firstRowNumIndex ==LastRowNumIndex) ){
            throw new TestErrorException("A ReadAllQuery with MaxRows="+maxRows+",FirstResult="+firstResult
                    +" did not generate proper SQL string Using Oracle pagination feature.");
        }
    }
    
    public ReadQuery getQuery(){
        if (queryToUse ==null){
            queryToUse = new ReadAllQuery(Employee.class);
        }
        return queryToUse;
    }
    
    public void getQuery(ReadQuery queryToUse){
        this.queryToUse = queryToUse;
    }
    
}
