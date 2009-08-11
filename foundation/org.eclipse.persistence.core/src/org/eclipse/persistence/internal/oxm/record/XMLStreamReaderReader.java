/*******************************************************************************
* Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.persistence.internal.oxm.record.namespaces.UnmarshalNamespaceContext;
import org.eclipse.persistence.oxm.XMLConstants;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.LexicalHandler;

/**
 * Convert and XMLStreamReader into SAX events. 
 */
public class XMLStreamReaderReader extends XMLReader {

    private static final String EMPTY_STRING = "";

    private ContentHandler contentHandler;
    private LexicalHandler lexicalHandler;
    private ErrorHandler errorHandler;
    private int depth = 0;

    public XMLStreamReaderReader() {
    }

    @Override
    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    @Override
    public void setContentHandler(ContentHandler aContentHandler) {
        this.contentHandler = aContentHandler;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler anErrorHandler) {
        this.errorHandler = anErrorHandler;
    }

    @Override
    public void setProperty (String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if(name.equals("http://xml.org/sax/properties/lexical-handler")) {
            lexicalHandler = (LexicalHandler)value;
        }
    }

    @Override
    public void parse(InputSource input) throws SAXException {
        if(input instanceof XMLStreamReaderInputSource) {
            XMLStreamReader xmlStreamReader = ((XMLStreamReaderInputSource) input).getXmlStreamReader();
            parse(xmlStreamReader);
        }
    }

    @Override
    public void parse(InputSource input, SAXUnmarshallerHandler saxUnmarshallerHandler) throws SAXException {
        if(input instanceof XMLStreamReaderInputSource) {
            XMLStreamReader xmlStreamReader = ((XMLStreamReaderInputSource) input).getXmlStreamReader();
            saxUnmarshallerHandler.setUnmarshalNamespaceResolver(new UnmarshalNamespaceContext(xmlStreamReader));
            parse(xmlStreamReader);
        }
    }

    public void parse(String systemId) throws SAXException {}

