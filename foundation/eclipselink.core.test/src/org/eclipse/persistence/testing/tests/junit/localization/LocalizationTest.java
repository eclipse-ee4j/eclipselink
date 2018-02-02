/*******************************************************************************
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
