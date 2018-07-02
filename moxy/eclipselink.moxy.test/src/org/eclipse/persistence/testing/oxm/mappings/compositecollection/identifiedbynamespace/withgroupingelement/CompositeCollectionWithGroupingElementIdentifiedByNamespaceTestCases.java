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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.withgroupingelement;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.MailingAddress;

public class CompositeCollectionWithGroupingElementIdentifiedByNamespaceTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/identifiedbynamespace/withgroupingelement/CompositeCollectionWithGroupingElementIdentifiedByNamespace.xml";
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

    public CompositeCollectionWithGroupingElementIdentifiedByNamespaceTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new CompositeCollectionWithGroupingElementIdentifiedByNamespaceProject());
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
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.withgroupingelement.CompositeCollectionWithGroupingElementIdentifiedByNamespaceTestCases" });
    }
}
