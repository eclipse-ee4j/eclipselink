/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
