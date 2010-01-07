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
package org.eclipse.persistence.testing.jaxb.jaxbelement.simple;

import java.io.InputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.jaxb.jaxbelement.JAXBElementTestCases;
import org.w3c.dom.Document;

public class JAXBElementSimpleTestCases extends JAXBElementTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/simple/employee.xml";
    private final static String CONTROL_ELEMENT_NAME = "employee-name";
    private final static Class targetClass = String.class;

    public JAXBElementSimpleTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setTargetClass(targetClass);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.jaxbelement.simple.JAXBElementSimpleTestCases" };
        TestRunner.main(arguments);
    }

    public Object getControlObject() {        
        return new JAXBElement(new QName(CONTROL_NAMESPACE_URI, CONTROL_ELEMENT_NAME), targetClass, CONTROL_PERSON_NAME);
    }

    public Document getWriteControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/jaxbelement/simple/employee-write.xml");
        Document writeControlDocument = parser.parse(inputStream);
        removeEmptyTextNodes(controlDocument);
        inputStream.close();
        return writeControlDocument;
    }
}
