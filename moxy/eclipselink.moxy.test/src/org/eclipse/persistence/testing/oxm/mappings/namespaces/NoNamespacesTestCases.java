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
package org.eclipse.persistence.testing.oxm.mappings.namespaces;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class NoNamespacesTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/namespaces/NoNamespaces.xml";

  private final static int CONTROL_ID = 123;
    private final static String CONTROL_NAME = "Jane Doe";

    public NoNamespacesTestCases(String name) throws Exception {
        super(name);
    setControlDocument(XML_RESOURCE);
    setProject(new NoNamespacesProject());
    }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.setId(CONTROL_ID);
        employee.setName(CONTROL_NAME);
    return employee;
  }

}
