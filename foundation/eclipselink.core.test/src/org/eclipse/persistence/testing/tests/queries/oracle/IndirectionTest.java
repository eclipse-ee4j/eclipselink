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
package org.eclipse.persistence.testing.tests.queries.oracle;

import java.util.*;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.proxyindirection.*;

/**
 * Not exactly sure what this test thought it was testing,
 * but now it at least works.
 */
public class IndirectionTest extends TestCase {
    public List results;

    public IndirectionTest() {
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        query.setHierarchicalQueryClause(builder.get("lastName").equal("Sutherland"), 
                                         builder.get("managedEmployees"), null);
        results = (List)getSession().executeQuery(query);
    }

    public void verify() {
        if (results != null) {
            Employee first = (Employee)results.get(0);
            Employee second = (Employee)results.get(1);
            Collection managed = first.getManagedEmployees();
            if (!managed.contains(second)) {
                throw new TestErrorException("The hierarchical results are incorrect.");
            }
        }
    }

}
