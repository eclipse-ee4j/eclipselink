/*
 * Copyright (c) 2013, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.6 - initial implementation
//     Radek Felman - 2.7.5 - Bug 389815 - Enhancement Request - JSON specific multidimensional array support
package org.eclipse.persistence.oxm.record;

import java.io.IOException;
import java.io.StringWriter;
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

public abstract class JsonRecord<T extends JsonRecord.Level> extends MarshalRecord<XMLMarshaller> {

    protected T position;
    protected CharacterEscapeHandler characterEscapeHandler;
    protected String attributePrefix;
    protected boolean isRootArray;
    protected static final String NULL = "null";
    protected boolean isLastEventStart;

    /**
     * INTERNAL:
     */
    @Override
    public void setMarshaller(XMLMarshaller marshaller) {
        super.setMarshaller(marshaller);
        attributePrefix = marshaller.getAttributePrefix();
        if (marshaller.getValueWrapper() != null) {
            textWrapperFragment = new XPathFragment();
            textWrapperFragment.setLocalName(marshaller.getValueWrapper());
        }
        characterEscapeHandler = marshaller.getCharacterEscapeHandler();
    }

    @Override
    public void forceValueWrapper() {
        setComplex(position, true);
        isLastEventStart = false;
    }

    @Override
    public void startDocument(String encoding, String version) {
        if (isRootArray) {
            if (position == null) {
                startCollection();
            }
            position.setEmptyCollection(false);

            position = createNewLevel(false, position, false);

            isLastEventStart = true;
        } else {
            startRootObject();
        }
    }

    protected T createNewLevel(boolean collection, T parentLevel, boolean nestedArray) {
        return (T) new Level(collection, position, nestedArray);
    }

    protected void startRootObject() {
        position = createNewLevel(false, null, false);
    }


    @Override
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        super.openStartElement(xPathFragment, namespaceResolver);
        String keyName1 = getKeyName(xPathFragment);
        if (position != null && position.isCollection && xPathFragment.getXMLField() != null && xPathFragment.getXMLField().isNestedArray()
                && this.marshaller.getJsonTypeConfiguration().isJsonDisableNestedArrayName()) {
            position.addSkip();
            position.setKeyName(keyName1);
            if (!position.isEmptyCollectionGenerated()) {
                startEmptyCollection();
                position.setEmptyCollectionGenerated(true);
            }
            return;
        }
        if (position != null) {
            T newLevel;
            if (xPathFragment.getXMLField() != null && xPathFragment.getXMLField().isNestedArray() && this.marshaller.getJsonTypeConfiguration().isJsonDisableNestedArrayName()) {
                newLevel = createNewLevel(false, position, true);
            } else {
                newLevel = createNewLevel(false, position, false);
            }

            if (isLastEventStart) {
                //this means 2 startevents in a row so the last this is a complex object
                setComplex(position, true);
            }

            String keyName = getKeyName(xPathFragment);
            if (keyName != null && !keyName.equals(Constants.EMPTY_STRING)) {
                if (position.isCollection && position.isEmptyCollection()) {
                    position.setKeyName(keyName);
                    startEmptyCollection();
                } else {
                    newLevel.setKeyName(keyName);
                }
            }
            if (!(newLevel.isNestedArray() && newLevel.isComplex())) {
                position = newLevel;
            }
            isLastEventStart = true;
        }
    }

    protected void startEmptyCollection() {
    }

    /**
     * Handle marshal of an empty collection.
     *
     * @param xPathFragment
     * @param namespaceResolver
     * @param openGrouping      if grouping elements should be marshalled for empty collections
     * @return
     */
    @Override
    public boolean emptyCollection(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, boolean openGrouping) {

        if (marshaller.isMarshalEmptyCollections()) {
            super.emptyCollection(xPathFragment, namespaceResolver, true);

            if (null != xPathFragment) {

                if (xPathFragment.isSelfFragment() || xPathFragment.nameIsText()) {
                    String keyName = position.getKeyName();
                    setComplex(position, false);
                    writeEmptyCollection((T) position.parentLevel, keyName);
                } else {
                    if (isLastEventStart) {
                        setComplex(position, true);
                    }
                    String keyName = getKeyName(xPathFragment);
                    if (keyName != null) {
                        writeEmptyCollection(position, keyName);
                    }
                }
                isLastEventStart = false;
            }

            return true;
        } else {
            return super.emptyCollection(xPathFragment, namespaceResolver, openGrouping);
        }
    }

    protected abstract void writeEmptyCollection(T level, String keyName);

    @Override
    public void endDocument() {
        if (position != null) {
            finishLevel();
        }
    }

    protected void finishLevel() {
        boolean notSkip = position.parentLevel == null || position.parentLevel.notSkip();
        if (notSkip) {
            position = (T) position.parentLevel;
        }
    }

    @Override
    public void startCollection() {
        if (position == null) {
            isRootArray = true;
            position = createNewLevel(true, null, false);
            startRootLevelCollection();
        } else {
            if (isLastEventStart) {
                setComplex(position, true);
            }
            position = createNewLevel(true, position, position.isNestedArray());
        }
        isLastEventStart = false;
    }

    protected void setComplex(T level, boolean complex) {
        level.setComplex(complex);
    }

    protected abstract void startRootLevelCollection();

    protected String getKeyName(XPathFragment xPathFragment) {
        String keyName = xPathFragment.getLocalName();

        if (isNamespaceAware()) {
            if (xPathFragment.getNamespaceURI() != null) {
                String prefix = null;
                if (getNamespaceResolver() != null) {
                    prefix = getNamespaceResolver().resolveNamespaceURI(xPathFragment.getNamespaceURI());
                } else if (namespaceResolver != null) {
                    prefix = namespaceResolver.resolveNamespaceURI(xPathFragment.getNamespaceURI());
                }
                if (prefix != null && !prefix.equals(Constants.EMPTY_STRING)) {
                    keyName = prefix + getNamespaceSeparator() + keyName;
                }
            }
        }
        if (xPathFragment.isAttribute() && attributePrefix != null) {
            keyName = attributePrefix + keyName;
        }

        return keyName;
    }

    @Override
    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, Object value, QName schemaType) {
        if (xPathFragment.getNamespaceURI() != null && xPathFragment.getNamespaceURI() == javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI) {
            return;
        }
        xPathFragment.setAttribute(true);
        openStartElement(xPathFragment, namespaceResolver);
        characters(schemaType, value, null, false, true);
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
     * The character used to separate the prefix and uri portions when namespaces are present
     *
     * @since 2.4
     */
    @Override
    public char getNamespaceSeparator() {
        return marshaller.getNamespaceSeparator();
    }

    /**
     * INTERNAL:
     * The optional fragment used to wrap the text() mappings
     *
     * @since 2.4
     */
    @Override
    public XPathFragment getTextWrapperFragment() {
        return textWrapperFragment;
    }

    @Override
    public boolean isWrapperAsCollectionName() {
        return marshaller.isWrapperAsCollectionName();
    }

    @Override
    public void element(XPathFragment frag) {
        isLastEventStart = false;
    }


    @Override
    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
        attribute(xPathFragment, namespaceResolver, value, null);
    }

    @Override
    public void attribute(String namespaceURI, String localName, String qName, String value) {
        XPathFragment xPathFragment = new XPathFragment();
        xPathFragment.setNamespaceURI(namespaceURI);
        xPathFragment.setAttribute(true);
        xPathFragment.setLocalName(localName);

        openStartElement(xPathFragment, namespaceResolver);
        characters(null, value, null, false, true);

        endElement(xPathFragment, namespaceResolver);

    }

    @Override
    public void closeStartElement() {
    }

    @Override
    public void characters(String value) {
        writeValue(value, null, false);
    }

    @Override
    public void characters(QName schemaType, Object value, String mimeType, boolean isCDATA) {
        characters(schemaType, value, mimeType, isCDATA, false);
    }

    public void characters(QName schemaType, Object value, String mimeType, boolean isCDATA, boolean isAttribute) {
        if (mimeType != null) {
            if (value instanceof List) {
                value = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesListForBinaryValues((List) value, marshaller, mimeType);
            } else {

                value = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(value, marshaller, mimeType).getData();
            }
        }
        if (schemaType != null && Constants.QNAME_QNAME.equals(schemaType)) {
            String convertedValue = getStringForQName((QName) value);
            writeValue(convertedValue, null, isAttribute);
        } else if (value.getClass() == String.class) {
            //if schemaType is set and it's a numeric or boolean type don't treat as a string
            if (schemaType != null && isNumericOrBooleanType(schemaType)) {
                ConversionManager conversionManager = getConversionManager();
                Class<?> theClass = conversionManager.javaType(schemaType);
                Object convertedValue = conversionManager.convertObject(value, theClass, schemaType);
                writeValue(convertedValue, schemaType, isAttribute);
            } else if (isCDATA) {
                cdata((String) value);
            } else {
                writeValue(value, null, isAttribute);
            }
        } else {
            Class<?> theClass = ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).javaType(schemaType);

            if (schemaType == null || theClass == null) {
                if (value.getClass() == CoreClassConstants.BOOLEAN || CoreClassConstants.NUMBER.isAssignableFrom(value.getClass())) {
                    writeValue(value, schemaType, isAttribute);
                } else {
                    String convertedValue = ((String) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.STRING, schemaType));
                    writeValue(convertedValue, schemaType, isAttribute);
                }
            } else if (schemaType != null && !isNumericOrBooleanType(schemaType)) {
                //if schemaType exists and is not boolean or number do write quotes (convert to string)
                String convertedValue = ((String) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.STRING, schemaType));
                writeValue(convertedValue, schemaType, isAttribute);
            } else if (isCDATA) {
                String convertedValue = ((String) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.STRING, schemaType));
                cdata(convertedValue);
            } else {
                writeValue(value, schemaType, isAttribute);
            }
        }
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

    public void writeValue(Object value, QName schemaType, boolean isAttribute) {

        if (characterEscapeHandler != null && value instanceof String) {
            try {
                StringWriter stringWriter = new StringWriter();
                characterEscapeHandler.escape(((String) value).toCharArray(), 0, ((String) value).length(), isAttribute, stringWriter);
                value = stringWriter.toString();
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        boolean textWrapperOpened = false;
        if (!isLastEventStart) {
            openStartElement(textWrapperFragment, namespaceResolver);
            textWrapperOpened = true;
        }

        T currentLevel = position;
        String keyName = position.getKeyName();
        if (!position.isComplex) {
            currentLevel = (T) position.parentLevel;
        }
        addValue(currentLevel, keyName, value, schemaType);
        isLastEventStart = false;
        if (textWrapperOpened) {
            endElement(textWrapperFragment, namespaceResolver);
        }
    }

    @Override
    public void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        if (position != null) {
            if (isLastEventStart) {
                setComplex(position, true);
            }
            if (position.isComplex) {
                finishLevel();
            } else {
                position = (T) position.parentLevel;
            }
            isLastEventStart = false;
        }
    }

    private void addValue(T currentLevel, String keyName, Object value, QName schemaType) {
        if (currentLevel.isCollection()) {
            addValueToArray(currentLevel, value, schemaType);
            currentLevel.setEmptyCollection(false);
        } else {
            addValueToObject(currentLevel, keyName, value, schemaType);
        }
    }

    protected abstract void addValueToObject(T currentLevel, String keyName, Object value, QName schemaType);

    protected abstract void addValueToArray(T currentLevel, Object value, QName schemaType);

    @Override
    public void cdata(String value) {
        characters(value);
    }

    @Override
    public void node(Node node, NamespaceResolver resolver, String uri, String name) {

        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            Attr attr = (Attr) node;
            String resolverPfx = null;
            if (getNamespaceResolver() != null) {
                resolverPfx = this.getNamespaceResolver().resolveNamespaceURI(attr.getNamespaceURI());
            }
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
            writeValue(node.getNodeValue(), null, false);
        } else {
            try {
                JsonRecordContentHandler wrcHandler = new JsonRecordContentHandler();

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
    protected String getStringForQName(QName qName) {
        if (null == qName) {
            return null;
        }
        CoreConversionManager xmlConversionManager = getSession().getDatasourcePlatform().getConversionManager();

        return (String) xmlConversionManager.convertObject(qName, String.class);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void namespaceDeclarations(NamespaceResolver namespaceResolver) {
    }

    @Override
    public void namespaceDeclaration(String prefix, String namespaceURI) {
    }

    @Override
    public void defaultNamespaceDeclaration(String defaultNamespace) {
    }

    /**
     * INTERNAL:
     */
    @Override
    public void nilComplex(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
        closeStartGroupingElements(groupingFragment);
        openStartElement(xPathFragment, namespaceResolver);
        characters(NULL);
        endElement(xPathFragment, namespaceResolver);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void nilSimple(NamespaceResolver namespaceResolver) {
        XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
        characters(NULL);
        closeStartGroupingElements(groupingFragment);
    }

    /**
     * Used when an empty simple value should be written
     *
     * @since EclipseLink 2.4
     */
    @Override
    public void emptySimple(NamespaceResolver namespaceResolver) {
        nilSimple(namespaceResolver);
    }

    @Override
    public void emptyAttribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
        openStartElement(xPathFragment, namespaceResolver);
        characters(NULL);
        endElement(xPathFragment, namespaceResolver);
        closeStartGroupingElements(groupingFragment);
    }

    /**
     * Used when an empty complex item should be written
     *
     * @since EclipseLink 2.4
     */
    @Override
    public void emptyComplex(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
        closeStartGroupingElements(groupingFragment);
        openStartElement(xPathFragment, namespaceResolver);
        endElement(xPathFragment, namespaceResolver);
    }


    /**
     * This class will typically be used in conjunction with an XMLFragmentReader.
     * The XMLFragmentReader will walk a given XMLFragment node and report events
     * to this class - the event's data is then written to the enclosing class'
     * writer.
     *
     * @see org.eclipse.persistence.internal.oxm.record.XMLFragmentReader
     */
    protected class JsonRecordContentHandler implements ExtendedContentHandler, LexicalHandler {

        JsonRecordContentHandler() {
        }

        // --------------------- CONTENTHANDLER METHODS --------------------- //
        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            XPathFragment xPathFragment = new XPathFragment(localName);
            xPathFragment.setNamespaceURI(namespaceURI);
            openStartElement(xPathFragment, namespaceResolver);
            handleAttributes(atts);
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            XPathFragment xPathFragment = new XPathFragment(localName);
            xPathFragment.setNamespaceURI(namespaceURI);

            JsonRecord.this.endElement(xPathFragment, namespaceResolver);
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String characters = new String(ch, start, length);
            characters(characters);
        }

        @Override
        public void characters(CharSequence characters) throws SAXException {
            JsonRecord.this.characters(characters.toString());
        }

        // --------------------- LEXICALHANDLER METHODS --------------------- //
        @Override
        public void comment(char[] ch, int start, int length) throws SAXException {
        }

        @Override
        public void startCDATA() throws SAXException {
        }

        @Override
        public void endCDATA() throws SAXException {
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
        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
        }

        @Override
        public void setDocumentLocator(Locator locator) {
        }

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        }

        // --------------- SATISFY LEXICALHANDLER INTERFACE --------------- //
        @Override
        public void startEntity(String name) throws SAXException {
        }

        @Override
        public void endEntity(String name) throws SAXException {
        }

        @Override
        public void startDTD(String name, String publicId, String systemId) throws SAXException {
        }

        @Override
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

        protected boolean isCollection;
        protected boolean emptyCollection;
        protected boolean emptyCollectionGenerated;
        protected String keyName;
        protected boolean isComplex;
        protected boolean nestedArray;
        protected Level parentLevel;
        private int skipCount;

        public Level(boolean isCollection, Level parentLevel, boolean nestedArray) {
            setCollection(isCollection);
            emptyCollection = true;
            this.parentLevel = parentLevel;
            this.nestedArray = nestedArray;
            this.skipCount = 0;
        }

        protected void addSkip() {
            skipCount++;
        }

        protected boolean notSkip() {
            if (skipCount > 0) {
                skipCount--;
                return false;
            } else {
                return true;
            }
        }

        protected int getSkipCount() {
            return skipCount;
        }

        public boolean isCollection() {
            return isCollection;
        }

        public void setCollection(boolean isCollection) {
            this.isCollection = isCollection;
        }

        public String getKeyName() {
            return keyName;
        }

        public void setKeyName(String keyName) {
            this.keyName = keyName;
        }

        public boolean isEmptyCollection() {
            return emptyCollection;
        }

        public void setEmptyCollection(boolean emptyCollection) {
            this.emptyCollection = emptyCollection;
        }

        public boolean isEmptyCollectionGenerated() {
            return emptyCollectionGenerated;
        }

        public void setEmptyCollectionGenerated(boolean emptyCollectionGenerated) {
            this.emptyCollectionGenerated = emptyCollectionGenerated;
        }

        public boolean isComplex() {
            return isComplex;
        }

        public void setComplex(boolean isComplex) {
            this.isComplex = isComplex;
        }

        public boolean isNestedArray() {
            return nestedArray;
        }

        public void setNestedArray(boolean nestedArray) {
            this.nestedArray = nestedArray;
        }

    }

}
