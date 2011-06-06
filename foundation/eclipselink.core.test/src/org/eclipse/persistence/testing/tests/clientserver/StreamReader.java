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
