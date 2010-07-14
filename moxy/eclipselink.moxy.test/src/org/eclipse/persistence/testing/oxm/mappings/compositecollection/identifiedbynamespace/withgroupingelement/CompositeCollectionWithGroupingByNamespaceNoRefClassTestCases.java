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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.withgroupingelement;

import java.io.InputStream;
import javax.xml.namespace.QName;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.MailingAddress;

public class CompositeCollectionWithGroupingByNamespaceNoRefClassTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/identifiedbynamespace/withgroupingelement/CompositeCollectionWithGroupingElementNoRefClass.xml";
    private final static String CONTROL_EMAIL_ADDRESS_1_USER_ID = "jane.doe";
    private final static String CONTROL_EMAIL_ADDRESS_1_DOMAIN = "example.com";
    private final static String CONTROL_EMAIL_ADDRESS_2_USER_ID = "jdoe";
    private final static String CONTROL_EMAIL_ADDRESS_2_DOMAIN = "test.com";
    private final static String CONTROL_MAILING_ADDRESS_1_STREET = "1 Any Street";
    private final static String CONTROL_MAILING_ADDRESS_1_CITY = "Ottawa";
    private final static String CONTROL_MAILING_ADDRESS_1_PROVINCE = "Ontario";
    private final static String CONTROL_MAILING_ADDRESS_1_POSTAL_CODE = "A1B 2C3";
    private final static String CONTROL_MAILING_ADDRESS_2_STREET = "2 Autre Rue.";
    private final static String CONTROL_MAILING_ADDRESS_2_CITY = "Gatineau";
    private final static String CONTROL_MAILING_ADDRESS_2_PROVINCE = "Quebec";
    private final static String CONTROL_MAILING_ADDRESS_2_POSTAL_CODE = "X1Y 2Z3";

    public CompositeCollectionWithGroupingByNamespaceNoRefClassTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new CompositeCollectionWithGroupingElementIdentifiedByNamespaceProject();

        ((XMLCompositeCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("emailAddresses")).setReferenceClass(null);
        ((XMLCompositeCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("emailAddresses")).setReferenceClassName(null);

        ((XMLCompositeCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("mailingAddresses")).setReferenceClass(null);
        ((XMLCompositeCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("mailingAddresses")).setReferenceClassName(null);

        ((XMLDescriptor)p.getDescriptor(EmailAddress.class)).setDefaultRootElement("email:addressType");

        QName qname = new QName("www.example.com/some-dir/mailing.xsd", "addressType");

        ((XMLField)((XMLCompositeCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("mailingAddresses")).getField()).setLeafElementType(qname);
        ((XMLDescriptor)p.getDescriptor(Employee.class)).getNonNullNamespaceResolver().put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);

        setProject(p);

    }

    protected Object getControlObject() {
        Employee employee = new Employee();

        EmailAddress emailAddress1 = new EmailAddress();
        emailAddress1.setUserID(CONTROL_EMAIL_ADDRESS_1_USER_ID);
        emailAddress1.setDomain(CONTROL_EMAIL_ADDRESS_1_DOMAIN);
        employee.getEmailAddresses().add(emailAddress1);

        EmailAddress emailAddress2 = new EmailAddress();
        emailAddress2.setUserID(CONTROL_EMAIL_ADDRESS_2_USER_ID);
        emailAddress2.setDomain(CONTROL_EMAIL_ADDRESS_2_DOMAIN);
        employee.getEmailAddresses().add(emailAddress2);

        MailingAddress mailingAddress1 = new MailingAddress();
        mailingAddress1.setStreet(CONTROL_MAILING_ADDRESS_1_STREET);
        mailingAddress1.setCity(CONTROL_MAILING_ADDRESS_1_CITY);
        mailingAddress1.setProvince(CONTROL_MAILING_ADDRESS_1_PROVINCE);
        mailingAddress1.setPostalCode(CONTROL_MAILING_ADDRESS_1_POSTAL_CODE);
        employee.getMailingAddresses().add(mailingAddress1);

        MailingAddress mailingAddress2 = new MailingAddress();
        mailingAddress2.setStreet(CONTROL_MAILING_ADDRESS_2_STREET);
        mailingAddress2.setCity(CONTROL_MAILING_ADDRESS_2_CITY);
        mailingAddress2.setProvince(CONTROL_MAILING_ADDRESS_2_PROVINCE);
        mailingAddress2.setPostalCode(CONTROL_MAILING_ADDRESS_2_POSTAL_CODE);
        employee.getMailingAddresses().add(mailingAddress2);

        return employee;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.withgroupingelement.CompositeCollectionWithGroupingByNamespaceNoRefClassTestCases" });
    }

    public void testNoDescriptorFound() throws Exception {
        String errorResource = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/identifiedbynamespace/withgroupingelement/CompositeCollectionWithGroupingElementNoRefClassError.xml";
        InputStream instream = ClassLoader.getSystemResourceAsStream(errorResource);

        try {
            Object testObject = xmlUnmarshaller.unmarshal(instream);
            instream.close();
            xmlToObjectTest(testObject);
        } catch (XMLMarshalException e) {
            if (e.getErrorCode() == XMLMarshalException.UNKNOWN_XSI_TYPE) {
                return;
            } else {
                fail("an error should have occurred");
            }
        }
        fail("an error should have occurred");
    }

    public void testNoDescriptorFound2() throws Exception {
        String errorResource = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/identifiedbynamespace/withgroupingelement/CompositeCollectionWithGroupingElementNoRefClassError2.xml";
        InputStream instream = ClassLoader.getSystemResourceAsStream(errorResource);
        try {
            Object testObject = xmlUnmarshaller.unmarshal(instream);
            instream.close();
            xmlToObjectTest(testObject);
        } catch (XMLMarshalException e) {
            if (e.getMessage().contains("No descriptor found while unmarshalling element mapped to attribute")) {
                return;
            } else {
                fail("an error should have occurred");
            }
        }
        fail("an error should have occurred");
    }
}
