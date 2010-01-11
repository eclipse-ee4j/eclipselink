/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbynamespace;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.MailingAddress;

public class CompositeObjectIdentifiedByNamespaceTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/identifiedbynamespace/CompositeObjectIdentifiedByNamespace.xml";
  private final static int CONTROL_EMPLOYEE_ID = 123;
  private final static String CONTROL_EMAIL_ADDRESS_USER_ID = "jane.doe";
  private final static String CONTROL_EMAIL_ADDRESS_DOMAIN = "example.com";
  private final static String CONTROL_MAILING_ADDRESS_STREET = "1 Any Street";
  private final static String CONTROL_MAILING_ADDRESS_CITY = "Ottawa";
  private final static String CONTROL_MAILING_ADDRESS_PROVINCE = "Ontario";
  private final static String CONTROL_MAILING_ADDRESS_POSTAL_CODE = "A1B 2C3";

  public CompositeObjectIdentifiedByNamespaceTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
		setProject(new CompositeObjectIdentifiedByNamespaceProject());
  }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.setID(CONTROL_EMPLOYEE_ID);

    EmailAddress emailAddress = new EmailAddress();
    emailAddress.setUserID(CONTROL_EMAIL_ADDRESS_USER_ID);
    emailAddress.setDomain(CONTROL_EMAIL_ADDRESS_DOMAIN);
    employee.setEmailAddress(emailAddress);

    MailingAddress mailingAddress = new MailingAddress();
    mailingAddress.setStreet(CONTROL_MAILING_ADDRESS_STREET);
    mailingAddress.setCity(CONTROL_MAILING_ADDRESS_CITY);
    mailingAddress.setProvince(CONTROL_MAILING_ADDRESS_PROVINCE);
    mailingAddress.setPostalCode(CONTROL_MAILING_ADDRESS_POSTAL_CODE);
    employee.setMailingAddress(mailingAddress);
    
    return employee;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.main(new String[] {"-c", "org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbynamespace.CompositeObjectIdentifiedByNamespaceTestCases"});
  }

}
