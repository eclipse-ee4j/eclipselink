/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * This class delegates all events corresponding to the UnmappedContentHandler.
 */
public class UnmappedContentHandlerWrapper extends UnmarshalRecord {
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
        unmappedContentHandler.setUnmarshalRecord(this);
    }

    public UnmappedContentHandlerWrapper(UnmappedContentHandler unmappedContentHandler, SAXUnmarshallerHandler saxUnmarshallerHandler) {
        super(null);
        this.depth = 0;
        this.unmappedContentHandler = unmappedContentHandler;
        setUnmarshaller(saxUnmarshallerHandler.getUnmarshaller());
        setXMLReader(saxUnmarshallerHandler.getXMLReader());
        setUnmarshalNamespaceResolver(saxUnmarshallerHandler.getUnmarshalNamespaceResolver());
        unmappedContentHandler.setUnmarshalRecord(this);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        this.unmappedContentHandler.characters(ch, start, length);
    }

    public void endDocument() throws SAXException {
        this.unmappedContentHandler.endDocument();
    }

    public void endElement(String uri, String localName, String name) throws SAXException {
        this.unmappedContentHandler.endElement(uri, localName, name);
        this.depth--;

        if (0 == depth) {
            parentRecord.endUnmappedElement(uri, localName, name);
            xmlReader.setContentHandler(parentRecord);
            xmlReader.setLexicalHandler(parentRecord);
        }
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        if(getParentRecord() != null) {
            getParentRecord().endPrefixMapping(prefix);
        }
        this.unmappedContentHandler.endPrefixMapping(prefix);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.unmappedContentHandler.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        this.unmappedContentHandler.processingInstruction(target, data);
    }

    public void setDocumentLocator(Locator locator) {
        this.unmappedContentHandler.setDocumentLocator(locator);
    }

    public void skippedEntity(String name) throws SAXException {
        this.unmappedContentHandler.skippedEntity(name);
    }

    public void startDocument() throws SAXException {
        this.unmappedContentHandler.startDocument();
    }

    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
        this.unmappedContentHandler.startElement(uri, localName, name, atts);
        this.depth++;
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.unmappedContentHandler.startPrefixMapping(prefix, uri);
    }
}
