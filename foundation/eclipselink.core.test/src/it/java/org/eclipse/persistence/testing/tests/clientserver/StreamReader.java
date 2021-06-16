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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class StreamReader extends Reader {
    public StreamReader(Server1 server, Session session) {
        super(server, session);
    }

    public void run() {
        try {
            ReadAllQuery empQuery = new ReadAllQuery(Employee.class);
            empQuery.useCursoredStream();
            CursoredStream empCS = (CursoredStream)this.clientSession.executeQuery(empQuery);
            empCS.read(2);
            empCS.close();

            ReadAllQuery addQuery = new ReadAllQuery(Employee.class);
            addQuery.useCursoredStream();
            CursoredStream addCS = (CursoredStream)this.clientSession.executeQuery(addQuery);
            addCS.close();
        } catch (Exception exception) {
            this.server.errorOccured = true;
            exception.printStackTrace(System.out);
        }

    }
}
