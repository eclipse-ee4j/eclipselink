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
package org.eclipse.persistence.testing.tests.plsql;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * This model tests calling PLSQL stored procedures with PLSQL types.
 */
public class PLSQLTest extends TestCase {
    protected String queryName;
    protected Class queryClass;
    protected List queryArguments;
    protected Object result;
    
    public PLSQLTest() {
    }
    
    public PLSQLTest(String queryName, Class queryClass, List queryArguments) {
        this.queryName = queryName;
        this.queryClass = queryClass;
        this.queryArguments = queryArguments;
    }
    
    public PLSQLTest(String queryName, Class queryClass, List queryArguments, Object result) {
        this.queryName = queryName;
        this.queryClass = queryClass;
        this.queryArguments = queryArguments;
        this.result = result;
    }

    /**
     * Execute the named query and compare the result with the expected result.
     */
    public void test() {
    	Object queryResult = null;
    	try {
    		queryResult = getSession().executeQuery(this.queryName, this.queryClass, this.queryArguments);
    	} catch (RuntimeException exception) {
    		if (this.result instanceof EclipseLinkException) {
    			if (exception.getClass() == this.result.getClass()
    						&& (((EclipseLinkException)exception).getErrorCode() == ((EclipseLinkException)this.result).getErrorCode())) {
    				return;
    			}
    		}
    		throw exception;
    	}
    	if (this.result == null) {
    		return;
    	}    	
        if (this.result.getClass() != queryResult.getClass()) {
        	if (queryResult instanceof List) {
        		queryResult = ((List)queryResult).get(0);
        	}
            if (this.result.getClass() != queryResult.getClass()) {
            	throwError("Results do not match: " + queryResult + " expected: " + this.result);
            }
        }
        if (this.result instanceof DatabaseRecord) {
        	DatabaseRecord record = (DatabaseRecord)this.result;
        	DatabaseRecord queryRecord = (DatabaseRecord)queryResult;
        	for (Iterator iterator = record.getFields().iterator(); iterator.hasNext(); ) {
        		DatabaseField field = (DatabaseField)iterator.next();
        		Object value = record.get(field);
        		Object queryValue = queryRecord.get(field);
        		if (value instanceof Number) {
        			// Avoid Java number type in-equality.
        			value = ((Number)value).toString();
        			queryValue = ((Number)queryValue).toString();
        		}
        		if (!value.equals(queryValue)) {
        			throwError("Results do not match: " + queryValue + " expected: " + value);
        		}
        	}
        }
    }
}
