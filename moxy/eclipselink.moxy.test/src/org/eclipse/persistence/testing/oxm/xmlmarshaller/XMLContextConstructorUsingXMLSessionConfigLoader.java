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
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.textui.TestRunner;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.w3c.dom.Document;

public class XMLContextConstructorUsingXMLSessionConfigLoader extends OXTestCase {
    public static String sessionpath = "org/eclipse/persistence/testing/oxm/xmlmarshaller";
    private final static int CONTROL_EMPLOYEE_ID = 123;
    private final static String CONTROL_EMAIL_ADDRESS_USER_ID = "jane.doe";
    private final static String CONTROL_EMAIL_ADDRESS_DOMAIN = "example.com";
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/mySessions.xml";
    private final static String XML_RESOURCE_INVALID = "invalid/path/mySessions.xml";
    private Object controlObject;
    private Document controlDocument;
    private DocumentBuilder parser;
    private XMLMarshaller marshaller;
    protected static String SESSION_NAME = "XMLMarshallerTestSession";
    protected static String SESSION_NAME2 = "XMLMarshallerTestSession2";

    public XMLContextConstructorUsingXMLSessionConfigLoader(String name) {
        super(name);
    }
    
    public void setUp()
    {
      SessionManager.setManager(new SessionManager());
    }
    // resource path.
    public void testMultipleSessionNames() {
        String sessionName = SESSION_NAME + ":" + SESSION_NAME2;

        //XMLSessionConfigLoader loader = new XMLSessionConfigLoader(XML_RESOURCE);
        XMLContext context = new XMLContext(sessionName, getClass().getClassLoader(), XML_RESOURCE);

        assertNotNull(context);

        XMLMarshaller marshaller = context.createMarshaller();
        StringWriter writer1 = new StringWriter();
        marshaller.marshal(new Employee(), writer1);

        StringWriter writer2 = new StringWriter();
        marshaller.marshal(new Car(), writer2);

    }

    // purpose: test constructor without arguement of classloader to allow input of 
    // resource path.
    public void testMultipleSessionNamesWithoutLoader() {
        String sessionName = SESSION_NAME + ":" + SESSION_NAME2;
        //XMLSessionConfigLoader loader = new XMLSessionConfigLoader(XML_RESOURCE);
        XMLContext context = new XMLContext(sessionName, XML_RESOURCE);

        assertNotNull(context);

        XMLMarshaller marshaller = context.createMarshaller();
        StringWriter writer1 = new StringWriter();
        marshaller.marshal(new Employee(), writer1);

        StringWriter writer2 = new StringWriter();
        marshaller.marshal(new Car(), writer2);

    }
    
    public void testMultipleSessionNamesWithNullResource() {
        String sessionName = SESSION_NAME + ":" + SESSION_NAME2;
        //XMLSessionConfigLoader loader = new XMLSessionConfigLoader(XML_RESOURCE);
        XMLContext context = new XMLContext(sessionName, (String)null);

        assertNotNull(context);

        XMLMarshaller marshaller = context.createMarshaller();
        StringWriter writer1 = new StringWriter();
        marshaller.marshal(new Employee(), writer1);

        StringWriter writer2 = new StringWriter();
        marshaller.marshal(new Car(), writer2);

    }

    // purpose: test invalid resource path.
    public void testMultipleSessionNamesInvalidPath() {
        String sessionName = SESSION_NAME + ":" + SESSION_NAME2;
        try {
            //XMLSessionConfigLoader loader = new XMLSessionConfigLoader(XML_RESOURCE_INVALID);
            XMLContext context = new XMLContext(sessionName, XML_RESOURCE_INVALID);
            this.fail("ValidationException should be thrown.");
        } catch (ValidationException e) {
            //e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLContextConstructorUsingXMLSessionConfigLoader" };
        TestRunner.main(arguments);
    }
}
