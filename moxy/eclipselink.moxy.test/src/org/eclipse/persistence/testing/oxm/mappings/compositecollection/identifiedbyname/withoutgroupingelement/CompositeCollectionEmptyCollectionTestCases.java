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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname.withoutgroupingelement;

import java.util.Vector;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.MailingAddress;

public class CompositeCollectionEmptyCollectionTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/identifiedbyname/withoutgroupingelement/CompositeCollectionEmptyCollection.xml";
    private final static int CONTROL_EMPLOYEE_ID = 123;
    private final static String CONTROL_EMAIL_ADDRESS_1_USER_ID = "jane.doe";
    private final static String CONTROL_EMAIL_ADDRESS_1_DOMAIN = "example.com";
    private final static String CONTROL_EMAIL_ADDRESS_2_USER_ID = "jdoe";
    private final static String CONTROL_EMAIL_ADDRESS_2_DOMAIN = "test.com";

    public CompositeCollectionEmptyCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new CompositeCollectionWithoutGroupingElementIdentifiedByNameProject());
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.setID(CONTROL_EMPLOYEE_ID);

        EmailAddress emailAddress1 = new EmailAddress();
        emailAddress1.setUserID(CONTROL_EMAIL_ADDRESS_1_USER_ID);
        emailAddress1.setDomain(CONTROL_EMAIL_ADDRESS_1_DOMAIN);
        employee.getEmailAddresses().add(emailAddress1);

        EmailAddress emailAddress2 = new EmailAddress();
        emailAddress2.setUserID(CONTROL_EMAIL_ADDRESS_2_USER_ID);
        emailAddress2.setDomain(CONTROL_EMAIL_ADDRESS_2_DOMAIN);
        employee.getEmailAddresses().add(emailAddress2);

        employee.setMailingAddresses(new Vector());

        return employee;
    }

    public void testXMLToObjectFromInputStream() throws Exception {
        //do nothing
    }

    public void testXMLToObjectFromURL() throws Exception {
        //do nothing
    }

    /*
        public void testXMLToObjectFromDocument() throws Exception {
            //do nothing
        }
            */
}
