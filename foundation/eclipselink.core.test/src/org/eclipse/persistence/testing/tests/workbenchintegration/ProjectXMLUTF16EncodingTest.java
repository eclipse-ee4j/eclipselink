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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;


public class ProjectXMLUTF16EncodingTest extends AutoVerifyTestCase {
    Exception exception = null;

    public ProjectXMLUTF16EncodingTest() {
        setDescription("Tests if UTF-16 is not supported");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        try {
            XMLProjectReader.read("org/eclipse/persistence/testing/models/workbenchintegration/Employee_utf16.xml",
                                  getClass().getClassLoader());
        } catch (Exception e) {
            exception = e;
        }
    }

    protected void verify() {
        if ((exception == null) || (!(exception instanceof XMLMarshalException)) ||
            (((XMLMarshalException)exception).getErrorCode() != XMLMarshalException.UNMARSHAL_EXCEPTION)) {
            throw new TestErrorException("ProjectXMLUTF16EncodingTest failed expecting XMLMarshalException",
                                         exception);
        }
    }
}
