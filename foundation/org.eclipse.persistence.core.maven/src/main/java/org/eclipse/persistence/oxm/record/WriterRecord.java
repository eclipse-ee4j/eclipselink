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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.oxm.record;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
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
 * <p>Use this type of MarshalRecord when the marshal target is a Writer and the
 * XML should not be formatted with carriage returns or indenting.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * WriterRecord writerRecord = new WriterRecord();<br>
 * writerRecord.setWriter(myWriter);<br>
 * xmlMarshaller.marshal(myObject, writerRecord);<br>
 * </code></p>
 * <p>If the marshal(Writer) and setFormattedOutput(false) method is called on
 * XMLMarshaller, then the Writer is automatically wrapped in a
 * WriterRecord.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * xmlMarshaller xmlMarshaller.setFormattedOutput(false);<br>
 * xmlMarshaller.marshal(myObject, myWriter);<br>
 * </code></p>
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */
public class WriterRecord extends MarshalRecord<XMLMarshaller> {

    protected StringBuilder builder = new StringBuilder(3072);
    protected boolean isStartElementOpen = false;
    protected boolean isProcessingCData = false;
    protected CharsetEncoder encoder;
    String charset = "UNKNOWN_CHARSET";
    private static final String defaultCharset = Constants.DEFAULT_XML_ENCODING.intern();
    private Writer writer;

    /**
     * Return the Writer that the object will be marshalled to.
     * @return The marshal target.
     */
    public Writer getWriter() {
        return writer;
    }

    /**
     * Set the Writer that the object will be marshalled to.
     * @param writer The marshal target.
     */
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void startDocument(String encoding, String version) {
            builder.append("<?xml version=\"");
            builder.append(version);
            builder.append('\"');
            if (null != encoding) {
                builder.append(" encoding=\"");
                builder.append(encoding);
                builder.append('\"');
            }
            builder.append("?>");
    }

