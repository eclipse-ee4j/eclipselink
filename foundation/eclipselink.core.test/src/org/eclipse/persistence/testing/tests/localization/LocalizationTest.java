/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.localization;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.localization.LoggingLocalization;

public class LocalizationTest extends AutoVerifyTestCase {
    String localizedString;
    String nonLocalizedString;

    public LocalizationTest() {
        setDescription("Verify that the correct translation is returned for a localized string and" + "the NoTranslationForThisLocale message is returned for a non-localized string");
    }

    public void test() {
        localizedString = LoggingLocalization.buildMessage("login_successful", new Object[] { "LoggingTestSession" });
        nonLocalizedString = LoggingLocalization.buildMessage("message_not_exist");
    }

    protected void verify() {
        if (!localizedString.equals("LoggingTestSession login successful")) {
            throw new TestErrorException("LoggingLocalization.buildMessage could not find the correct translation.");
        }

        if (!nonLocalizedString.equals("message_not_exist (There is no English translation for this message.)")) {
            throw new TestErrorException("LoggingLocalization.buildMessage could not find NoTranslationForThisLocale message.");
        }
    }
}
