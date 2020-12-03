/*******************************************************************************
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.test.cachedeadlock.cdi.event;

import org.eclipse.persistence.jpa.test.cachedeadlock.model.CacheDeadLockDetectionMaster;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

public class EventProducerImpl
        implements EventProducer {

    @Inject
    @Any
    Event<CacheDeadLockDetectionMaster> myEvent;

    @Override
    public void fireAsyncEvent(CacheDeadLockDetectionMaster cacheDeadLockDetectionMaster) {
        myEvent.fireAsync(cacheDeadLockDetectionMaster);
    }

}
