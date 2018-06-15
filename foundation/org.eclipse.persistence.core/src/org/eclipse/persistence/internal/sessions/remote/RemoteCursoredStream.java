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
import java.util.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;

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
    @Override
    public void close() throws DatabaseException {
        if (isClosed()) {
            return;
        }

        (((DistributedSession)getSession()).getRemoteConnection()).cursoredStreamClose(getID());
    }

    /**
     * INTERNAL:
     * Retreive the size of the wrapped cursored stream.
     */
    @Override
    protected int getCursorSize() {
        return (((DistributedSession)getSession()).getRemoteConnection()).cursoredStreamSize(getID());
    }

    /**
     * INTERNAL:
     * Retrieve the OID of the wrapped cursored stream.
     */
    public ObjID getID() {
        return this.id;
    }

    /**
     * INTERNAL:
     * Return if the stream is closed.
     */
    @Override
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Read the next page of objects from the server.
     */
    @Override
    protected Object retrieveNextPage() throws DatabaseException {
        if (isClosed()) {
            return null;
        }
        Vector nextPageObjects = (((DistributedSession)getSession()).getRemoteConnection()).cursoredStreamNextPage(this, getPolicy().getQuery(), (DistributedSession)getSession(), getPageSize());
        if ((nextPageObjects == null) || nextPageObjects.isEmpty()) {
            return null;
        }
        getObjectCollection().addAll(nextPageObjects);
        return getObjectCollection().get(getObjectCollection().size() - 1);
    }
}
