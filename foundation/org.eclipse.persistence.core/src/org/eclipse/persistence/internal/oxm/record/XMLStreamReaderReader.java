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
*     bdoughan - June 24/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import javax.xml.stream.XMLStreamReader;

import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.record.namespaces.UnmarshalNamespaceContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;

/**
 * Convert an XMLStreamReader into SAX events.
 */
public class XMLStreamReaderReader extends XMLReaderAdapter {

    private int depth = 0;
    private UnmarshalNamespaceContext unmarshalNamespaceContext;
    private XMLStreamReaderAttributes indexedAttributeList;
    private boolean qNameAware;
    private XMLStreamReader xmlStreamReader;

    public XMLStreamReaderReader() {
        unmarshalNamespaceContext = new UnmarshalNamespaceContext();
        indexedAttributeList = new XMLStreamReaderAttributes();
    }

    public XMLStreamReaderReader(Unmarshaller xmlUnmarshaller) {
        super(xmlUnmarshaller);
        unmarshalNamespaceContext = new UnmarshalNamespaceContext();
        indexedAttributeList = new XMLStreamReaderAttributes();
    }
    
    public Locator getLocator(){
    	if(locator == null){
    		locator = new StreamReaderLocator(xmlStreamReader);
    	}
    	return locator;
    }

    @Override
    public void setContentHandler (ContentHandler handler) {   
        if(handler instanceof UnmarshalRecord){
            ((UnmarshalRecord)handler).setUnmarshalNamespaceResolver(unmarshalNamespaceContext);
            qNameAware = false;
            if(null == validatingContentHandler) {
            	this.contentHandler = (UnmarshalRecord) handler;
            }else{
            	 validatingContentHandler.setContentHandler(handler);
            }
        }else if(handler instanceof SAXUnmarshallerHandler){
            ((SAXUnmarshallerHandler)handler).setUnmarshalNamespaceResolver(unmarshalNamespaceContext);
            qNameAware = true;
            if(null == validatingContentHandler) {
            	this.contentHandler = (SAXUnmarshallerHandler) handler;
            }else{
            	 validatingContentHandler.setContentHandler(handler);
            }
        } else {
        	super.setContentHandler(handler);
            qNameAware = true;
        }               
    }

    @Override
    public void parse(InputSource input) throws SAXException {
        if(null == contentHandler) {
            return;
        }
        if(input instanceof XMLStreamReaderInputSource) {
            XMLStreamReader xmlStreamReader = ((XMLStreamReaderInputSource) input).getXmlStreamReader();
            parse(xmlStreamReader);
        }
    }
  
    public void parse(XMLStreamReader xmlStreamReader) throws SAXException {
    	this.xmlStreamReader = xmlStreamReader;
        unmarshalNamespaceContext.setXmlStreamReader(xmlStreamReader);
        indexedAttributeList.setXmlStreamReader(xmlStreamReader);
        try {
            contentHandler.startDocument();
            parseEvent(xmlStreamReader, xmlStreamReader.getEventType());
            while(depth > 0) {
                int eventType = xmlStreamReader.next();
                parseEvent(xmlStreamReader, eventType);
            }
            contentHandler.endDocument();
        } catch(SAXException e ) {
            throw e;
        } catch(Exception e) {
            throw new SAXException(e);
        }
    }

