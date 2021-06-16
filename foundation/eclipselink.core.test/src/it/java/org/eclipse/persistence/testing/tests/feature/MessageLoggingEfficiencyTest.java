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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Test the efficiency of Message Logging.  This is for CR#2272 to test that toString()
 *  is not unnecessarily called when Message Logging is enabled, but Debug logging disabled.
 */
public class MessageLoggingEfficiencyTest extends AutoVerifyTestCase {
    SessionLog oldLog;
    protected Exception caughtException = null;

    public MessageLoggingEfficiencyTest() {
        setDescription("Tests that Message Logging does not call toString on objects unless Debugging is enabled");
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
        UnitOfWork uow = s.acquireUnitOfWork();
        MessageLoggingEfficiencyTestClass testClass = new MessageLoggingEfficiencyTestClass();
        try {
            uow.registerNewObject(testClass);
        } catch (TestErrorException tee) {
            caughtException = tee;
        } catch (org.eclipse.persistence.exceptions.ValidationException vE) {
            //a ValidationException is expected because the MessageLoggingEfficiencyTestClass is not mapped
        }
        try {
            uow.registerExistingObject(testClass);
        } catch (TestErrorException tee) {
            caughtException = tee;
        } catch (org.eclipse.persistence.exceptions.ValidationException vE) {
            //a ValidationException is expected because the MessageLoggingEfficiencyTestClass is not mapped
        }
        try {
            uow.registerObject(testClass);
        } catch (TestErrorException tee) {
            caughtException = tee;
        } catch (org.eclipse.persistence.exceptions.ValidationException vE) {
            //a ValidationException is expected because the MessageLoggingEfficiencyTestClass is not mapped
        }
        try {
            uow.mergeClone(testClass);
        } catch (TestErrorException tee) {
            caughtException = tee;
        } catch (java.lang.NullPointerException npE) {
            //a NullPointerException is expected because the MessageLoggingEfficiencyTestClass is not mapped
        }
    }

    public void verify() throws Exception {
        if (caughtException != null) {
            //The caughtException is the TestErrorException thrown from the MessageLoggingEfficiencyTestClass
            // object's toString() method
            throw caughtException;
        }
    }

    public void reset() {
        getSession().setSessionLog(oldLog);
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

    }
}
