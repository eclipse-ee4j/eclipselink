/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * mmacivor - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbelement.enumeration;

import java.io.InputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import junit.textui.TestRunner;

import org.eclipse.persistence.testing.jaxb.jaxbelement.JAXBElementTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.Person;
import org.w3c.dom.Document;

public class JAXBElementEnumTestCases extends JAXBElementTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/enum/coin.xml";
    private final static String CONTROL_ELEMENT_NAME = "coin";
    private final static Class targetClass = Coin.class;

    public JAXBElementEnumTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setTargetClass(targetClass);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.jaxbelement.complex.JAXBElementComplexTestCases" };
        TestRunner.main(arguments);
    }

    public Document getWriteControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/jaxbelement/enum/coin.xml");
        Document writeControlDocument = parser.parse(inputStream);
        removeEmptyTextNodes(controlDocument);
        inputStream.close();
        return writeControlDocument;
    }

    public Object getControlObject() {
        Coin coin = Coin.NICKEL;

        return new JAXBElement(new QName(CONTROL_NAMESPACE_URI, "coin"), targetClass, coin);
    } 
    
    @Override
    public Class[] getClasses() {
        return new Class[]{Coin.class};
    }
}