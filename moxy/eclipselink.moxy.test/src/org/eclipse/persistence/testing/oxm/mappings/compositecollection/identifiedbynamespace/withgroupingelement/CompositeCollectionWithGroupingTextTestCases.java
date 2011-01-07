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
 *     Denise Smith - April 21/2009 - 2.0 - Initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.withgroupingelement;

import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.MailingAddress;

public class CompositeCollectionWithGroupingTextTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/identifiedbynamespace/withgroupingelement/CompositeCollectionWithGroupingElementText.xml";
    private final static String CONTROL_EMAIL_ADDRESS_2_USER_ID = "jdoe";
    private final static String CONTROL_EMAIL_ADDRESS_2_DOMAIN = "test.com";
    private final static String CONTROL_MAILING_ADDRESS_2_STREET = "2 Autre Rue.";
    private final static String CONTROL_MAILING_ADDRESS_2_CITY = "Gatineau";
    private final static String CONTROL_MAILING_ADDRESS_2_PROVINCE = "Quebec";
    private final static String CONTROL_MAILING_ADDRESS_2_POSTAL_CODE = "X1Y 2Z3";

    public CompositeCollectionWithGroupingTextTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new CompositeCollectionWithGroupingElementIdentifiedByNamespaceProject();
        
        ((XMLCompositeCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("emailAddresses")).setReferenceClass(null);
        ((XMLCompositeCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("emailAddresses")).setReferenceClassName(null);

        ((XMLCompositeCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("mailingAddresses")).setReferenceClass(null);
        ((XMLCompositeCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("mailingAddresses")).setReferenceClassName(null);
        ((XMLField)((XMLCompositeCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("mailingAddresses")).getField()).setIsTypedTextField(true);
        ((XMLDescriptor)p.getDescriptor(Employee.class)).getNonNullNamespaceResolver().put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);
        
        setProject(p);
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        
        employee.getEmailAddresses().add("jane.doe@example.com");

        EmailAddress emailAddress2 = new EmailAddress();
        emailAddress2.setUserID(CONTROL_EMAIL_ADDRESS_2_USER_ID);
        emailAddress2.setDomain(CONTROL_EMAIL_ADDRESS_2_DOMAIN);
        employee.getEmailAddresses().add(emailAddress2);
        
        employee.getMailingAddresses().add("1 Any Street, Ottawa, Ontario, A1B 2C3");

        MailingAddress mailingAddress2 = new MailingAddress();
        mailingAddress2.setStreet(CONTROL_MAILING_ADDRESS_2_STREET);
        mailingAddress2.setCity(CONTROL_MAILING_ADDRESS_2_CITY);
        mailingAddress2.setProvince(CONTROL_MAILING_ADDRESS_2_PROVINCE);
        mailingAddress2.setPostalCode(CONTROL_MAILING_ADDRESS_2_POSTAL_CODE);
        employee.getMailingAddresses().add(mailingAddress2);

        employee.getMailingAddresses().add(new Integer(5));
        
        return employee;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.withgroupingelement.CompositeCollectionWithGroupingTextTestCases" });
    }
}
