/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sessions.remote;

import java.rmi.server.ObjID;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: This class provides an implementation of a RemoteSessionCommand
 * <p>
 * <b>Description</b>: This command provides the implementation for cache synchronization messages
 * <p>
 */
public class RemoveServerSideRemoteValueHolderCommand implements RemoteCommand {
    //This is the remote valueholder id
    protected ObjID objID;

    public RemoveServerSideRemoteValueHolderCommand(ObjID objID) {
        this.objID = objID;
    }

    /**
     * INTERNAL:
     * Use this method to get the the remote ValueHolder ID
     */
    public ObjID getObjID() {
        return this.objID;
    }

    /**
     * INTERNAL:
     * Use this method to set the remote ValueHolder ID
     */
    public void setObjID(ObjID objID) {
        this.objID = objID;
    }

    /**
     * INTERNAL:
     * This method is used bye the remote Session to execute the command
     */
    @Override
    public void execute(AbstractSession session, RemoteSessionController remoteSessionController) {
        remoteSessionController.getRemoteValueHolders().remove(objID);
    }
}
