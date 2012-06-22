/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nulltests;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.MailingAddress;

public class CompositeObjectEmptyObjectTests extends XMLMappingTestCases
{
  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/nulltests/CompositeObjectEmptyObjectTests.xml";
  private final static int CONTROL_EMPLOYEE_ID = 123;
	private final static String CONTROL_DOMAIN = "example.com";
  private final static String CONTROL_USER_ID = "jane.doe";

	
	public CompositeObjectEmptyObjectTests(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
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
