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

import java.util.Hashtable;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sessions.remote.RemoteConnection;
import org.eclipse.persistence.internal.sessions.remote.RemoteFunctionCall;
import org.eclipse.persistence.internal.sessions.remote.SequencingFunctionCall;

/*
 * RemoteConnectionSequencing class provides Sequencing through RemoteConnection.
 * It caches some info and communicates with Sequencing object
 * on its master session by sending SequencingFunctionCall objects
 * through the RemoteConnection.
 * Note that individual SequencingFunctionCalls
 * are implemented as static inner classes in SequenceFunctionCall class:
 * like SequencingFunctionCall.DoesExist.
 */
class RemoteConnectionSequencing implements Sequencing {
    protected RemoteConnection remoteConnection;
    protected Hashtable classToShouldAcquireValueAfterInsert;
    protected int whenShouldAcquireValueForAll;

    public static boolean masterSequencingExists(RemoteConnection con) {
        return ((Boolean)con.getSequenceNumberNamed(new SequencingFunctionCall.DoesExist())).booleanValue();
    }

    public RemoteConnectionSequencing(RemoteConnection remoteConnection) {
        this.remoteConnection = remoteConnection;
        whenShouldAcquireValueForAll = ((Integer)processFunctionCall(new SequencingFunctionCall.WhenShouldAcquireValueForAll())).intValue();
        if (whenShouldAcquireValueForAll == UNDEFINED) {
            classToShouldAcquireValueAfterInsert = new Hashtable(20);
        }
    }

    public int whenShouldAcquireValueForAll() {
        return whenShouldAcquireValueForAll;
    }

    public Object getNextValue(Class cls) {
        return processFunctionCall(new SequencingFunctionCall.GetNextValue(cls));
    }

    protected Object processFunctionCall(RemoteFunctionCall call) {
        return remoteConnection.getSequenceNumberNamed(call);
    }
}