    private void parse(XMLStreamReader xmlStreamReader) throws SAXException {
        if(null == getContentHandler()) {
            return;
        }

        switch (xmlStreamReader.getEventType()) {
            case XMLStreamReader.ATTRIBUTE:  {
                break;
            }
            case XMLStreamReader.CDATA: {
                if(null == lexicalHandler) {
                    getContentHandler().characters(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
                } else {
                    lexicalHandler.startCDATA();
                    getContentHandler().characters(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
                    lexicalHandler.endCDATA();
                }
                break;
            }
            case XMLStreamReader.CHARACTERS: {
                getContentHandler().characters(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
                break;
            }
            case XMLStreamReader.COMMENT: {
                if(null != lexicalHandler) {
                    lexicalHandler.comment(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
                }
                break;
            }
            case XMLStreamReader.DTD: {
                break;
            }
            case XMLStreamReader.END_DOCUMENT: {
                depth--;
                getContentHandler().endDocument();
                return;
            }
            case XMLStreamReader.END_ELEMENT: {
                depth--;
                String prefix = xmlStreamReader.getPrefix();
                if(null == prefix || EMPTY_STRING.equals(prefix)) {
                    getContentHandler().endElement(xmlStreamReader.getNamespaceURI(), xmlStreamReader.getLocalName(), xmlStreamReader.getLocalName());                    
                } else {
                    getContentHandler().endElement(xmlStreamReader.getNamespaceURI(), xmlStreamReader.getLocalName(), prefix + ':' + xmlStreamReader.getLocalName());                    
                }
                break;
            }
            case XMLStreamReader.ENTITY_DECLARATION: {
                break;
            }
            case XMLStreamReader.ENTITY_REFERENCE: {
                break;
            }
            case XMLStreamReader.NAMESPACE: {
                break;
            }
            case XMLStreamReader.NOTATION_DECLARATION: {
                break;
            }
            case XMLStreamReader.PROCESSING_INSTRUCTION: {
                getContentHandler().processingInstruction(xmlStreamReader.getPITarget(), xmlStreamReader.getPIData());
                break;
            }
            case XMLStreamReader.SPACE: {
                char[] characters = xmlStreamReader.getTextCharacters(); 
                getContentHandler().characters(characters, 0, characters.length);
                break;
            }
            case XMLStreamReader.START_DOCUMENT: {
                depth++;
                getContentHandler().startDocument();
                break;
            }
            case XMLStreamReader.START_ELEMENT: {
                depth++;
                String prefix = xmlStreamReader.getPrefix();
                if(null == prefix || EMPTY_STRING.equals(prefix)) {
                    getContentHandler().startElement(xmlStreamReader.getNamespaceURI(), xmlStreamReader.getLocalName(), xmlStreamReader.getLocalName(), new IndexedAttributeList(xmlStreamReader));                    
                } else {
                    getContentHandler().startElement(xmlStreamReader.getNamespaceURI(), xmlStreamReader.getLocalName(), prefix + ':' + xmlStreamReader.getLocalName(), new IndexedAttributeList(xmlStreamReader));                    
                }
                break;
            }
        }
        try {
            if(depth > 0 && xmlStreamReader.hasNext()) {
                xmlStreamReader.next();
                parse(xmlStreamReader);
            }
        } catch(XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private static class IndexedAttributeList implements Attributes {

        private static final String CDATA = "CDATA";

        private List<Attribute> attributes;

        public IndexedAttributeList(XMLStreamReader xmlStreamReader) {
            int namespaceCount = xmlStreamReader.getNamespaceCount(); 
            int attributeCount = xmlStreamReader.getAttributeCount(); 

            attributes = new ArrayList<Attribute>(attributeCount + namespaceCount); 
 
            for(int x=0; x<namespaceCount; x++) { 
                String uri = XMLConstants.XMLNS_URL; 
                String localName = xmlStreamReader.getNamespacePrefix(x); 
                String qName; 
                if(null == localName || EMPTY_STRING.equals(localName)) { 
                    localName = XMLConstants.XMLNS; 
                    qName = XMLConstants.XMLNS; 
                } else { 
                    qName = XMLConstants.XMLNS + ':' + localName; 
                } 
                String value = xmlStreamReader.getNamespaceURI(x); 
                attributes.add(new Attribute(uri, localName, qName, value)); 
            } 

            for(int x=0; x<attributeCount; x++) {
                String uri = xmlStreamReader.getAttributeNamespace(x);
                String localName = xmlStreamReader.getAttributeLocalName(x);
                String prefix = xmlStreamReader.getAttributePrefix(x);
                String qName;
                if(null == prefix || "".equals(prefix)) {
                    qName = localName;
                } else {
                    qName = prefix + ':' + localName;
                }
                String value = xmlStreamReader.getAttributeValue(x);
                attributes.add(new Attribute(uri, localName, qName, value));
            }
        }

        public int getIndex(String qName) {
            if(null == qName) {
                return -1;
            }
            int index = 0;
            for(Attribute attribute : attributes) {
                if(qName.equals(attribute.getName())) {
                    return index;
                }
                index++;
            }
            return -1;
        }

        public int getIndex(String uri, String localName) {
            if(null == localName) {
                return -1;
            }
            int index = 0;
            for(Attribute attribute : attributes) {
                QName testQName = new QName(uri, localName);
                if(attribute.getQName().equals(testQName)) {
                    return index;
                }
                index++;
            }
            return -1;
        }

        public int getLength() {
            return attributes.size();
        }

        public String getLocalName(int index) {
            return attributes.get(index).getQName().getLocalPart();
        }

        public String getQName(int index) {
            return attributes.get(index).getName();
        }

        public String getType(int index) {
            return CDATA;
        }

        public String getType(String name) {
            return CDATA;
        }

        public String getType(String uri, String localName) {
            return CDATA;
        }

        public String getURI(int index) {
            return attributes.get(index).getQName().getNamespaceURI();
        }

        public String getValue(int index) {
            return attributes.get(index).getValue();
        }

        public String getValue(String qName) {
            int index = getIndex(qName);
            if(-1 == index) {
                return null;
            } 
            return attributes.get(index).getValue();
        }

        public String getValue(String uri, String localName) {
            int index = getIndex(uri, localName);
            if(-1 == index) {
                return null;
            }
            return attributes.get(index).getValue();
        }

    }

    private static class Attribute {

        private QName qName;
        private String name;
        private String value;

        public Attribute(String uri, String localName, String name, String value) {
            this.qName = new QName(uri, localName);
            this.name = name;
            this.value = value;
        }

        public QName getQName() {
            return qName;
        }

        public String getName() {
            return name;
        }
        
        public String getValue() {
            return value;
        }

    }

}
