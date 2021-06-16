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

    @Override
    public int whenShouldAcquireValueForAll() {
        return whenShouldAcquireValueForAll;
    }

    @Override
    public Object getNextValue(Class cls) {
        return processFunctionCall(new SequencingFunctionCall.GetNextValue(cls));
    }

    protected Object processFunctionCall(RemoteFunctionCall call) {
        return remoteConnection.getSequenceNumberNamed(call);
    }
}
