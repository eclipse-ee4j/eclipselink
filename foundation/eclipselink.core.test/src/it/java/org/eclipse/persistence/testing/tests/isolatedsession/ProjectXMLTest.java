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
package org.eclipse.persistence.testing.tests.isolatedsession;

import java.io.File;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.factories.*;
import org.eclipse.persistence.sessions.*;

public class ProjectXMLTest extends AutoVerifyTestCase {
    public static String PROJECT_FILE = "org/eclipse/persistence/testing/workbench_integration/MWIntegrationTestEmployeeProject.xml";

    public ProjectXMLTest() {
        setDescription("This test will verify that the isolation setting is set within the project xml");
    }

    public void test() {
        XMLProjectWriter.write("IsolatedProject.xml", new IsolatedEmployeeProject());
        Project project = XMLProjectReader.read("IsolatedProject.xml", getClass().getClassLoader());
        if (!project.getDescriptor(IsolatedEmployee.class).isIsolated()) {
            throw new TestErrorException("IsIsolated flag not copied to and from XML");
        }
    }

    public void reset() {
        File file = new File("IsolatedProject.xml");
        file.delete();
    }
}
