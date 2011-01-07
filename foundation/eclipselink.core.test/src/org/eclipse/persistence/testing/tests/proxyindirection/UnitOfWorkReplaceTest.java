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
package org.eclipse.persistence.testing.tests.proxyindirection;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Tests uow works when the proxy is replaced without first accessing the proxy.
 */
public class UnitOfWorkReplaceTest extends AutoVerifyTestCase {
    public UnitOfWorkReplaceTest() {
        setDescription("Tests uow works when the proxy is replaced without first accessing the proxy.");
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employee = (Employee)uow.readObject(Employee.class, new ExpressionBuilder().get("firstName").like("%Angie%"));

        employee.setAddress(new AddressImpl());

        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        Employee emp = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("Angie"));

        Address address = emp.getAddress();

        if (address.getStreet() != null) {
            throw new TestErrorException("Proxy object update did not work properly.\n" + address);
        }
    }
}
