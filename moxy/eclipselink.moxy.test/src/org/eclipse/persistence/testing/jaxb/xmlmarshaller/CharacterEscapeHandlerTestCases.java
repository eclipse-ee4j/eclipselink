/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 10 February 2012 - 2.3.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CharacterEscapeHandlerTestCases extends JAXBWithJSONTestCases {

    private final static String CHAR_ESCAPE = "org/eclipse/persistence/testing/jaxb/xmlmarshaller/charEscape.xml";
    private final static String CHAR_ESCAPE_JSON = "org/eclipse/persistence/testing/jaxb/xmlmarshaller/charEscape.json";

    public CharacterEscapeHandlerTestCases(String name) throws Exception {
        super(name);
        setControlDocument(CHAR_ESCAPE);
        setControlJSON(CHAR_ESCAPE_JSON);
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
        jaxbMarshaller.setProperty(MarshallerProperties.CHARACTER_ESCAPE_HANDLER, new CustomCharacterEscapeHandler());
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
    public void testObjectToXMLStreamWriterRecord() throws Exception {
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
