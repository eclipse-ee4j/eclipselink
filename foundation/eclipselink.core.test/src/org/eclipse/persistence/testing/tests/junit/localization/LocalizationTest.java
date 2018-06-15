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
package org.eclipse.persistence.testing.tests.junit.localization;

import org.junit.Assert;
import org.junit.Test;
import org.eclipse.persistence.internal.localization.LoggingLocalization;

public class LocalizationTest {

    @Test
    public void test() {
        Assert.assertEquals("LoggingLocalization.buildMessage could not find the correct translation.",
                "LoggingTestSession login successful", LoggingLocalization.buildMessage("login_successful", new Object[] { "LoggingTestSession" }));
        Assert.assertEquals("LoggingLocalization.buildMessage could not find the correct translation.",
                "message_not_exist (There is no English translation for this message.)", LoggingLocalization.buildMessage("message_not_exist"));
    }
}
