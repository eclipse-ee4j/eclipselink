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
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import org.eclipse.persistence.queries.CursoredStream;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * BUG 5367029.
 */
public class CustomSQLCursoredStreamReadTest extends AutoVerifyTestCase {
    protected boolean m_exceptionCaughtOnSQLQueryExecution;
    protected boolean m_exceptionCaughtOnSizeQueryExecution;
    
    public CustomSQLCursoredStreamReadTest() {
        setDescription("Tests that the additionalSizeQueryNotSpecified exception is thrown at the right time for a custom sql query.");
    }

    protected void setup() {
        m_exceptionCaughtOnSQLQueryExecution = false;
        m_exceptionCaughtOnSizeQueryExecution = false;
    }

    protected void test() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setSQLString("SELECT * FROM EMPLOYEE");
        query.useCursoredStream();
        CursoredStream stream = null;
        
        try {
            stream = (CursoredStream) getSession().executeQuery(query);
            
            try {
                stream.size();
            } catch (Exception e) {
                m_exceptionCaughtOnSizeQueryExecution = true;
            }
        } catch (Exception e) {
            m_exceptionCaughtOnSQLQueryExecution = true;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * 
     */
    protected void verify() throws Exception {
        if (m_exceptionCaughtOnSQLQueryExecution) {
            throw new TestErrorException("The additionalSizeQueryNotSpecified exception was thrown when preparing the custom sql query.");
        }
        
        if (! m_exceptionCaughtOnSizeQueryExecution) {
            throw new TestErrorException("The additionalSizeQueryNotSpecified exception was not thrown when size was called on the cursored stream.");
        }
    }
}
