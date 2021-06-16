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
