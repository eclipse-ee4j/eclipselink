/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.oxm.record;

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
    private final String cr = Constants.cr();

    private static final String DEFAULT_TAB = "   ".intern();

    public FormattedWriterRecord() {
        super();
        numberOfTabs = 0;
        complexType = true;
        isLastEventText = false;
    }

    private String tab() {
        if (tab == null) {

            if (DEFAULT_TAB.equals(getMarshaller().getIndentString())) {
                tab = DEFAULT_TAB;
                return DEFAULT_TAB;
            }

            StringBuilder sb = new StringBuilder();
            // Escape the tab using writeValue
            writeValue(getMarshaller().getIndentString(), false, sb);
            tab = sb.toString();
        }
        return tab;
    }


    public void startDocument(String encoding, String version) {
        super.startDocument(encoding, version);
            builder.append(cr);
    }

    /**
     * INTERNAL:
     */
    public void endDocument() {
            builder.append(cr);
    }

    /**
     * INTERNAL
     */
    public void writeHeader() {
            builder.append(getMarshaller().getXmlHeader());
            builder.append(cr);
    }

    /**
     * INTERNAL:
     */
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        this.addPositionalNodes(xPathFragment, namespaceResolver);
            if (isStartElementOpen) {
                builder.append('>');
            }
            if (!isLastEventText) {
                if (numberOfTabs > 0) {
                    builder.append(cr);
                }
                for (int x = 0; x < numberOfTabs; x++) {
                    builder.append(tab());
                }
            }
            isStartElementOpen = true;
            builder.append('<');
            builder.append(getNameForFragment(xPathFragment));
            if(xPathFragment.isGeneratedPrefix()){
                namespaceDeclaration(xPathFragment.getPrefix(), xPathFragment.getNamespaceURI());
            }
            numberOfTabs++;
            isLastEventText = false;
    }

    /**
     * INTERNAL:
     */
    public void element(XPathFragment frag) {
            isLastEventText = false;
            if (isStartElementOpen) {
                builder.append('>');
                isStartElementOpen = false;
            }
            builder.append(Constants.cr());
            for (int x = 0; x < numberOfTabs; x++) {
                builder.append(tab());
            }
            super.element(frag);
    }

    /**
     * INTERNAL:
     */
    public void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
            isLastEventText = false;
            numberOfTabs--;
            if (isStartElementOpen) {
                builder.append('/');
                builder.append('>');
                isStartElementOpen = false;
                return;
            }
            if (complexType) {
                builder.append(cr);
                for (int x = 0; x < numberOfTabs; x++) {
                    builder.append(tab());
                }
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
                builder.append('>');
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
                if (isStartElementOpen) {
                    builder.append('>');
                }
                if (!isLastEventText) {
                    builder.append(cr);
                    for (int x = 0; x < numberOfTabs; x++) {
                        builder.append(tab());
                    }
                }
                builder.append('<');
                builder.append(qName);
                numberOfTabs++;
                isStartElementOpen = true;
                isLastEventText = false;
                // Handle attributes
                handleAttributes(atts);
                // Handle prefix mappings
                writePrefixMappings();
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
                isLastEventText = false;
                numberOfTabs--;
                if (isStartElementOpen) {
                    builder.append('/');
                    builder.append('>');
                    isStartElementOpen = false;
                    complexType = true;
                    return;
                }
                if (complexType) {
                    builder.append(cr);
                    for (int x = 0; x < numberOfTabs; x++) {
                        builder.append(tab());
                    }
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
                    builder.append('>');
                    builder.append(cr);
                    isStartElementOpen = false;
                }
                writeComment(ch, start, length);
                complexType = false;
        }
    }

}
