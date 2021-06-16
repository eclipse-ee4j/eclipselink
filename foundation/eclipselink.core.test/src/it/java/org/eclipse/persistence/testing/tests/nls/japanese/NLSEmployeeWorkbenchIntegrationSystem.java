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
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.sessions.factories.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 *  This test system uses the Employee test system to test the integration between the
 *  Mapping Workbench and the Foundation Library.  To do this, it writes our test project
 *  to an XML file and then reads the XML file and runs the employee tests on it.
 *  @author Tom Ware
 */
public class NLSEmployeeWorkbenchIntegrationSystem extends NLSEmployeeSystem {

    public static String PROJECT_FILE =
        "org/eclipse/persistence/testing/nlstests/japanese/NLSJapaneseMWIntegrationTestEmployeeProject";

    public ClassDescriptor employeeDescriptor;

    /**
     * Override the constructor for employee system to allow us to read and write XML
     */
    public NLSEmployeeWorkbenchIntegrationSystem() {
        this(PROJECT_FILE + ".xml");
    }

    /**
     * Override the constructor for employee system to allow us to read and write XML
     */
    public NLSEmployeeWorkbenchIntegrationSystem(String fileName) {
        project = null;
        project = XMLProjectReader.read(fileName);
    }
}
