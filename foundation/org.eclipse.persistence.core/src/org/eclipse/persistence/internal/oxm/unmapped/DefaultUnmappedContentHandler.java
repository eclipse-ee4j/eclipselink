/*******************************************************************************
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.5 - initial implementation
 ******************************************************************************/
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

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

}
