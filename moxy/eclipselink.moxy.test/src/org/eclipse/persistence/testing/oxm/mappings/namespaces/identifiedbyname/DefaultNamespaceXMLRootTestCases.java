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
package org.eclipse.persistence.testing.oxm.mappings.namespaces.identifiedbyname;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.namespaces.Employee;

public class DefaultNamespaceXMLRootTestCases extends XMLMappingTestCases  {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/namespaces/identifiedbyname/DefaultNamespace.xml";

  private final static int CONTROL_ID = 123;
    private final static String CONTROL_NAME = "Jane Doe";

    public DefaultNamespaceXMLRootTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new DefaultNamespaceProject());
    }

  public Object getWriteControlObject() {
      XMLRoot root = new XMLRoot();
      root.setLocalName("employee");
      root.setNamespaceURI("http://www.example.com/EMPLOYEE");
      Employee employee = (Employee)getControlObject();
      root.setObject(employee);
      return root;
  }

  public Object getControlObject() {
      Employee employee = new Employee();
      employee.setId(CONTROL_ID);
      employee.setName(CONTROL_NAME);
      return employee;
  }

}