    private void parseEvent(XMLStreamReader xmlStreamReader, int eventType) throws SAXException {
        switch (eventType) {
            case XMLStreamReader.START_ELEMENT: {
                depth++;
                int namespaceCount = xmlStreamReader.getNamespaceCount();
                if(namespaceCount > 0) {
                    for(int x=0; x<namespaceCount; x++) {
                        contentHandler.startPrefixMapping(xmlStreamReader.getNamespacePrefix(x), xmlStreamReader.getNamespaceURI(x));
                    }
                }
                String localName = xmlStreamReader.getLocalName();
                String namespaceURI = xmlStreamReader.getNamespaceURI();
                if(Constants.EMPTY_STRING.equals(namespaceURI)) {
                    namespaceURI = null;
                }
                if(qNameAware) {
                    String prefix = xmlStreamReader.getPrefix();
                    if(null == prefix || prefix.length() == 0) {
                        contentHandler.startElement(namespaceURI, localName, localName, indexedAttributeList.reset());
                    } else {
                        contentHandler.startElement(namespaceURI, localName, prefix + Constants.COLON + localName, indexedAttributeList.reset());
                    }
                } else {
                    contentHandler.startElement(namespaceURI, localName, null, indexedAttributeList.reset());
                }
                break;
            }
            case XMLStreamReader.END_ELEMENT: {
                depth--;
                String localName = xmlStreamReader.getLocalName();
                String namespaceURI = xmlStreamReader.getNamespaceURI();
                if(Constants.EMPTY_STRING.equals(namespaceURI)) {
                    namespaceURI = null;
                }
                if(qNameAware) {
                    String prefix = xmlStreamReader.getPrefix();
                    if(null == prefix || prefix.length() == 0) {
                        contentHandler.endElement(namespaceURI, localName, localName);
                    } else {
                        contentHandler.endElement(namespaceURI, localName, prefix + Constants.COLON + localName);
                    }
                } else {
                    contentHandler.endElement(namespaceURI, localName, null);
                }
                int namespaceCount = xmlStreamReader.getNamespaceCount();
                if(namespaceCount > 0) {
                    for(int x=0; x<namespaceCount; x++) {
                        contentHandler.endPrefixMapping(xmlStreamReader.getNamespacePrefix(x));
                    }
                }
               break;
            }
            case XMLStreamReader.PROCESSING_INSTRUCTION: {
                contentHandler.processingInstruction(xmlStreamReader.getPITarget(), xmlStreamReader.getPIData());
                break;
            }
            case XMLStreamReader.CHARACTERS: {
                parseCharactersEvent(xmlStreamReader);
                break;
            }
            case XMLStreamReader.COMMENT: {
                if(null != lexicalHandler) {
                    lexicalHandler.comment(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
                }
                break;
            }
            case XMLStreamReader.SPACE: {
                contentHandler.characters(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
                break;
            }
            case XMLStreamReader.START_DOCUMENT: {
                depth++;
                break;
            }
            case XMLStreamReader.END_DOCUMENT: {
                depth--;
                return;
            }
            case XMLStreamReader.ENTITY_REFERENCE: {
                break;
            }
            case XMLStreamReader.ATTRIBUTE: {
                break;
            }
            case XMLStreamReader.DTD: {
                break;
            }
            case XMLStreamReader.CDATA: {
                char[] characters = xmlStreamReader.getText().toCharArray();
                if(null == lexicalHandler) {
                    parseCharactersEvent(xmlStreamReader);
                } else {
                    lexicalHandler.startCDATA();
                    parseCharactersEvent(xmlStreamReader);
                    lexicalHandler.endCDATA();
                }
                break;
            }
        }
    }

    /**
     * Subclasses of this class can override this method to provide alternate
     * mechanisms for processing the characters event.  One possibility is
     * obtaining a CharSequence and calling the corresponding characters method
     * on the extended content handler.
     */
    protected void parseCharactersEvent(XMLStreamReader xmlStreamReader) throws SAXException {
        contentHandler.characters(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
    }

    private static class XMLStreamReaderAttributes  extends IndexedAttributeList {

        private XMLStreamReader xmlStreamReader;

        public void setXmlStreamReader(XMLStreamReader xmlStreamReader) {
            this.xmlStreamReader = xmlStreamReader;
        }

        @Override
        protected Attribute[] attributes() {
            if(null == attributes) {
                int namespaceCount = xmlStreamReader.getNamespaceCount();
                int attributeCount = xmlStreamReader.getAttributeCount();
                if(namespaceCount + attributeCount == 0) {
                    attributes = NO_ATTRIBUTES;
                } else {
                    attributes = new Attribute[attributeCount + namespaceCount];
                    for(int x=0; x<attributeCount; x++) {
                        String uri = xmlStreamReader.getAttributeNamespace(x);
                        String localName = xmlStreamReader.getAttributeLocalName(x);
                        String prefix = xmlStreamReader.getAttributePrefix(x);
                        String qName;
                        if(null == prefix || prefix.length() == 0) {
                            qName = localName;
                        } else {
                            qName = prefix + Constants.COLON + localName;
                        }
                        String value = xmlStreamReader.getAttributeValue(x);
                        attributes[x] = new Attribute(uri, localName, qName, value);
                    }
                    for(int x=0; x<namespaceCount; x++) {
                        String uri = javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
                        String localName = xmlStreamReader.getNamespacePrefix(x);
                        String qName;
                        if(null == localName || localName.length() == 0) {
                            localName = javax.xml.XMLConstants.XMLNS_ATTRIBUTE;
                            qName = javax.xml.XMLConstants.XMLNS_ATTRIBUTE;
                        } else {
                            qName = javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON + localName;
                        }
                        String value = xmlStreamReader.getNamespaceURI(x);
                        attributes[x + attributeCount] = new Attribute(uri, localName, qName, value);
                    }               

                }
            }
            return attributes;
        }

        @Override
        public String getValue(String uri, String localName) {
            if (Constants.EMPTY_STRING.equals(uri)) {
                uri = null;
            }
            return xmlStreamReader.getAttributeValue(uri, localName);
        }

    }

    /**
     * <p>An implementation of Locator, created from an existing XMLStreamReader.</p>
     *
     * @see org.xml.sax.Locator
     * @see javax.xml.stream.XMLStreamReader
     */
    private class StreamReaderLocator implements Locator2 {

        private XMLStreamReader reader;

        /**
         * Instantiates a new StreamReaderLocator.
         *
         * @param r the XMLStreamReader object from which to copy location information.
         */
        public StreamReaderLocator(XMLStreamReader r) {
            this.reader = r;
        }

        /**
         * Returns the public ID of this Locator.
         */
        public String getPublicId() {
            return this.reader.getLocation().getPublicId();
        }

        /**
         * Returns the system ID of this Locator.
         */
        public String getSystemId() {
            return this.reader.getLocation().getSystemId();
        }

        /**
         * Returns the line number of this Locator.
         */
        public int getLineNumber() {
            return this.reader.getLocation().getLineNumber();
        }

        /**
         * Returns the column number of this Locator.
         */
        public int getColumnNumber() {
            return this.reader.getLocation().getColumnNumber();
        }

        public String getXMLVersion() {
            return null;
        }

        public String getEncoding() {
            return null;
        }

    }
    
}