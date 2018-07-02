/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     rbarkhouse - 2009-06-16 10:40:00 - initial implementation
package org.eclipse.persistence.testing.jaxb.simpledocument;


import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests the various combinations of absent/empty elements and their corresponding null/empty string
 * values when unmarshalled.
 */
public class SimpleDocumentWhitespaceNullTestCases extends JAXBWithJSONTestCases{

    private final static String ELEMENT_WITH_VALUE      = "org/eclipse/persistence/testing/jaxb/simpledocument/elemWithValue.xml";
    private final static String ABSENT_ELEMENT          = "org/eclipse/persistence/testing/jaxb/simpledocument/absentElem.xml";
    private final static String EMPTY_ELEMENT           = "org/eclipse/persistence/testing/jaxb/simpledocument/emptyElem.xml";
    private final static String ELEMENT_WITH_WHITESPACE = "org/eclipse/persistence/testing/jaxb/simpledocument/elemWithWhitespace.xml";
    private final static String JSON_ELEMENT_WITH_VALUE      = "org/eclipse/persistence/testing/jaxb/simpledocument/elemWithValue.json";
    private final static String JSON_ABSENT_ELEMENT          = "org/eclipse/persistence/testing/jaxb/simpledocument/absentElem.json";
    private final static String JSON_EMPTY_ELEMENT           = "org/eclipse/persistence/testing/jaxb/simpledocument/emptyElem.json";
    private final static String JSON_ELEMENT_WITH_WHITESPACE = "org/eclipse/persistence/testing/jaxb/simpledocument/elemWithWhitespace.json";
    private final static int TEST_ELEM_WITH_VALUE       = 1;
    private final static int TEST_ABSENT_ELEM           = 2;
    private final static int TEST_EMPTY_ELEM            = 3;
    private final static int TEST_ELEM_WITH_WHITESPACE  = 4;

    private int state = TEST_ELEM_WITH_VALUE;

    public SimpleDocumentWhitespaceNullTestCases(String name) throws Exception {
        super(name);
        setControlDocument(ELEMENT_WITH_VALUE);
        setControlJSON(JSON_ELEMENT_WITH_VALUE);
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

    public void testElemWithValueJSON() throws Exception {
        this.state = TEST_ELEM_WITH_VALUE;
        this.controlJSONLocation = JSON_ELEMENT_WITH_VALUE;
        super.testJSONUnmarshalFromURL();

        this.state = TEST_ELEM_WITH_VALUE;
    }

    public void testAbsentElemJSON() throws Exception {
        this.state = TEST_ABSENT_ELEM;

        this.controlJSONLocation = JSON_ABSENT_ELEMENT;
        super.testJSONUnmarshalFromURL();

        this.state = TEST_ELEM_WITH_VALUE;
    }

    public void testEmptyElemJSON() throws Exception {
        this.state = TEST_EMPTY_ELEM;

        this.controlJSONLocation = JSON_EMPTY_ELEMENT;
        super.testJSONUnmarshalFromURL();
        this.state = TEST_ELEM_WITH_VALUE;
    }

    public void testElemWithWhitespaceJSON() throws Exception {
        this.state = TEST_ELEM_WITH_WHITESPACE;

        this.controlJSONLocation = JSON_ELEMENT_WITH_WHITESPACE;
        super.testJSONUnmarshalFromURL();

        this.state = TEST_ELEM_WITH_VALUE;
    }
}