    /**
     * INTERNAL
     */
    @Override
    public void writeHeader() {
            builder.append(getMarshaller().getXmlHeader());
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
                builder.append('>');
            }
            isStartElementOpen = true;
            builder.append('<');
            builder.append(getNameForFragment(xPathFragment));
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
                builder.append('>');
                isStartElementOpen = false;
            }
            builder.append('<');
            builder.append(getNameForFragment(frag));
            builder.append('/');
            builder.append('>');
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
     * override so we don't iterate over namespaces when startPrefixMapping doesn't do anything
     */
    @Override
    public void startPrefixMappings(NamespaceResolver namespaceResolver) {
    }

    /**
     * INTERNAL:
     */
    @Override
    public void attribute(String namespaceURI, String localName, String qName, String value) {
            builder.append(' ');
            builder.append(qName);
            builder.append('=');
            builder.append('\"');
            writeValue(value, true, this.builder);
            builder.append('\"');
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
                builder.append('/');
                builder.append('>');
                isStartElementOpen = false;
            } else {
                builder.append('<');
                builder.append('/');
                builder.append(getNameForFragment(xPathFragment));
                builder.append('>');
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
                builder.append('>');
            }
            writeValue(value);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void cdata(String value) {
            if(isStartElementOpen) {
                isStartElementOpen = false;
                builder.append('>');
            }
            for (String part : MarshalRecord.splitCData(value)) {
                builder.append("<![CDATA[");
                builder.append(part);
                builder.append("]]>");
            }
    }

    /**
     * INTERNAL:
     */
    protected void writeValue(String value) {
        writeValue(value, false, this.builder);
    }

    /**
     * INTERNAL:
     */
    protected void writeValue(String value, boolean isAttribute, StringBuilder writer) {
        CharacterEscapeHandler escapeHandler = null;
        if (marshaller != null) {
            escapeHandler = marshaller.getCharacterEscapeHandler();
        }
        if (escapeHandler != null) {
            try {
                StringWriter sw = new StringWriter();
                escapeHandler.escape(value.toCharArray(), 0, value.length(), isAttribute, sw);
                writer.append(sw.toString());
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
            return;
        }

        if(null == encoder) {
            encoder = Constants.DEFAULT_CHARSET.newEncoder();
            charset = defaultCharset;
        }
      char[] chars = value.toCharArray();
      int nClosingSquareBracketsInRow = 0;
      for (int x = 0, charsSize = chars.length; x < charsSize; x++) {
          char character = chars[x];
          switch (character) {
          case '&': {
              writer.append("&amp;");
              break;
          }
          case '<': {
              writer.append("&lt;");
              break;
          }
          case '>': {
              if (nClosingSquareBracketsInRow >= 2) {
                  writer.append("&gt;");
              } else {
                  writer.append(character);
              }
              break;
          }
          case '"': {
              writer.append("&quot;");
              break;
          }
          case '\n' : {
              if(isAttribute) {
                  writer.append("&#xa;");
              } else {
                  writer.append('\n');
              }
              break;
          }
          case '\r': {
              writer.append("&#xd;");
              break;
          }
          default:
              if (charset == defaultCharset) {
                  writer.append(character);
              } else {
                  if(encoder.canEncode(character)) {
                      writer.append(character);
                  } else {
                      writer.append("&#");
                      writer.append(Integer.toString(character));
                      writer.append(';');
                  }
              }
          }
          if (!isAttribute) {
              // count ] to escape ]]>
              if (']' == character) {
                  ++nClosingSquareBracketsInRow;
              } else {
                  nClosingSquareBracketsInRow = 0;
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
    public void node(Node node, NamespaceResolver namespaceResolver, String newNamespace, String newName) {
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
                WriterRecordContentHandler wrcHandler = new WriterRecordContentHandler();
                XMLFragmentReader xfragReader = new XMLFragmentReader(namespaceResolver);
                xfragReader.setContentHandler(wrcHandler);
                xfragReader.setProperty("http://xml.org/sax/properties/lexical-handler", wrcHandler);
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
     */
    protected class WriterRecordContentHandler implements ExtendedContentHandler, LexicalHandler {
        Map<String, String> prefixMappings;

        WriterRecordContentHandler() {
            prefixMappings = new HashMap<>();
        }

        // --------------------- CONTENTHANDLER METHODS --------------------- //
        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
                if (isStartElementOpen) {
                    builder.append('>');
                }

                builder.append('<');
                builder.append(qName);
                isStartElementOpen = true;
                // Handle attributes
                handleAttributes(atts);
                // Handle prefix mappings
                writePrefixMappings();
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
                if (isStartElementOpen) {
                    builder.append('/');
                    builder.append('>');
                } else {
                        builder.append('<');
                        builder.append('/');
                        builder.append(qName);
                        builder.append('>');
                }
                isStartElementOpen = false;
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
                    builder.append('>');
                    isStartElementOpen = false;
            }
            writeValue(characters.toString());
        }

        // --------------------- LEXICALHANDLER METHODS --------------------- //
        @Override
        public void comment(char[] ch, int start, int length) throws SAXException {
                if (isStartElementOpen) {
                    builder.append('>');
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
                if (!prefixMappings.isEmpty()) {
                    Set<Entry<String, String>> entries = prefixMappings.entrySet();
                    Iterator<Entry<String, String>> iter = entries.iterator();
                    while(iter.hasNext()){
                        Entry<String, String> nextEntry = iter.next();
                        String prefix = nextEntry.getKey();
                        builder.append(' ');
                        builder.append(javax.xml.XMLConstants.XMLNS_ATTRIBUTE);
                        if(null != prefix && prefix.length() > 0) {
                            builder.append(Constants.COLON);
                            builder.append(prefix);
                        }
                        builder.append('=');
                        builder.append('"');
                        String uri = nextEntry.getValue();
                        if(null != uri) {
                            builder.append(uri);
                        }
                        builder.append('"');
                    }
                    prefixMappings.clear();
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
                builder.append('<');
                builder.append('!');
                builder.append('-');
                builder.append('-');
                for (int x = start; x < length; x++) {
                    builder.append(chars[x]);
                }
                builder.append('-');
                builder.append('-');
                builder.append('>');
        }

        protected void writeCharacters(char[] chars, int start, int length) {
                for (int x = start; x < length; x++) {
                    builder.append(chars[x]);
                }
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
    public void setMarshaller(XMLMarshaller marshaller) {
        super.setMarshaller(marshaller);
        if (charset == marshaller.getEncoding()) {
            encoder.reset();
        } else {
            encoder = Charset.forName(marshaller.getEncoding()).newEncoder();
            charset = marshaller.getEncoding();
        }
    }

    @Override
    public void flush() {
        try {
            writer.write(builder.toString());
            builder.setLength(0);
            writer.flush();
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

}
