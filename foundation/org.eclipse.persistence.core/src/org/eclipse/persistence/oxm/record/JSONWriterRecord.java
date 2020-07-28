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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.core.helper.CoreConversionManager;
import org.eclipse.persistence.internal.oxm.CharacterEscapeHandler;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.ConversionManager;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.ObjectBuilder;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.XMLBinaryDataHelper;
import org.eclipse.persistence.internal.oxm.XMLMarshaller;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
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
 * JSON should not be formatted with carriage returns or indenting.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * JSONRecord jsonWriterRecord = new JSONWriterRecord();<br>
 * jsonWriterRecord.setWriter(myWriter);<br>
 * xmlMarshaller.marshal(myObject, jsonWriterRecord);<br>
 * </code></p>
 * <p>If the marshal(Writer) and setMediaType(MediaType.APPLICATION_JSON) and
 * setFormattedOutput(false) method is called on XMLMarshaller, then the Writer
 * is automatically wrapped in a JSONWriterRecord.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * xmlMarshaller.setMediaType(MediaType.APPLICATION_JSON);
 * xmlMarshaller xmlMarshaller.setFormattedOutput(false);<br>
 * xmlMarshaller.marshal(myObject, myWriter);<br>
 * </code></p>
 *
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */
public class JSONWriterRecord extends MarshalRecord<XMLMarshaller> {

    protected boolean isProcessingCData = false;
    protected static final String NULL = "null";
    protected String attributePrefix;
    protected boolean charactersAllowed = false;
    protected CharsetEncoder encoder;
    protected CharacterEscapeHandler characterEscapeHandler;
    protected String callbackName;
    protected Output writer;
    protected Level level;

    public JSONWriterRecord() {
        super();
    }

    public JSONWriterRecord(OutputStream outputStream) {
        this();
        writer = new OutputStreamOutput(outputStream);
    }

    public JSONWriterRecord(OutputStream outputStream, String callbackName) {
        this(outputStream);
        setCallbackName(callbackName);
    }

    public JSONWriterRecord(Writer writer) {
        this();
        setWriter(writer);
    }

