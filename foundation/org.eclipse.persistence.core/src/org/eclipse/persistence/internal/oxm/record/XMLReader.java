/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.LexicalHandler;
import javax.xml.validation.ValidatorHandler;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.MediaType;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide a wrapper for an org.xml.sax.XMLReader instance and define some extra
 * event methods that can be used by TopLink during the unmarshal process. These events are no ops
 * in this class, but may be overridden in subclasses.
 * <p><b>Responsibilities</b><ul>
 * <li>Wrap an instance of org.xml.sax.XMLReader and provide all the required API</li>
 * <li>Provide empty implementations of some callback methods that can be overridden in subclasses</li>
 * 
 *  @see org.eclipse.persistence.internal.oxm.record.DOMReader
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class XMLReader implements org.xml.sax.XMLReader {
    
    public static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    public static final String REPORT_IGNORED_ELEMENT_CONTENT_WHITESPACE_FEATURE = "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace";

    private org.xml.sax.XMLReader reader;
    private boolean supportsLexicalHandler;
    private LexicalHandlerWrapper lexicalHandlerWrapper;
    protected ValidatingContentHandler validatingContentHandler;
    protected boolean namespaceAware;
    protected char namespaceSeparator;
    protected Locator locator;    

    public XMLReader(org.xml.sax.XMLReader internalReader) {
        this();
        this.reader = internalReader;
    }

    public XMLReader() {
        this.supportsLexicalHandler = true;
        namespaceAware = true;
        namespaceSeparator = Constants.COLON;
    }
    
    /**
     * INTERNAL:
     * return the Locator object associated with this reader
     * @since 2.4
     */
    public Locator getLocator(){
    	return locator;
    }
    
    /**
     * INTERNAL:
     * set the Locator object to associate with this reader
     * @since 2.4
     */
    public void setLocator(Locator newLocator){
    	locator = newLocator;
    }

    public ContentHandler getContentHandler () {
        return reader.getContentHandler();
    }

    public void setContentHandler (ContentHandler handler) {
        if(validatingContentHandler != null) {
            validatingContentHandler.setContentHandler(handler);
        } else {
            reader.setContentHandler(handler);
        }
    }
    
    /**
     * INTERNAL:
     * Determine if namespaces will be considered during marshal/unmarshal operations.
     * @since 2.4
     */
    public boolean isNamespaceAware() {
    	return namespaceAware;
    }
    
    /**
     * INTERNAL:
	 * The character used to separate the prefix and uri portions when namespaces are present 
     * @since 2.4
     */
    public char getNamespaceSeparator(){
    	return namespaceSeparator;
    }
    
    /**
     *  INTERNAL:
     *  @return The MediaType associated with this reader  
     */
    public MediaType getMediaType(){
    	return Constants.APPLICATION_XML;
    }
   
    
    /**
     * INTERNAL:
     * @since 2.4
     */
    public Object convertValueBasedOnSchemaType(Field xmlField, Object value, XMLConversionManager xmlConversionManager, AbstractUnmarshalRecord record) {
    	return xmlField.convertValueBasedOnSchemaType(value, xmlConversionManager, record);    	
    }

    public DTDHandler getDTDHandler () {
        return reader.getDTDHandler();
    }

    public void setDTDHandler (DTDHandler handler) {
        reader.setDTDHandler(handler);
    }

    public void setEntityResolver (EntityResolver resolver) {
        reader.setEntityResolver(resolver);
    }

    public EntityResolver getEntityResolver () {
        return reader.getEntityResolver();
    }

    public ErrorHandler getErrorHandler () {
        return reader.getErrorHandler();
    }

    public void setErrorHandler (ErrorHandler handler) {
        if(validatingContentHandler != null) {
            validatingContentHandler.setErrorHandler(handler);
        } else {
            reader.setErrorHandler(handler);
        }
    }

    public LexicalHandler getLexicalHandler() {
        if(supportsLexicalHandler) {
            try {
                return (LexicalHandler) reader.getProperty(Constants.LEXICAL_HANDLER_PROPERTY);
            } catch (SAXException e) {
                supportsLexicalHandler = false;
            }
        }
        return null;
    }

    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        if(supportsLexicalHandler) {
                if(null == lexicalHandlerWrapper) {
                    try {
                        lexicalHandlerWrapper = new LexicalHandlerWrapper(lexicalHandler);
                        reader.setProperty(Constants.LEXICAL_HANDLER_PROPERTY, lexicalHandlerWrapper);
                    } catch (SAXException e) {
                        supportsLexicalHandler = false;
                    }
                } else {
                    lexicalHandlerWrapper.setLexicalHandler(lexicalHandler);
                }
        }
    }

    public boolean getFeature (String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return reader.getFeature(name);
    }

    public void setFeature (String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        reader.setFeature(name, value);
    }

    public Object getProperty (String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if(Constants.LEXICAL_HANDLER_PROPERTY.equals(name)) {
            return getLexicalHandler();
        } else {
            return reader.getProperty(name);
        }
    }

    public void setProperty (String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if(Constants.LEXICAL_HANDLER_PROPERTY.equals(name)) {
            setLexicalHandler((LexicalHandler) value);
        } else {
            reader.setProperty(name, value);
        }
    }

    public void parse(InputSource input) throws IOException, SAXException {
        try {
            reader.parse(input);
        } catch(SAXNotSupportedException e) {
            String message = e.getMessage();
            if(message != null && message.contains("namespace-prefix")) {
                reader.setFeature(NAMESPACE_PREFIXES_FEATURE, false);
                reader.parse(input);
            } else {
                throw e;
            }
        }
    }

    public void parse (String systemId) throws IOException, SAXException {
        try {
            reader.parse(systemId);
        } catch(SAXNotSupportedException e) {
            String message = e.getMessage();
            if(message != null && message.contains("namespace-prefix")) {
                reader.setFeature(NAMESPACE_PREFIXES_FEATURE, false);
                reader.parse(systemId);
            } else {
                throw e;
            }
        }
    }

    public void setValidatorHandler(ValidatorHandler validatorHandler) {
        ErrorHandler errorHandler = getErrorHandler();
        ContentHandler contentHandler;
        if(null == this.validatingContentHandler) {
            contentHandler = getContentHandler();
        } else {
            contentHandler = validatorHandler.getContentHandler();
            this.validatingContentHandler = null;
        }
        ValidatingContentHandler validatingContentHandler = null;
        if(null != validatorHandler) {
            validatingContentHandler = new ValidatingContentHandler(validatorHandler);
            validatingContentHandler.setContentHandler(contentHandler);
            contentHandler = validatingContentHandler;
        }
        if(null != reader) {
            reader.setContentHandler(contentHandler);
        }
        setContentHandler(contentHandler);
        this.validatingContentHandler = validatingContentHandler;
        setErrorHandler(errorHandler);
    }

    public ValidatorHandler getValidatorHandler() {
        if(null == validatingContentHandler) {
            return null;
        }
        return this.validatingContentHandler.getValidatorHandler();
    }
    
    public void newObjectEvent(Object object, Object parent, Mapping selfRecordMapping) {
        //no op in this class.
    }

    public Object getCurrentObject(CoreAbstractSession session, Mapping selfRecordMapping) {
        return null;
    }

    /**
     * This call back mechanism provides an opportunity for the XMLReader to
     * provide an alternate conversion.  This optimization is currently only 
     * leveraged for properties annotated with @XmlInlineBinaryData.
     * @param characters The characters to be converted.
     * @param dataType The type to be converted to.
     * @return The converted value
     */
    public Object getValue(CharSequence characters, Class<?> dataType) {
        return null;
    }
    
    public boolean isNullRepresentedByXsiNil(AbstractNullPolicy nullPolicy){
    	return nullPolicy.isNullRepresentedByXsiNil();    	
    }

    
    public boolean isInCollection(){
    	return true;
    }
 

    
    /**
     * Performance Optimization:
     * It is expensive to change the LexicalHandler on the underlying XMLReader
     * constantly through the setProperty(String, Object) mechanism.  So instead
     * the LexicalHandlerWrapper is set once this way, and the "real" 
     * LexicalHandler is changed on the LexicalHandlerWrapper.
     */
    private static class LexicalHandlerWrapper implements LexicalHandler {

        private LexicalHandler lexicalHandler;

        public LexicalHandlerWrapper(LexicalHandler lexicalHandler) {
            this.lexicalHandler = lexicalHandler;
        }

        public void setLexicalHandler(LexicalHandler lexicalHandler) {
            this.lexicalHandler = lexicalHandler;
        }

        public void comment(char[] ch, int start, int length) throws SAXException {
            if(null != lexicalHandler) {
                lexicalHandler.comment(ch, start, length);
            }
        }

        public void endCDATA() throws SAXException {
            if(null  != lexicalHandler) {
                lexicalHandler.endCDATA();
            }
        }

        public void endDTD() throws SAXException {
            if(null != lexicalHandler) {
                lexicalHandler.endDTD();
            }
        }

        public void endEntity(String name) throws SAXException {
            if(null != lexicalHandler) {
                lexicalHandler.endEntity(name);
            }
        }

        public void startCDATA() throws SAXException {
            if(null != lexicalHandler) {
                lexicalHandler.startCDATA();
            }
        }

        public void startDTD(String name, String publicId, String systemId) throws SAXException {
            if(null != lexicalHandler) {
                lexicalHandler.startCDATA();
            }
        }

        public void startEntity(String name) throws SAXException {
            if(null != lexicalHandler) {
                lexicalHandler.startEntity(name);
            }
        }

    }

    /**
     * Validate the SAX events reported to the ContentHandler.  This class is 
     * being used rather than a ValidatorHandler in order to prevent default 
     * values from being populated.
     */
    protected static class ValidatingContentHandler implements ContentHandler {

        private ValidatorHandler validatorHandler;
        private ContentHandler contentHandler;

        public ValidatingContentHandler(ValidatorHandler validatorHandler) {
            this.validatorHandler = validatorHandler;
        }

        public ContentHandler getContentHandler() {
            return contentHandler;
        }

        public void setContentHandler(ContentHandler contentHandler) {
            this.contentHandler = contentHandler;
        }

        public void setErrorHandler(ErrorHandler errorHandler) {
            validatorHandler.setErrorHandler(errorHandler);
        }

        public ValidatorHandler getValidatorHandler() {
            return validatorHandler;
        }

        public void setValidatorHandler(ValidatorHandler validatorHandler) {
            this.validatorHandler = validatorHandler;
        }

        public void setDocumentLocator(Locator locator) {
            validatorHandler.setDocumentLocator(locator);
            contentHandler.setDocumentLocator(locator);
        }

        public void startDocument() throws SAXException {
            validatorHandler.startDocument();
            contentHandler.startDocument();
        }

        public void endDocument() throws SAXException {
            validatorHandler.endDocument();
            contentHandler.endDocument();
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            validatorHandler.startPrefixMapping(prefix, uri);
            contentHandler.startPrefixMapping(prefix, uri);
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            validatorHandler.endPrefixMapping(prefix);
            contentHandler.endPrefixMapping(prefix);
        }

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            validatorHandler.startElement(uri, localName, qName, atts);
            contentHandler.startElement(uri, localName, qName, atts);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            validatorHandler.endElement(uri, localName, qName);
            contentHandler.endElement(uri, localName, qName);
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            validatorHandler.characters(ch, start, length);
            contentHandler.characters(ch, start, length);
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            validatorHandler.ignorableWhitespace(ch, start, length);
            contentHandler.characters(ch, start, length);
        }

        public void processingInstruction(String target, String data) throws SAXException {
            validatorHandler.processingInstruction(target, data);
            contentHandler.processingInstruction(target, data);
        }

        public void skippedEntity(String name) throws SAXException {
            validatorHandler.skippedEntity(name);
            contentHandler.skippedEntity(name);
        }

    }

}