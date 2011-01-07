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
package org.eclipse.persistence.testing.oxm.descriptor.rootelement.identifiedbyname;

import java.io.InputStream;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.EmailAddress;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.MailingAddress;

public class RootElementIdentifiedByNameTestCases extends OXTestCase {
    private final static String XML_RESOURCE_EMAIL = "org/eclipse/persistence/testing/oxm/descriptor/rootelement/identifiedbyname/EmailAddress.xml";
    private final static String XML_RESOURCE_MAILING = "org/eclipse/persistence/testing/oxm/descriptor/rootelement/identifiedbyname/MailingAddress.xml";
    private final static String XML_RESOURCE_BILLING = "org/eclipse/persistence/testing/oxm/descriptor/rootelement/identifiedbyname/BillingAddress.xml";
    private final static String XML_RESOURCE_XSITYPE= "org/eclipse/persistence/testing/oxm/descriptor/rootelement/identifiedbyname/XSITypeAddress.xml";
    private XMLContext xmlContext;
    private XMLUnmarshaller xmlUnmarshaller;

    public RootElementIdentifiedByNameTestCases(String name) {
        super(name);
        xmlContext = getXMLContext(new RootElementIdentifiedByNameProject());
        xmlUnmarshaller = xmlContext.createUnmarshaller();
    }

    public void testReadEmailAddress() {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_EMAIL);
            EmailAddress testObject = (EmailAddress)xmlUnmarshaller.unmarshal(inputStream);
        } catch (ClassCastException e) {
            fail();
        }
    }

    public void testReadMailingAddress() {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_MAILING);
            MailingAddress testObject = (MailingAddress)xmlUnmarshaller.unmarshal(inputStream);
        } catch (ClassCastException e) {
            fail();
        }
    }

    public void testReadBillingAddress() {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_BILLING);
            Object testObject = xmlUnmarshaller.unmarshal(inputStream);
            fail();
        } catch (XMLMarshalException e) {
            // PASS - an XMLMarshalException should be caught
        }
    }
    
    public void testReadBasedOnXSIType() {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_XSITYPE);
            Object testObject = xmlUnmarshaller.unmarshal(inputStream);
            assertTrue(testObject instanceof XMLRoot);
            assertTrue(((XMLRoot)testObject).getObject().getClass().equals(EmailAddress.class));
        } catch (XMLMarshalException e) {
        	fail();
        }
    }
        
}
