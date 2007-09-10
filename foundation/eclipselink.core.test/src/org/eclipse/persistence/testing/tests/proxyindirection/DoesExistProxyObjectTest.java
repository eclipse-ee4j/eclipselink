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
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class DoesExistProxyObjectTest extends AutoVerifyTestCase {

    boolean doesExist;
    
    public DoesExistProxyObjectTest() {
        setDescription("Tests doesObjectExist on a proxy object using Proxy Indirection.");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        Employee emp = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").like("Rick%"));
        doesExist = ((org.eclipse.persistence.sessions.Session)getSession()).doesObjectExist(emp.getAddress());
    }

    public void verify() {
        if (!doesExist) {
            throw new TestErrorException("Address does not exist");
        }
    }
}