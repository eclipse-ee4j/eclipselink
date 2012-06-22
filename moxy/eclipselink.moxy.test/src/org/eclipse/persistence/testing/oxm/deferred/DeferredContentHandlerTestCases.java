/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.deferred;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import junit.textui.TestRunner;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DeferredContentHandlerTestCases extends OXTestCase {
    private SAXParser saxParser;
    private TestDeferredContentHandler tdch;

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.deferred.DeferredContentHandlerTestCases" };
        TestRunner.main(arguments);
    }

    public DeferredContentHandlerTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        saxParserFactory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        saxParser = saxParserFactory.newSAXParser();               
        tdch = new TestDeferredContentHandler(null, saxParser.getXMLReader(), saxParser.getXMLReader().getContentHandler());
        saxParser.getXMLReader().setContentHandler(tdch);
    }

    public void testEmptyElement() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/deferred/emptyElements.xml");
        saxParser.getXMLReader().parse(new InputSource(inputStream));
        assertEquals(1, tdch.PROCESS_EMPTY);
        assertEquals(0, tdch.PROCESS_EMPTY_WITH_ATTRIBUTES);
        assertEquals(0, tdch.PROCESS_SIMPLE_ELEMENT);
        assertEquals(0, tdch.PROCESS_COMPLEX_ELEMENT);
    }

    public void testNonNullComplexElement() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/deferred/complexElementWithContent.xml");
        saxParser.getXMLReader().parse(new InputSource(inputStream));
        assertEquals(1, tdch.PROCESS_COMPLEX_ELEMENT);
        assertEquals(0, tdch.PROCESS_EMPTY);
        assertEquals(0, tdch.PROCESS_EMPTY_WITH_ATTRIBUTES);
        assertEquals(0, tdch.PROCESS_SIMPLE_ELEMENT);
    }

    public void testNonNullSimpleElement() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/deferred/simpleElementWithContent.xml");
        saxParser.getXMLReader().parse(new InputSource(inputStream));
        assertEquals(1, tdch.PROCESS_SIMPLE_ELEMENT);
        assertEquals(0, tdch.PROCESS_COMPLEX_ELEMENT);
        assertEquals(0, tdch.PROCESS_EMPTY);
        assertEquals(0, tdch.PROCESS_EMPTY_WITH_ATTRIBUTES);
    }

    private static String EMPTY_1 = "<root/>";
    private static String EMPTY_2 = "<root></root>";
    private static String EMPTY_3 = "<root xmlns='urn:foo'/>";
    private static String EMPTY_4 = "<root xmlns:foo='urn:foo'/>";
    private static String EMPTY_5 = "<root xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:type='xsd:float'/>";
    private static String SIMPLE_1 = "<root>FOO</root>";
    private static String COMPLEX_1 = "<root><child/></root>";
    private static String COMPLEX_2 = "<root att='FOO'/>";

    public void testEmpty1() throws IOException, SAXException {
        saxParser.getXMLReader().parse(new InputSource(new StringReader(EMPTY_1)));
        assertEquals(1, tdch.PROCESS_EMPTY);
        assertEquals(0, tdch.PROCESS_EMPTY_WITH_ATTRIBUTES);
        assertEquals(0, tdch.PROCESS_SIMPLE_ELEMENT);
        assertEquals(0, tdch.PROCESS_COMPLEX_ELEMENT);
    }

    public void testEmpty2() throws IOException, SAXException {
        saxParser.getXMLReader().parse(new InputSource(new StringReader(EMPTY_2)));
        assertEquals(1, tdch.PROCESS_EMPTY);
        assertEquals(0, tdch.PROCESS_EMPTY_WITH_ATTRIBUTES);
        assertEquals(0, tdch.PROCESS_SIMPLE_ELEMENT);
        assertEquals(0, tdch.PROCESS_COMPLEX_ELEMENT);
    }

    public void testEmpty3() throws IOException, SAXException {
        try {
            saxParser.getXMLReader().parse(new InputSource(new StringReader(EMPTY_3)));
        } catch(Exception e) {
        }
        assertEquals(1, tdch.PROCESS_EMPTY);
        assertEquals(0, tdch.PROCESS_EMPTY_WITH_ATTRIBUTES);
        assertEquals(0, tdch.PROCESS_SIMPLE_ELEMENT);
        assertEquals(0, tdch.PROCESS_COMPLEX_ELEMENT);
    }

    public void testEmpty4() throws IOException, SAXException {
        try {
            saxParser.getXMLReader().parse(new InputSource(new StringReader(EMPTY_4)));
        } catch(Exception e) {
        }
        assertEquals(1, tdch.PROCESS_EMPTY);
        assertEquals(0, tdch.PROCESS_EMPTY_WITH_ATTRIBUTES);
        assertEquals(0, tdch.PROCESS_SIMPLE_ELEMENT);
        assertEquals(0, tdch.PROCESS_COMPLEX_ELEMENT);
    }

    public void testEmpty5() throws IOException, SAXException {
        try {
            saxParser.getXMLReader().parse(new InputSource(new StringReader(EMPTY_5)));
        } catch(Exception e) {
        }
        assertEquals(1, tdch.PROCESS_EMPTY);
        assertEquals(0, tdch.PROCESS_EMPTY_WITH_ATTRIBUTES);
        assertEquals(0, tdch.PROCESS_SIMPLE_ELEMENT);
        assertEquals(0, tdch.PROCESS_COMPLEX_ELEMENT);
    }

    public void testSimple1() throws IOException, SAXException {
        saxParser.getXMLReader().parse(new InputSource(new StringReader(SIMPLE_1)));
        assertEquals(0, tdch.PROCESS_EMPTY);
        assertEquals(0, tdch.PROCESS_EMPTY_WITH_ATTRIBUTES);
        assertEquals(1, tdch.PROCESS_SIMPLE_ELEMENT);
        assertEquals(0, tdch.PROCESS_COMPLEX_ELEMENT);
    }

    public void testComplex1() throws IOException, SAXException {
        saxParser.getXMLReader().parse(new InputSource(new StringReader(COMPLEX_1)));
        assertEquals(0, tdch.PROCESS_EMPTY);
        assertEquals(0, tdch.PROCESS_EMPTY_WITH_ATTRIBUTES);
        assertEquals(0, tdch.PROCESS_SIMPLE_ELEMENT);
        assertEquals(1, tdch.PROCESS_COMPLEX_ELEMENT);
    }

    public void testComplex2() throws IOException, SAXException {
        saxParser.getXMLReader().parse(new InputSource(new StringReader(COMPLEX_2)));
        assertEquals(0, tdch.PROCESS_EMPTY);
        assertEquals(1, tdch.PROCESS_EMPTY_WITH_ATTRIBUTES);
        assertEquals(0, tdch.PROCESS_SIMPLE_ELEMENT);
        assertEquals(0, tdch.PROCESS_COMPLEX_ELEMENT);
    }

}