/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.eis.xmlfile;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.factories.*;

/**
 * <b>Purpose</b>: To define system behavior.
 *
 * <p>
 * <b>Responsibilities</b>:
 *
 * <ul>
 * <li>
 * Login and return an initialize database session.
 * </li>
 * <li>
 * Create and populate the database.
 * </li>
 * </ul>
 * </p>
 */
public class EmployeeNSSystem extends EmployeeSystem {
    public EmployeeNSSystem() {

        /**
         * Use the EmployeeNSProject which is the basic EmployeeProject but with namespace
         * qualified elements.
         */
        project = XMLProjectReader.read("org/eclipse/persistence/testing/models/employee/eis/xmlfile/employee-ns-project.xml", getClass().getClassLoader());
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = XMLProjectReader.read("org/eclipse/persistence/testing/models/employee/eis/xmlfile/employee-ns-project.xml", getClass().getClassLoader());
        }

        session.addDescriptors(project);
    }
}
