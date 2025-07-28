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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


public class ProjectXMLUTF16EncodingTest extends AutoVerifyTestCase {
    Exception exception = null;

    public ProjectXMLUTF16EncodingTest() {
        setDescription("Tests if UTF-16 is not supported");
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    protected void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void test() {
        try {
            XMLProjectReader.read("org/eclipse/persistence/testing/models/workbenchintegration/Employee_utf16.xml",
                                  getClass().getClassLoader());
        } catch (Exception e) {
            exception = e;
        }
    }

    @Override
    protected void verify() {
        if ((exception == null) || (!(exception instanceof XMLMarshalException)) ||
            (((XMLMarshalException)exception).getErrorCode() != XMLMarshalException.UNMARSHAL_EXCEPTION)) {
            throw new TestErrorException("ProjectXMLUTF16EncodingTest failed expecting XMLMarshalException",
                                         exception);
        }
    }
}
