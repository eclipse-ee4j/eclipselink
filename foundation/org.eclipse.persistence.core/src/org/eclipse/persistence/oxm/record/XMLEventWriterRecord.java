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
* mmacivor - September 09/2009 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.oxm.record;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.DomToXMLEventWriter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XMLEventWriterRecord extends MarshalRecord {
    private Map<String, String> prefixMapping;

    private XMLEventWriter xmlEventWriter;
    private XMLEventFactory xmlEventFactory;
    private DomToXMLEventWriter domToXMLEventWriter;

    public XMLEventWriterRecord(XMLEventWriter xmlEventWriter) {
        this.xmlEventWriter = xmlEventWriter;
        this.prefixMapping = new HashMap<String, String>();
        this.xmlEventFactory = XMLEventFactory.newInstance();
        this.domToXMLEventWriter = new DomToXMLEventWriter(xmlEventFactory);
    }
    
    public XMLEventWriter getXMLEventWriter() {
        return xmlEventWriter;
    }

    public void setXMLEventWriter(XMLEventWriter anXMLEventWriter) {
        this.xmlEventWriter = anXMLEventWriter;
    }

    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
        try {
            String namespaceURI = xPathFragment.getNamespaceURI();
            if(namespaceURI == null) {
                xmlEventWriter.add(xmlEventFactory.createAttribute(xPathFragment.getLocalName(), value));
            } else {
                String prefix = xPathFragment.getPrefix();
                if(prefix == null) {
                    xmlEventWriter.add(xmlEventFactory.createAttribute(xPathFragment.getPrefix(), namespaceURI, xPathFragment.getLocalName(), value));
                } else {
                    xmlEventWriter.add(xmlEventFactory.createAttribute(prefix, namespaceURI, xPathFragment.getLocalName(), value));
                }
            }
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }

    }
    
    public void attribute(String namespaceURI, String localName, String name, String value) {
        try {
            if(namespaceURI != null && namespaceURI.equals(XMLConstants.XMLNS_URL)) {
                if(localName.equals(XMLConstants.XMLNS)) {
                    xmlEventWriter.add(xmlEventFactory.createNamespace(value));
                }  else {
                    xmlEventWriter.add(xmlEventFactory.createNamespace(localName, value));
                }
            } else {
                NamespaceContext ctx = xmlEventWriter.getNamespaceContext();
                if(namespaceURI == null || namespaceURI.length() == 0) {
                    xmlEventWriter.add(xmlEventFactory.createAttribute(localName, value));
                } else {
                    xmlEventWriter.add(xmlEventFactory.createAttribute(xmlEventWriter.getNamespaceContext().getPrefix(namespaceURI), namespaceURI, localName, value));;
                }
            }
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }
    
    public void cdata(String value) {
        try {
            XMLEvent cdataEvent = this.xmlEventFactory.createCData(value);
            this.xmlEventWriter.add(cdataEvent);
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
       }
    }
    
    public void characters(String value) {
        try {
            XMLEvent charactersEvent = this.xmlEventFactory.createCharacters(value);
            this.xmlEventWriter.add(charactersEvent);
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }
    
    public void closeStartElement() {
    }

    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        super.openStartElement(xPathFragment, namespaceResolver);
        try {
            String namespaceURI = xPathFragment.getNamespaceURI();
            if(namespaceURI == null) {
                XMLEvent startElement = this.xmlEventFactory.createStartElement("", "", xPathFragment.getLocalName());
                xmlEventWriter.add(startElement);
                String defaultNamespace = xmlEventWriter.getNamespaceContext().getNamespaceURI(XMLConstants.EMPTY_STRING);
                if(defaultNamespace != null && defaultNamespace.length() > 0 ) {
                    this.xmlEventWriter.setDefaultNamespace("");
                    this.xmlEventWriter.add(xmlEventFactory.createNamespace(""));
                }

            } else {
                String prefix = xPathFragment.getPrefix();
                if(prefix == null) {
                    prefix = XMLConstants.EMPTY_STRING;
                }
                XMLEvent startElement = this.xmlEventFactory.createStartElement(prefix, namespaceURI, xPathFragment.getLocalName());
                xmlEventWriter.add(startElement);
            }
            writePrefixMappings();
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }
    
    public void element(String namespaceURI, String localName, String Name) {
        try {
            String prefix = "";
            if(Name.indexOf(':') != -1) {
                prefix = Name.substring(0, Name.indexOf(':'));
            }
            XMLEvent startElement = this.xmlEventFactory.createStartElement(prefix, namespaceURI, localName);
            this.xmlEventWriter.add(startElement);
            this.xmlEventWriter.add(this.xmlEventFactory.createEndElement(prefix, namespaceURI, localName));
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }    
    }
    
    public void endDocument() {
        try {
            XMLEvent endDoc = this.xmlEventFactory.createEndDocument();
            this.xmlEventWriter.add(endDoc);
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }
    
    public void endElement(XPathFragment pathFragment, NamespaceResolver namespaceResolver) {
        String namespaceURI = pathFragment.getNamespaceURI();
        String prefix = pathFragment.getPrefix();
        if(prefix == null) {
            prefix = "";
        }
        try {
            XMLEvent endElement = this.xmlEventFactory.createEndElement(prefix, namespaceURI, pathFragment.getLocalName());
            this.xmlEventWriter.add(endElement);
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }
    
    public void node(Node node, NamespaceResolver resolver) {
        try {
            if(node.getNodeType() == Node.DOCUMENT_NODE) {
                node = ((Document)node).getDocumentElement();
            }
            domToXMLEventWriter.writeToEventWriter(node, this.xmlEventWriter);
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }
    
    public void startDocument(String encoding, String version) {
        try {
            XMLEvent startDoc = this.xmlEventFactory.createStartDocument(encoding, version, false);
            this.xmlEventWriter.add(startDoc);
        } catch(Exception e) {
            throw XMLMarshalException.marshalException(e);
        }
    }
    
    private String getString(String string) {
        return string;
    }
    
    public void startPrefixMapping(String prefix, String namespaceUri) {
        this.prefixMapping.put(prefix, namespaceUri);
    }

    private void writePrefixMappings() {
        for(Map.Entry<String, String> entry:this.prefixMapping.entrySet()) {
            try {
                XMLEvent namespace = xmlEventFactory.createNamespace(entry.getKey(), entry.getValue());
                xmlEventWriter.add(namespace);
            } catch(XMLStreamException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }
        this.prefixMapping = new HashMap<String, String>();
    }

    
    public void namespaceDeclarations(NamespaceResolver namespaceResolver) {
    }
    
    public void startPrefixMappings(NamespaceResolver namespaceResolver) {
        if (namespaceResolver != null) {
            if(namespaceResolver.getDefaultNamespaceURI() != null) {
                startPrefixMapping(XMLConstants.EMPTY_STRING, namespaceResolver.getDefaultNamespaceURI());
            }
            Enumeration prefixes = namespaceResolver.getPrefixes();
            String prefix;
            String uri;
            while (prefixes.hasMoreElements()) {
                prefix = (String)prefixes.nextElement();
                uri = namespaceResolver.resolveNamespacePrefix(prefix);
                startPrefixMapping(prefix, uri);
            }
        }
    }
}
