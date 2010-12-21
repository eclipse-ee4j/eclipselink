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
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

import java.io.InputStream;
import org.w3c.dom.Document;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class ContactAsNestedCdnAddressTestCases extends XMLMappingTestCases {
    private static final String READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/customer_with_contact_as_cdnaddress_noxsi.xml";
    private static final String WRITE_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/customer_with_contact_noxsi.xml";
    
    public ContactAsNestedCdnAddressTestCases(String name) throws Exception {
        super(name);
        setProject(new TypeProject());
        setControlDocument(READ_DOC);
    }

    public Document getWriteControlDocument() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(WRITE_DOC);
        Document controlDocument = null;
        try {
            controlDocument = parser.parse(inputStream);
            removeEmptyTextNodes(controlDocument);
            inputStream.close();
        } catch (Exception ex) {}
        return controlDocument;
    }
    
    public Object getControlObject() {
		Customer cust = new Customer();
        ContactMethod cm = new ContactMethod();
        cm.setId("123");
		cust.setContact(cm);
        return cust;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.ContactAsNestedCdnAddressTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
