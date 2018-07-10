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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nulltests;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.MailingAddress;

public class CompositeObjectEmptyObjectTests extends XMLWithJSONMappingTestCases
{
  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/nulltests/CompositeObjectEmptyObjectTests.xml";
  private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/nulltests/CompositeObjectEmptyObjectTests.json";
  private final static int CONTROL_EMPLOYEE_ID = 123;
    private final static String CONTROL_DOMAIN = "example.com";
  private final static String CONTROL_USER_ID = "jane.doe";


    public CompositeObjectEmptyObjectTests(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setControlJSON(JSON_RESOURCE);
        setProject(new CompositeObjectNullTestsProject());
}

    protected Object getControlObject() {
    Employee employee = new Employee();
    employee.setID(CONTROL_EMPLOYEE_ID);

        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setDomain(CONTROL_DOMAIN);
        emailAddress.setUserID(CONTROL_USER_ID);
    employee.setEmailAddress(emailAddress);

    MailingAddress mailingAddress = new MailingAddress();
    //mailingAddress.setStreet(CONTROL_MAILING_ADDRESS_STREET);
        //the "city" variable on mailingAddress is not being set for this test
        //mailingAddress.setProvince(CONTROL_MAILING_ADDRESS_PROVINCE);
    //mailingAddress.setPostalCode(CONTROL_MAILING_ADDRESS_POSTAL_CODE);
    employee.setMailingAddress(mailingAddress);

    return employee;
  }
}
