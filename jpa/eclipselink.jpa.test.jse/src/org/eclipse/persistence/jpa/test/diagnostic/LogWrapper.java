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

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jpa.test.diagnostic;

import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

//Simple log handler which counts selected messages.
public class LogWrapper extends DefaultSessionLog {

    private final String CHECKED_MESSAGE;
    private int messageCounter = 0;

    public LogWrapper(String checkedMessage) {
        CHECKED_MESSAGE = checkedMessage;
    }


    @Override
    public synchronized void log(SessionLogEntry entry) {
        if (CHECKED_MESSAGE.equals(entry.getMessage())) {
            messageCounter++;
        }
        super.log(entry);
    }

    @Override
    public boolean shouldLog(int level, String category) {
        return true;
    }

    public String getCheckedMessage() {
        return CHECKED_MESSAGE;
    }

    public int getMessageCount() {
        return messageCounter;
    }
}
