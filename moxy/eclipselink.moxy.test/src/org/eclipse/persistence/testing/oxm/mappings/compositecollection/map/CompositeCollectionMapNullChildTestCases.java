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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.map;

import java.io.InputStream;
import junit.textui.TestRunner;
import org.w3c.dom.Document;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeCollectionMapNullChildTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/map/CompositeCollectionMapNullChild.xml";
		private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/map/CompositeCollectionMapNullChild_Write.xml";
    private final static int CONTROL_EMPLOYEE_ID = 123;
    private final static String CONTROL_MAILING_ADDRESS_1_TYPE = "home";
		private final static String CONTROL_MAILING_ADDRESS_2_TYPE = "work";

    public CompositeCollectionMapNullChildTestCases(String name) throws Exception {
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

        employee.addMailingAddress(mailingAddress1);

        MailingAddress mailingAddress2 = new MailingAddress();
        mailingAddress2.setAddressType(CONTROL_MAILING_ADDRESS_2_TYPE);
        mailingAddress2.setTest("123");

        employee.addMailingAddress(mailingAddress2);



        return employee;
    }

    protected Document getWriteControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_WRITE_RESOURCE);        
        Document writeControlDocument = parser.parse(inputStream);
        removeEmptyTextNodes(writeControlDocument);
        inputStream.close();
        return writeControlDocument;
    }
    
     public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositecollection.map.CompositeCollectionMapNullChildTestCases" };
        TestRunner.main(arguments);
    }
}
