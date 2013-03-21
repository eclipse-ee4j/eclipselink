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

import java.io.Writer;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XMLMarshaller;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.ExtendedContentHandler;
import org.eclipse.persistence.internal.oxm.record.XMLFragmentReader;
import org.eclipse.persistence.oxm.CharacterEscapeHandler;
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

    protected Writer writer;
    protected boolean isStartElementOpen = false;
    protected boolean isProcessingCData = false;
    protected CharsetEncoder encoder;

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
    public void startDocument(String encoding, String version) {
        try {
            writer.write("<?xml version=\"");
            writer.write(version);
            writer.write('\"');
            if (null != encoding) {
                writer.write(" encoding=\"");
                writer.write(encoding);
                writer.write('\"');
            }
            writer.write("?>");
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
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void endDocument() {}

    /**
     * INTERNAL:
     */
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        super.openStartElement(xPathFragment, namespaceResolver);
        try {
            if (isStartElementOpen) {
                writer.write('>');
            }
            isStartElementOpen = true;
            writer.write('<');
            writer.write(getNameForFragment(xPathFragment));
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void element(XPathFragment frag) {
        try {
            if (isStartElementOpen) {
                writer.write('>');
                isStartElementOpen = false;
            }
            writer.write('<');
            writer.write(getNameForFragment(frag));
            writer.write('/');
            writer.write('>');
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
        attribute(null, xPathFragment.getLocalName(), getNameForFragment(xPathFragment), value);
    }

    /**
     * INTERNAL:
     * override so we don't iterate over namespaces when startPrefixMapping doesn't do anything
     */
    public void startPrefixMappings(NamespaceResolver namespaceResolver) {
    }

    /**
     * INTERNAL:
     */
    public void attribute(String namespaceURI, String localName, String qName, String value) {
        try {
            writer.write(' ');
            writer.write(qName);
            writer.write('=');
            writer.write('\"');
            writeValue(value, true, this.writer);
            writer.write('\"');
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void closeStartElement() {}

    /**
     * INTERNAL:
     */
    public void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        try {
            if (isStartElementOpen) {
                writer.write('/');
                writer.write('>');
                isStartElementOpen = false;
            } else {
                writer.write('<');
                writer.write('/');
                writer.write(getNameForFragment(xPathFragment));
                writer.write('>');
            }
            isStartElementOpen = false;
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void characters(String value) {
        try {
            if (isStartElementOpen) {
                isStartElementOpen = false;
                writer.write('>');
            }
            writeValue(value);
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void cdata(String value) {
        try {
            if(isStartElementOpen) {
                isStartElementOpen = false;
                writer.write('>');
            }
            writer.write("<![CDATA[");
            writer.write(value);
            writer.write("]]>");
        } catch(IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    protected void writeValue(String value) {
        writeValue(value, false, this.writer);
    }

    /**
     * INTERNAL:
     */
    protected void writeValue(String value, boolean isAttribute, Writer writer) {
        CharacterEscapeHandler escapeHandler = null;
        if (marshaller != null) {
            escapeHandler = marshaller.getCharacterEscapeHandler();
        }
        if (escapeHandler != null) {
            try {
                escapeHandler.escape(value.toCharArray(), 0, value.length(), isAttribute, writer);
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
            return;
        }

        try {
            if((null != encoder && encoder.maxBytesPerChar() < 4) || value.indexOf('"') > -1 || value.indexOf('&') > -1 || value.indexOf('<') > -1) {
                  char[] chars = value.toCharArray();
                  for (int x = 0, charsSize = chars.length; x < charsSize; x++) {
                      char character = chars[x];
                      switch (character) {
                      case '&': {
                          writer.write("&amp;");
                          break;
                      }
                      case '<': {
                          writer.write("&lt;");
                          break;
                      }
                      case '"': {
                          writer.write("&quot;");
                          break;
                      }
                      default:
                          if(encoder.canEncode(character)) {
                              writer.write(character);
                          } else {
                              writer.write("&#");
                              writer.write(String.valueOf((int) character));
                              writer.write(';');
                          }
                      }
                  }
            } else {
                writer.write(value);
            }
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
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
                resolverPfx = this.getNamespaceResolver().resolveNamespaceURI(attr.getNamespaceURI());
            } 
            String namespaceURI = attr.getNamespaceURI();
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
            prefixMappings = new HashMap<String, String>();
        }

        // --------------------- CONTENTHANDLER METHODS --------------------- //
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            try {
                if (isStartElementOpen) {
                    writer.write('>');
                }

                writer.write('<');
                writer.write(qName);
                isStartElementOpen = true;
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
                if (isStartElementOpen) {
                    writer.write('/');
                    writer.write('>');
                } else {
                        writer.write('<');
                        writer.write('/');
                        writer.write(qName);
                        writer.write('>');
                }
                isStartElementOpen = false;
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            String namespaceUri = getNamespaceResolver().resolveNamespacePrefix(prefix);
            if(namespaceUri == null || !namespaceUri.equals(uri)) {
                prefixMappings.put(prefix, uri);
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            String characters = new String (ch, start, length);
            characters(characters);
        }

        public void characters(CharSequence characters) throws SAXException {
            if (isProcessingCData) {
                cdata(characters.toString());
                return;
            }

            if (isStartElementOpen) {
                try {
                    writer.write('>');
                    isStartElementOpen = false;
                } catch (IOException e) {
                    throw XMLMarshalException.marshalException(e);
                }
            }
            writeValue(characters.toString());
        }
        
        // --------------------- LEXICALHANDLER METHODS --------------------- //
        public void comment(char[] ch, int start, int length) throws SAXException {
            try {
                if (isStartElementOpen) {
                    writer.write('>');
                    isStartElementOpen = false;
                }
                writeComment(ch, start, length);
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        public void startCDATA() throws SAXException {
            isProcessingCData = true;
        }

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
                        writer.write(' ');
                        writer.write(javax.xml.XMLConstants.XMLNS_ATTRIBUTE);
                        if(null != prefix && prefix.length() > 0) {
                            writer.write(Constants.COLON);
                            writer.write(prefix);
                        }
                        writer.write('=');
                        writer.write('"');
                        String uri = nextEntry.getValue();
                        if(null != uri) {
                            writer.write(uri);
                        }
                        writer.write('"');
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
            try {
                writer.write('<');
                writer.write('!');
                writer.write('-');
                writer.write('-');
                for (int x = start; x < length; x++) {
                    writer.write(chars[x]);
                }
                writer.write('-');
                writer.write('-');
                writer.write('>');
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        protected void writeCharacters(char[] chars, int start, int length) {
            try {
                for (int x = start; x < length; x++) {
                    writer.write(chars[x]);
                }
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }
        // --------------- SATISFY CONTENTHANDLER INTERFACE --------------- //
        public void endPrefixMapping(String prefix) throws SAXException {}
        public void processingInstruction(String target, String data) throws SAXException {}
        public void setDocumentLocator(Locator locator) {}
        public void startDocument() throws SAXException {}
        public void endDocument() throws SAXException {}
        public void skippedEntity(String name) throws SAXException {}
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

        // --------------- SATISFY LEXICALHANDLER INTERFACE --------------- //
        public void startEntity(String name) throws SAXException {}
        public void endEntity(String name) throws SAXException {}
        public void startDTD(String name, String publicId, String systemId) throws SAXException {}
        public void endDTD() throws SAXException {}
		@Override
		public void setNil(boolean isNil) {}

    }

    @Override
    public void setMarshaller(XMLMarshaller marshaller) {
        super.setMarshaller(marshaller);
        encoder = Charset.forName(marshaller.getEncoding()).newEncoder();
    }

}
