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
                    //					ConcurrentRefreshOnUpdateTest.writerWaiting = true;


                    //					ConcurrentRefreshOnUpdateTest.writerWaiting = false;
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
