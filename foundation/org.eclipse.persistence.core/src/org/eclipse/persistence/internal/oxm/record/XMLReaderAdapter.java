/*
 * Copyright (c) 2011, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan = 2.1 - Initial implementation
//     Juan Pablo Gardella = 2.7.4 - Fix for the bug #543063
package org.eclipse.persistence.internal.oxm.record;

import javax.xml.validation.Schema;

import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.LexicalHandler;

/**
 * A common super class for all non SAX based XMLReaders.
 */
public abstract class XMLReaderAdapter extends XMLReader {

    protected ExtendedContentHandler contentHandler;
    private DTDHandler dtdHandler;
    private EntityResolver entityResolver;
    private ErrorHandler errorHandler;
    protected LexicalHandler lexicalHandler;

    public XMLReaderAdapter() {
        super();
    }

    public XMLReaderAdapter(Unmarshaller xmlUnmarshaller) {
        super();
        if(null != xmlUnmarshaller) {
            Schema schema = xmlUnmarshaller.getSchema();
            if(null != schema) {
                validatingContentHandler = new ValidatingContentHandler(schema.newValidatorHandler());
                this.contentHandler = new ExtendedContentHandlerAdapter(validatingContentHandler);
            }
            setErrorHandler(xmlUnmarshaller.getErrorHandler());
        }
    }

    @Override
    public ExtendedContentHandler getContentHandler() {
        return contentHandler;
    }

    @Override
    public void setContentHandler(ContentHandler contentHandler) {
        if(null == validatingContentHandler) {
            if(contentHandler instanceof  ExtendedContentHandler) {
                this.contentHandler = (ExtendedContentHandler) contentHandler;
            } else {
                this.contentHandler = new ExtendedContentHandlerAdapter(contentHandler);
            }
        } else {
            validatingContentHandler.setContentHandler(contentHandler);
        }
    }

    @Override
    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }

    @Override
    public void setDTDHandler(DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }

    @Override
    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    @Override
    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        if(null != validatingContentHandler) {
            validatingContentHandler.setErrorHandler(errorHandler);
        }
    }

    @Override
    public LexicalHandler getLexicalHandler() {
        return lexicalHandler;
    }

    @Override
    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        this.lexicalHandler = lexicalHandler;
    }

    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return false;
    }

    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if(Constants.LEXICAL_HANDLER_PROPERTY.equals(name)) {
            return getLexicalHandler();
        }
        return null;
    }

    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if(Constants.LEXICAL_HANDLER_PROPERTY.equals(name)) {
            setLexicalHandler((LexicalHandler) value);
        }
    }

    @Override
    public void parse(String systemId) {
    }

    /**
     * Convert a ContentHandler to an ExtendedContentHandler
     */
    public static class ExtendedContentHandlerAdapter implements ExtendedContentHandler {

        private ContentHandler contentHandler;

        public ExtendedContentHandlerAdapter(ContentHandler contentHandler) {
            this.contentHandler = contentHandler;
        }

        public ContentHandler getContentHandler() {
            return contentHandler;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            contentHandler.setDocumentLocator(locator);
        }

        @Override
        public void startDocument() throws SAXException {
            contentHandler.startDocument();
        }

        @Override
        public void endDocument() throws SAXException {
            contentHandler.endDocument();
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            contentHandler.startPrefixMapping(prefix, uri);
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
            contentHandler.endPrefixMapping(prefix);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            contentHandler.startElement(uri, localName, qName, atts);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            contentHandler.endElement(uri, localName, qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            contentHandler.characters(ch, start, length);
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            contentHandler.ignorableWhitespace(ch, start, length);
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            contentHandler.processingInstruction(target, data);
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
            contentHandler.skippedEntity(name);
        }

        @Override
        public void characters(CharSequence characters) throws SAXException {
            if(null == characters) {
                return;
            }
            contentHandler.characters(characters.toString().toCharArray(), 0, characters.length());
        }

        @Override
        public void setNil(boolean isNil) {}

    }

    protected static abstract class IndexedAttributeList implements Attributes {

        protected static final Attribute[] NO_ATTRIBUTES = new Attribute[0];

        protected Attribute[] attributes;

        protected abstract Attribute[] attributes();

        @Override
        public int getIndex(String qName) {
            if(null == qName) {
                return -1;
            }
            int index = 0;
            for(Attribute attribute : attributes()) {
                if(qName.equals(attribute.getName())) {
                    return index;
                }
                index++;
            }
            return -1;
        }

        @Override
        public int getIndex(String uri, String localName) {
            if(null == localName) {
                return -1;
            }
            int index = 0;
            for(Attribute attribute : attributes()) {
                  if(localName.equals(attribute.getLocalName()) && uri.equals(attribute.getUri())){
                    return index;
                }
                index++;
            }
            return -1;
        }

        @Override
        public int getLength() {
            return attributes().length;
        }

        @Override
        public String getLocalName(int index) {
            return attributes()[index].getLocalName();
        }

        @Override
        public String getQName(int index) {
            return attributes()[index].getName();
        }

        @Override
        public String getType(int index) {
            return Constants.CDATA;
        }

        @Override
        public String getType(String name) {
            return Constants.CDATA;
        }

        @Override
        public String getType(String uri, String localName) {
            return Constants.CDATA;
        }

        @Override
        public String getURI(int index) {
            return attributes()[index].getUri();
        }

        @Override
        public String getValue(int index) {
            return attributes()[index].getValue();
        }

        @Override
        public String getValue(String qName) {
            int index = getIndex(qName);
            if(-1 == index) {
                return null;
            }
            return attributes()[index].getValue();
        }

        @Override
        public String getValue(String uri, String localName) {
            int index = getIndex(uri, localName);
            if(-1 == index) {
                return null;
            }
            return attributes()[index].getValue();
        }

        public IndexedAttributeList reset() {
            attributes = null;
            return this;
        }
    }

    protected static class Attribute {

        private String localName;
        private String uri;
        private String name;
        private String value;

        public Attribute(String uri, String localName, String name, String value) {
            this.localName = localName;
            if(uri == null){
                this.uri = Constants.EMPTY_STRING;
            }else{
                this.uri = uri;
            }
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public String getLocalName() {
            return localName;
        }

        public String getUri() {
            return uri;
        }
    }

}
