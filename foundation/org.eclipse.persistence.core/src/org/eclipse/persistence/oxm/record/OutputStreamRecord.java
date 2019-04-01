/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - November 2, 2009
package org.eclipse.persistence.oxm.record;

import java.io.CharArrayWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.CharacterEscapeHandler;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XMLMarshaller;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.ExtendedContentHandler;
import org.eclipse.persistence.internal.oxm.record.XMLFragmentReader;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * <p>Use this type of MarshalRecord when the marshal target is an OutputStream and the
 * XML should not be formatted with carriage returns or indenting.  This type is only
 * used if the encoding of the OutputStream is UTF-8</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * OutputStreamRecord record = new OutputStreamRecord();<br>
 * record.setOutputStream(myOutputStream);<br>
 * xmlMarshaller.marshal(myObject, record);<br>
 * </code></p>
 * <p>If the marshal(OutputStream) and setFormattedOutput(false) method is called on
 * XMLMarshaller and the encoding is UTF-8, then the OutputStream is automatically wrapped
 * in an OutputStream.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * xmlMarshaller xmlMarshaller.setFormattedOutput(false);<br>
 * xmlMarshaller.marshal(myObject, myOutputStream);<br>
 * </code></p>
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */
public class OutputStreamRecord extends MarshalRecord<XMLMarshaller> {

    protected static final byte[] OPEN_XML_PI_AND_VERSION_ATTRIBUTE = "<?xml version=\"".getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte[] OPEN_ENCODING_ATTRIBUTE = " encoding=\"".getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte[] CLOSE_PI = "?>".getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte SPACE = (byte) ' ';
    protected static final byte CLOSE_ATTRIBUTE_VALUE = (byte) '"';
    protected static final byte[] OPEN_CDATA = "<![CDATA[".getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte[] CLOSE_CDATA = "]]>".getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte[] OPEN_COMMENT = "<!--".getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte[] CLOSE_COMMENT = "-->".getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte OPEN_START_ELEMENT = (byte) '<';
    protected static final byte CLOSE_ELEMENT = (byte) '>';
    protected static final byte[] AMP = "&amp;".getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte[] LT = "&lt;".getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte[] GT = "&gt;".getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte[] QUOT = "&quot;".getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte[] ENCODING = Constants.DEFAULT_XML_ENCODING.getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte[] SLASH_N = "&#xa;".getBytes(Constants.DEFAULT_CHARSET);
    protected static final byte[] SLASH_R = "&#xd;".getBytes(Constants.DEFAULT_CHARSET);

    protected OutputStream outputStream;
    protected boolean isStartElementOpen = false;
    protected boolean isProcessingCData = false;

    private static final int BUFFER_SIZE = 512;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private int bufferIndex = 0;

    /**
     * Return the OutputStream that the object will be marshalled to.
     * @return The marshal target.
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Set the OutputStream that the object will be marshalled to.
     * @param outputStream The marshal target.
     */
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * INTERNAL:
     * override so we don't iterate over namespaces when startPrefixMapping doesn't do anything
     */
    @Override
    public void startPrefixMappings(NamespaceResolver namespaceResolver) {
    }

