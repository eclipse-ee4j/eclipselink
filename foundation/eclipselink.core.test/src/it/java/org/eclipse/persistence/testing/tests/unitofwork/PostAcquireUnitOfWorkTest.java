/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;


public class PostAcquireUnitOfWorkTest extends UnitOfWorkEventTest {
    protected boolean eventTriggered;

    @Override
    public void setup() {
        super.setup();
        setDescription("Test the postAcquireUnitOfWork Event");
        SessionEventAdapter tvAdapter = new SessionEventAdapter() {
                // Listen for PostAcquireUnitOfWorkEvents

                @Override
                public void postAcquireUnitOfWork(SessionEvent event) {
                    setEventTriggered(true);
                }
            };
        getSession().getEventManager().addListener(tvAdapter);
    }

    @Override
    public void test() {
        UnitOfWork tvUnitOfWork = getSession().acquireUnitOfWork();
    }

    @Override
    public void verify() {
        if (!isEventTriggered()) {
            throw new TestErrorException("The postAcquireUnitOfWork event was not triggered.");
        }
    }
}
