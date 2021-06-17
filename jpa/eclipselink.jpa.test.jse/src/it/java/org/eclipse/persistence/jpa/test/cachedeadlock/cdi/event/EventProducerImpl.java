/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.jpa.test.cachedeadlock.cdi.event;

import org.eclipse.persistence.jpa.test.cachedeadlock.model.CacheDeadLockDetectionMaster;

import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;

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
