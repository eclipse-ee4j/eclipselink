/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class TestContentHandler implements ContentHandler {

    private List<Event> events = new ArrayList<Event>();

    
    public List<Event> getEvents() {
        return events;
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
        events.add(new StartDocumentEvent());
    }

    public void endDocument() throws SAXException {
        events.add(new EndDocumentEvent());
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        events.add(new StartPrefixMappingEvent(prefix, uri));
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        events.add(new EndPrefixMappingEvent(prefix));
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        events.add(new StartElementEvent(uri, localName, qName));
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        events.add(new EndElementEvent(uri, localName, qName));
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        events.add(new CharactersEvent(new String(ch, start, length)));
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

}