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
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.reader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.internal.oxm.record.DOMReader;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class DOMReaderTestCases extends ReaderTestCases {

    private static final String NO_NAMESPACE_RESOURCE = "org/eclipse/persistence/testing/oxm/reader/nonamespace.xml";
    private Document document;
    private Document noNamespaceDocument;
    
    public DOMReaderTestCases(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        document = db.parse(xmlInputStream);
        
        InputStream noNamespaceInput = Thread.currentThread().getContextClassLoader().getResourceAsStream(NO_NAMESPACE_RESOURCE);
        noNamespaceDocument = db.parse(noNamespaceInput);
    }

    public void testDOMReader() throws Exception {
        DOMReader domReader = new DOMReader();
        TestContentHandler testContentHandler = new TestContentHandler();
        domReader.setContentHandler(testContentHandler);
        domReader.parse(document);

        assertEquals(getControlEvents(), testContentHandler.getEvents());
    }
    
    public void testNoNamespaceDOMReader() throws Exception {
        DOMReader domReader = new DOMReader();
        domReader.setContentHandler(new ContentHandler() {
            
            public void startPrefixMapping(String prefix, String uri) throws SAXException {
            }
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                if(uri == null) {
                    throw new SAXException();
                }
            }
            public void startDocument() throws SAXException {
            }            
            public void skippedEntity(String name) throws SAXException {
            }
            public void setDocumentLocator(Locator locator) {
            }
            public void processingInstruction(String target, String data) throws SAXException {
            }
            public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            }
            public void endPrefixMapping(String prefix) throws SAXException {
            }
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if(uri == null) {
                    throw new SAXException();
                }
            }
            public void endDocument() throws SAXException {
            }
            public void characters(char[] ch, int start, int length) throws SAXException {
            }
        });
        domReader.parse(noNamespaceDocument);
    }

}