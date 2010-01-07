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
package org.eclipse.persistence.testing.oxm.deferred;

import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import junit.textui.TestRunner;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.xml.sax.InputSource;

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
        assertEquals(0, tdch.PROCESS_SIMPLE_ELEMENT);
        assertEquals(0, tdch.PROCESS_COMPLEX_ELEMENT);
    }

    public void testNonNullComplexElement() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/deferred/complexElementWithContent.xml");
        saxParser.getXMLReader().parse(new InputSource(inputStream));
        assertEquals(1, tdch.PROCESS_COMPLEX_ELEMENT);
        assertEquals(0, tdch.PROCESS_EMPTY);
        assertEquals(0, tdch.PROCESS_SIMPLE_ELEMENT);
    }

    public void testNonNullSimpleElement() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/deferred/simpleElementWithContent.xml");
        saxParser.getXMLReader().parse(new InputSource(inputStream));
        assertEquals(1, tdch.PROCESS_SIMPLE_ELEMENT);
        assertEquals(0, tdch.PROCESS_COMPLEX_ELEMENT);
        assertEquals(0, tdch.PROCESS_EMPTY);
    }
}
