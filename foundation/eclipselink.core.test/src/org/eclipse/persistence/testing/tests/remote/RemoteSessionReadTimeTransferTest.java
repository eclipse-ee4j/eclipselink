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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Test to ensure read times are transfered from the server when using a remote session.
 */
public class RemoteSessionReadTimeTransferTest extends TestCase {

    protected Employee employee = null;

    public RemoteSessionReadTimeTransferTest() {
        setDescription("Test to ensure read times are transferred into the remote session from the server session");
    }


    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getAbstractSession().beginTransaction();
    }

    public void test() {
        employee = (Employee)getSession().readObject(Employee.class);
    }

    public void verify() {
        if (((AbstractSession)getSession()).getIdentityMapAccessorInstance().getCacheKeyForObject(employee).getReadTime() == 
            0) {
            throw new TestErrorException("Read time was not properly transferred to the remote session.");
        }
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
