/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.5 - initial implementation
package org.eclipse.persistence.internal.oxm.unmapped;

import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * <p><b>Purpose:</b>Provide a default implementation of the UnmappedContentHandler
 * <p><b>Responsibilities:</b><ul>
 * <li>This handler swallows all SAX events corresponding to unmapped content so
 * when used unmapped content will not be processed.
 */
public class DefaultUnmappedContentHandler <UNMARSHAL_RECORD extends UnmarshalRecord> implements UnmappedContentHandler<UNMARSHAL_RECORD> {

    @Override
    public void setUnmarshalRecord(UNMARSHAL_RECORD unmarshalRecord) {
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void endElement(String uri, String localName, String name) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void startDocument() throws SAXException {
    }

    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

}
