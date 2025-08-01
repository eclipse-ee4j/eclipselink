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
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.io.File;

public class ProjectXMLTest extends AutoVerifyTestCase {
    public static String PROJECT_FILE = "org/eclipse/persistence/testing/workbench_integration/MWIntegrationTestEmployeeProject.xml";

    public ProjectXMLTest() {
        setDescription("This test will verify that the isolation setting is set within the project xml");
    }

    @Override
    public void test() {
        XMLProjectWriter.write("IsolatedProject.xml", new IsolatedEmployeeProject());
        Project project = XMLProjectReader.read("IsolatedProject.xml", getClass().getClassLoader());
        if (!project.getDescriptor(IsolatedEmployee.class).isIsolated()) {
            throw new TestErrorException("IsIsolated flag not copied to and from XML");
        }
    }

    @Override
    public void reset() {
        File file = new File("IsolatedProject.xml");
        file.delete();
    }
}
