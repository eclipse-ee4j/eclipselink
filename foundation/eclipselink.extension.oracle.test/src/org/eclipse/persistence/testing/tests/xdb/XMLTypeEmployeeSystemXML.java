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
package org.eclipse.persistence.testing.tests.xdb;

import java.io.*;

import org.eclipse.persistence.sessions.factories.*;

public class XMLTypeEmployeeSystemXML extends XMLTypeEmployeeSystem {
    public static String PROJECT_FILE = "MWIntegrationTestXDBProject";

    /**
     * Override the constructor for system to allow us to read and write XML
     */
    public XMLTypeEmployeeSystemXML() {
        this(PROJECT_FILE + System.currentTimeMillis() + ".xml");
    }

    /**
     * Override the constructor for system to allow us to read and write XML
     */
    public XMLTypeEmployeeSystemXML(String fileName) {
        org.eclipse.persistence.sessions.Project tempProject = new Employee_XMLProject();
        XMLProjectWriter.write(fileName, tempProject);
        project = XMLProjectReader.read(fileName);
        File file = new File(fileName);
        file.delete();
    }
}
