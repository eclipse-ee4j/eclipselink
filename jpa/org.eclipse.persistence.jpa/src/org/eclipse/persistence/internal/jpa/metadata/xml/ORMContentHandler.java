/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     James Sutherland - initial implementation
package org.eclipse.persistence.internal.jpa.metadata.xml;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

public class ORMContentHandler implements ContentHandler {
    private static final String ENTITY_MAPPINGS = "entity-mappings";
    private static final String VERSION = "version";
    private static final String ECLIPSELINK = "eclipselink";

    private boolean isEclipseLink;

    private String version;

    public ORMContentHandler() {
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (ENTITY_MAPPINGS.equals(localName)) {
            this.version = atts.getValue(VERSION);
            this.isEclipseLink = namespaceURI.indexOf(ECLIPSELINK) != -1;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
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

    public boolean isEclipseLink() {
        return isEclipseLink;
    }

    public String getVersion() {
        return version;
    }
}
