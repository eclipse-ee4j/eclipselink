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

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;


public class PostCommitUnitOfWorkTest extends UnitOfWorkEventTest {
    public void setup() {
        super.setup();
        setDescription("Test the postCommitUnitOfWork Event");
        SessionEventAdapter tvAdapter = new SessionEventAdapter() {
                // Listen for PostCommitUnitOfWorkEvents

                public void postCommitUnitOfWork(SessionEvent event) {
                    setEventTriggered(true);
                }
            };
        getSession().getEventManager().addListener(tvAdapter);
    }

    public void test() {
        UnitOfWork tvUnitOfWork = getSession().acquireUnitOfWork();
        tvUnitOfWork.commit();
    }

    public void verify() {
        if (!isEventTriggered()) {
            throw new TestErrorException("The postCommitUnitOfWork event was not triggered.");
        }
    }
}
