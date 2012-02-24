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
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm.record;

import java.io.IOException;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.XMLFragmentReader;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <p>Use this type of MarshalRecord when the marshal target is a Writer and the
 * JSON should be formatted with carriage returns and indenting.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * JSONFormattedWriterRecord jsonFormattedRecord = new JSONFormattedWriterRecord();<br>
 * jsonFormattedWriterRecord.setWriter(myWriter);<br>
 * xmlMarshaller.marshal(myObject, jsonFormattedWriterRecord);<br>
 * </code></p>
 * <p>If the marshal(Writer) and setMediaType(MediaType.APPLICATION_JSON) and
 *  setFormattedOutput(true) method is called on XMLMarshaller, then the Writer
 *  is automatically wrapped in a JSONFormattedWriterRecord.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * xmlMarshaller.setMediaType(MediaType.APPLICATION_JSON);
 * xmlMarshaller.setFormattedOutput(true);<br>
 * xmlMarshaller.marshal(myObject, myWriter);<br>
 * </code></p>
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */
public class JSONFormattedWriterRecord extends JSONWriterRecord {

    private String tab;
    private int numberOfTabs;
    private boolean complexType;
    private boolean isLastEventText;

    public JSONFormattedWriterRecord() {
        numberOfTabs = 1;
        complexType = true;
        isLastEventText = false;
    }

