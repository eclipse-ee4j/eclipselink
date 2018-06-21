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
package org.eclipse.persistence.testing.oxm.mappings.namespaces.identifiedbyname;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.namespaces.Employee;

public class GloballyDefinedNodesTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/namespaces/identifiedbyname/GloballyDefinedNodes.xml";

  private final static int CONTROL_ID = 123;
    private final static String CONTROL_NAME = "Jane Doe";

    public GloballyDefinedNodesTestCases(String name) throws Exception {
        super(name);
    setControlDocument(XML_RESOURCE);
    setProject(new GloballyDefinedNodesProject());
    }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.setId(CONTROL_ID);
        employee.setName(CONTROL_NAME);
    return employee;
  }

}
