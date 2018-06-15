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
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.exceptions.SessionLoaderException;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Tests Sessions XML schema with misplaced dependent tag.
 *
 * @author Edwin Tang
 * @version 1.0
 * @date December 2, 2004
 */
public class SessionsXMLSchemaMisplacedDependentTagTest extends TestCase {
    Exception exception = null;

    public SessionsXMLSchemaMisplacedDependentTagTest() {
        setDescription("Test Sessions XML schema with misplaced dependent tag.");
    }

    public void test() {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaMisplacedDependentTag.xml");
        try {
            SessionManager.getManager().getSession(loader, "EmployeeSession", getClass().getClassLoader(), false, true);
        } catch (Exception e) {
            exception = e;
        }
    }

    protected void verify() {
        if (exception == null || ((SessionLoaderException)exception).getErrorCode() != SessionLoaderException.FINAL_EXCEPTION) {
            throw new TestErrorException("SessionsXMLSchemaInvalidTagTest failed.", exception);
        }
    }
}
