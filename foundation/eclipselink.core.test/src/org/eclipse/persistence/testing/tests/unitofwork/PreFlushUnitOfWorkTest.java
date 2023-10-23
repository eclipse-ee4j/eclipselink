/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class PreFlushUnitOfWorkTest extends UnitOfWorkEventTest {
    @Override
    public void setup() {
        super.setup();
        setDescription("Test the preFlushUnitOfWork Event");
        SessionEventAdapter tvAdapter = new SessionEventAdapter() {
            // Listen for PreFlushUnitOfWorkEvents

            @Override
            public void preFlushUnitOfWork(SessionEvent event) {
                setEventTriggered(true);
            }
        };
        getSession().getEventManager().addListener(tvAdapter);
    }

    @Override
    public void test() {
        UnitOfWork tvUnitOfWork = getSession().acquireUnitOfWork();
        tvUnitOfWork.writeChanges();
    }

    @Override
    public void verify() {
        if (!isEventTriggered()) {
            throw new TestErrorException("The preFlushUnitOfWork event was not triggered.");
        }
    }
}
