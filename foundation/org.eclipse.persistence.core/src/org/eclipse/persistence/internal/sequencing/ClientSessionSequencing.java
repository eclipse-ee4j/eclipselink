/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sequencing;

import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.sessions.server.ClientSession;

/**
 * ClientSessionSequencing is private to EclipseLink.
 * It provides sequencing for ClientSession.
 * It contains a reference to SequencingServer object owned by
 * ClientSession's parent ServerSession.
 *
 * @see SequencingServer
 * @see org.eclipse.persistence.sessions.server.ClientSession
 *
 */
class ClientSessionSequencing implements Sequencing {
    // ownerClientSession
    protected ClientSession clientSession;

    // SequencingServer owned by clientSession's parent ServerSession
    protected SequencingServer sequencingServer;

    /**
    * INTERNAL:
    * Takes a potential owner - ClientSession as an argument.
    * This static method is called before an instance of this class is created.
    * The goal is to verify whether the instance of ClientSessionSequencing should be created.
    */
    public static boolean sequencingServerExists(ClientSession cs) {
        return cs.getParent().getSequencingServer() != null;
    }

    /**
    * INTERNAL:
    * Takes an owner - ClientSession as an argument.
    */
    public ClientSessionSequencing(ClientSession clientSession) {
        this.clientSession = clientSession;
        sequencingServer = clientSession.getParent().getSequencingServer();
    }

    /**
    * INTERNAL:
    * Simply calls the same method on SequencingServer
    */
    public int whenShouldAcquireValueForAll() {
        return sequencingServer.whenShouldAcquireValueForAll();
    }

    /**
    * INTERNAL:
    * This method is the reason for this class to exist:
    * SequencingServer.getNextValue takes two arguments
    * the first argument being a session which owns write connection
    * (either DatabaseSession or ClientSession).
    */
    public Object getNextValue(Class cls) {
        return sequencingServer.getNextValue(clientSession, cls);
    }
}
