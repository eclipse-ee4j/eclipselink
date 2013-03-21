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
package org.eclipse.persistence.sessions.coordination.corba.sun;

import java.io.IOException;
import java.net.InetAddress;
import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.internal.sessions.coordination.corba.*;
import org.eclipse.persistence.internal.sessions.coordination.corba.sun.SunCORBAConnectionImpl;
import org.eclipse.persistence.internal.sessions.coordination.corba.sun.SunCORBAConnectionHelper;
import org.eclipse.persistence.sessions.coordination.corba.CORBATransportManager;

public class SunCORBATransportManager extends CORBATransportManager {
    public SunCORBATransportManager(RemoteCommandManager rcm) {
        super(rcm);
    }

    /**
     * INTERNAL:
     * Overwrite super method and return the default local URL .
     * i.e iiop://66.178.2.33:9090
     */
    public String getDefaultLocalUrl() {
        try {
            // Look up the local host name and paste it in a default URL
            String localHost = InetAddress.getLocalHost().getHostName();
            return "iiop://" + localHost + ":" + DEFAULT_URL_PORT;
        } catch (IOException exception) {
            throw RemoteCommandManagerException.errorGettingHostName(exception);
        }
    }

    public String getDefaultInitialContextFactoryName() {
        return "com.sun.jndi.cosnaming.CNCtxFactory";
    }

    /**
     * INTERNAL:
     * Implement abstract method that delegates the narrow call to the generated <code>SunCORBAConnectionHelper</code> class.
     *
     */
    public CORBAConnection narrow(org.omg.CORBA.Object object) {
        return (CORBAConnection)SunCORBAConnectionHelper.narrow(object);
    }

    /**
     * INTERNAL:
     * Implement abstract method.  The method returns a specific CORBA implementation instance that implements
     * <code>CORBAConnection</code> interface.
     *
     */
    public CORBAConnection buildCORBAConnection() {
        return new SunCORBAConnectionImpl(rcm);
    }
}
