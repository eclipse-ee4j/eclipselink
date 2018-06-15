/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.events.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Tests the triggering of the post calculate unit of work change set event.
 * BUG# 4138562.
 *
 * @author Guy Pelletier
 * @version 1.0 July 28/05
 */
public class PostCalculateUOWChangeSetEventTest extends EventHookTestCase {
    private int m_finalEventCount;

    public void setup() {
        super.setup();
        getDatabaseSession().writeObject(getEmailAccount());
    }

    protected void test() {
        int eventCountStart = EventHookSystem.POST_CALCULATE_UOW_CHANGE_SET;

        UnitOfWork uow = getSession().acquireUnitOfWork();
        EmailAccount m_emailAccount = (EmailAccount)uow.readObject(getEmailAccount());
        m_emailAccount.setHostName("anEmailHost");
        uow.commit();

        m_finalEventCount = EventHookSystem.POST_CALCULATE_UOW_CHANGE_SET - eventCountStart;
    }

    protected void verify() {
        if (m_finalEventCount != 1) {
            throw new TestErrorException("The post calculate unit of work change set event fired: " + m_finalEventCount + " times. Should fire only once.");
        }
    }
}