    private String tab() {
        if (tab == null) {
            tab = getMarshaller().getIndentString();
        }
        return tab;
    }
    /**
     * INTERNAL:
     */
    @Override
    public void endDocument() {
        try {
            if(levels.size() != 1) {
                writer.write(Helper.cr());
            }
            super.endDocument();
            if(levels.isEmpty()) {
                writer.write(Helper.cr());
            }
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    @Override
    protected void closeComplex() throws IOException {
        writer.write(Helper.cr());
        for (int x = 0; x < numberOfTabs; x++) {
            writeValue(tab());
        }
        writer.write('}');
    }

    /**
     * INTERNAL:
     */
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        try {
        	
            Level position = null;
            if(levels.isEmpty()) {
                levels.push(new Level(true, true));
            } else {
                position = levels.peek();
                levels.push(new Level(true, true));
                if(position.isFirst()) {
                    position.setFirst(false);
                } else {
                    writer.write(',');
                }
            }

            if(xPathFragment.nameIsText()){
               if(position != null && position.isCollection() && position.isEmptyCollection()) {
            	    if(!charactersAllowed){
              		     throw JAXBException.jsonValuePropertyRequired("[");   
              	    }
                    writer.write('[');
                    writer.write(' ');
                    position.setEmptyCollection(false);
                    position.setNeedToOpenComplex(false);
                    return;
                }
            }

            this.addPositionalNodes(xPathFragment, namespaceResolver);
            if(position == null || !position.isCollection() || position.isEmptyCollection()){
                if(position.isNeedToOpenComplex()){
                    writer.write('{');
                    position.setNeedToOpenComplex(false);
                    position.setNeedToCloseComplex(true);
                }
            }
            if (!isLastEventText) {
                if(position.isCollection() && !position.isEmptyCollection()) {
                    writer.write(' ');
                } else {
                    writer.write(Helper.cr());
                    for (int x = 0; x < numberOfTabs; x++) {
                        writeValue(tab());
                    }
                }
            }
            if(position == null || !position.isCollection() || position.isEmptyCollection()){
                super.writeKey(xPathFragment);

                if(position != null && position.isCollection()) {
                    writer.write('[');
                    writer.write(' ');
                }
                if(position !=null && position.isEmptyCollection()){
                    position.setEmptyCollection(false);
                }
            }

            numberOfTabs++;
            isLastEventText = false;
            charactersAllowed = true;
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void element(XPathFragment frag) {
        try {
            isLastEventText = false;
            if (isStartElementOpen) {
                writer.write('>');
                isStartElementOpen = false;
            }
            writer.write(Helper.cr());
            for (int x = 0; x < numberOfTabs; x++) {
                writeValue(tab());
            }
            super.element(frag);
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        isLastEventText = false;
        numberOfTabs--;
        super.endElement(xPathFragment, namespaceResolver);
    }

    @Override
    public void startCollection() {
        if(levels.isEmpty()) {
            try {
                super.startCollection();
                writer.write(' ');
            } catch(IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        } else {
            super.startCollection();
        }
    }

    @Override
    public void endCollection() {
        try {
            writer.write(' ');
            super.endCollection();
            if(levels.size() == 1) {
                writer.write(Helper.cr());
            }
        } catch(IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void characters(String value) {
        super.characters(value);
        isLastEventText = true;
        complexType = false;
    }

    /**
     * Receive notification of a node.
     * @param node The Node to be added to the document
     * @param namespaceResolver The NamespaceResolver can be used to resolve the
     * namespace URI/prefix of the node
     */
    public void node(Node node, NamespaceResolver namespaceResolver) {
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            Attr attr = (Attr) node;
            String resolverPfx = null;
            if (getNamespaceResolver() != null) {
                resolverPfx = getNamespaceResolver().resolveNamespaceURI(attr.getNamespaceURI());
            }
            // If the namespace resolver contains a prefix for the attribute's URI,
            // use it instead of what is set on the attribute
            if (resolverPfx != null) {
                attribute(attr.getNamespaceURI(), XMLConstants.EMPTY_STRING, resolverPfx+XMLConstants.COLON+attr.getLocalName(), attr.getNodeValue());
            } else {
                attribute(attr.getNamespaceURI(), XMLConstants.EMPTY_STRING, attr.getName(), attr.getNodeValue());
                // May need to declare the URI locally
                if (attr.getNamespaceURI() != null) {
                    attribute(XMLConstants.XMLNS_URL, XMLConstants.EMPTY_STRING,XMLConstants.XMLNS + XMLConstants.COLON + attr.getPrefix(), attr.getNamespaceURI());
                    this.getNamespaceResolver().put(attr.getPrefix(), attr.getNamespaceURI());
                }
            }
        } else if (node.getNodeType() == Node.TEXT_NODE) {
            nonStringCharacters(node.getNodeValue());
        } else {
            try {
                FormattedWriterRecordContentHandler wrcHandler = new FormattedWriterRecordContentHandler();
                XMLFragmentReader xfragReader = new XMLFragmentReader(namespaceResolver);
                xfragReader.setContentHandler(wrcHandler);
                xfragReader.setProperty("http://xml.org/sax/properties/lexical-handler", wrcHandler);
                xfragReader.parse(node);
            } catch (SAXException sex) {
                throw XMLMarshalException.marshalException(sex);
            }
        }
    }

    @Override
    protected void writeKey(XPathFragment xPathFragment) throws IOException {
        writer.write(Helper.cr());
        for (int x = 0; x < numberOfTabs; x++) {
            writeValue(tab());
        }
        super.writeKey(xPathFragment);
    }


    /**
     * This class will typically be used in conjunction with an XMLFragmentReader.
     * The XMLFragmentReader will walk a given XMLFragment node and report events
     * to this class - the event's data is then written to the enclosing class'
     * writer.
     *
     * @see org.eclipse.persistence.internal.oxm.record.XMLFragmentReader
     * @see org.eclipse.persistence.oxm.record.WriterRecord.WriterRecordContentHandler
     */
    private class FormattedWriterRecordContentHandler extends WriterRecordContentHandler {
        // --------------------- CONTENTHANDLER METHODS --------------------- //
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            try {
                if (isStartElementOpen) {
                    writer.write('>');
                }
                if (!isLastEventText) {
                    writer.write(Helper.cr());
                    for (int x = 0; x < numberOfTabs; x++) {
                        writeValue(tab());
                    }
                }
                writer.write('<');
                writer.write(qName);
                numberOfTabs++;
                isStartElementOpen = true;
                isLastEventText = false;
                // Handle attributes
                handleAttributes(atts);
                // Handle prefix mappings
                writePrefixMappings();
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            try {
                isLastEventText = false;
                numberOfTabs--;
                if (isStartElementOpen) {
                    writer.write('/');
                    writer.write('>');
                    isStartElementOpen = false;
                    complexType = true;
                    return;
                }
                if (complexType) {
                    writer.write(Helper.cr());
                    for (int x = 0; x < numberOfTabs; x++) {
                        writeValue(tab());
                    }
                } else {
                    complexType = true;
                }
                super.endElement(namespaceURI, localName, qName);
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            if (isProcessingCData) {
                cdata(new String (ch, start, length));
                return;
            }
            if (new String(ch).trim().length() == 0) {
                return;
            }
            super.characters(ch, start, length);
            isLastEventText = true;
            complexType = false;
        }

    // --------------------- LEXICALHANDLER METHODS --------------------- //
    public void comment(char[] ch, int start, int length) throws SAXException {
            try {
                if (isStartElementOpen) {
                    writer.write('>');
                    writer.write(Helper.cr());
                    isStartElementOpen = false;
                }
                writeComment(ch, start, length);
                complexType = false;
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }
    }

}