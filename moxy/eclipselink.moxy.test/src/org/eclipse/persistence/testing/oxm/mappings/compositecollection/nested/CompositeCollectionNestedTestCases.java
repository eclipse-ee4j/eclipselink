/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.nested;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.MailingAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.nested.Project;

public class CompositeCollectionNestedTestCases extends XMLWithJSONMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/nested/CompositeCollectionNested.xml";
  private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/nested/CompositeCollectionNested.json";
  private final static int CONTROL_EMPLOYEE_ID = 123;
  private final static int CONTROL_EMPLOYEE2_ID = 789;
  private final static String CONTROL_PROJECT_NAME = "secret project";
  private final static String CONTROL_EMAIL_ADDRESS_1_USER_ID = "jane.doe";
  private final static String CONTROL_EMAIL_ADDRESS_1_DOMAIN = "example.com";
  private final static String CONTROL_EMAIL_ADDRESS_2_USER_ID = "jdoe";
  private final static String CONTROL_EMAIL_ADDRESS_2_DOMAIN = "test.com";
  private final static String CONTROL_EMAIL_ADDRESS_3_USER_ID = "jon.doe";
  private final static String CONTROL_EMAIL_ADDRESS_3_DOMAIN = "example.com";
  private final static String CONTROL_EMAIL_ADDRESS_4_USER_ID = "jondoe";
  private final static String CONTROL_EMAIL_ADDRESS_4_DOMAIN = "test.com";
  private final static String CONTROL_MAILING_ADDRESS_1_STREET = "1 Any Street";
  private final static String CONTROL_MAILING_ADDRESS_1_CITY = "Ottawa";
  private final static String CONTROL_MAILING_ADDRESS_1_PROVINCE = "Ontario";
  private final static String CONTROL_MAILING_ADDRESS_1_POSTAL_CODE = "A1B 2C3";
  private final static String CONTROL_MAILING_ADDRESS_2_STREET = "2 Autre Rue.";
  private final static String CONTROL_MAILING_ADDRESS_2_CITY = "Gatineau";
  private final static String CONTROL_MAILING_ADDRESS_2_PROVINCE = "Quebec";
  private final static String CONTROL_MAILING_ADDRESS_2_POSTAL_CODE = "X1Y 2Z3";

  public CompositeCollectionNestedTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setControlJSON(JSON_RESOURCE);
    setProject(new CompositeCollectionNestedProject());
  }

  protected Object getControlObject() {
		Project project = new Project();
		project.setName(CONTROL_PROJECT_NAME);
	
    EmailAddress emailAddress1 = new EmailAddress();
    emailAddress1.setUserID(CONTROL_EMAIL_ADDRESS_1_USER_ID);
    emailAddress1.setDomain(CONTROL_EMAIL_ADDRESS_1_DOMAIN);

    EmailAddress emailAddress2 = new EmailAddress();
    emailAddress2.setUserID(CONTROL_EMAIL_ADDRESS_2_USER_ID);
    emailAddress2.setDomain(CONTROL_EMAIL_ADDRESS_2_DOMAIN);

		EmailAddress emailAddress3 = new EmailAddress();
    emailAddress3.setUserID(CONTROL_EMAIL_ADDRESS_3_USER_ID);
    emailAddress3.setDomain(CONTROL_EMAIL_ADDRESS_3_DOMAIN);

		EmailAddress emailAddress4 = new EmailAddress();
    emailAddress4.setUserID(CONTROL_EMAIL_ADDRESS_4_USER_ID);
    emailAddress4.setDomain(CONTROL_EMAIL_ADDRESS_4_DOMAIN);

    MailingAddress mailingAddress1 = new MailingAddress();
    mailingAddress1.setStreet(CONTROL_MAILING_ADDRESS_1_STREET);
    mailingAddress1.setCity(CONTROL_MAILING_ADDRESS_1_CITY);
    mailingAddress1.setProvince(CONTROL_MAILING_ADDRESS_1_PROVINCE);
    mailingAddress1.setPostalCode(CONTROL_MAILING_ADDRESS_1_POSTAL_CODE);

    MailingAddress mailingAddress2 = new MailingAddress();
    mailingAddress2.setStreet(CONTROL_MAILING_ADDRESS_2_STREET);
    mailingAddress2.setCity(CONTROL_MAILING_ADDRESS_2_CITY);
    mailingAddress2.setProvince(CONTROL_MAILING_ADDRESS_2_PROVINCE);
    mailingAddress2.setPostalCode(CONTROL_MAILING_ADDRESS_2_POSTAL_CODE);
    
    MailingAddress mailingAddress3 = new MailingAddress();
    mailingAddress3.setStreet(CONTROL_MAILING_ADDRESS_1_STREET);
    mailingAddress3.setCity(CONTROL_MAILING_ADDRESS_1_CITY);
    mailingAddress3.setProvince(CONTROL_MAILING_ADDRESS_1_PROVINCE);
    mailingAddress3.setPostalCode(CONTROL_MAILING_ADDRESS_1_POSTAL_CODE);

    MailingAddress mailingAddress4 = new MailingAddress();
    mailingAddress4.setStreet(CONTROL_MAILING_ADDRESS_2_STREET);
    mailingAddress4.setCity(CONTROL_MAILING_ADDRESS_2_CITY);
    mailingAddress4.setProvince(CONTROL_MAILING_ADDRESS_2_PROVINCE);
    mailingAddress4.setPostalCode(CONTROL_MAILING_ADDRESS_2_POSTAL_CODE);

		Employee employee = new Employee();
    employee.setID(CONTROL_EMPLOYEE_ID);
		employee.getEmailAddresses().add(emailAddress1);
		employee.getEmailAddresses().add(emailAddress2);
		employee.getMailingAddresses().add(mailingAddress1);
		employee.getMailingAddresses().add(mailingAddress2);

		Employee employee2 = new Employee();
    employee2.setID(CONTROL_EMPLOYEE2_ID);
		employee2.getEmailAddresses().add(emailAddress3);
		employee2.getEmailAddresses().add(emailAddress4);
		employee2.getMailingAddresses().add(mailingAddress4);
		employee2.getMailingAddresses().add(mailingAddress3);

		project.getEmployees().add(employee);
		project.getEmployees().add(employee2);
		
    return project;
  }

}
