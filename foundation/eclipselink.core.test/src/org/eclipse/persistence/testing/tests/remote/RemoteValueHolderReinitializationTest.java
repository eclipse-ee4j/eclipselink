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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.server.*;

/**
 * Test for bug 3145211.
 * We used to accidentally serialize the value in RemoteValueholder.  We now use the
 * Externalizable interface to avoid that.
 */
public class RemoteValueHolderReinitializationTest extends TestCase {

    // the non-remote session used by the remote model
    protected ClientSession localSession = null;

    protected Master master = null;

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        // get the server session from the RemoteModel - it is strange that getServerSession() returns a client session
        localSession = (ClientSession)org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession();

    }

    public void test() {
        ExpressionBuilder masters = new ExpressionBuilder();
        Expression expression = masters.get("primaryKey").equal(1);
        master = (Master)getSession().readObject(Master.class, expression);

        // trigger indirection on the server side
        master = (Master)localSession.readObject(Master.class, expression);
        master.getSlaves().size();
        master.setSlavesSerialized(false);

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        master = (Master)getSession().readObject(Master.class, expression);
    }

    public void verify() {
        if (master.slavesSerialized()) {
            throw new TestErrorException("TopLink unnecessarily serialized data on the many side " + 
                                         "of a one-many relationship");
        }
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
