/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.Server;

public class StoredProcedureORParametersClientSessionTest extends StoredProcedureVARRAYParametersTest {
    Server server;
    ClientSession clientSession;

    public StoredProcedureORParametersClientSessionTest() {
        super();
    }
    public StoredProcedureORParametersClientSessionTest(boolean useCustomSQL) {
        super(useCustomSQL);
    }

    public org.eclipse.persistence.sessions.Session getSession(){
        if ( (server ==null) || (!server.isConnected()) ){
            server = super.getSession().getProject().createServerSession(1,1);
            server.getLogin().useExternalConnectionPooling();
            server.setSessionLog(super.getSession().getSessionLog());
            server.login();
        }
        if (clientSession==null){
            clientSession = server.acquireClientSession();
        }
        return clientSession;
    }
    public void reset() throws Throwable {
        super.reset();
        if (clientSession!=null){
            clientSession.release();
            clientSession=null;
        }
        if ((server!=null)&&(server.isConnected())){
            server.logout();
        }
    }
    public void setup() {
        super.setup();
        getSession();
    }

}
