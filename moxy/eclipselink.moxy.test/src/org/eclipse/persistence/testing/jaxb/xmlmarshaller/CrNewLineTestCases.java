/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CrNewLineTestCases extends JAXBWithJSONTestCases {

    private static final String XML = "org/eclipse/persistence/testing/jaxb/xmlmarshaller/crNewLine.xml";
    private static final String JSON = "org/eclipse/persistence/testing/jaxb/xmlmarshaller/crNewLine.json";

    public CrNewLineTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {CrNewLineRoot.class});
        setControlDocument(XML);
        setControlJSON(JSON);
    }

    @Override
    protected CrNewLineRoot getControlObject() {
        CrNewLineRoot control = new CrNewLineRoot();
        control.slashRAttribute = "Hello\rWorld";
        control.slashNAttribute = "Hello\nWorld";
        control.slashNslashRAttribute = "Hello\n\rWorld";
        control.slashRslashNAttribute = "Hello\r\nWorld";
        control.slashRElement = "Hello\rWorld";
        control.slashNElement = "Hello\nWorld";
        control.slashNslashRElement = "Hello\n\rWorld";
        control.slashRslashNElement = "Hello\r\nWorld";
        return control;
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

    @Override
    public void testObjectToContentHandler() throws Exception {
    }

    @Override
    public void testObjectToXMLDocument() throws Exception {
    }

    @Override
    public void testObjectToXMLEventWriter() throws Exception {
        // XMLEventWriter is responsible for the encoding.  There is too much
        // variability between providers to include this test case.
    }

    @Override
    public void testObjectToXMLStreamWriter() throws Exception {
        // XMLStreamWriter is responsible for the encoding.  There is too much
        // variability between providers to include this test case.
    }

    @Override
    public void testObjectToXMLStreamWriterRecord() throws Exception {
        // XMLStreamWriter is responsible for the encoding.  There is too much
        // variability between providers to include this test case.
    }

}
