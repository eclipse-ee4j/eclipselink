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
package org.eclipse.persistence.internal.sequencing;

import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.remote.DistributedSession;
import org.eclipse.persistence.internal.sessions.remote.RemoteConnection;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.broker.SessionBroker;

/**
 * SequencingFactory is private to EclipseLink.
 * It instantiates sequencing classes for sessions.
 * It allows sequencing classes to be encapsulated in
 * this package.
 *
 * @see ClientSessionSequencing
 * @see RemoteConnectionSequencing
 * @see SessionBrokerSequencing
 * @see SequencingManager
 *
 */
public class SequencingFactory {

    /**
    * INTERNAL:
    * Takes a potential owner - a Session, returns Sequencing object.
    * Note that before creating a Sequencing object there is a check performed
    * to determine whether the object could be created.
    */
    public static Sequencing createSequencing(AbstractSession session) {
        Sequencing sequencing = null;
        if (session.isClientSession()) {
            ClientSession cs = (ClientSession)session;
            if (ClientSessionSequencing.sequencingServerExists(cs)) {
                sequencing = new ClientSessionSequencing(cs);
            }
        } else if (session.isRemoteSession()) {
            RemoteConnection con = ((DistributedSession)session).getRemoteConnection();
            if (RemoteConnectionSequencing.masterSequencingExists(con)) {
                sequencing = new RemoteConnectionSequencing(con);
            }
        } else if (session.isBroker()) {
            SessionBroker br = (SessionBroker)session;
            if (SessionBrokerSequencing.atLeastOneSessionHasSequencing(br)) {
                sequencing = new SessionBrokerSequencing(br);
            }
        }
        return sequencing;
    }

    /**
    * INTERNAL:
    * Takes a potential owner - a DatabaseSession, returns SequencingHome object.
    * Only DatabaseSession and ServerSession should be passed (not SessionBroker).
    */
    public static SequencingHome createSequencingHome(DatabaseSessionImpl ownerSession) {
        SequencingHome home = null;
        if (!ownerSession.isBroker()) {
            home = new SequencingManager(ownerSession);
        }
        return home;
    }
}
