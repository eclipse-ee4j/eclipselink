/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     cdelahun - Bug 214534: added as a public helper for JMS RCM message processing
package org.eclipse.persistence.sessions.coordination.jms;

import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.internal.sessions.coordination.jms.JMSTopicRemoteConnection;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;

/**
 * This helper class allows access to abstract JMSPublishingTransportManager internals when processing
 * JMS messages for RCM, and can be expanded upon to include other EclipseLink JMS functionality.
 *
 * @author Chris Delahunt
 */
public class JMSPublishingHelper {
    /**
     * PUBLIC:
     * Processes the received RCM messaged from a JMS provider for cache coordination.
     * This will use the local connection from the configured TransportManager from the session's RemoteCommandManager.
     *
     * @param message
     * @param session
     *
     *
     */
    public static void processJMSMessage(javax.jms.Message message, AbstractSession session){
        RemoteCommandManager rcm = (RemoteCommandManager)session.getCommandManager();
        if(rcm.isStopped()){
            throw RemoteCommandManagerException.remoteCommandManagerIsClosed();
        }
        JMSTopicRemoteConnection connection = (JMSTopicRemoteConnection)rcm.getTransportManager().getConnectionToLocalHost();
        connection.onMessage(message);
    }

}
