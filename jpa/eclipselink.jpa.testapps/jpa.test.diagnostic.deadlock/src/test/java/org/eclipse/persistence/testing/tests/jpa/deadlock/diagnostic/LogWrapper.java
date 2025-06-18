/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.deadlock.diagnostic;

import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Simple log handler which counts selected messages.
public class LogWrapper extends DefaultSessionLog {

    private StringWriter sw = new StringWriter();

    public LogWrapper() {
        super.setWriter(sw);
    }


    @Override
    public synchronized void log(SessionLogEntry entry) {
        super.log(entry);
    }

    @Override
    public boolean shouldLog(int level, String category) {
        return true;
    }

    public long getMessageCount(String checkedMessage) {
        String logOutput = sw.toString();
        Matcher matcher = Pattern.compile(Pattern.quote(checkedMessage)).matcher(logOutput);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
