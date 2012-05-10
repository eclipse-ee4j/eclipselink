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
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;

public class VerifyExclusiveConnectionTest extends ClientServerTest {
    public boolean isPostEventFired = false;
    public boolean isPreEventFired = false;

    public VerifyExclusiveConnectionTest() {
        super(true);
        setDescription("This test verifies that an exclusive connection is created for the client. and the appropriate events are thrown.");
    }

    public VerifyExclusiveConnectionTest(String exclusiveConnectionMode, boolean isIsolated) {
        super(exclusiveConnectionMode, isIsolated);
        String str = exclusiveConnectionMode;
        if(isIsolated) {
            str += "_Isolated";
        }
        setDescription("This test verifies that an exclusive connection is created for the client and the appropriate events are thrown. " + str);
        setName(getName() + " " + str);
    }

    public void test() {
        this.server.getEventManager().addListener(new IsolatedSessionEventAdaptor(this));
        ((Session)this.clients.get(0)).readObject(IsolatedEmployee.class);
        ((Session)this.clients.get(0)).release();
        this.clients.remove(0);
    }

    public void verify() {
        if (!this.isPostEventFired) {
            throw new TestErrorException("The post acquire Exclusive Connection event was not fired");
        }
        if (!this.isPreEventFired) {
            throw new TestErrorException("The pre release Exclusive Connection event was not fired");
        }
    }

    public void setup() {
        super.setup();
        this.isPostEventFired = false;
        this.isPreEventFired = false;
    }
}
