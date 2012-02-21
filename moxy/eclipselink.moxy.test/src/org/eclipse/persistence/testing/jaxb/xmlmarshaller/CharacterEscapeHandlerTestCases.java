/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 10 February 2012 - 2.3.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class CharacterEscapeHandlerTestCases extends JAXBTestCases {

    private final static String CHAR_ESCAPE = "org/eclipse/persistence/testing/jaxb/xmlmarshaller/charEscape.xml";

    public CharacterEscapeHandlerTestCases(String name) throws Exception {
        super(name);
        setControlDocument(CHAR_ESCAPE);
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        Employee emp = new Employee();
        emp.setName("elem*");
        emp.setEmpCode("attrib*");
        return emp;
    }

    public void setUp() throws Exception {
        super.setUp();
        jaxbMarshaller.setProperty(JAXBMarshaller.CHARACTER_ESCAPE_HANDLER, new CustomCharacterEscapeHandler());
    }

    public boolean isUnmarshalTest() {
        return false;
    }

    /**
     * CharacterEscapeHandler is not supported for this marshal target,
     * so just pass.
     */
    public void testObjectToXMLStreamWriter() throws Exception {
    }

    /**
     * CharacterEscapeHandler is not supported for this marshal target,
     * so just pass.
     */
    public void testObjectToXMLEventWriter() throws Exception {
    }

    /**
     * CharacterEscapeHandler is not supported for this marshal target,
     * so just pass.
     */
    public void testObjectToContentHandler() throws Exception {
    }

    /**
     * CharacterEscapeHandler is not supported for this marshal target,
     * so just pass.
     */
    public void testObjectToXMLDocument() throws Exception {
    }

}