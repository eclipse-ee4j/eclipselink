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
package org.eclipse.persistence.testing.tests.proxyindirection;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Bug 3894351
 * Test expressions that use expression.equal(objectA) where objectA is a java dynamic proxy rather than
 * an actual object
 */
public class ReadWithProxyEqualityExpression extends AutoVerifyTestCase {
    Employee employee;

    public ReadWithProxyEqualityExpression() {
        setDescription("Tests ReadObject using an Expression using Proxy Indirection and equality to a proxy object.");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        // Read an employee so we can get it's address proxy
        ReadObjectQuery q = new ReadObjectQuery();
        q.setReferenceClass(Employee.class);
        ExpressionBuilder employeeBuilder = new ExpressionBuilder();
        Expression exp = employeeBuilder.get("firstName").equal("James");
        exp = exp.and(employeeBuilder.get("lastName").equal("Sutherland"));
        q.setSelectionCriteria(exp);
        employee = (Employee)getSession().executeQuery(q);

        // Try to read an employee based on the address proxy.
        q = new ReadObjectQuery();
        q.setReferenceClass(Employee.class);
        q.setSelectionCriteria(new ExpressionBuilder().get("address").equal(employee.getAddress()));
        employee = (Employee)getSession().executeQuery(q);
    }

    public void verify() {
        if (employee == null) {
            throw new TestErrorException("Employee not returned when searched by address");
        }

        // Test the indirection
        if (((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(AddressImpl.class).getSize() == 0) {
            throw new TestErrorException("ProxyIndirection did not work - Address was not read when triggered");
        }

        if (!(employee.getFirstName().equals("James") && employee.getLastName().equals("Sutherland"))) {
            throw new TestErrorException("The incorrect employee was read in a query based on equality to a proxy object.");
        }
    }
}
