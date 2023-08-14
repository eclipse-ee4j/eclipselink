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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

public class TestContentHandler implements ContentHandler {

    private List<Event> events = new ArrayList<Event>();


    public List<Event> getEvents() {
        return events;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void startDocument() throws SAXException {
        events.add(new StartDocumentEvent());
    }

    @Override
    public void endDocument() throws SAXException {
        events.add(new EndDocumentEvent());
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        events.add(new StartPrefixMappingEvent(prefix, uri));
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        events.add(new EndPrefixMappingEvent(prefix));
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        events.add(new StartElementEvent(uri, localName, qName));
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        events.add(new EndElementEvent(uri, localName, qName));
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        events.add(new CharactersEvent(new String(ch, start, length)));
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

}
