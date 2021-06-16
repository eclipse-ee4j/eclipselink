/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
