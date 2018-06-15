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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlattribute;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.Employee;

public class DirectToXMLAttributeIdentifiedByNameEmptyTestCases extends XMLMappingTestCases {
    // 20070921: round trip is not preserved because EMPTY_STRING equates to null for default NullPolicy behavior
  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/identifiedbyname/xmlattribute/DirectToXMLAttributeEmpty.xml";
  private final static int CONTROL_ID = 0;
  private final static String CONTROL_FIRST_NAME = "";
  private final static String CONTROL_LAST_NAME = "Doe";

  public DirectToXMLAttributeIdentifiedByNameEmptyTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
        setProject(new DirectToXMLAttributeIdentifiedByNameProject());
    }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.setID(CONTROL_ID);
    employee.setFirstName(null);//CONTROL_FIRST_NAME);
    employee.setLastName(CONTROL_LAST_NAME);
    return employee;
  }
}
