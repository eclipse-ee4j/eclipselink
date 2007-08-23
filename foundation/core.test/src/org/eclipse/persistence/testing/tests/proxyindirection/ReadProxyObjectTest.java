/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.proxyindirection;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class ReadProxyObjectTest extends AutoVerifyTestCase {

    Address address;

    public ReadProxyObjectTest() {
        setDescription("Tests ReadObject on a proxy object using Proxy Indirection.");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        Employee emp = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").like("Rick%"));
        ReadObjectQuery read = new ReadObjectQuery(emp.getAddress());
        address = (Address)getSession().executeQuery(read);
    }

    public void verify() {
        if (address == null) {
            throw new TestErrorException("No address was returned");
        }

        if (!address.getStreet().equals("509-171 Lees Ave.")) {
            throw new TestErrorException("The wrong address was returned: " + address.getStreet() + ".  It should be: 509-171 Lees Ave.");
        }
    }
}