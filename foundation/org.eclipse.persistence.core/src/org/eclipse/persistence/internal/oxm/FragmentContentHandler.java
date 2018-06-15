/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * This class is used when marshalling to a ContentHandler when the fragment flag on XMLMarshaller is true
 * It wraps a given ContentHandler and passes the events to that ContentHandler
 * The startDocument and endDocumentevents are not triggered on the reference ContentHandler.
 */
public class FragmentContentHandler implements ContentHandler {
    private ContentHandler refContentHandler;

    public FragmentContentHandler(ContentHandler handler) {
        refContentHandler = handler;
    }

    public void setRefContentHandler(ContentHandler refContentHandler) {
        this.refContentHandler = refContentHandler;
    }

    public ContentHandler getRefContentHandler() {
        return refContentHandler;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        refContentHandler.startElement(namespaceURI, localName, qName, atts);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        refContentHandler.endElement(namespaceURI, localName, qName);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        refContentHandler.startPrefixMapping(prefix, uri);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        refContentHandler.processingInstruction(target, data);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        refContentHandler.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        //do nothing.  startDocument() on the content handler is not triggered when fragment on XMLMarshaller is true
    }

    @Override
    public void endDocument() throws SAXException {
        //do nothing.  startDocument() on the content handler is not triggered when fragment on XMLMarshaller is true
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        refContentHandler.skippedEntity(name);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        refContentHandler.endPrefixMapping(prefix);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        refContentHandler.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        refContentHandler.characters(ch, start, length);
    }
}
