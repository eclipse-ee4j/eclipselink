/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
