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
package org.eclipse.persistence.testing.tests.junit.localization;

import org.junit.Assert;
import org.junit.Test;
import org.eclipse.persistence.internal.localization.LoggingLocalization;

public class LocalizationTest {

    @Test
    public void test() {
        Assert.assertEquals("LoggingLocalization.buildMessage could not find the correct translation.",
                "EclipseLink, version: EXAMPLE", LoggingLocalization.buildMessage("topLink_version", new Object[] { "EXAMPLE" }));
        Assert.assertEquals("LoggingLocalization.buildMessage could not find the correct translation.",
                "message_not_exist (There is no English translation for this message.)", LoggingLocalization.buildMessage("message_not_exist"));
    }
}
