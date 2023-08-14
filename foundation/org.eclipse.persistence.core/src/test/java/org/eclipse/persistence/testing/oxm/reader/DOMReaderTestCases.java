/*
 * Copyright (c) 2011, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.oxm.reader;

import org.eclipse.persistence.internal.oxm.record.DOMReader;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

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

            @Override
            public void startPrefixMapping(String prefix, String uri) throws SAXException {
            }
            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                if(uri == null) {
                    throw new SAXException();
                }
            }
            @Override
            public void startDocument() throws SAXException {
            }
            @Override
            public void skippedEntity(String name) throws SAXException {
            }
            @Override
            public void setDocumentLocator(Locator locator) {
            }
            @Override
            public void processingInstruction(String target, String data) throws SAXException {
            }
            @Override
            public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            }
            @Override
            public void endPrefixMapping(String prefix) throws SAXException {
            }
            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if(uri == null) {
                    throw new SAXException();
                }
            }
            @Override
            public void endDocument() throws SAXException {
            }
            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
            }
        });
        domReader.parse(noNamespaceDocument);
    }

}
