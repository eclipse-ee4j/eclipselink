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
package org.eclipse.persistence.internal.sessions.coordination.corba;

import org.eclipse.persistence.exceptions.CommunicationException;
import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.internal.helper.SerializationHelper;
import org.eclipse.persistence.internal.sessions.coordination.RemoteConnection;
import org.eclipse.persistence.sessions.coordination.Command;
import org.omg.CORBA.SystemException;

/**
 * <p>
 * <b>Purpose</b>: Define an Sun CORBA implementation class for the remote object that
 * can execute a remote command.
 * <p>
 * <b>Description</b>: This implementation class is the CORBA transport version of
 * the connection that is used by the remote command manager to send remote
 * commands. This object just wraps the CORBAConnectionImpl remote object
 *
 * @author Steven Vo
 * @since OracleAS TopLink 10.0.3
 */
public class CORBARemoteCommandConnection extends RemoteConnection {
    CORBAConnection wrappedConnection;

    public CORBARemoteCommandConnection(CORBAConnection connection) {
        super();
        this.wrappedConnection = connection;
    }

    /**
     * INTERNAL:
     * This method invokes the remote object with the Command argument, and causes
     * it to execute the command in the remote VM. The result is currently assumed
     * to be either null if successful, or an exception string if an exception was
     * thrown during execution.
     *
     * If a SystemException occurred then a communication problem occurred. In this
     * case the exception will be wrapped in a CommunicationException and re-thrown.
     */
    public Object executeCommand(Command command) throws CommunicationException {
        byte[] serializedCmd = null;
        try {
            // serialize the Command to be used for remote method call
            serializedCmd = SerializationHelper.serialize(command);

        } catch (Exception e) {
            RemoteCommandManagerException.errorSerializeOrDeserialzeCommand(e);
        }

        try {
            byte[] result = wrappedConnection.executeCommand(serializedCmd);

            // Error executed command
            if (result != null) {
                // return the exeception String
                return new String(result);
            }
        } catch (SystemException exception) {
            //SystemException indicates that some thing goes wrong when trying to invoke the remote method 
            // before the method is executed.  i.e. communication problem.
            throw CommunicationException.errorInInvocation(exception);
        }

        // Success - return null 
        return null;
    }
}
