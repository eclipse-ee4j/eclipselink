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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.singleelement;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class CompositeObjectSingleElementTestCases extends XMLWithJSONMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/singleelement/CompositeObjectSingleElement.xml";
  private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/singleelement/CompositeObjectSingleElement.json";

  private final static int CONTROL_EMPLOYEE_ID = 123;
  private final static String CONTROL_EMAIL_ADDRESS_USER_ID = "jane.doe";

  public CompositeObjectSingleElementTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setControlJSON(JSON_RESOURCE);
        setProject(new CompositeObjectSingleElementProject());
  }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.setID(CONTROL_EMPLOYEE_ID);

    EmailAddress emailAddress = new EmailAddress();
    emailAddress.setUserID(CONTROL_EMAIL_ADDRESS_USER_ID);
    employee.setEmailAddress(emailAddress);

    return employee;
  }

}
