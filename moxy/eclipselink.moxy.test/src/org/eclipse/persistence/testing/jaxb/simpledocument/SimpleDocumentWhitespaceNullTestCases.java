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
 *     rbarkhouse - 2009-06-16 10:40:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.simpledocument;

import java.io.InputStream;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;

/**
 * Tests the various combinations of absent/empty elements and their corresponding null/empty string 
 * values when unmarshalled.
 */
public class SimpleDocumentWhitespaceNullTestCases extends JAXBTestCases {

    private final static String ELEMENT_WITH_VALUE      = "org/eclipse/persistence/testing/jaxb/simpledocument/elemWithValue.xml";
    private final static String ABSENT_ELEMENT          = "org/eclipse/persistence/testing/jaxb/simpledocument/absentElem.xml";
    private final static String EMPTY_ELEMENT           = "org/eclipse/persistence/testing/jaxb/simpledocument/emptyElem.xml";
    private final static String ELEMENT_WITH_WHITESPACE = "org/eclipse/persistence/testing/jaxb/simpledocument/elemWithWhitespace.xml";

    private final static int TEST_ELEM_WITH_VALUE       = 1;
    private final static int TEST_ABSENT_ELEM           = 2;
    private final static int TEST_EMPTY_ELEM            = 3;
    private final static int TEST_ELEM_WITH_WHITESPACE  = 4;

    private int state = TEST_ELEM_WITH_VALUE;

    public SimpleDocumentWhitespaceNullTestCases(String name) throws Exception {
        super(name);
        setControlDocument(ELEMENT_WITH_VALUE);
        Class[] classes = new Class[1];
        classes[0] = Root.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        Root controlRoot = new Root();
        switch (state) {
            case TEST_ELEM_WITH_VALUE:
                controlRoot.id = "123";
                break;
            case TEST_ABSENT_ELEM:
                controlRoot.id = null;
                break;
            case TEST_EMPTY_ELEM:
                controlRoot.id = "";
                break;
            case TEST_ELEM_WITH_WHITESPACE:
                controlRoot.id = "     ";
                break;
        }
        return controlRoot;
    }

    public void testElemWithValue() throws Exception {
        this.state = TEST_ELEM_WITH_VALUE;
        this.resourceName = ELEMENT_WITH_VALUE;

        super.testXMLToObjectFromURL();

        this.state = TEST_ELEM_WITH_VALUE;
    }

    public void testAbsentElem() throws Exception {
        this.state = TEST_ABSENT_ELEM;
        this.resourceName = ABSENT_ELEMENT;

        super.testXMLToObjectFromURL();

        this.state = TEST_ELEM_WITH_VALUE;
    }

    public void testEmptyElem() throws Exception {
        this.state = TEST_EMPTY_ELEM;
        this.resourceName = EMPTY_ELEMENT;

        super.testXMLToObjectFromURL();

        this.state = TEST_ELEM_WITH_VALUE;
    }

    public void testElemWithWhitespace() throws Exception {
        this.state = TEST_ELEM_WITH_WHITESPACE;
        this.resourceName = ELEMENT_WITH_WHITESPACE;

        super.testXMLToObjectFromURL();

        this.state = TEST_ELEM_WITH_VALUE;
    }

}