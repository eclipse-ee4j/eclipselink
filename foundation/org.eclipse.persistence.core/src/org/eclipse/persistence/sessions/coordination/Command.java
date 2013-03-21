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
package org.eclipse.persistence.sessions.coordination;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Provide an extendable framework class for a Command object
 * that can be remotely executed.
 * <p>
 * <b>Description</b>: Is the root command class from which all other remotely
 * executable commands must extend. A Command is invoked by calling
 * propagateCommand() on a local CommandManager, and is executed on each remote
 * service by each remote CommandManager invoking processCommand() on its local
 *
 * @see CommandManager
 * @see CommandProcessor
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public abstract class Command implements java.io.Serializable {

    /** The unique calling card of the service that initiated the command */
    ServiceId serviceId;

    /**
     * INTERNAL:
     * If the CommandProcessor is a EclipseLink session then this method will
     * get executed.
     *
     * @param session The session that can be used to execute the command on.
     */
    public abstract void executeWithSession(AbstractSession session);

    /**
     * PUBLIC:
     * Return the service identifier of the service where the command originated
     *
     * @return The unique identifier of the sending RCM service
     */
    public ServiceId getServiceId() {
        return serviceId;
    }

    /**
     * ADVANCED:
     * Set the service identifier of the service where the command originated
     *
     * @param newServiceId The unique identifier of the sending RCM service
     */
    public void setServiceId(ServiceId newServiceId) {
        serviceId = newServiceId;
    }

    /**
     * INTERNAL:
     * Determine whether this command is public or internal to EclipseLink.
     * User commands must return false.
     */
    public boolean isInternalCommand() {
        return false;
    }
}
