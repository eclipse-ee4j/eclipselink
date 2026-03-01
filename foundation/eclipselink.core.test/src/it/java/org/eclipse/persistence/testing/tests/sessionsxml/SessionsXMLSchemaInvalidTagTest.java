/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.sessions.factories.SessionLoaderException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


/**
 * Tests Sessions XML schema with invalid tag.
 *
 * @author Edwin Tang
 * @version 1.0
 */
public class SessionsXMLSchemaInvalidTagTest extends TestCase {
    Exception exception = null;

    public SessionsXMLSchemaInvalidTagTest() {
        setDescription("Test Sessions XML schema with invalid tag.");
    }

    @Override
    public void test() {
        SessionManager.getManager().getSessions().remove("EmployeeSession");
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaInvalidTag.xml");
        try {
            SessionManager.getManager().getSession(loader, "EmployeeSession", getClass().getClassLoader(), false, false);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Override
    protected void verify() {
        if (exception == null || ((SessionLoaderException)exception).getErrorCode() != SessionLoaderException.FINAL_EXCEPTION) {
            throw new TestErrorException("SessionsXMLSchemaInvalidTagTest failed.", exception);
        }
    }
}