    public JSONWriterRecord(Writer writer, String callbackName) {
        this(writer);
        setCallbackName(callbackName);
    }

    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }

    /**
     * INTERNAL:
     */
    public void setMarshaller(XMLMarshaller marshaller) {
        super.setMarshaller(marshaller);
        attributePrefix = marshaller.getAttributePrefix();
        encoder = Charset.forName(marshaller.getEncoding()).newEncoder();
        if (marshaller.getValueWrapper() != null) {
            textWrapperFragment = new XPathFragment();
            textWrapperFragment.setLocalName(marshaller.getValueWrapper());
        }
        characterEscapeHandler = marshaller.getCharacterEscapeHandler();
        writer.setMarshaller(marshaller);
    }


    /**
     * Handle marshal of an empty collection.
     *
     * @param xPathFragment
     * @param namespaceResolver
     * @param openGrouping      if grouping elements should be marshalled for empty collections
     * @return
     */
    public boolean emptyCollection(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, boolean openGrouping) {
        if (marshaller.isMarshalEmptyCollections()) {
            super.emptyCollection(xPathFragment, namespaceResolver, true);
            if (null != xPathFragment) {
                startCollection();
                if (!xPathFragment.isSelfFragment()) {
                    openStartElement(xPathFragment, namespaceResolver);
                    if (null != level) {
                        level.setNeedToCloseComplex(false);
                        level.setNeedToOpenComplex(false);
                    }
                    endElement(xPathFragment, namespaceResolver);
                }
                endEmptyCollection();
            }
            return true;
        } else {
            return super.emptyCollection(xPathFragment, namespaceResolver, openGrouping);
        }
    }

    public void forceValueWrapper() {
        charactersAllowed = false;
    }

    /**
     * Return the Writer that the object will be marshalled to.
     *
     * @return The marshal target.
     */
    public Writer getWriter() {
        return writer.getWriter();
    }

    /**
     * Set the Writer that the object will be marshalled to.
     *
     * @param writer The marshal target.
     */
    public void setWriter(Writer writer) {
        this.writer = new WriterOutput(writer);
    }

    public void namespaceDeclaration(String prefix, String namespaceURI) {
    }

    public void defaultNamespaceDeclaration(String defaultNamespace) {
    }

    /**
     * INTERNAL:
     */
    public void startDocument(String encoding, String version) {
        try {
            if (null != level) {
                if (level.isFirst()) {
                    level.setFirst(false);
                } else {
                    writeListSeparator();
                }
            } else if (callbackName != null) {
                startCallback();
            }
            level = new Level(true, false, false, level);

            writer.write('{');
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    protected void writeListSeparator() throws IOException {
        writer.write(',');
    }

    protected void writeSeparator() throws IOException {
        writer.write(Constants.COLON);
    }

    /**
     * INTERNAL:
     *
     * @throws IOException
     */
    protected void startCallback() throws IOException {
        if (callbackName != null) {
            writer.write(callbackName);
            writer.write('(');
        }
    }

    /**
     * INTERNAL:
     */
    public void endDocument() {
        try {
            closeComplex();
            if (null != level && null == level.getPreviousLevel()) {
                endCallback();
            }
            level = level.getPreviousLevel();
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        try {
            if (level.isFirst()) {
                level.setFirst(false);
            } else {
                writer.write(',');
            }
            if (xPathFragment.nameIsText()) {
                if (level.isCollection() && level.isEmptyCollection()) {
                    writer.write('[');
                    level.setEmptyCollection(false);
                    level.setNeedToOpenComplex(false);
                    charactersAllowed = true;
                    level = new Level(true, true, false, level);
                    return;
                }
            }

            if (level.needToOpenComplex) {
                if (!level.isNestedArray()) {
                    writer.write('{');
                }
                level.needToOpenComplex = false;
                level.needToCloseComplex = true;
            }

            //write the key unless this is a a non-empty collection
            if (!(level.isCollection() && !level.isEmptyCollection())) {
                if (!level.isNestedArray()) {
                    writeKey(xPathFragment);
                }
                //if it is the first thing in the collection also add the [
                if (level.isCollection() && level.isEmptyCollection()) {
                    writer.write('[');
                    level.setEmptyCollection(false);
                }
            }


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

    /**
     * INTERNAL:
     */
    public void attribute(String namespaceURI, String localName, String qName, String value) {
        XPathFragment xPathFragment = new XPathFragment();
        xPathFragment.setNamespaceURI(namespaceURI);
        xPathFragment.setAttribute(true);
        xPathFragment.setLocalName(localName);

        openStartElement(xPathFragment, namespaceResolver);
        characters(null, value, null, false, true);
        endElement(xPathFragment, namespaceResolver);
    }

    /**
     * INTERNAL:
     */
    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
        attribute(xPathFragment, namespaceResolver, value, null);
    }

    /**
     * INTERNAL:
     * override so we don't iterate over namespaces when startPrefixMapping doesn't do anything
     */
    public void startPrefixMappings(NamespaceResolver namespaceResolver) {
    }

    /**
     * INTERNAL:
     * override so we don't iterate over namespaces when endPrefixMapping doesn't do anything
     */
    public void endPrefixMappings(NamespaceResolver namespaceResolver) {
    }

    /**
     * INTERNAL:
     */
    public void closeStartElement() {
    }

    /**
     * INTERNAL:
     */
    public void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        try {
            if (null != level) {
                if (level.needToOpenComplex) {
                    writer.write('{');
                    closeComplex();
                } else if (level.needToCloseComplex && !level.nestedArray) {
                    closeComplex();
                }
                charactersAllowed = false;
                level = level.getPreviousLevel();
            }
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    protected void closeComplex() throws IOException {
        writer.write('}');
    }

    @Override
    public void startCollection() {
        if (null == level) {
            try {
                startCallback();
                writer.write('[');
                level = new Level(true, false, false, level);
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        } else {
            level.setCollection(true);
            level.setEmptyCollection(true);
            charactersAllowed = false;
        }
    }

    protected void endEmptyCollection() {
        endCollection();
    }

    protected void endCallback() throws IOException {
        if (callbackName != null) {
            writer.write(')');
            writer.write(';');
        }
    }

    @Override
    public void endCollection() {
        try {
            if (level != null && null == level.getPreviousLevel()) {
                writer.write(']');
                endCallback();
            } else {
                if (level != null && level.isCollection() && !level.isEmptyCollection()) {
                    writer.write(']');
                }
            }
            level.setCollection(false);
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void characters(String value) {
        characters(value, true, false);
    }

    /**
     * INTERNAL:
     */
    public void characters(String value, boolean isString, boolean isAttribute) {
        boolean textWrapperOpened = false;
        if (!charactersAllowed) {
            if (textWrapperFragment != null) {
                openStartElement(textWrapperFragment, namespaceResolver);
                textWrapperOpened = true;
            }
        }

        level.setNeedToOpenComplex(false);
        try {
            if (isString) {
                writer.write('"');
                writeValue(value, isAttribute);
                writer.write('"');
            } else {
                writer.write(value);
            }

        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
        if (textWrapperOpened) {
            if (textWrapperFragment != null) {
                endElement(textWrapperFragment, namespaceResolver);
            }
        }
    }

    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, Object value, QName schemaType) {
        if (xPathFragment.getNamespaceURI() != null && xPathFragment.getNamespaceURI() == javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI) {
            return;
        }
        xPathFragment.setAttribute(true);
        openStartElement(xPathFragment, namespaceResolver);
        characters(schemaType, value, null, false, true);
        endElement(xPathFragment, namespaceResolver);
    }

    public void characters(QName schemaType, Object value, String mimeType, boolean isCDATA) {
        characters(schemaType, value, mimeType, isCDATA, false);
    }

    public void characters(QName schemaType, Object value, String mimeType, boolean isCDATA, boolean isAttribute) {
        if (mimeType != null) {
            if (value instanceof List) {
                value = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesListForBinaryValues(//
                        (List) value, marshaller, mimeType);
            } else {

                value = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                        value, marshaller, mimeType).getData();
            }
        }
        if (schemaType != null && Constants.QNAME_QNAME.equals(schemaType)) {
            String convertedValue = getStringForQName((QName) value);
            characters((String) convertedValue);
        } else if (value.getClass() == String.class) {
            //if schemaType is set and it's a numeric or boolean type don't treat as a string
            if (schemaType != null && isNumericOrBooleanType(schemaType)) {
                String convertedValue = ((String) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.STRING, schemaType));
                characters(convertedValue, false, isAttribute);
            } else if (isCDATA) {
                cdata((String) value);
            } else {
                characters((String) value);
            }
        } else {
            ConversionManager conversionManager = getConversionManager();
            String convertedValue = (String) conversionManager.convertObject(value, CoreClassConstants.STRING, schemaType);
            Class theClass = conversionManager.javaType(schemaType);

            if (schemaType == null || theClass == null) {
                if (value.getClass() == CoreClassConstants.BOOLEAN || CoreClassConstants.NUMBER.isAssignableFrom(value.getClass())) {
                    characters(convertedValue, false, isAttribute);
                } else {
                    characters(convertedValue);

                }
            } else if (schemaType != null && !isNumericOrBooleanType(schemaType)) {
                //if schemaType exists and is not boolean or number do write quotes
                characters(convertedValue);
            } else if (isCDATA) {
                cdata(convertedValue);
            } else {
                characters(convertedValue, false, isAttribute);
            }
        }
        charactersAllowed = false;

    }


    private boolean isNumericOrBooleanType(QName schemaType) {
        if (schemaType == null) {
            return false;
        } else if (schemaType.equals(Constants.BOOLEAN_QNAME)
                || schemaType.equals(Constants.INTEGER_QNAME)
                || schemaType.equals(Constants.INT_QNAME)
                || schemaType.equals(Constants.BYTE_QNAME)
                || schemaType.equals(Constants.DECIMAL_QNAME)
                || schemaType.equals(Constants.FLOAT_QNAME)
                || schemaType.equals(Constants.DOUBLE_QNAME)
                || schemaType.equals(Constants.SHORT_QNAME)
                || schemaType.equals(Constants.LONG_QNAME)
                || schemaType.equals(Constants.NEGATIVE_INTEGER_QNAME)
                || schemaType.equals(Constants.NON_NEGATIVE_INTEGER_QNAME)
                || schemaType.equals(Constants.NON_POSITIVE_INTEGER_QNAME)
                || schemaType.equals(Constants.POSITIVE_INTEGER_QNAME)
                || schemaType.equals(Constants.UNSIGNED_BYTE_QNAME)
                || schemaType.equals(Constants.UNSIGNED_INT_QNAME)
                || schemaType.equals(Constants.UNSIGNED_LONG_QNAME)
                || schemaType.equals(Constants.UNSIGNED_SHORT_QNAME)
                ) {
            return true;
        }
        return false;
    }

    /**
     * INTERNAL:
     */
    public void namespaceDeclarations(NamespaceResolver namespaceResolver) {
    }

    /**
     * INTERNAL:
     */
    public void nilComplex(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
        closeStartGroupingElements(groupingFragment);
        openStartElement(xPathFragment, namespaceResolver);
        characters(NULL, false, false);
        endElement(xPathFragment, namespaceResolver);
    }

    /**
     * INTERNAL:
     */
    public void nilSimple(NamespaceResolver namespaceResolver) {
        XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
        characters(NULL, false, false);
        closeStartGroupingElements(groupingFragment);
    }

    /**
     * Used when an empty simple value should be written
     *
     * @since EclipseLink 2.4
     */
    public void emptySimple(NamespaceResolver namespaceResolver) {
        nilSimple(namespaceResolver);
    }

    public void emptyAttribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
        openStartElement(xPathFragment, namespaceResolver);
        characters(NULL, false, false);
        endElement(xPathFragment, namespaceResolver);
        closeStartGroupingElements(groupingFragment);
    }

    /**
     * Used when an empty complex item should be written
     *
     * @since EclipseLink 2.4
     */
    public void emptyComplex(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
        closeStartGroupingElements(groupingFragment);
        openStartElement(xPathFragment, namespaceResolver);
        endElement(xPathFragment, namespaceResolver);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void marshalWithoutRootElement(ObjectBuilder treeObjectBuilder, Object object, Descriptor descriptor, Root root, boolean isXMLRoot) {
        if (treeObjectBuilder != null) {
            addXsiTypeAndClassIndicatorIfRequired(descriptor, null, descriptor.getDefaultRootElementField(), root, object, isXMLRoot, true);
            treeObjectBuilder.marshalAttributes(this, object, session);
        }
    }

    /**
     * INTERNAL:
     */
    public void cdata(String value) {
        characters(value);
    }

    /**
     * INTERNAL:
     * The character used to separate the prefix and uri portions when namespaces are present
     *
     * @since 2.4
     */
    public char getNamespaceSeparator() {
        return marshaller.getNamespaceSeparator();
    }

    /**
     * INTERNAL:
     * The optional fragment used to wrap the text() mappings
     *
     * @since 2.4
     */
    public XPathFragment getTextWrapperFragment() {
        return textWrapperFragment;
    }

    protected void writeKey(XPathFragment xPathFragment) throws IOException {
        if (xPathFragment.getLocalName() != null && !xPathFragment.getLocalName().equals(Constants.EMPTY_STRING)) {
            super.openStartElement(xPathFragment, namespaceResolver);
            writer.write('"');
            if (xPathFragment.isAttribute() && attributePrefix != null) {
                writer.writeAttributePrefix();
            }

            if (isNamespaceAware()) {
                if (xPathFragment.getNamespaceURI() != null) {
                    String prefix = null;
                    if (getNamespaceResolver() != null) {
                        prefix = getNamespaceResolver().resolveNamespaceURI(xPathFragment.getNamespaceURI());
                    } else if (namespaceResolver != null) {
                        prefix = namespaceResolver.resolveNamespaceURI(xPathFragment.getNamespaceURI());
                    }
                    if (prefix != null && !prefix.equals(Constants.EMPTY_STRING)) {
                        writer.write(prefix);
                        writer.writeNamespaceSeparator();
                    }
                }
            }

            writer.writeLocalName(xPathFragment);
            writer.write('"');

            writeSeparator();
        }
    }

    /**
     * INTERNAL:
     */
    protected void writeValue(String value, boolean isAttribute) {
        try {
            if (characterEscapeHandler != null) {
                writer.writeResultFromCharEscapeHandler(value, isAttribute);
                return;
            }

            char[] chars = value.toCharArray();
            for (int x = 0, charsSize = chars.length; x < charsSize; x++) {
                char character = chars[x];
                switch (character) {
                    case '"': {
                        writer.write("\\\"");
                        break;
                    }
                    case '\b': {
                        writer.write("\\b");
                        break;
                    }
                    case '\f': {
                        writer.write("\\f");
                        break;
                    }
                    case '\n': {
                        writer.write("\\n");
                        break;
                    }
                    case '\r': {
                        writer.write("\\r");
                        break;
                    }
                    case '\t': {
                        writer.write("\\t");
                        break;
                    }
                    case '\\': {
                        writer.write("\\\\");
                        break;
                    }
                    default: {
                        if (Character.isISOControl(character) || !encoder.canEncode(character)) {
                            writer.write("\\u");
                            String hex = Integer.toHexString(character).toUpperCase();
                            for (int i = hex.length(); i < 4; i++) {
                                writer.write("0");
                            }
                            writer.write(hex);
                        } else {
                            writer.write(character);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    protected String getStringForQName(QName qName) {
        if (null == qName) {
            return null;
        }
        CoreConversionManager xmlConversionManager = getSession().getDatasourcePlatform().getConversionManager();

        return (String) xmlConversionManager.convertObject(qName, String.class);
    }

    /**
     * Receive notification of a node.
     *
     * @param node              The Node to be added to the document
     * @param namespaceResolver The NamespaceResolver can be used to resolve the
     *                          namespace URI/prefix of the node
     */
    public void node(Node node, NamespaceResolver namespaceResolver, String uri, String name) {
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
                attribute(attr.getNamespaceURI(), Constants.EMPTY_STRING, resolverPfx + Constants.COLON + attr.getLocalName(), attr.getNodeValue());
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
                JSONWriterRecordContentHandler wrcHandler = new JSONWriterRecordContentHandler();

                XMLFragmentReader xfragReader = new XMLFragmentReader(namespaceResolver);
                xfragReader.setContentHandler(wrcHandler);
                xfragReader.setProperty("http://xml.org/sax/properties/lexical-handler", wrcHandler);
                xfragReader.parse(node, uri, name);
            } catch (SAXException sex) {
                throw XMLMarshalException.marshalException(sex);
            }
        }
    }

    @Override
    public boolean isWrapperAsCollectionName() {
        return marshaller.isWrapperAsCollectionName();
    }

    @Override
    public void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
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
    protected class JSONWriterRecordContentHandler implements ExtendedContentHandler, LexicalHandler {

        JSONWriterRecordContentHandler() {
        }

        // --------------------- CONTENTHANDLER METHODS --------------------- //
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            XPathFragment xPathFragment = new XPathFragment(localName);
            xPathFragment.setNamespaceURI(namespaceURI);
            openStartElement(xPathFragment, namespaceResolver);
            handleAttributes(atts);
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            XPathFragment xPathFragment = new XPathFragment(localName);
            xPathFragment.setNamespaceURI(namespaceURI);

            JSONWriterRecord.this.endElement(xPathFragment, namespaceResolver);
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            String characters = new String(ch, start, length);
            characters(characters);
        }

        public void characters(CharSequence characters) throws SAXException {
            JSONWriterRecord.this.characters(characters.toString());
        }

        // --------------------- LEXICALHANDLER METHODS --------------------- //
        public void comment(char[] ch, int start, int length) throws SAXException {
        }

        public void startCDATA() throws SAXException {
            isProcessingCData = true;
        }

        public void endCDATA() throws SAXException {
            isProcessingCData = false;
        }

        // --------------------- CONVENIENCE METHODS --------------------- //
        protected void handleAttributes(Attributes atts) {
            for (int i = 0, attsLength = atts.getLength(); i < attsLength; i++) {
                String qName = atts.getQName(i);
                if ((qName != null && (qName.startsWith(javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON) || qName.equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)))) {
                    continue;
                }
                attribute(atts.getURI(i), atts.getLocalName(i), qName, atts.getValue(i));
            }
        }

        protected void writeComment(char[] chars, int start, int length) {
        }

        protected void writeCharacters(char[] chars, int start, int length) {
            try {
                characters(chars, start, length);
            } catch (SAXException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        // --------------- SATISFY CONTENTHANDLER INTERFACE --------------- //
        public void endPrefixMapping(String prefix) throws SAXException {
        }

        public void processingInstruction(String target, String data) throws SAXException {
        }

        public void setDocumentLocator(Locator locator) {
        }

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void skippedEntity(String name) throws SAXException {
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        }

        // --------------- SATISFY LEXICALHANDLER INTERFACE --------------- //
        public void startEntity(String name) throws SAXException {
        }

        public void endEntity(String name) throws SAXException {
        }

        public void startDTD(String name, String publicId, String systemId) throws SAXException {
        }

        public void endDTD() throws SAXException {
        }

        @Override
        public void setNil(boolean isNil) {
        }

    }

    /**
     * Instances of this class are used to maintain state about the current
     * level of the JSON message being marshalled.
     */
    protected static class Level {

        private boolean first;
        private boolean collection;
        private boolean emptyCollection;
        private boolean needToOpenComplex;
        private boolean needToCloseComplex;
        private boolean nestedArray;
        private Level previousLevel;

        public Level(boolean value, boolean needToOpen, boolean nestedArray) {
            this.first = value;
            needToOpenComplex = needToOpen;
            this.nestedArray = nestedArray;
        }

        public Level(boolean value, boolean needToOpen, boolean nestedArray, Level previousLevel) {
            this(value, needToOpen, nestedArray);
            this.previousLevel = previousLevel;
        }

        public boolean isNeedToOpenComplex() {
            return needToOpenComplex;
        }

        public void setNeedToOpenComplex(boolean needToOpenComplex) {
            this.needToOpenComplex = needToOpenComplex;
        }

        public boolean isNeedToCloseComplex() {
            return needToCloseComplex;
        }

        public void setNeedToCloseComplex(boolean needToCloseComplex) {
            this.needToCloseComplex = needToCloseComplex;
        }

        public boolean isEmptyCollection() {
            return emptyCollection;
        }

        public void setEmptyCollection(boolean emptyCollection) {
            this.emptyCollection = emptyCollection;
        }

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean value) {
            this.first = value;
        }

        public boolean isCollection() {
            return collection;
        }

        public void setCollection(boolean collection) {
            this.collection = collection;
        }

        public Level getPreviousLevel() {
            return previousLevel;
        }

        public boolean isNestedArray() {
            return nestedArray;
        }

        public void setNestedArray(boolean nestedArray) {
            this.nestedArray = nestedArray;
        }
    }

    protected static interface Output {

        public void flush() throws IOException;

        public XMLMarshaller getMarshaller();

        public OutputStream getOutputStream();

        public Writer getWriter();

        public void setMarshaller(XMLMarshaller marshaller);

        public void write(char character) throws IOException;

        public void write(String text) throws IOException;

        public void writeAttributePrefix() throws IOException;

        public void writeCR() throws IOException;

        public void writeLocalName(XPathFragment xPathFragment) throws IOException;

        public void writeNamespaceSeparator() throws IOException;

        public void writeResultFromCharEscapeHandler(String value, boolean isAttribute);

    }

    protected static class OutputStreamOutput implements Output {

        private static final int BUFFER_SIZE = 512;

        private byte[] attributePrefix;
        private byte[] buffer = new byte[BUFFER_SIZE];
        private int bufferIndex = 0;
        private CharacterEscapeHandler characterEscapeHandler;
        private byte[] cr = Constants.cr().getBytes(Constants.DEFAULT_CHARSET);
        private XMLMarshaller marshaller;
        private char namespaceSeparator;
        private OutputStream outputStream;

        protected OutputStreamOutput(OutputStream writer) {
            this.outputStream = writer;
        }

        @Override
        public void flush() throws IOException {
            outputStream.write(buffer, 0, bufferIndex);
            bufferIndex = 0;
            outputStream.flush();
        }

        @Override
        public XMLMarshaller getMarshaller() {
            return marshaller;
        }

        @Override
        public OutputStream getOutputStream() {
            return outputStream;
        }

        @Override
        public Writer getWriter() {
            return null;
        }

        @Override
        public void setMarshaller(XMLMarshaller marshaller) {
            this.marshaller = marshaller;
            String attributePrefix = marshaller.getAttributePrefix();
            if (null != attributePrefix) {
                this.attributePrefix = attributePrefix.getBytes(Constants.DEFAULT_CHARSET);
            }
            this.characterEscapeHandler = marshaller.getCharacterEscapeHandler();
            this.namespaceSeparator = marshaller.getNamespaceSeparator();
        }

        private void write(byte[] bytes) {
            int bytesLength = bytes.length;
            if (bufferIndex + bytesLength >= BUFFER_SIZE) {
                try {
                    outputStream.write(buffer, 0, bufferIndex);
                    bufferIndex = 0;
                    if (bytesLength > BUFFER_SIZE) {
                        outputStream.write(bytes);
                        return;
                    }
                } catch (IOException e) {
                    throw XMLMarshalException.marshalException(e);
                }
            }
            System.arraycopy(bytes, 0, buffer, bufferIndex, bytes.length);
            bufferIndex += bytesLength;
        }

        @Override
        public void write(char character) throws IOException {
            if (character > 0x7F) {
                if (character > 0x7FF) {
                    if ((character >= Character.MIN_HIGH_SURROGATE) && (character <= Character.MAX_LOW_SURROGATE)) {
                        int uc = ((character & 0x3ff) << 10);
                        // 11110zzz
                        write((byte) (0xF0 | ((uc >> 18))));
                        // 10zzyyyy
                        write((byte) (0x80 | ((uc >> 12) & 0x3F)));
                        // 10yyyyxx
                        write((byte) (0x80 | ((uc >> 6) & 0x3F)));
                        // 10xxxxxx
                        write((byte) (0x80 + (uc & 0x3F)));
                        return;
                    } else {
                        // 1110yyyy
                        write((byte) (0xE0 + (character >> 12)));
                    }
                    // 10yyyyxx
                    write((byte) (0x80 + ((character >> 6) & 0x3F)));
                } else {
                    // 110yyyxx
                    write((byte) (0xC0 + (character >> 6)));
                }
                write((byte) (0x80 + (character & 0x3F)));
            } else {
                write((byte) character);
            }
        }

        private void write(byte b) {
            if (bufferIndex == BUFFER_SIZE) {
                try {
                    outputStream.write(buffer, 0, BUFFER_SIZE);
                    bufferIndex = 0;
                } catch (IOException e) {
                    throw XMLMarshalException.marshalException(e);
                }
            }
            buffer[bufferIndex++] = b;
        }

        @Override
        public void write(String text) throws IOException {
            write(text.getBytes(Constants.DEFAULT_CHARSET));
        }

        @Override
        public void writeAttributePrefix() throws IOException {
            write(attributePrefix);
        }

        @Override
        public void writeCR() throws IOException {
            write(cr);
        }

        @Override
        public void writeLocalName(XPathFragment xPathFragment) throws IOException {
            write(xPathFragment.getLocalNameBytes());
        }

        @Override
        public void writeNamespaceSeparator() throws IOException {
            write(namespaceSeparator);
        }

        @Override
        public void writeResultFromCharEscapeHandler(String value, boolean isAttribute) {
            try {
                CharArrayWriter out = new CharArrayWriter();
                characterEscapeHandler.escape(value.toCharArray(), 0, value.length(), isAttribute, out);
                byte[] bytes = out.toString().getBytes();
                write(bytes);
                out.close();
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

    }

    private static class WriterOutput implements Output {

        private String attributePrefix;
        private CharacterEscapeHandler characterEscapeHandler;
        private String cr = Constants.cr();
        private XMLMarshaller marshaller;
        private char namespaceSeparator;
        private Writer writer;

        @Override
        public void flush() throws IOException {
            writer.flush();
        }

        protected WriterOutput(Writer writer) {
            this.writer = writer;
        }

        public XMLMarshaller getMarshaller() {
            return marshaller;
        }

        @Override
        public OutputStream getOutputStream() {
            return null;
        }

        @Override
        public Writer getWriter() {
            return writer;
        }

        @Override
        public void setMarshaller(XMLMarshaller marshaller) {
            this.marshaller = marshaller;
            this.attributePrefix = marshaller.getAttributePrefix();
            this.characterEscapeHandler = marshaller.getCharacterEscapeHandler();
            this.namespaceSeparator = marshaller.getNamespaceSeparator();
        }

        @Override
        public void writeAttributePrefix() throws IOException {
            writer.write(attributePrefix);
        }

        @Override
        public void write(char character) throws IOException {
            writer.write(character);
        }

        @Override
        public void write(String text) throws IOException {
            writer.write(text);
        }

        public void writeCR() throws IOException {
            writer.write(cr);
        }

        @Override
        public void writeLocalName(XPathFragment xPathFragment) throws IOException {
            writer.write(xPathFragment.getLocalName());
        }

        @Override
        public void writeNamespaceSeparator() throws IOException {
            writer.write(namespaceSeparator);
        }

        @Override
        public void writeResultFromCharEscapeHandler(String value, boolean isAttribute) {
            try {
                characterEscapeHandler.escape(value.toCharArray(), 0, value.length(), isAttribute, writer);
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

    }

}
