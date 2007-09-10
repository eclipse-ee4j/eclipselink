/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.remote.jms.mdb;

import org.eclipse.persistence.internal.sessions.remote.RemoteConnection;
import org.eclipse.persistence.sessions.remote.jms.JMSClusteringService;
import org.eclipse.persistence.sessions.Session;


public class JMSPublishingOnlyClusteringService extends JMSClusteringService {
    /**
     * PUBLIC:
     * Creates a JMSPublishingChangesOnlyClusteringService
     */
    public JMSPublishingOnlyClusteringService(Session session) {
        super(session);
    }

    /**
     * INTERNAL:
     * Initializes the clustering service.  Overwrite super method and not spawning thread
     */
    public void initialize() {
        run();
    }

    /**
     * INTERNAL:
     * This method is called by the cache synchronization manager when this server should
     * connect back ('handshake') to the server from which this remote connection came.
     */
    public void connectBackToRemote(RemoteConnection connection)
        throws Exception {
        // -- Do nothing.  This method is intended to set a JMS MessageListener that handle receving messages in the super class.
        // The sending messages part is expected to be handled by another source such as a MessageDrivenBean.
    }
}
