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

import java.util.*;
import java.util.concurrent.*;

/**
 * Handles the storage and allocation of sequence values.
 * This is held by the session (ServerSession) through the SequencingManager.
 * @see SequencingManager
 */
class PreallocationHandler implements SequencingLogInOut {
    protected Map<String, Queue> preallocatedSequences;

    public PreallocationHandler() {
        super();
    }

    /**
     * Returns the Queue of sequences from the global sequences for the seqName.
     * If there is not one, a new empty Queue is registered.
     * This queue is thread-safe, and threads can concurrent poll the queue to remove the first element.
     */
    public Queue getPreallocated(String sequenceName) {
        Queue sequences = preallocatedSequences.get(sequenceName);
        if (sequences == null) {
            synchronized (preallocatedSequences) {
                sequences = preallocatedSequences.get(sequenceName);
                if (sequences == null) {
                    sequences = new ConcurrentLinkedQueue();
                    preallocatedSequences.put(sequenceName, sequences);
                }
            }
        }
        return sequences;
    }

    // SequencingLogInOut
    public void onConnect() {
        initializePreallocated();
    }

    public void onDisconnect() {
        preallocatedSequences = null;
    }

    public boolean isConnected() {
        return preallocatedSequences != null;
    }

    /**
     * Removes all preallocated objects.
     * A dangerous method to use in multithreaded environment method,
     * but so handy for testing.
     */
    public void initializePreallocated() {
        preallocatedSequences = new ConcurrentHashMap(20);
    }

    /**
     * Removes all preallocated objects for the specified seqName.
     * A dangerous method to use in multithreaded environment method,
     * but so handy for testing.
     */
    public void initializePreallocated(String seqName) {
        preallocatedSequences.remove(seqName);
    }

    /**
     * Add the preallocated sequences to the global sequence pool for the sequence name.
     * Although this method is thread-safe, a lock should typically be obtained from the sequence manager before calling this method,
     * to ensure sequential numbers.
     */
    public void setPreallocated(String seqName, Vector sequences) {
        getPreallocated(seqName).addAll(sequences);
    }
}
