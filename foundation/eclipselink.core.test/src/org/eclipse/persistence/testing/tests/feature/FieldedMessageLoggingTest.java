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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Test the Logging message containing formatting string.  This is for bug211741.
 */
public class FieldedMessageLoggingTest extends AutoVerifyTestCase {
    SessionLog oldLog;
    protected Exception caughtException = null;

    public FieldedMessageLoggingTest() {
        setDescription("Tests that message Logging containing formatting string like '{0' - does not cause a parse exception");
    }

     public void setup() {
        caughtException = null;
        oldLog = getSession().getSessionLog();
        DefaultSessionLog newLog = new DefaultSessionLog();
        newLog.setLevel(SessionLog.FINE);

        getSession().setSessionLog(newLog);
    }

    public void test() {
        Session s = getSession();
        try {
           s.getSessionLog().log(SessionLog.FINE, "Time {0.5HR}");
        }catch(Exception e) {
            caughtException = e;
        }
    }

    public void verify() throws Exception {
        if (caughtException != null) {
            throw caughtException;
        }
    }

    public void reset() {
        getSession().setSessionLog(oldLog);
    }
}
