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

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.UnitOfWork;

public class UpdateTest extends AutoVerifyTestCase {
    public UpdateTest() {
        setDescription("Tests UpdateObject using Proxy Indirection.");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        Employee james = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").like("%James%"));
        UnitOfWork uow = getSession().acquireUnitOfWork();
        james = (Employee)uow.registerObject(james);
        Address add = james.getAddress();

        add.setCity("Tuscon");
        add.setState("AZ");
        add.setCountry("USA");

        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        Employee james = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("James"));

        Address address = james.getAddress();

        if (!address.getCity().equals("Tuscon") || !address.getState().equals("AZ") || !address.getCountry().equals("USA")) {
            throw new TestErrorException("Proxy object update did not work properly.\n" + address);
        }
    }
}
