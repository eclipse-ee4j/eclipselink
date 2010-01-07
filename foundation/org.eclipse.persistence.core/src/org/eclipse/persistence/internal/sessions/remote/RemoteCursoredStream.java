/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.sessions.remote;

import java.rmi.server.ObjID;
import java.util.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * <p><b>Purpose</b>:
 * The object is used as a wrapper to the CursoredStream object in the remote transaction (via RMI, CORBA, etc.)
 * <p>
 * <p><b>Responsibilities</b>:
 * Wraps a database result set cursor to provide a stream to the remote client side on the resulting selected objects.
 *
 * @author King (Yaoping) Wang
 * @since TOPLink/Java 3.0
 */
public class RemoteCursoredStream extends CursoredStream {

    /** This is a unique id for remote cursored stream */
    protected ObjID id;

    /** Return if the stream is closed. */
    protected boolean isClosed;

    /** Return the cursored stream page size. */
    protected int pageSize;

    public RemoteCursoredStream(CursoredStream serverCursoredStream) {
        this.id = new ObjID();
        this.position = serverCursoredStream.getPosition();
        this.pageSize = serverCursoredStream.getPageSize();
        this.isClosed = (serverCursoredStream.getResultSet() == null);
    }

    /**
     * Close the wrapped cursored stream
     * This should be performed whenever the user has finished with the stream.
     */
    public void close() throws DatabaseException {
        if (isClosed()) {
            return;
        }

        (((RemoteSession)getSession()).getRemoteConnection()).cursoredStreamClose(getID());
    }

    /**
     * INTERNAL:
     * Retreive the size of the wrapped cursored stream.
     */
    protected int getCursorSize() {
        return (((RemoteSession)getSession()).getRemoteConnection()).cursoredStreamSize(getID());
    }

    /**
     * INTERNAL:
     * Retreive the OID of the wrapped cursored stream.
     */
    public ObjID getID() {
        return this.id;
    }

    /**
     * INTERNAL:
     * Return if the stream is closed.
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Read the next page of objects from the server.
     */
    protected Object retrieveNextPage() throws DatabaseException {
        if (isClosed()) {
            return null;
        }

        Vector nextPageObjects = (((RemoteSession)getSession()).getRemoteConnection()).cursoredStreamNextPage(this, getPolicy().getQuery(), (RemoteSession)getSession(), getPageSize());
        if ((nextPageObjects == null) || nextPageObjects.isEmpty()) {
            return null;
        }

        Helper.addAllToVector(getObjectCollection(), nextPageObjects);
        return getObjectCollection().lastElement();
    }
}
