/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;


/**
 * Event listener used to test locking.
 */
public class LockOnCloneListener extends DescriptorEventAdapter {
    public LockOnCloneListener() {
    }

    /**
     *   Wait until the refresh has completed
     */
    public void postRefresh(DescriptorEvent event) {
        ConcurrentAddress address = (ConcurrentAddress)event.getObject();
        synchronized (address) {
            try {
                if (ConcurrentAddress.RUNNING_TEST == ConcurrentAddress.LOCK_ON_CLONE_DEADLOCK) {
                    address.notifyAll(); //wake the merge
                    address.wait(10000); //wait for merge to start merging address
                }
                address.setStreet("Corrupted");
                address.setPostalCode("A1A1A1");
                address.notifyAll(); //let the merge continue
            } catch (InterruptedException ex) {
            }
        }
    }
}
