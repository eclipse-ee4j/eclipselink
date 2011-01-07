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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class CMClient extends Thread {
    protected CMServer server;
    protected Session clientSession;
    protected Session session;
    protected Employee objectRead;

    public CMClient(CMServer server, int index, Session session) {
        this.server = server;
        this.session = session;
        this.clientSession = server.serverSession.acquireClientSession();
    }

    public Employee getObjectRead() {
        return objectRead;
    }

    public void release() {
        this.clientSession.release();
    }

    public void run() {
        try {
            Expression exp = new ExpressionBuilder().get("firstName").equal("Marcus");
            this.objectRead = (Employee)this.clientSession.readObject(Employee.class, exp);
            if ((objectRead == null) || objectRead.getLastName().equals("") || objectRead.getAddress().getCountry().equals("")) {
                throw new TestErrorException("read object on thread #" + this + " fails as null object/attribute is returned which should not be");
            }
        } catch (Exception exception) {
            this.server.errorOccured = true;
            exception.printStackTrace(System.out);
        }
    }
}
