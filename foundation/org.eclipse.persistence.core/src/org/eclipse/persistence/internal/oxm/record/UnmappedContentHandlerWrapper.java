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
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.oxm.unmapped.UnmappedContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * This class delegates all events corresponding to the UnmappedContentHandler.
 */
public class UnmappedContentHandlerWrapper extends UnmarshalRecordImpl {
    private int depth;
    private UnmappedContentHandler unmappedContentHandler;

    public UnmappedContentHandlerWrapper(UnmarshalRecord parentRecord, UnmappedContentHandler unmappedContentHandler) {
        super(null);
        this.depth = 0;
        this.unmappedContentHandler = unmappedContentHandler;
        setParentRecord(parentRecord);
        setUnmarshaller(parentRecord.getUnmarshaller());
        setXMLReader(parentRecord.getXMLReader());
        setUnmarshalNamespaceResolver(parentRecord.getUnmarshalNamespaceResolver());
        unmappedContentHandler.setUnmarshalRecord(new org.eclipse.persistence.oxm.record.UnmarshalRecord(this));
    }

    public UnmappedContentHandlerWrapper(UnmappedContentHandler unmappedContentHandler, SAXUnmarshallerHandler saxUnmarshallerHandler) {
        super(null);
        this.depth = 0;
        this.unmappedContentHandler = unmappedContentHandler;
        setUnmarshaller(saxUnmarshallerHandler.getUnmarshaller());
        setXMLReader(saxUnmarshallerHandler.getXMLReader());
        setUnmarshalNamespaceResolver(saxUnmarshallerHandler.getUnmarshalNamespaceResolver());
        unmappedContentHandler.setUnmarshalRecord(new org.eclipse.persistence.oxm.record.UnmarshalRecord(this));
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.unmappedContentHandler.characters(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        this.unmappedContentHandler.endDocument();
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        this.unmappedContentHandler.endElement(uri, localName, name);
        this.depth--;

        if (0 == depth) {
            parentRecord.endUnmappedElement(uri, localName, name);
            xmlReader.setContentHandler(parentRecord);
            xmlReader.setLexicalHandler(parentRecord);
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        if(getParentRecord() != null) {
            getParentRecord().endPrefixMapping(prefix);
        }
        this.unmappedContentHandler.endPrefixMapping(prefix);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.unmappedContentHandler.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.unmappedContentHandler.processingInstruction(target, data);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.unmappedContentHandler.setDocumentLocator(locator);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        this.unmappedContentHandler.skippedEntity(name);
    }

    @Override
    public void startDocument() throws SAXException {
        this.unmappedContentHandler.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
        this.unmappedContentHandler.startElement(uri, localName, name, atts);
        this.depth++;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.unmappedContentHandler.startPrefixMapping(prefix, uri);
    }
}
