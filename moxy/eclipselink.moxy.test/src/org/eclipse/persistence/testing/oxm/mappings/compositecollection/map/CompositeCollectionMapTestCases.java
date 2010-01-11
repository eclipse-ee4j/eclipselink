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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.map;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeCollectionMapTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/map/CompositeCollectionMap.xml";
    private final static int CONTROL_EMPLOYEE_ID = 123;
    private final static String CONTROL_MAILING_ADDRESS_1_TYPE = "home";
    private final static String CONTROL_MAILING_ADDRESS_1_STREET = "1 Any Street";
    private final static String CONTROL_MAILING_ADDRESS_1_CITY = "Ottawa";
    private final static String CONTROL_MAILING_ADDRESS_1_PROVINCE = "Ontario";
    private final static String CONTROL_MAILING_ADDRESS_1_POSTAL_CODE = "A1B 2C3";
    private final static String CONTROL_MAILING_ADDRESS_2_TYPE = "work";
    private final static String CONTROL_MAILING_ADDRESS_2_STREET = "2 Autre Rue.";
    private final static String CONTROL_MAILING_ADDRESS_2_CITY = "Gatineau";
    private final static String CONTROL_MAILING_ADDRESS_2_PROVINCE = "Quebec";
    private final static String CONTROL_MAILING_ADDRESS_2_POSTAL_CODE = "X1Y 2Z3";

    public CompositeCollectionMapTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new CompositeCollectionMapProject());
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.setID(CONTROL_EMPLOYEE_ID);

        MailingAddress mailingAddress1 = new MailingAddress();
        mailingAddress1.setAddressType(CONTROL_MAILING_ADDRESS_1_TYPE);
        mailingAddress1.setTest("456");
        AddressInfo info = new AddressInfo();
        info.setStreet(CONTROL_MAILING_ADDRESS_1_STREET);
        info.setCity(CONTROL_MAILING_ADDRESS_1_CITY);
        info.setProvince(CONTROL_MAILING_ADDRESS_1_PROVINCE);
        info.setPostalCode(CONTROL_MAILING_ADDRESS_1_POSTAL_CODE);
        mailingAddress1.setAddressInfo(info);
        employee.addMailingAddress(mailingAddress1);

        MailingAddress mailingAddress2 = new MailingAddress();
        mailingAddress2.setAddressType(CONTROL_MAILING_ADDRESS_2_TYPE);
        mailingAddress2.setTest("123");
        AddressInfo info2 = new AddressInfo();
        info2.setStreet(CONTROL_MAILING_ADDRESS_2_STREET);
        info2.setCity(CONTROL_MAILING_ADDRESS_2_CITY);
        info2.setProvince(CONTROL_MAILING_ADDRESS_2_PROVINCE);
        info2.setPostalCode(CONTROL_MAILING_ADDRESS_2_POSTAL_CODE);
        mailingAddress2.setAddressInfo(info2);
        employee.addMailingAddress(mailingAddress2);

        return employee;
    }
}
