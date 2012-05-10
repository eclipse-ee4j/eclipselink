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

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class UOWRegisterProxyObjectTest extends AutoVerifyTestCase {
    public UOWRegisterProxyObjectTest() {
        setDescription("Tests registering a proxy object with a UnitOfWork using Proxy Indirection.");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        Employee emp = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").like("%Angie%"));

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Address aClone = (Address)uow.registerObject(emp.getAddress());

        aClone.setStreet("706-171 Elm St.");
        aClone.setCity("Toronto");

        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        Employee emp = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("Angie"));

        Address address = emp.getAddress();

        if (!address.getStreet().equals("706-171 Elm St.") || !address.getCity().equals("Toronto")) {
            throw new TestErrorException("Proxy object address update did not work properly.\n" + address);
        }
    }
}
