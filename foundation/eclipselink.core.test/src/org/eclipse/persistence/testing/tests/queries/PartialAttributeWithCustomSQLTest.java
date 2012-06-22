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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Bug 245986 - Add regression testing for queries using custom SQL and partial attribute population
 * Regression test to run a query against the database using partial attributes and adding
 * some partial attribute expressions to the query. The query is not cached.
 * @author dminsky
 */
@SuppressWarnings("deprecation")
public class PartialAttributeWithCustomSQLTest extends TestCase {

    protected Exception caughtException;

    public PartialAttributeWithCustomSQLTest() {
        super();
        setDescription("Test querying with partial attributes in conjunction with using custom SQL");
    }
    
    public void test() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ClassDescriptor descriptor = getSession().getClassDescriptor(Employee.class);
        String tableName = descriptor.getTableName();
        query.setCall(new SQLCall("SELECT * FROM " + tableName)); 
        query.addPartialAttribute("id");
        query.addPartialAttribute("firstName");
        query.addPartialAttribute("lastName");
        query.addPartialAttribute("gender");
        query.dontMaintainCache();
        
        try {
            getSession().executeQuery(query);
        } catch (Exception exception) {
            caughtException = exception;
        }
    }
    
    public void verify() {
        if (caughtException != null) {
            final String msg = "Caught an unexpected exception while querying with partial attributes and custom SQL";
            throw new TestErrorException(msg, caughtException);
        }
    }
    
}
