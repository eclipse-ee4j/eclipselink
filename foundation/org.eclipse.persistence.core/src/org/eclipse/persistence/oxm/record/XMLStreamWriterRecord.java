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
* mmacivor - June 24/2009 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.oxm.record;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.DomToXMLStreamWriter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
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
    private static int COUNTER = 0;
    private static String EMPTY_STRING = "";
    private DomToXMLStreamWriter domToStreamWriter;
    private Map<String, String> prefixMapping;

    private XMLStreamWriter xmlStreamWriter;

    public XMLStreamWriterRecord(XMLStreamWriter xmlStreamWriter) {
        this.xmlStreamWriter = xmlStreamWriter;
        this.domToStreamWriter = new DomToXMLStreamWriter();
        this.prefixMapping = new HashMap<String, String>();
    }
    public XMLStreamWriter getXMLStreamWriter() {
        return xmlStreamWriter;
    }

    public void setXMLStreamWriter(XMLStreamWriter anXMLStreamWriter) {
        this.xmlStreamWriter = anXMLStreamWriter;
    }

    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
        try {
            String namespaceURI = resolveNamespacePrefix(xPathFragment, namespaceResolver);
            if(namespaceURI == null) {
                xmlStreamWriter.writeAttribute(xPathFragment.getLocalName(), value);
            } else {
                String prefix = xPathFragment.getPrefix();
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

    public void attribute(String namespaceURI, String localName, String name, String value) {
        try {
            if(namespaceURI != null && namespaceURI.equals(XMLConstants.XMLNS_URL)) {
                if(localName.equals(XMLConstants.XMLNS)) {
                    xmlStreamWriter.writeDefaultNamespace(value);
                }  else {
                    xmlStreamWriter.writeNamespace(localName, value);
                }
            } else {
                NamespaceContext ctx = xmlStreamWriter.getNamespaceContext();
                if(namespaceURI == null || namespaceURI.equals("")) {
                    xmlStreamWriter.writeAttribute(localName, value);
                } else {
                    xmlStreamWriter.writeAttribute(xmlStreamWriter.getNamespaceContext().getPrefix(namespaceURI), namespaceURI, localName, value);
                }
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

    public void closeStartElement() {
    }

    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        super.openStartElement(xPathFragment, namespaceResolver);
        try {
            String namespaceURI = resolveNamespacePrefix(xPathFragment, namespaceResolver);
            if(namespaceURI == null) {
                xmlStreamWriter.writeStartElement("", xPathFragment.getLocalName(), "");
                String defaultNamespace = xmlStreamWriter.getNamespaceContext().getNamespaceURI("");
                if(defaultNamespace != null && !defaultNamespace.equals("")) {
                    xmlStreamWriter.writeDefaultNamespace("");
                }

            } else {
                String prefix = xPathFragment.getPrefix();
                if(prefix == null) {
                    prefix = "";
                }
                xmlStreamWriter.writeStartElement(prefix, xPathFragment.getLocalName(), namespaceURI);
            }
            writePrefixMappings();
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }

    }

    public void element(String namespaceURI, String localName, String Name) {
        try {
            xmlStreamWriter.writeStartElement(Name);
            xmlStreamWriter.writeEndElement();
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void endDocument() {
        try {
            xmlStreamWriter.writeEndDocument();
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

    public void node(Node node, NamespaceResolver resolver) {
        try {
            if(node.getNodeType() == Node.DOCUMENT_NODE) {
                node = ((Document)node).getDocumentElement();
            }
            domToStreamWriter.writeToStream(node, this.xmlStreamWriter);
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    public void startDocument(String encoding, String version) {
        try {
            xmlStreamWriter.writeStartDocument(encoding, version);
        } catch(XMLStreamException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    private String getString(String string) {
        if(null == string) {
            return EMPTY_STRING;
        } else {
            return string;
        }
    }

    public void startPrefixMapping(String prefix, String namespaceUri) {
        this.prefixMapping.put(prefix, namespaceUri);
    }

    private void writePrefixMappings() {
        for(String prefix:this.prefixMapping.keySet()) {
            try {
                xmlStreamWriter.writeNamespace(prefix, prefixMapping.get(prefix));
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
                startPrefixMapping("", namespaceResolver.getDefaultNamespaceURI());
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
