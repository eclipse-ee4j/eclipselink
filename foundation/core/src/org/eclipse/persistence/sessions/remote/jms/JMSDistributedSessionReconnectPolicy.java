/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.remote.jms;

import org.eclipse.persistence.internal.sessions.remote.RemoteConnection;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.logging.SessionLog;
import javax.naming.*;
import javax.jms.*;

/**
 * <p>
 * <b>PURPOSE</b>:To Provide policy for reconnecting distributed sessions for cache Synch</p>
 * <p>
 * <b>Descripton</b>:This class Defines the behavior for attempting to reconnect sessions.  It will
 * attempt to reconnect based on information from the old connection</p>
 *
 * @author Gordon Yorke
 * @see org.eclipse.persistence.sessions.remote.jms.JMSClusteringService
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager}
 */
public class JMSDistributedSessionReconnectPolicy extends DistributedSessionReconnectPolicy {
    protected AbstractClusteringService clusteringService;

    public JMSDistributedSessionReconnectPolicy(AbstractClusteringService clusteringService) {
        this.clusteringService = clusteringService;
    }

    /**
     * PUBLIC:
     * This method is called by the Cache Synchronization manager when a connection to the remote
     * service or remote sessions fails and must be re-connected.  Overload this method to provide
     * custom behaviour.  by Default the behaviour is not to attempt reconnection.  This will be
     * taken care of by the Clustering Service when the remote announcemnet from the other server comes
     * in.
     */
    public RemoteConnection reconnect(RemoteConnection oldConnection) {
        ((org.eclipse.persistence.internal.sessions.AbstractSession)clusteringService.getSession()).log(SessionLog.FINEST, SessionLog.PROPAGATION, "attempting_to_reconnect_to_JMS_service");
        try {
            return ((JMSClusteringService)clusteringService).createRemoteConnection();
        } catch (NamingException exception) {
            throw SynchronizationException.errorLookingUpJMSService(oldConnection.getServiceName(), exception);
        } catch (JMSException exception) {
            throw SynchronizationException.errorLookingUpJMSService(oldConnection.getServiceName(), exception);
        }
    }
}