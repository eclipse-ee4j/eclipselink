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


public class ConcurrentRefreshEventAdapter extends DescriptorEventAdapter {

    public ConcurrentRefreshEventAdapter() {
    }

    /**
     *   Wait until the refresh has completed
     */
    public void postMerge(DescriptorEvent event) {
        try {
            synchronized (ConcurrentRefreshOnUpdateTest.lock) {
                if (ConcurrentRefreshOnUpdateTest.depth <= 0) {
                    if (ConcurrentRefreshOnUpdateTest.readerWaiting) {
                        ConcurrentRefreshOnUpdateTest.lock.notifyAll();
                    }
                    //                    ConcurrentRefreshOnUpdateTest.writerWaiting = true;


                    //                    ConcurrentRefreshOnUpdateTest.writerWaiting = false;
                } else {
                    --ConcurrentRefreshOnUpdateTest.depth;
                }
            }
            //we will sleep he in hopes that the refresh will have had time to complete
            //waiting is not possible as there is no events available before releaseDefered lock is attempted
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            //ignore
        }
    }

}
