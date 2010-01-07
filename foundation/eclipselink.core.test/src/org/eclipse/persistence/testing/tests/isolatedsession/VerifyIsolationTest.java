/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.sessions.IsolatedClientSession;

public class VerifyIsolationTest extends ClientServerTest {
    public IsolatedEmployee readEmployee;

    public VerifyIsolationTest(boolean isExclusive) {
        super(isExclusive);
        setDescription("This test verifies that certain data is only available to the client with exclusiveConnectionMode");
    }

    public VerifyIsolationTest(String exclusiveConnectionMode) {
        super(exclusiveConnectionMode, true);
        setDescription("This test verifies that certain data is only available to the client with exclusiveConnectionMode "+ this.exclusiveConnectionMode);
        setName(getName() + " " + this.exclusiveConnectionMode);
    }

    public void test() {
        Session session = (Session)this.clients.get(0);
        if (!(session instanceof IsolatedClientSession)) {
            throw new TestErrorException("The session created was not an Isolated Session");
        }
        readEmployee = (IsolatedEmployee)session.readObject(IsolatedEmployee.class);
        //load a isolated related object into memory
        readEmployee.getPhoneNumbers();
        //load non isolated related object
        readEmployee.getAddress();
    }

    public void verify() {
        if (this.server.getIdentityMapAccessor().getFromIdentityMap(readEmployee) != null) {
            throw new TestErrorException("The Employee class was not Isolated");
        }
        if (readEmployee.getPhoneNumbers().isEmpty() || (this.server.getIdentityMapAccessor().getFromIdentityMap(readEmployee.getPhoneNumbers().get(0)) != null)) {
            throw new TestErrorException("The related class Phonenumber class was not Isolated");
        }
        if (this.server.getIdentityMapAccessor().getFromIdentityMap(readEmployee.getAddress()) == null) {
            throw new TestErrorException("The related class address was isolated ");
        }
    }
}
