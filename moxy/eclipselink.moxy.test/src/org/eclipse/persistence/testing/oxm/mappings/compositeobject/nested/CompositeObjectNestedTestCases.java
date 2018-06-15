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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nested;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

import org.eclipse.persistence.testing.oxm.mappings.compositeobject.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.MailingAddress;

public class CompositeObjectNestedTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/nested/CompositeObjectNested.xml";
  private final static String CONTROL_PROJECT_NAME = "pilot-project";
  private final static int CONTROL_EMPLOYEE_ID = 123;
  private final static String CONTROL_MAILING_ADDRESS_STREET = "1 Any Street";
  private final static String CONTROL_MAILING_ADDRESS_CITY = "Ottawa";
  private final static String CONTROL_MAILING_ADDRESS_PROVINCE = "Ontario";
  private final static String CONTROL_MAILING_ADDRESS_POSTAL_CODE = "A1B 2C3";

  public CompositeObjectNestedTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
        setProject(new CompositeObjectNestedProject());
  }

  protected Object getControlObject() {

        Project project = new Project();
        project.setName(CONTROL_PROJECT_NAME);

    Employee employee = new Employee();
    employee.setID(CONTROL_EMPLOYEE_ID);

    MailingAddress mailingAddress = new MailingAddress();
    mailingAddress.setStreet(CONTROL_MAILING_ADDRESS_STREET);
    mailingAddress.setCity(CONTROL_MAILING_ADDRESS_CITY);
    mailingAddress.setProvince(CONTROL_MAILING_ADDRESS_PROVINCE);
    mailingAddress.setPostalCode(CONTROL_MAILING_ADDRESS_POSTAL_CODE);

    employee.setMailingAddress(mailingAddress);

    project.setLeader(employee);

    return project;
  }

}
