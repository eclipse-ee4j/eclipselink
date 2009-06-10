/*******************************************************************************
* Copyright (c) 1998, 2008 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* mmacivor - June 09/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.stax;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


/**
 * INTERNAL:
 * <p><b>Purpose:</b> Provide a wrapper for an XMLStreamReader. This wrapper will delegate the majority of calls to a wrapper
 * XMLStreamReader. Used to avoid an NPE when doing a Transformation from a StAXSource.
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement XMLStreamReader interface</li>
 * <li>Delegate calls down to the wrapper XMLStreamReader</li>
 * <li>Override getNamespaceURI to return empty string instead of null to avoid NPE during transform</li>
 * </ul> 
 * @author mmacivor
 *
 */
public class XMLStreamReaderWrapper implements XMLStreamReader {
    private XMLStreamReader xmlStreamReader;
    public XMLStreamReaderWrapper(XMLStreamReader wrappedReader) {
        this.xmlStreamReader = wrappedReader;
    }
    public Object getProperty(java.lang.String name) throws java.lang.IllegalArgumentException {
        return this.xmlStreamReader.getProperty(name);
    }
    public boolean hasNext() throws XMLStreamException {
        return this.xmlStreamReader.hasNext();
    }
    public void close() throws XMLStreamException {
        this.xmlStreamReader.close();
    }
    public String getNamespaceURI(String prefix) {
        return this.xmlStreamReader.getNamespaceURI(prefix);
    }
    public boolean isStartElement() {
        return this.xmlStreamReader.isStartElement();
    }
    public boolean isEndElement() {
        return this.xmlStreamReader.isEndElement();
    }
    public boolean isCharacters() {
        return this.xmlStreamReader.isCharacters();
    }
    public boolean isWhiteSpace() {
        return this.xmlStreamReader.isWhiteSpace();
    }
    public String getAttributeValue(String namespaceURI, String localName) {
        return this.xmlStreamReader.getAttributeValue(namespaceURI, localName);
    }
    public int getAttributeCount() {
        return this.xmlStreamReader.getAttributeCount();
    }
    public QName getAttributeName(int index) {
        return this.xmlStreamReader.getAttributeName(index);
    }
    public String getAttributeNamespace(int index) {
        return this.xmlStreamReader.getAttributeNamespace(index);
    }
    public String getAttributeLocalName(int index) {
        return this.xmlStreamReader.getAttributeLocalName(index);
    }
    public String getAttributePrefix(int index) {
        return this.xmlStreamReader.getAttributePrefix(index);
    }
    public String getAttributeType(int index) {
        return this.xmlStreamReader.getAttributeType(index);
    }
    public String getAttributeValue(int index) {
        return this.xmlStreamReader.getAttributeValue(index);
    }
    public int getNamespaceCount() {
        return this.xmlStreamReader.getNamespaceCount();
    }
    public boolean isAttributeSpecified(int index) {
        return this.xmlStreamReader.isAttributeSpecified(index);
    }
    public String getNamespacePrefix(int index) {
        return this.xmlStreamReader.getNamespacePrefix(index);
    }
    public String getNamespaceURI(int index) {
        String value = xmlStreamReader.getNamespaceURI(index);
        if(value == null) {
            value = "";
        }
        return value;
    }
    public NamespaceContext getNamespaceContext() {
        return this.xmlStreamReader.getNamespaceContext();
    }
    public int getEventType() {
        return this.xmlStreamReader.getEventType();
    }
    public String getText() {
        return this.xmlStreamReader.getText();
    }
    public char[] getTextCharacters() {
        return this.xmlStreamReader.getTextCharacters();
    }
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        return this.xmlStreamReader.getTextCharacters(sourceStart, target, targetStart, length);
    }
    public int getTextStart() {
        return this.xmlStreamReader.getTextStart();
    }
    public int getTextLength() {
        return this.xmlStreamReader.getTextLength();
    }
    public String getEncoding() {
        return this.xmlStreamReader.getEncoding();
    }
    public boolean hasText() {
        return this.xmlStreamReader.hasText();
    }
    public Location getLocation() {
        return this.xmlStreamReader.getLocation();
    }
    public QName getName() {
        return this.xmlStreamReader.getName();
    }
    public String getLocalName() {
        return this.xmlStreamReader.getLocalName();
    }
    public boolean hasName() {
        return this.xmlStreamReader.hasName();
    }
    public String getNamespaceURI() {
        return this.xmlStreamReader.getNamespaceURI();
    }
    public String getPrefix() {
        return this.xmlStreamReader.getPrefix();
    }
    public String getVersion() {
        return this.xmlStreamReader.getVersion();
    }
    public boolean isStandalone() {
        return this.xmlStreamReader.isStandalone();
    }
    public boolean standaloneSet() {
        return this.xmlStreamReader.standaloneSet();
    }
    public String getCharacterEncodingScheme() {
        return this.xmlStreamReader.getCharacterEncodingScheme();
    }
    public String getPITarget() {
        return this.xmlStreamReader.getPITarget();
    }
    public String getPIData() {
        return this.xmlStreamReader.getPIData();
    }
    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        this.xmlStreamReader.require(type, namespaceURI, localName);
    }
    public String getElementText() throws XMLStreamException {
        return this.xmlStreamReader.getElementText();
    }
    public int nextTag() throws XMLStreamException {
        return this.xmlStreamReader.nextTag();
    }
    public int next() throws XMLStreamException {
        return this.xmlStreamReader.next();
    }

}