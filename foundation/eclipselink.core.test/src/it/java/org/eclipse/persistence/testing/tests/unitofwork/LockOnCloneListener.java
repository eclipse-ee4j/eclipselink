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
