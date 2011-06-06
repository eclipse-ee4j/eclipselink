/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
