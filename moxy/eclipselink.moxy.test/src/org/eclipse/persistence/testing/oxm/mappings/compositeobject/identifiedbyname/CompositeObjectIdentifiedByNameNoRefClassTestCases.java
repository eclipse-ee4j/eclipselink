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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname;

import java.io.InputStream;
import javax.xml.namespace.QName;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.MailingAddress;

public class CompositeObjectIdentifiedByNameNoRefClassTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/identifiedbyname/CompositeObjectNoRefClass.xml";
    private final static int CONTROL_EMPLOYEE_ID = 123;
    private final static String CONTROL_EMAIL_ADDRESS_USER_ID = "jane.doe";
    private final static String CONTROL_EMAIL_ADDRESS_DOMAIN = "example.com";
    private final static String CONTROL_MAILING_ADDRESS_STREET = "1 Any Street";
    private final static String CONTROL_MAILING_ADDRESS_CITY = "Ottawa";
    private final static String CONTROL_MAILING_ADDRESS_PROVINCE = "Ontario";
    private final static String CONTROL_MAILING_ADDRESS_POSTAL_CODE = "A1B 2C3";

    public CompositeObjectIdentifiedByNameNoRefClassTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new CompositeObjectIdentifiedByNameProject();

        ((XMLCompositeObjectMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("emailAddress")).setReferenceClass(null);
        ((XMLCompositeObjectMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("emailAddress")).setReferenceClassName(null);
        ((XMLCompositeObjectMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("mailingAddress")).setReferenceClass(null);
        ((XMLCompositeObjectMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("mailingAddress")).setReferenceClassName(null);
        ((XMLDescriptor)p.getDescriptor(EmailAddress.class)).setDefaultRootElement("emailAddressType");

        QName qname = new QName("", "mailingAddressType");//set this to schemaContext

        ((XMLField)((XMLCompositeObjectMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("mailingAddress")).getField()).setLeafElementType(qname);

        setProject(p);
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
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname.CompositeObjectIdentifiedByNameNoRefClassTestCases" });
    }

    public void testNoDescriptorFound() throws Exception {
        String errorResource = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/identifiedbyname/CompositeObjectNoRefClassError.xml";
        InputStream instream = ClassLoader.getSystemResourceAsStream(errorResource);

        try {
            Object testObject = xmlUnmarshaller.unmarshal(instream);
            instream.close();
            //xmlToObjectTest(testObject);
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
        String errorResource = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/identifiedbyname/CompositeObjectNoRefClassError2.xml";
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
