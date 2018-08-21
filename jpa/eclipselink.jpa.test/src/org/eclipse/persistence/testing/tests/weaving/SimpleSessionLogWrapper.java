/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.weaving;

// TopLink imports
import org.eclipse.persistence.logging.*;

public class SimpleSessionLogWrapper extends AbstractSessionLog implements SessionLog {

    protected SessionLog sessionLog;
    protected String expectedMessage;
    protected boolean expected = false;

    public SimpleSessionLogWrapper(SessionLog sessionLog) {
        this.sessionLog = sessionLog;
    }

    public void log(SessionLogEntry entry) {
        if (entry.getMessage().indexOf(expectedMessage) >= 0) {
            expected = true;
        }
        sessionLog.log(entry);
    }

    public String getExpectedMessage() {
        return expectedMessage;
    }
    public void setExpectedMessage(String expectedMessage) {
        this.expectedMessage = expectedMessage;
    }

    public boolean expected() {
        boolean foo = expected;
        expected = false;
        return foo;
    }

}