    /**
     * INTERNAL:
     */
    @Override
    public void startDocument(String encoding, String version) {
        try {
            outputStreamWrite(OPEN_XML_PI_AND_VERSION_ATTRIBUTE);
            outputStreamWrite(version.getBytes(Constants.DEFAULT_XML_ENCODING));
            outputStreamWrite(CLOSE_ATTRIBUTE_VALUE);
            if (null != encoding) {
                outputStreamWrite(OPEN_ENCODING_ATTRIBUTE);
                outputStreamWrite(ENCODING);
                outputStreamWrite(CLOSE_ATTRIBUTE_VALUE);
            }
            outputStreamWrite(CLOSE_PI);
        } catch(UnsupportedEncodingException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL
     */
    @Override
    public void writeHeader() {
        outputStreamWrite(getMarshaller().getXmlHeader().getBytes());
    }

    /**
     * INTERNAL:
     */
    @Override
    public void endDocument() {}

    /**
     * INTERNAL:
     */
    @Override
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        super.openStartElement(xPathFragment, namespaceResolver);
        if (isStartElementOpen) {
            outputStreamWrite(CLOSE_ELEMENT);
        }
        isStartElementOpen = true;
        outputStreamWrite(OPEN_START_ELEMENT);
        byte[] prefixBytes = getPrefixBytes(xPathFragment);
        if(null != prefixBytes) {
            outputStreamWrite(prefixBytes);
            outputStreamWrite((byte)':');
        }
        outputStreamWrite(xPathFragment.getLocalNameBytes());
        if(xPathFragment.isGeneratedPrefix()){
            namespaceDeclaration(xPathFragment.getPrefix(), xPathFragment.getNamespaceURI());
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public void element(XPathFragment frag) {
        if (isStartElementOpen) {
            outputStreamWrite(CLOSE_ELEMENT);
            isStartElementOpen = false;
        }
        outputStreamWrite(OPEN_START_ELEMENT);
        try {
            outputStreamWrite(getNameForFragment(frag).getBytes(Constants.DEFAULT_XML_ENCODING));
        } catch (UnsupportedEncodingException e) {
        }
        outputStreamWrite((byte)'/');
        outputStreamWrite((byte)'>');
    }

    /**
     * INTERNAL:
     */
    @Override
    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
        attribute(null, xPathFragment.getLocalName(), getNameForFragment(xPathFragment), value);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void attribute(String namespaceURI, String localName, String qName, String value) {
        try {
            outputStreamWrite(SPACE);
            outputStreamWrite(qName.getBytes(Constants.DEFAULT_XML_ENCODING));
            outputStreamWrite((byte)'=');
            outputStreamWrite((byte)'"');
            writeValue(value, true, true, this.outputStream);
            outputStreamWrite(CLOSE_ATTRIBUTE_VALUE);
        } catch (UnsupportedEncodingException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public void closeStartElement() {}

    /**
     * INTERNAL:
     */
    @Override
    public void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        if (isStartElementOpen) {
            outputStreamWrite((byte) '/');
            outputStreamWrite((byte) '>');
            isStartElementOpen = false;
        } else {
            outputStreamWrite((byte)'<');
            outputStreamWrite((byte)'/');
            byte[] prefixBytes = getPrefixBytes(xPathFragment);
            if(null != prefixBytes) {
                outputStreamWrite(prefixBytes);
                outputStreamWrite((byte)':');
            }
            outputStreamWrite(xPathFragment.getLocalNameBytes());
            outputStreamWrite(CLOSE_ELEMENT);
        }
        isStartElementOpen = false;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void characters(String value) {
        if (isStartElementOpen) {
            isStartElementOpen = false;
            outputStreamWrite(CLOSE_ELEMENT);
        }
        writeValue(value, true);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void cdata(String value) {
        try {
            if(isStartElementOpen) {
                isStartElementOpen = false;
                outputStreamWrite(CLOSE_ELEMENT);
            }
            for (String part : MarshalRecord.splitCData(value)) {
                outputStreamWrite(OPEN_CDATA);
                outputStreamWrite(part.getBytes(Constants.DEFAULT_XML_ENCODING));
                outputStreamWrite(CLOSE_CDATA);
            }
        } catch(UnsupportedEncodingException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    protected void writeValue(String value, boolean escapeChars) {
        writeValue(value, escapeChars, false, this.outputStream);
    }

    /**
     * INTERNAL:
     */
    protected void writeValue(String value, boolean escapeChars, boolean isAttribute, OutputStream os) {
        if (escapeChars) {
            CharacterEscapeHandler escapeHandler = marshaller.getCharacterEscapeHandler();
            if (escapeHandler != null) {
                try {
                    CharArrayWriter out = new CharArrayWriter();
                    escapeHandler.escape(value.toCharArray(), 0, value.length(), isAttribute, out);
                    byte[] bytes = out.toString().getBytes();
                    outputStreamWrite(bytes);
                    out.close();
                } catch (IOException e) {
                    throw XMLMarshalException.marshalException(e);
                }
                return;
            }
        }

        // UTF-8 Byte Representations:
        //     0x0 - 0x7F                                 0xxxxxxx
        //    0x80 - 0x7FF                       110yyyxx 10xxxxxx
        //   0x800 - 0xFFFF             1110yyyy 10yyyyxx 10xxxxxx
        // 0x10000 - 0x10FFFF  11110zzz 10zzyyyy 10yyyyxx 10xxxxxx
        int nClosingSquareBracketsInRow = 0;
        for (int x = 0, length=value.length(); x < length; x++) {
            final char character = value.charAt(x);
            if (character > 0x7F) {
                if(character > 0x7FF) {
                    if((character >= Character.MIN_HIGH_SURROGATE) && (character <= Character.MAX_LOW_SURROGATE)) {
                        int uc = (((character & 0x3ff) << 10) | (value.charAt(++x) & 0x3ff)) + 0x10000;
                        // 11110zzz
                        outputStreamWrite((byte)(0xF0 | ((uc >> 18))), os);
                        // 10zzyyyy
                        outputStreamWrite((byte)(0x80 | ((uc >> 12) & 0x3F)), os);
                        // 10yyyyxx
                        outputStreamWrite((byte)(0x80 | ((uc >> 6) & 0x3F)), os);
                        // 10xxxxxx
                        outputStreamWrite((byte)(0x80 + (uc & 0x3F)), os);
                       continue;
                    } else {
                        // 1110yyyy
                        outputStreamWrite((byte)(0xE0 + (character >> 12)), os);
                    }
                    // 10yyyyxx
                    outputStreamWrite((byte)(0x80 + ((character >> 6) & 0x3F)), os);
                } else {
                    // 110yyyxx
                    outputStreamWrite((byte)(0xC0 + (character >> 6)), os);
                }
                outputStreamWrite((byte)(0x80 + (character & 0x3F)), os);
            } else {
                // 0xxxxxxx
                if(escapeChars) {
                    switch (character) {
                    case '&': {
                        outputStreamWrite(AMP, os);
                        break;
                    }
                    case '<': {
                        outputStreamWrite(LT, os);
                        break;
                    }
                    case '>': {
                        // escape only within ]]>
                        if (nClosingSquareBracketsInRow >= 2) {
                            outputStreamWrite(GT, os);
                        } else {
                            outputStreamWrite((byte)character, os);
                        }
                        break;
                    }
                    case '"': {
                        outputStreamWrite(QUOT, os);
                        break;
                    }
                    case '\n' : {
                        if(isAttribute) {
                            outputStreamWrite(SLASH_N, os);
                        } else {
                            outputStreamWrite((byte) character, os);
                        }
                        break;
                    }
                    case '\r': {
                        outputStreamWrite(SLASH_R, os);
                        break;
                    }
                    default:
                        outputStreamWrite((byte) character, os);
                    }
                    if (!isAttribute) {
                        // count ] to escape ]]>
                        if (']' == character) {
                            ++nClosingSquareBracketsInRow;
                        } else {
                            nClosingSquareBracketsInRow = 0;
                        }
                    }
                } else {
                    outputStreamWrite((byte) character, os);
                }
            }
        }
    }

    /**
     * Receive notification of a node.
     * @param node The Node to be added to the document
     * @param namespaceResolver The NamespaceResolver can be used to resolve the
     * namespace URI/prefix of the node
     */
    @Override
    public void node(Node node, NamespaceResolver namespaceResolver, String uri, String localName) {
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            Attr attr = (Attr) node;
            String resolverPfx = null;
            if (getNamespaceResolver() != null) {
                resolverPfx = this.getNamespaceResolver().resolveNamespaceURI(attr.getNamespaceURI());
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
                OutputStreamRecordContentHandler handler = new OutputStreamRecordContentHandler();
                XMLFragmentReader xfragReader = new XMLFragmentReader(namespaceResolver);
                xfragReader.setContentHandler(handler);
                xfragReader.setProperty(Constants.LEXICAL_HANDLER_PROPERTY, handler);
                xfragReader.parse(node, uri, localName);
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
     */
    protected class OutputStreamRecordContentHandler implements ExtendedContentHandler, LexicalHandler {
        Map<String, String> prefixMappings;

        OutputStreamRecordContentHandler() {
            prefixMappings = new HashMap<>();
        }

        // --------------------- CONTENTHANDLER METHODS --------------------- //
        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            try {
                if (isStartElementOpen) {
                    outputStreamWrite(CLOSE_ELEMENT);
                }
                outputStreamWrite(OPEN_START_ELEMENT);
                outputStreamWrite(qName.getBytes(Constants.DEFAULT_XML_ENCODING));
                isStartElementOpen = true;
                // Handle attributes
                handleAttributes(atts);
                // Handle prefix mappings
                writePrefixMappings();
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            try {
                if (isStartElementOpen) {
                    outputStreamWrite((byte)'/');
                    outputStreamWrite((byte)'>');
                } else {
                    outputStreamWrite((byte) '<');
                    outputStreamWrite((byte) '/');
                    outputStreamWrite(qName.getBytes(Constants.DEFAULT_XML_ENCODING));
                    outputStreamWrite(CLOSE_ELEMENT);
                }
                isStartElementOpen = false;
            } catch (UnsupportedEncodingException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            String namespaceUri = getNamespaceResolver().resolveNamespacePrefix(prefix);
            if(namespaceUri == null || !namespaceUri.equals(uri)) {
                prefixMappings.put(prefix, uri);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String characters = new String (ch, start, length);
            characters(characters);
        }

        @Override
        public void characters(CharSequence characters) throws SAXException {
            if (isProcessingCData) {
                cdata(characters.toString());
                return;
            }

            if (isStartElementOpen) {
                outputStreamWrite(CLOSE_ELEMENT);
                isStartElementOpen = false;
            }
            writeValue(characters.toString(), true);
        }

        // --------------------- LEXICALHANDLER METHODS --------------------- //
        @Override
        public void comment(char[] ch, int start, int length) throws SAXException {
            if (isStartElementOpen) {
                outputStreamWrite(CLOSE_ELEMENT);
                isStartElementOpen = false;
            }
            writeComment(ch, start, length);
        }

        @Override
        public void startCDATA() throws SAXException {
            isProcessingCData = true;
        }

        @Override
        public void endCDATA() throws SAXException {
            isProcessingCData = false;
        }

        // --------------------- CONVENIENCE METHODS --------------------- //
        protected void writePrefixMappings() {
            try {
                if (!prefixMappings.isEmpty()) {
                    Set<Entry<String, String>> entries = prefixMappings.entrySet();
                    Iterator<Entry<String, String>> iter = entries.iterator();
                    while(iter.hasNext()){
                        Entry<String, String> nextEntry = iter.next();
                        String prefix = nextEntry.getKey();
                        outputStreamWrite(SPACE);
                        outputStreamWrite(javax.xml.XMLConstants.XMLNS_ATTRIBUTE.getBytes(Constants.DEFAULT_XML_ENCODING));
                        if(null != prefix && prefix.length() > 0) {
                            outputStreamWrite((byte)Constants.COLON);
                            outputStreamWrite(prefix.getBytes(Constants.DEFAULT_XML_ENCODING));
                        }
                        outputStreamWrite((byte)'=');
                        outputStreamWrite((byte)'"');
                        String uri = nextEntry.getValue();
                        if(null != uri) {
                            outputStreamWrite(uri.getBytes(Constants.DEFAULT_XML_ENCODING));
                        }
                        outputStreamWrite(CLOSE_ATTRIBUTE_VALUE);
                    }
                    prefixMappings.clear();
                }
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        protected void handleAttributes(Attributes atts) {
            for (int i=0, attsLength = atts.getLength(); i<attsLength; i++) {
                String qName = atts.getQName(i);
                if((qName != null && (qName.startsWith(javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON) || qName.equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)))) {
                    continue;
                }
                attribute(atts.getURI(i), atts.getLocalName(i), qName, atts.getValue(i));
            }
        }

        protected void writeComment(char[] chars, int start, int length) {
            outputStreamWrite(OPEN_COMMENT);
            writeValue(new String(chars, start, length), false);
            outputStreamWrite(CLOSE_COMMENT);
        }

        protected void writeCharacters(char[] chars, int start, int length) {
            writeValue(new String(chars, start, length), true);
        }
        // --------------- SATISFY CONTENTHANDLER INTERFACE --------------- //
        @Override
        public void endPrefixMapping(String prefix) throws SAXException {}
        @Override
        public void processingInstruction(String target, String data) throws SAXException {}
        @Override
        public void setDocumentLocator(Locator locator) {}
        @Override
        public void startDocument() throws SAXException {}
        @Override
        public void endDocument() throws SAXException {}
        @Override
        public void skippedEntity(String name) throws SAXException {}
        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

        // --------------- SATISFY LEXICALHANDLER INTERFACE --------------- //
        @Override
        public void startEntity(String name) throws SAXException {}
        @Override
        public void endEntity(String name) throws SAXException {}
        @Override
        public void startDTD(String name, String publicId, String systemId) throws SAXException {}
        @Override
        public void endDTD() throws SAXException {}


        @Override
        public void setNil(boolean isNil) {}
    }

    @Override
    public void flush() {
        try {
            outputStream.write(buffer, 0, bufferIndex);
            bufferIndex = 0;
            outputStream.flush();
        } catch(IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    protected void outputStreamWrite(byte[] bytes) {
        outputStreamWrite(bytes, this.outputStream);
    }

    protected void outputStreamWrite(byte[] bytes, OutputStream os) {
        if (os != this.outputStream) {
            // Not using our buffer
            try {
                os.write(bytes);
                return;
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }
        int bytesLength = bytes.length;
        if(bufferIndex + bytesLength >= BUFFER_SIZE) {
            try {
                os.write(buffer, 0, bufferIndex);
                bufferIndex = 0;
                if(bytesLength > BUFFER_SIZE) {
                    os.write(bytes);
                    return;
                }
            } catch(IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }
        System.arraycopy(bytes, 0, buffer, bufferIndex, bytes.length);
        bufferIndex += bytesLength;
    }

    protected void outputStreamWrite(byte aByte) {
        outputStreamWrite(aByte, this.outputStream);
    }

    protected void outputStreamWrite(byte aByte, OutputStream os) {
        if (os != this.outputStream) {
            // Not using our buffer
            try {
                os.write(aByte);
                return;
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }
        if(bufferIndex == BUFFER_SIZE) {
            try {
                os.write(buffer, 0, BUFFER_SIZE);
                bufferIndex = 0;
            } catch(IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }
        buffer[bufferIndex++] = aByte;
    }

}
