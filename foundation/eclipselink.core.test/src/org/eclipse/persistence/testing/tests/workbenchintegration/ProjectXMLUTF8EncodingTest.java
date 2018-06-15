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

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;


public class ProjectXMLUTF8EncodingTest extends AutoVerifyTestCase {
    Exception exception = null;

    public ProjectXMLUTF8EncodingTest() {
        setDescription("Tests if only UTF-8 is supported");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        XMLProjectReader.read("org/eclipse/persistence/testing/models/workbenchintegration/Employee_utf8.xml",
                              getClass().getClassLoader());
    }
}
