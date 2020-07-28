/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.oxm.record;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.XMLFragmentReader;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
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
    private boolean isLastEventText;

    public JSONFormattedWriterRecord() {
        numberOfTabs = 0;
        isLastEventText = false;
    }

    public JSONFormattedWriterRecord(OutputStream outputStream){
        this();
        this.writer = new OutputStreamOutput(outputStream);
    }

    public JSONFormattedWriterRecord(OutputStream outputStream, String callbackName){
        this(outputStream);
        setCallbackName(callbackName);
    }

    public JSONFormattedWriterRecord(Writer writer){
        this();
        setWriter(writer);
    }

    public JSONFormattedWriterRecord(Writer writer, String callbackName){
        this(writer);
        setCallbackName(callbackName);
    }

    private String tab() {
        if (tab == null) {
            tab = getMarshaller().getIndentString();
        }
        return tab;
    }

    public void startDocument(String encoding, String version) {
        super.startDocument(encoding, version);
        numberOfTabs++;;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void endDocument() {
         numberOfTabs--;
         super.endDocument();
    }

    @Override
    protected void closeComplex() throws IOException {
        writer.writeCR();
        for (int x = 0; x < numberOfTabs; x++) {
            writeValue(tab(), false);
        }
        writer.write('}');
    }

    /**
     * INTERNAL:
     */
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        try {
            if(level.isFirst()) {
                level.setFirst(false);
            } else {
                writer.write(',');
            }
            if(xPathFragment.nameIsText()){
                if(level.isCollection() && level.isEmptyCollection()) {
                    writer.write('[');
                    writer.write(' ');
                    level.setEmptyCollection(false);
                    level.setNeedToOpenComplex(false);
                    level = new Level(true, true, false, level);
                    numberOfTabs++;
                    return;
                }
            }

            if(level.isNeedToOpenComplex()){
                if (!level.isNestedArray()) {
                    writer.write('{');
                }
                level.setNeedToOpenComplex(false);
                level.setNeedToCloseComplex(true);
            }
            if (!isLastEventText) {
                if(level.isCollection() && !level.isEmptyCollection()) {
                    writer.write(' ');
                } else {
                    writer.writeCR();
                    for (int x = 0; x < numberOfTabs; x++) {
                        writeValue(tab(), false);
                    }
                }
            }

          //write the key unless this is a a non-empty collection
            if(!(level.isCollection() && !level.isEmptyCollection())){
                if (!level.isNestedArray()) {
                    super.writeKey(xPathFragment);
                }
                //if it is the first thing in the collection also add the [
                if(level.isCollection() && level.isEmptyCollection()){
                     writer.write('[');
                     writer.write(' ');
                     level.setEmptyCollection(false);
                }
            }

            numberOfTabs++;
            isLastEventText = false;
            charactersAllowed = true;
            if (xPathFragment.getXMLField() != null && xPathFragment.getXMLField().isNestedArray() && this.marshaller.getJsonTypeConfiguration().isJsonDisableNestedArrayName()) {
                level = new Level(true, true, true, level);
            } else {
                level = new Level(true, true, false, level);
            }
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void element(XPathFragment frag) {
    }

    protected void writeListSeparator() throws IOException{
        super.writeListSeparator();
        writer.write(' ');
    }

    protected void writeSeparator() throws IOException{
        writer.write(' ');
        writer.write(Constants.COLON);
        writer.write(' ');
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
        if(null == level) {
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
    protected void endEmptyCollection(){
        super.endCollection();
    }

    @Override
    public void endCollection() {
        try {
            writer.write(' ');
            super.endCollection();
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
                attribute(attr.getNamespaceURI(), Constants.EMPTY_STRING, resolverPfx+Constants.COLON+attr.getLocalName(), attr.getNodeValue());
            } else {
                attribute(attr.getNamespaceURI(), Constants.EMPTY_STRING, attr.getName(), attr.getNodeValue());
                // May need to declare the URI locally
                if (attr.getNamespaceURI() != null) {
                    attribute(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, Constants.EMPTY_STRING, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON + attr.getPrefix(), attr.getNamespaceURI());
                    this.getNamespaceResolver().put(attr.getPrefix(), attr.getNamespaceURI());
                }
            }
        } else if (node.getNodeType() == Node.TEXT_NODE) {
            characters(node.getNodeValue(), false, false);
        } else {
            try {
                JSONFormattedWriterRecordContentHandler wrcHandler = new JSONFormattedWriterRecordContentHandler();
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
        writer.writeCR();
        for (int x = 0; x < numberOfTabs; x++) {
            writeValue(tab(), false);
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
    private class JSONFormattedWriterRecordContentHandler extends JSONWriterRecordContentHandler {
        // --------------------- CONTENTHANDLER METHODS --------------------- //
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            XPathFragment xPathFragment = new XPathFragment(localName);
            xPathFragment.setNamespaceURI(namespaceURI);

            JSONFormattedWriterRecord.this.endElement(xPathFragment, namespaceResolver);
        }


    }

}
