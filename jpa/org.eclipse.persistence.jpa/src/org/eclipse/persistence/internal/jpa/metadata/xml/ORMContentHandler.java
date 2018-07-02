/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (ENTITY_MAPPINGS.equals(localName)) {
            this.version = atts.getValue(VERSION);
            this.isEclipseLink = namespaceURI.indexOf(ECLIPSELINK) != -1;
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public boolean isEclipseLink() {
        return isEclipseLink;
    }

    public String getVersion() {
        return version;
    }
}
