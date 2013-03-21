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
package org.eclipse.persistence.internal.sequencing;

import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.server.ConnectionPool;

/**
 * <p>
 * <b>Purpose</b>: Define interface for sequencing server.
 * <p>
 * <b>Description</b>: This interface accessed through
 * ServerSession.getSequencingServer() method.
 * Used for creation of ClientSessionSequencing object
 * and for access to sequencing connection pool.
 * Note that if session is disconnected ServerSession.getSequencingServer() always returns null.
 * Setup of SequencingConnectionPool is done only through SequencingControl interface.
 * Even if getSequencingControl().setShouldUseSeparateConnection(true) is specified,
 * SequencingConnectionPool is NOT created unless the session has at least one Sequence object
 * that requires transaction.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Connects sequencing on ClientSession with sequencing on ServerSession.
 * </ul>
 * @see ClientSessionSequencing
 */
public interface SequencingServer extends Sequencing {
    Object getNextValue(AbstractSession writeSession, Class cls);
    ConnectionPool getConnectionPool();
}
