/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.clientserver;

import java.util.*;
import org.eclipse.persistence.sessions.*;

public class AnotherClient extends Thread {
    protected Server server;
    protected Session clientSession;
    protected Session session;
    protected UnitOfWork unitOfWork;
    protected int index;

    public AnotherClient(Server server, int index, Session session) {
        super();
        this.server = server;
        this.session = session;
        this.index = index;
        this.clientSession = server.serverSession.acquireClientSession();
    }

    public void release() {
        this.clientSession.release();
    }

    public void run() {
        try {
            this.unitOfWork = this.clientSession.acquireUnitOfWork();
            org.eclipse.persistence.queries.ReadAllQuery raq = new org.eclipse.persistence.queries.ReadAllQuery();
            raq.addAscendingOrdering("id");
            raq.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Project.class);
            Vector projects = (Vector)unitOfWork.executeQuery(raq);
            org.eclipse.persistence.testing.models.employee.domain.Project project = (org.eclipse.persistence.testing.models.employee.domain.Project)projects.elementAt(this.index);
            project.setName(new Integer(this.index).toString());

            this.unitOfWork.commit();
        } catch (Exception exception) {
            this.server.errorOccured = true;
            exception.printStackTrace(System.out);
        }
    }
}
