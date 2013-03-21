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
* mmacivor - June 24/2009 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.oxm.record;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.DomToXMLStreamWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <p>Use this type of MarshalRecord when the marshal target is an XMLStreamWriter
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * XMLStreamWriterRecord writerRecord = new XMLStreamWriterRecord(xmlStreamWriter);<br>
 * xmlMarshaller.marshal(myObject, writerRecord);<br>
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */
public class XMLStreamWriterRecord extends MarshalRecord {

    private DomToXMLStreamWriter domToStreamWriter;
    private Map<String, String> prefixMapping;
    private NamespaceResolver namespaceResolver;
    private XMLStreamWriter xmlStreamWriter;

    public XMLStreamWriterRecord(XMLStreamWriter xmlStreamWriter) {
        this.xmlStreamWriter = xmlStreamWriter;
    }

    public XMLStreamWriter getXMLStreamWriter() {
        return xmlStreamWriter;
    }

    public void setXMLStreamWriter(XMLStreamWriter anXMLStreamWriter) {
        this.xmlStreamWriter = anXMLStreamWriter;
    }

    private DomToXMLStreamWriter getDomToXMLStreamWriter() {
        if(null == domToStreamWriter) {
            domToStreamWriter = new DomToXMLStreamWriter();
        }
        return domToStreamWriter;
    }

    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
        try {
            String namespaceURI = xPathFragment.getNamespaceURI();
            if(namespaceURI == null) {
                xmlStreamWriter.writeAttribute(xPathFragment.getLocalName(), value);
                
            } else {
                String prefix = getPrefixForFragment(xPathFragment);
                if(prefix == null) {
                    xmlStreamWriter.writeAttribute(namespaceURI, xPathFragment.getLocalName(), value);
                } else {
                    xmlStreamWriter.writeAttribute(prefix, namespaceURI, xPathFragment.getLocalName(), value);
                }
            }
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void defaultNamespaceDeclaration(String defaultNamespace){
        try{
            xmlStreamWriter.writeDefaultNamespace(defaultNamespace);
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }
    
    public void namespaceDeclaration(String prefix, String namespaceURI){
        try{
            xmlStreamWriter.writeNamespace(prefix, namespaceURI);
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }
    
    public void attributeWithoutQName(String namespaceURI, String localName, String prefix, String value){    
        try {          
            if(namespaceURI == null || namespaceURI.length() == 0) {
                xmlStreamWriter.writeAttribute(localName, value);
            } else {
                xmlStreamWriter.writeAttribute(xmlStreamWriter.getNamespaceContext().getPrefix(namespaceURI), namespaceURI, localName, value);
            }
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }       
    }
    
    public void attribute(String namespaceURI, String localName, String name, String value) {
        try {          
             if(namespaceURI == null || namespaceURI.length() == 0) {
                 xmlStreamWriter.writeAttribute(localName, value);
             } else {
                xmlStreamWriter.writeAttribute(xmlStreamWriter.getNamespaceContext().getPrefix(namespaceURI), namespaceURI, localName, value);
             }
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void cdata(String value) {
        try {
            xmlStreamWriter.writeCData(value);
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void characters(String value) {
        try {
            xmlStreamWriter.writeCharacters(value);
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }
    
    public boolean isNamespaceAware() {
    	return true;    	
    }

    public void closeStartElement() {
    }

    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        super.openStartElement(xPathFragment, namespaceResolver);
        try {
            String namespaceURI = xPathFragment.getNamespaceURI();
            if(namespaceURI == null) {
                NamespaceContext namespaceContext = xmlStreamWriter.getNamespaceContext();
                if(null == namespaceContext) {
                    xmlStreamWriter.writeStartElement(xPathFragment.getLocalName());
                } else {
                    String defaultNamespace = namespaceContext.getNamespaceURI(Constants.EMPTY_STRING);                    
                    xmlStreamWriter.writeStartElement(Constants.EMPTY_STRING, xPathFragment.getLocalName(), Constants.EMPTY_STRING);
                    if(defaultNamespace != null && defaultNamespace.length() > 0 ) {
                        xmlStreamWriter.writeDefaultNamespace(Constants.EMPTY_STRING);
                    }
                }
            } else {
                String prefix = getPrefixForFragment(xPathFragment);
                if(prefix == null) {
                    prefix = Constants.EMPTY_STRING;
                }
                xmlStreamWriter.writeStartElement(prefix, xPathFragment.getLocalName(), namespaceURI);
            }
            writePrefixMappings();
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void element(XPathFragment frag) {
        try {
            xmlStreamWriter.writeStartElement(getNameForFragment(frag));
            xmlStreamWriter.writeEndElement();
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void endDocument() {
        try {
            xmlStreamWriter.writeEndDocument();
            xmlStreamWriter.flush();
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void endElement(XPathFragment pathFragment, NamespaceResolver namespaceResolver) {
        try {
            xmlStreamWriter.writeEndElement();
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void node(Node node, NamespaceResolver resolver,String uri, String name) {
        try {
            if(node.getNodeType() == Node.DOCUMENT_NODE) {
                node = ((Document)node).getDocumentElement();
            }
            getDomToXMLStreamWriter().writeToStream(node, uri, name , this.xmlStreamWriter);
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void startDocument(String encoding, String version) {
        try {
            if(Constants.DEFAULT_XML_ENCODING.equals(encoding)) {
                xmlStreamWriter.writeStartDocument(version);
            } else {
                xmlStreamWriter.writeStartDocument(encoding, version);
            }
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void startPrefixMapping(String prefix, String namespaceUri) {
        if(null == this.prefixMapping) {
            this.prefixMapping = new HashMap<String, String>(); 
        }
        this.prefixMapping.put(prefix, namespaceUri);
    }

    private void writePrefixMappings() {
        try {
            if(null != namespaceResolver) {
                String defaultNamespace = namespaceResolver.getDefaultNamespaceURI();
                if(defaultNamespace != null) {
                    xmlStreamWriter.writeNamespace(Constants.EMPTY_STRING, defaultNamespace);
                }
                if(namespaceResolver.hasPrefixesToNamespaces()) {
                    for(Map.Entry<String, String> entry:this.namespaceResolver.getPrefixesToNamespaces().entrySet()) {
                        xmlStreamWriter.writeNamespace(entry.getKey(), entry.getValue());
                    }
                }
                namespaceResolver = null;
            }
            if(null != prefixMapping) {
                for(Map.Entry<String, String> entry:this.prefixMapping.entrySet()) {
                    xmlStreamWriter.writeNamespace(entry.getKey(), entry.getValue());
                }
                prefixMapping = null;
            }
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void namespaceDeclarations(NamespaceResolver namespaceResolver) {
    }

    public void startPrefixMappings(NamespaceResolver namespaceResolver) {
        this.namespaceResolver = namespaceResolver;
    }

}