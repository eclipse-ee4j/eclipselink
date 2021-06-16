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
