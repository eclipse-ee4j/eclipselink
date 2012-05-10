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
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.workbenchintegration.ExpressionPersistence;

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;


/**
 * Test that the original expression created in-memory matches the one read from XML.
 */
public class ExpressionPersistenceTest extends AutoVerifyTestCase {
    protected DatabaseQuery basicQuery;
    protected DatabaseQuery systemQuery;
    protected String queryName;

    public ExpressionPersistenceTest(String queryName, DatabaseQuery query) {
        this.basicQuery = query;
        this.queryName = queryName;
        setName(getName() + ":" + queryName);
        setDescription("Test that expressions persisted by the WorkBench in the deployent XML works correctly");
    }

    public void setup() {
        if ((queryName.startsWith("AddStandardDeviationReportQuery") || 
             queryName.startsWith("AddVarianceReportQuery")) && 
            (getSession().getPlatform().isSybase() || getSession().getPlatform().isSQLAnywhere() || getSession().getPlatform().isSQLServer()) || getSession().getPlatform().isDB2() || getSession().getPlatform().isDerby() || getSession().getPlatform().isSymfoware()) {
            throw new TestWarningException("The test is not supported on this database.");
        }
    }
    
    public void test() {
        getSession().executeQuery(basicQuery);
        systemQuery = 
                (getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class)).getQueryManager().getQuery(queryName);
        getSession().executeQuery(systemQuery);
    }

    public void verify() {
        if (!basicQuery.getCall().getSQLString().equals(systemQuery.getCall().getSQLString())) {
            throw new TestErrorException("Persisted query not the same as original");
        }
    }
}
