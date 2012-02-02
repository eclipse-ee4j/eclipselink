/*******************************************************************************
* Copyright (c) 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     Blaise Doughan = 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import javax.xml.validation.Schema;

import org.eclipse.persistence.oxm.XMLUnmarshaller;
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
    }

    public XMLReaderAdapter(XMLUnmarshaller xmlUnmarshaller) {
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
        if(LEXICAL_HANDLER_PROPERTY.equals(name)) {
            return getLexicalHandler();
        }
        return null;
    }

    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if(LEXICAL_HANDLER_PROPERTY.equals(name)) {
            setLexicalHandler((LexicalHandler) value);
        }
    }

    @Override
    public void parse(String systemId) {
    }

    /**
     * Convert a ContentHandler to an ExtendedContentHandler
     */
    private static class ExtendedContentHandlerAdapter implements ExtendedContentHandler {

        private ContentHandler contentHandler;

        public ExtendedContentHandlerAdapter(ContentHandler contentHandler) {
            this.contentHandler = contentHandler; 
        }

        public void setDocumentLocator(Locator locator) {
            contentHandler.setDocumentLocator(locator);
        }

        public void startDocument() throws SAXException {
            contentHandler.startDocument();
        }

        public void endDocument() throws SAXException {
            contentHandler.endDocument();
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            contentHandler.startPrefixMapping(prefix, uri);
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            contentHandler.endPrefixMapping(prefix);
        }

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            contentHandler.startElement(uri, localName, qName, atts);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            contentHandler.endElement(uri, localName, qName);
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            contentHandler.characters(ch, start, length);
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            contentHandler.ignorableWhitespace(ch, start, length);
        }

        public void processingInstruction(String target, String data) throws SAXException {
            contentHandler.processingInstruction(target, data);
        }

        public void skippedEntity(String name) throws SAXException {
            contentHandler.skippedEntity(name);
        }

        public void characters(CharSequence characters) throws SAXException {
            if(null == characters) {
                return;
            }
            contentHandler.characters(characters.toString().toCharArray(), 0, characters.length());
        }

    }

}