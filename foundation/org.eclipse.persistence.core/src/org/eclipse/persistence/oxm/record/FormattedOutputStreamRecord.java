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
 *      Denise Smith - November 2, 2009
 ******************************************************************************/  
package org.eclipse.persistence.oxm.record;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.XMLFragmentReader;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <p>Use this type of MarshalRecord when the marshal target is an OutputStream and the
 * XML should be formatted with carriage returns and indenting. This type is only
 * used if the encoding of the OutputStream is UTF-8</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * FormattedOutputStreamRecord record = new FormattedOutputStreamRecord();<br>
 * record.setOutputStream(myOutputStream);<br>
 * xmlMarshaller.marshal(myObject, record);<br>
 * </code></p>
 * <p>If the marshal(OutputStream) and setFormattedOutput(true) method is called on
 * XMLMarshaller and the encoding is UTF-8, then the OutputStream is automatically wrapped
 * in a FormattedOutputStreamRecord.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * xmlMarshaller xmlMarshaller.setFormattedOutput(true);<br>
 * xmlMarshaller.marshal(myObject, myOutputStream);<br>
 * </code></p>
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */
public class FormattedOutputStreamRecord extends OutputStreamRecord {

    private byte[] tab;
    private int numberOfTabs;
    private boolean complexType;
    private boolean isLastEventText;

    public FormattedOutputStreamRecord() {
        super();
        numberOfTabs = 0;
        complexType = true;
        isLastEventText = false;
    }

    private byte[] tab() {
        if (tab == null) {
            String sTab = getMarshaller().getIndentString(); 
            // Escape the tab using writeValue
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writeValue(sTab, true, false, baos);
            tab = baos.toByteArray();
        }
        return tab;
    }

    /**
     * INTERNAL:
     */
    public void endDocument() {
        outputStreamWrite(CR);
    }

    /**
     * INTERNAL
     */
    public void writeHeader() {
        outputStreamWrite(getMarshaller().getXmlHeader().getBytes());
        outputStreamWrite(CR);
    }

    /**
     * INTERNAL:
     */
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        this.addPositionalNodes(xPathFragment, namespaceResolver);
        if (isStartElementOpen) {
            outputStreamWrite(CLOSE_ELEMENT);
        }
        if (!isLastEventText) {
            if (numberOfTabs > 0) {
                outputStreamWrite(CR);
            }
            outputStreamWriteTab();
        }
        isStartElementOpen = true;
        outputStreamWrite(OPEN_START_ELEMENT);
        outputStreamWrite(getNameForFragmentBytes(xPathFragment));
        numberOfTabs++;
        isLastEventText = false;
    }

    /**
     * INTERNAL:
     */
    public void element(XPathFragment frag) {
        isLastEventText = false;
        if (isStartElementOpen) {
            outputStreamWrite(CLOSE_ELEMENT);
            isStartElementOpen = false;
        }
        outputStreamWrite(CR);
        outputStreamWriteTab();
        super.element(frag);
    }

    /**
     * INTERNAL:
     */
    public void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        isLastEventText = false;
        numberOfTabs--;
        if (isStartElementOpen) {
            outputStreamWrite((byte) '/');
            outputStreamWrite((byte) '>');
            isStartElementOpen = false;
            return;
        }
        if (complexType) {
            outputStreamWrite(CR);
            outputStreamWriteTab();
        } else {
            complexType = true;
        }
        super.endElement(xPathFragment, namespaceResolver);
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
     * INTERNAL:
     */
    public void cdata(String value) {
        //Format the CDATA on it's own line
        if(isStartElementOpen) {
            outputStreamWrite(CLOSE_ELEMENT);
            isStartElementOpen = false;
        }
        super.cdata(value);
        complexType=false;
    }

    /**
     * Receive notification of a node.
     * @param node The Node to be added to the document
     * @param namespaceResolver The NamespaceResolver can be used to resolve the
     * namespace URI/prefix of the node
     */
    public void node(Node node, NamespaceResolver namespaceResolver, String newNamespace, String newName) {
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            Attr attr = (Attr) node;
            String resolverPfx = null;
            if (namespaceResolver != null) {
                resolverPfx = namespaceResolver.resolveNamespaceURI(attr.getNamespaceURI());
            }
            // If the namespace resolver contains a prefix for the attribute's URI,
            // use it instead of what is set on the attribute
            if (resolverPfx != null) {
                attribute(attr.getNamespaceURI(), Constants.EMPTY_STRING, resolverPfx+Constants.COLON+attr.getLocalName(), attr.getNodeValue());
            } else {
                attribute(attr.getNamespaceURI(), Constants.EMPTY_STRING, attr.getName(), attr.getNodeValue());
                // May need to declare the URI locally
                if (attr.getNamespaceURI() != null) {                    
                    namespaceDeclaration(attr.getPrefix(),  attr.getNamespaceURI());
                }
            }
        } else if (node.getNodeType() == Node.TEXT_NODE) {
            characters(node.getNodeValue());
        } else {
            try {
                FormattedOutputStreamRecordContentHandler handler = new FormattedOutputStreamRecordContentHandler();
                XMLFragmentReader xfragReader = new XMLFragmentReader(namespaceResolver);
                xfragReader.setContentHandler(handler);
                xfragReader.setProperty(Constants.LEXICAL_HANDLER_PROPERTY, handler);
                xfragReader.parse(node, newNamespace, newName);
            } catch (SAXException sex) {
                throw XMLMarshalException.marshalException(sex);
            }
        }
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
    private class FormattedOutputStreamRecordContentHandler extends OutputStreamRecordContentHandler {
        // --------------------- CONTENTHANDLER METHODS --------------------- //
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            try {
                if (isStartElementOpen) {
                    outputStreamWrite(CLOSE_ELEMENT);
                }
                if (!isLastEventText) {
                    outputStreamWrite(CR);
                    outputStreamWriteTab();
                }
                outputStreamWrite(OPEN_START_ELEMENT);
                outputStreamWrite(qName.getBytes(Constants.DEFAULT_XML_ENCODING));
                numberOfTabs++;
                isStartElementOpen = true;
                isLastEventText = false;
                // Handle attributes
                handleAttributes(atts);
                // Handle prefix mappings
                writePrefixMappings();
            } catch (UnsupportedEncodingException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            isLastEventText = false;
            numberOfTabs--;
            if (isStartElementOpen) {
                outputStreamWrite((byte) '/');
                outputStreamWrite((byte) '>');
                isStartElementOpen = false;
                complexType = true;
                return;
            }
            if (complexType) {
                outputStreamWrite(CR);
                outputStreamWriteTab();
            } else {
                complexType = true;
            }
            super.endElement(namespaceURI, localName, qName);
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
            if (isStartElementOpen) {
                outputStreamWrite(CLOSE_ELEMENT);
                outputStreamWrite(CR);
                isStartElementOpen = false;
            }
            writeComment(ch, start, length);
            complexType = false;
        }
    }

    private void outputStreamWriteTab() {
        for (int x = 0; x < numberOfTabs; x++) {
            outputStreamWrite(tab());
        }
    }

}