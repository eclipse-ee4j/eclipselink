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
//    Denise Smith - April 21/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname;

import java.math.BigInteger;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeObjectIdentifiedByNameTextChildTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/identifiedbyname/CompositeObjectIdentifiedByNameText.xml";
  private final static int CONTROL_EMPLOYEE_ID = 123;

  public CompositeObjectIdentifiedByNameTextChildTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setProject(new CompositeObjectIdentifiedByNameTextProject());
  }

  protected Object getControlObject() {
    EmployeeWithObjects employee = new EmployeeWithObjects();
    employee.setID(CONTROL_EMPLOYEE_ID);

    employee.setMailingAddress("1 Any Street, Ottawa, Ontario, A1B 2C3");
    employee.setEmailAddress("jane.doe@example.com");
    employee.setSalary(new BigInteger("5000"));

    return employee;
  }

  public static void main(String[] args) {
      junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname.CompositeObjectIdentifiedByNameTextChildTestCases" });
  }

}
