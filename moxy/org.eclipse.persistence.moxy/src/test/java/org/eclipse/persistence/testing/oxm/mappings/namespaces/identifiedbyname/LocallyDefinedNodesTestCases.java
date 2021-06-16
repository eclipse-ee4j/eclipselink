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
package org.eclipse.persistence.testing.oxm.mappings.namespaces.identifiedbyname;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.namespaces.Employee;

public class LocallyDefinedNodesTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/namespaces/identifiedbyname/LocallyDefinedNodes.xml";

  private final static int CONTROL_ID = 123;
    private final static String CONTROL_NAME = "Jane Doe";

    public LocallyDefinedNodesTestCases(String name) throws Exception {
        super(name);
    setControlDocument(XML_RESOURCE);
    setProject(new LocallyDefinedNodesProject());
    }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.setId(CONTROL_ID);
        employee.setName(CONTROL_NAME);
    return employee;
  }

}
