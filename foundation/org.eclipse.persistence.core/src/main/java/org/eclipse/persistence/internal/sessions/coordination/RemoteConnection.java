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
package org.eclipse.persistence.internal.sessions.coordination;

import org.eclipse.persistence.exceptions.CommunicationException;
import org.eclipse.persistence.sessions.coordination.ServiceId;
import org.eclipse.persistence.sessions.coordination.Command;

/**
 * <p>
 * <b>Purpose</b>: Define an abstract class for the remote object that can execute
 * a remote command using different transport protocols.
 * <p>
 * <b>Description</b>: This abstract class represents the remote object that is
 * used by the remote command manager to send remote commands. The underlying
 * transport mechanism is transparently implemented by the transport subclass
 * implementations.
 *
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public abstract class RemoteConnection implements java.io.Serializable {

    /** The service on the receiving end of this connection */
    protected ServiceId serviceId;

    /**
     * INTERNAL:
     * Execute the remote command. The result of execution is returned.
     */
    public abstract Object executeCommand(Command command) throws CommunicationException;

    /**
     * INTERNAL:
     * Execute the remote command. The result of execution is returned.
     */
    public abstract Object executeCommand(byte[] command) throws CommunicationException;

    /**
     * INTERNAL:
     * Return the service info of the receiving service
     */
    public ServiceId getServiceId() {
        return serviceId;
    }

    /**
     * INTERNAL:
     * Set the service info of the receiving service
     */
    public void setServiceId(ServiceId newServiceId) {
        serviceId = newServiceId;
    }

    @Override
    public String toString() {
        return "RemoteConnection[" + serviceId + "]";
    }

    /**
     * INTERNAL:
     * cleanup whatever is necessary.  Invoked when the TransportManager discard connections.
     */
    public void close() {
    }
}
