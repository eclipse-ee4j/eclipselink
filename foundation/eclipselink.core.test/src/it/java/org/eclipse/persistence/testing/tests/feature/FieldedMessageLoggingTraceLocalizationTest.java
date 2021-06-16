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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;

/**
 * Test logging a message that requires a key:value lookup in TraceLocalization without translation
 * See bug 222698.
 */
public class FieldedMessageLoggingTraceLocalizationTest extends AutoVerifyTestCase {
    SessionLog oldLog;
    protected Exception caughtException = null;
    public static final String MESSAGE_KEY = "cmp_init_transformer_is_null";

    public FieldedMessageLoggingTraceLocalizationTest() {
        setDescription("Tests that message Logging keys are translated to values for FINE* levels'");
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
           s.getSessionLog().log(SessionLog.FINE, MESSAGE_KEY);
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
