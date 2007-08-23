/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sequencing;

import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Define interface for sequencing server.
 * <p>
 * <b>Description</b>: This interface accessed through
 * ServerSession.getSequencingServer() method.
 * Used for creation of ClientSessionSequencing object only.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Connects sequencing on ClientSession with sequencing on ServerSession.
 * </ul>
 * @see ClientSessionSequencing
 */
public interface SequencingServer extends Sequencing {
    public Object getNextValue(AbstractSession writeSession, Class cls);
}
