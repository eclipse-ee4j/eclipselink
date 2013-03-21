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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.record;

import java.io.CharArrayWriter;
import java.io.IOException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.XMLFragmentReader;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <p>Use this type of MarshalRecord when the marshal target is a Writer and the
 * XML should be formatted with carriage returns and indenting.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * FormattedWriterRecord formattedWriterRecord = new FormattedWriterRecord();<br>
 * formattedWriterRecord.setWriter(myWriter);<br>
 * xmlMarshaller.marshal(myObject, formattedWriterRecord);<br>
 * </code></p>
 * <p>If the marshal(Writer) and setFormattedOutput(true) method is called on
 * XMLMarshaller, then the Writer is automatically wrapped in a
 * FormattedWriterRecord.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * xmlMarshaller xmlMarshaller.setFormattedOutput(true);<br>
 * xmlMarshaller.marshal(myObject, myWriter);<br>
 * </code></p>
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */
public class FormattedWriterRecord extends WriterRecord {

    private String tab;
    private int numberOfTabs;
    private boolean complexType;
    private boolean isLastEventText;

    public FormattedWriterRecord() {
        super();
        numberOfTabs = 0;
        complexType = true;
        isLastEventText = false;
    }

    private String tab() {
        if (tab == null) {
            CharArrayWriter out = new CharArrayWriter();
            // Escape the tab using writeValue
            writeValue(getMarshaller().getIndentString(), false, out);
            out.close();
            tab = out.toString();
        }
        return tab;
    }

    /**
     * INTERNAL:
     */
    public void endDocument() {
        try {
            writer.write(Helper.cr());
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL
     */
    public void writeHeader() {
        try {
            writer.write(getMarshaller().getXmlHeader());
            writer.write(Helper.cr());
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        this.addPositionalNodes(xPathFragment, namespaceResolver);
        try {
            if (isStartElementOpen) {
                writer.write('>');
            }
            if (!isLastEventText) {
                if (numberOfTabs > 0) {
                    writer.write(Helper.cr());
                }
                for (int x = 0; x < numberOfTabs; x++) {
                    writer.write(tab());
                }
            }
            isStartElementOpen = true;
            writer.write('<');
            writer.write(getNameForFragment(xPathFragment));
            numberOfTabs++;
            isLastEventText = false;
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
            	writer.write(tab());
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
        try {
            isLastEventText = false;
            numberOfTabs--;
            if (isStartElementOpen) {
                writer.write('/');
                writer.write('>');
                isStartElementOpen = false;
                return;
            }
            if (complexType) {
                writer.write(Helper.cr());
                for (int x = 0; x < numberOfTabs; x++) {
                	writer.write(tab());
                }
            } else {
                complexType = true;
            }
            super.endElement(xPathFragment, namespaceResolver);
        } catch (IOException e) {
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
     * INTERNAL:
     */
    public void cdata(String value) {
        //Format the CDATA on it's own line
        try {
            if(isStartElementOpen) {
                writer.write('>');
                isStartElementOpen = false;
            }
            super.cdata(value);
            complexType=false;
        }catch(IOException ex) {
            throw XMLMarshalException.marshalException(ex);
        }
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
                    namespaceDeclaration(attr.getPrefix(), attr.getNamespaceURI());
                    this.getNamespaceResolver().put(attr.getPrefix(), attr.getNamespaceURI());
                }
            }
        } else if (node.getNodeType() == Node.TEXT_NODE) {
            characters(node.getNodeValue());
        } else {
            try {
                FormattedWriterRecordContentHandler wrcHandler = new FormattedWriterRecordContentHandler();
                XMLFragmentReader xfragReader = new XMLFragmentReader(namespaceResolver);
                xfragReader.setContentHandler(wrcHandler);
                xfragReader.setProperty(Constants.LEXICAL_HANDLER_PROPERTY, wrcHandler);
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
                    	writer.write(tab());
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
                    	writer.write(tab());
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
