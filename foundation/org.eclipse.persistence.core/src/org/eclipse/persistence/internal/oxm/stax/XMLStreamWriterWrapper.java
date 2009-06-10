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
 *     rbarkhouse - 2009-06-09 14:54:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.stax;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.persistence.oxm.XMLMarshaller;

/**
 * <p><b>INTERNAL</b></p>
 * 
 * <p><b>Purpose:</b> This implementation of XMLStreamWriter will bypass the generation of an XML header
 * string if the underlying XMLMarshaller is configured to marshal to an XML fragment.</p>
 * 
 * <p><b>Responsibilities:</b>
 * <ul>
 *  <li>Implement XMLStreamWriter interface</li>
 *  <li>Delegate calls to the wrapped XMLStreamWriter</li>
 *  <li>Override writeStartDocument/writeEndDocument to be no-ops if XMLMarshaller.isFragment()</li>
 * </ul></p>
 * 
 * @author rbarkhouse
 * @since EclipseLink 2.0
 */
public class XMLStreamWriterWrapper implements XMLStreamWriter {

    private XMLStreamWriter xsw;
    private XMLMarshaller xmlMarshaller;
    
    public XMLStreamWriterWrapper(XMLStreamWriter xsw, XMLMarshaller xmlMarshaller) {
        this.xsw = xsw;
        this.xmlMarshaller = xmlMarshaller;
    }
    
    public void close() throws XMLStreamException {
        xsw.close();
    }

    public void flush() throws XMLStreamException {
        xsw.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return xsw.getNamespaceContext();
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return xsw.getPrefix(uri);
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return xsw.getProperty(name);
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        xsw.setDefaultNamespace(uri);
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        xsw.setNamespaceContext(context);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        xsw.setPrefix(prefix, uri);
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        xsw.writeAttribute(localName, value);
    }

    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        xsw.writeAttribute(namespaceURI, localName, value);
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        xsw.writeAttribute(prefix, namespaceURI, localName, value);
    }

    public void writeCData(String data) throws XMLStreamException {
        xsw.writeCData(data);
    }

    public void writeCharacters(String text) throws XMLStreamException {
        xsw.writeCharacters(text);
    }

    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        xsw.writeCharacters(text, start, len);
    }

    public void writeComment(String data) throws XMLStreamException {
        xsw.writeComment(data);
    }

    public void writeDTD(String dtd) throws XMLStreamException {
        xsw.writeDTD(dtd);
    }

    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        xsw.writeDefaultNamespace(namespaceURI);
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        xsw.writeEmptyElement(localName);
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        xsw.writeEmptyElement(namespaceURI, localName);
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        xsw.writeEmptyElement(prefix, localName, namespaceURI);
    }

    public void writeEndDocument() throws XMLStreamException {
        if (!xmlMarshaller.isFragment()) {
            xsw.writeEndDocument();
        }
    }

    public void writeEndElement() throws XMLStreamException {
        xsw.writeEndElement();
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        xsw.writeEntityRef(name);
    }

    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        xsw.writeNamespace(prefix, namespaceURI);
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        xsw.writeProcessingInstruction(target);
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        xsw.writeProcessingInstruction(target, data);
    }

    public void writeStartDocument() throws XMLStreamException {
        if (!xmlMarshaller.isFragment()) {
            xsw.writeStartDocument();
        }
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        if (!xmlMarshaller.isFragment()) {
            xsw.writeStartDocument(version);
        }
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        if (!xmlMarshaller.isFragment()) {
            xsw.writeStartDocument(encoding, version);
        }
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        xsw.writeStartElement(localName);
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        xsw.writeStartElement(namespaceURI, localName);
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        xsw.writeStartElement(prefix, localName, namespaceURI);
    }

}