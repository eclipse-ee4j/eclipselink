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
//     Juan Pablo Gardella = 2.7.4 - Fix for the bug #543063
package org.eclipse.persistence.internal.oxm.record.json;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonException;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.CollectionGroupingElementNodeValue;
import org.eclipse.persistence.internal.oxm.ConversionManager;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.MediaType;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.OXMSystemProperties;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.AbstractUnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.SAXUnmarshallerHandler;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.record.XMLReaderAdapter;
import org.eclipse.persistence.internal.oxm.record.deferred.DeferredContentHandler;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.record.XMLRootRecord;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class JsonStructureReader extends XMLReaderAdapter {

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private String attributePrefix = null;
    private NamespaceResolver namespaces = null;
    private boolean includeRoot;
    private String textWrapper;
    private Class unmarshalClass;
    private boolean isInCollection;
    private JsonStructure jsonStructure;
    private JsonAttributes attributes = new JsonAttributes();

    /**
     * If we should treat unqualified type property in JSON as MOXy type discriminator.
     */
    private boolean jsonTypeCompatibility;

    public JsonStructureReader(Unmarshaller u) {
        this(u, null);
    }

    public JsonStructureReader(Unmarshaller u, Class clazz) {
        this.attributePrefix = u.getAttributePrefix();
        if (Constants.EMPTY_STRING.equals(attributePrefix)) {
            attributePrefix = null;
        }
        namespaces = u.getNamespaceResolver();

        setNamespaceAware(u.getNamespaceResolver() != null);
        setNamespaceSeparator(u.getNamespaceSeparator());
        this.includeRoot = u.isIncludeRoot();
        this.setErrorHandler(u.getErrorHandler());
        this.textWrapper = u.getValueWrapper();
        this.unmarshalClass = clazz;
        this.jsonTypeCompatibility = u.getJsonTypeConfiguration().useJsonTypeCompatibility();
    }

    public void setJsonStructure(JsonStructure jsonStructure) {
        this.jsonStructure = jsonStructure;
    }

    @Override
    public void parse(InputSource input) throws IOException, SAXException, JsonException {
        if (input == null) {
            if (jsonStructure != null) {
                parseRoot(jsonStructure);
            }
            return;
        }

        try {
            InputStream inputStream = null;
            JsonReader jsonReader;
            if (null != input.getByteStream()) {
                inputStream = input.getByteStream();
                jsonReader = Json.createReader(inputStream);
            } else if (null != input.getCharacterStream()) {
                jsonReader = Json.createReader(input.getCharacterStream());
            } else {
                try {
                    URL url = new URL(input.getSystemId());
                    inputStream = url.openStream();
                } catch (MalformedURLException malformedURLException) {
                    try {
                        inputStream = new FileInputStream(input.getSystemId());
                    } catch (FileNotFoundException fileNotFoundException) {
                        throw malformedURLException;
                    }
                }
                jsonReader = Json.createReader(inputStream);
            }
            if (jsonReader != null) {
                JsonStructure structure = jsonReader.read();
                parseRoot(structure);
            }

            if (null != inputStream) {
                inputStream.close();
            }
        } catch (JsonException je) {
            throw XMLMarshalException.unmarshalException(je);
        }
    }

    @Override
    public void parse(String systemId) {
        try {
            parse(new InputSource(systemId));
        } catch (IOException | SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public void parseRoot(JsonValue jsonValue) throws SAXException {
        if (namespaces != null) {
            Map<String, String> namespacePairs = namespaces.getPrefixesToNamespaces();
            for (Entry<String, String> namespacePair : namespacePairs.entrySet()) {
                contentHandler.startPrefixMapping(namespacePair.getKey(), namespacePair.getValue());
            }
        }

        if (jsonValue.getValueType() == ValueType.OBJECT) {
            contentHandler.startDocument();
            JsonObject jsonObject = (JsonObject) jsonValue;

            Set<Entry<String, JsonValue>> children = jsonObject.entrySet();
            if (children.size() == 0 && unmarshalClass == null) {
                return;
            }

            Iterator<Entry<String, JsonValue>> iter = children.iterator();

            if (includeRoot) {
                if (children.size() > 0) {
                    Entry<String, JsonValue> nextEntry = iter.next();
                    parsePair(nextEntry.getKey(), nextEntry.getValue());
                }

            } else {

                contentHandler.startElement(Constants.EMPTY_STRING, Constants.EMPTY_STRING, null, attributes.setValue(jsonValue, attributePrefix, namespaces, getNamespaceSeparator(), isNamespaceAware()));

                while (iter.hasNext()) {
                    Entry<String, JsonValue> nextEntry = iter.next();
                    parsePair(nextEntry.getKey(), nextEntry.getValue());
                }
                contentHandler.endElement(Constants.EMPTY_STRING, Constants.EMPTY_STRING, null);
            }
            contentHandler.endDocument();
        } else if (jsonValue.getValueType() == ValueType.ARRAY) {

            SAXUnmarshallerHandler rootContentHandler = null;
            if (getContentHandler() instanceof SAXUnmarshallerHandler) {
                rootContentHandler = (SAXUnmarshallerHandler) getContentHandler();
            }
            JsonArray jsonArray = (JsonArray) jsonValue;

            List<Object> list = new ArrayList<>(jsonArray.size());
            for (JsonValue aJsonArray : jsonArray) {
                parseRoot(aJsonArray);
                if (getContentHandler() instanceof SAXUnmarshallerHandler) {
                    SAXUnmarshallerHandler saxUnmarshallerHandler = (SAXUnmarshallerHandler) contentHandler;
                    list.add(saxUnmarshallerHandler.getObject());
                    saxUnmarshallerHandler.setObject(null);
                } else if (getContentHandler() instanceof UnmarshalRecord) {
                    UnmarshalRecord unmarshalRecord = (UnmarshalRecord) contentHandler;
                    Object unmarshalledObject = unmarshalRecord.getCurrentObject();
                    if (includeRoot && unmarshalClass != null) {
                        if (!(unmarshalledObject instanceof Root)) {
                            Root xmlRoot = unmarshalRecord.createRoot();
                            xmlRoot.setNamespaceURI(unmarshalRecord.getRootElementNamespaceUri());
                            xmlRoot.setLocalName(unmarshalRecord.getLocalName());
                            xmlRoot.setObject(unmarshalledObject);
                            unmarshalledObject = xmlRoot;
                        }
                    }
                    list.add(unmarshalledObject);
                    unmarshalRecord.setCurrentObject(null);
                    unmarshalRecord.setRootElementName(null);
                    unmarshalRecord.setLocalName(null);
                }
            }
            if (getContentHandler() instanceof SAXUnmarshallerHandler) {
                ((SAXUnmarshallerHandler) getContentHandler()).setObject(list);
            } else if (getContentHandler() instanceof UnmarshalRecord) {
                ((UnmarshalRecord) getContentHandler()).setCurrentObject(list);
                ((UnmarshalRecord) getContentHandler()).setRootElementName(Constants.EMPTY_STRING);
                ((UnmarshalRecord) getContentHandler()).setLocalName(Constants.EMPTY_STRING);
                if (rootContentHandler != null) {
                    rootContentHandler.setObject(list);
                }
            }

        } else {
            getContentHandler().startDocument();
            parseValue(jsonValue);
        }
    }

    private void parseValue(JsonValue jsonValue) throws SAXException {
        switch (jsonValue.getValueType()) {
            case STRING: {
                String string = ((JsonString) jsonValue).getString();
                contentHandler.characters(string);
                break;
            }
            case FALSE: {
                contentHandler.characters(FALSE);
                break;
            }
            case TRUE: {
                contentHandler.characters(TRUE);
                break;
            }
            case NUMBER: {
                JsonNumber number = ((JsonNumber) jsonValue);
                contentHandler.characters(number.toString());
                break;
            }
            case OBJECT: {
                Entry<String, JsonValue> xmlValueEntry = null;
                for (Entry<String, JsonValue> nextEntry : ((JsonObject) jsonValue).entrySet()) {
                    if (textWrapper != null && textWrapper.equals(nextEntry.getKey())) {
                        xmlValueEntry = nextEntry;
                    } else {
                        parsePair(nextEntry.getKey(), nextEntry.getValue());
                    }
                }
                //Proceed JSON value mapped to @XmlValue property as a last
                if (xmlValueEntry != null) {
                    parsePair(xmlValueEntry.getKey(), xmlValueEntry.getValue());
                }
                break;
            }
            case ARRAY: {
                for (JsonValue value : (JsonArray) jsonValue) {
                    parseValue(value);
                }
                break;
            }
            case NULL: {
                contentHandler.setNil(true);
                break;
            }
            default:
                throw new IllegalStateException("Unhandled valueType: " + jsonValue.getValueType());
        }
    }

    private void parsePair(String name, JsonValue jsonValue) throws SAXException {
        if (jsonValue == null) {
            return;
        }
        ValueType valueType = jsonValue.getValueType();

        if (valueType == ValueType.ARRAY) {
            JsonArray jsonArray = (JsonArray) jsonValue;
            String parentLocalName = name;

            if (attributePrefix != null && parentLocalName.startsWith(attributePrefix)) {
                // do nothing;
                return;
            }
            String uri = Constants.EMPTY_STRING;
            if (isNamespaceAware() && namespaces != null) {
                if (parentLocalName.length() > 2) {
                    int nsIndex = parentLocalName.indexOf(getNamespaceSeparator(), 1);
                    if (nsIndex > -1) {
                        String prefix = parentLocalName.substring(0, nsIndex);
                        uri = namespaces.resolveNamespacePrefix(prefix);
                    }
                    if (uri == null || uri == Constants.EMPTY_STRING) {
                        uri = namespaces.getDefaultNamespaceURI();
                    } else {
                        parentLocalName = parentLocalName.substring(nsIndex + 1);
                    }
                } else {
                    uri = namespaces.getDefaultNamespaceURI();
                }
            }

            boolean isTextValue;
            int arraySize = jsonArray.size();
            if (arraySize == 0) {
                if (contentHandler instanceof UnmarshalRecord || isUnmarshalRecordWithinAdapter()) {
                    final UnmarshalRecord ur = this.contentHandler instanceof UnmarshalRecord ? (UnmarshalRecord) this.contentHandler : getUnmarshalRecordFromAdapter();
                    XPathNode node = ur.getNonAttributeXPathNode(uri, parentLocalName, parentLocalName, null);
                    if (node != null) {
                        NodeValue nv = node.getNodeValue();
                        if (nv == null && node.getTextNode() != null) {
                            nv = node.getTextNode().getUnmarshalNodeValue();
                        }
                        if (nv != null && nv.isContainerValue()) {
                            ur.getContainerInstance(((ContainerValue) nv));
                        }
                    }
                }
            }
            startCollection();

            XPathFragment groupingXPathFragment = null;
            XPathFragment itemXPathFragment = null;
            if (contentHandler instanceof UnmarshalRecord || isUnmarshalRecordWithinAdapter()) {
                final UnmarshalRecord contentHandler_ = contentHandler instanceof UnmarshalRecord ? (UnmarshalRecord) contentHandler : getUnmarshalRecordFromAdapter();
                isTextValue = isTextValue(parentLocalName, contentHandler_);
                UnmarshalRecord unmarshalRecord = contentHandler_;
                if (unmarshalRecord.getUnmarshaller().isWrapperAsCollectionName()) {
                    XPathNode unmarshalRecordXPathNode = unmarshalRecord.getXPathNode();
                    if (null != unmarshalRecordXPathNode) {
                        XPathFragment currentFragment = new XPathFragment();
                        currentFragment.setLocalName(parentLocalName);
                        currentFragment.setNamespaceURI(uri);
                        currentFragment.setNamespaceAware(isNamespaceAware());
                        XPathNode groupingXPathNode = unmarshalRecordXPathNode.getNonAttributeChildrenMap().get(currentFragment);
                        if (groupingXPathNode != null) {
                            if (groupingXPathNode.getUnmarshalNodeValue() instanceof CollectionGroupingElementNodeValue) {
                                groupingXPathFragment = groupingXPathNode.getXPathFragment();
                                contentHandler_.startElement(uri, parentLocalName, parentLocalName, new AttributesImpl());
                                XPathNode itemXPathNode = groupingXPathNode.getNonAttributeChildren().get(0);
                                itemXPathFragment = itemXPathNode.getXPathFragment();
                            } else if (groupingXPathNode.getUnmarshalNodeValue() == null) {
                                XPathNode itemXPathNode = groupingXPathNode.getNonAttributeChildren().get(0);
                                if (itemXPathNode != null) {
                                    if ((itemXPathNode.getUnmarshalNodeValue()).isContainerValue()) {
                                        groupingXPathFragment = groupingXPathNode.getXPathFragment();
                                        contentHandler_.startElement(uri, parentLocalName, parentLocalName, new AttributesImpl());
                                        itemXPathFragment = itemXPathNode.getXPathFragment();
                                    }
                                }
                            }
                        }
                    }
                }

                for (JsonValue nextArrayValue : jsonArray) {
                    if (nextArrayValue.getValueType() == ValueType.NULL) {
                        contentHandler.setNil(true);
                    }

                    if (!isTextValue) {
                        if (null != itemXPathFragment) {
                            contentHandler.startElement(itemXPathFragment.getNamespaceURI(), itemXPathFragment.getLocalName(), itemXPathFragment.getLocalName(), attributes.setValue(nextArrayValue, attributePrefix, namespaces, getNamespaceSeparator(), isNamespaceAware()));
                        } else {
                            contentHandler.startElement(uri, parentLocalName, parentLocalName, attributes.setValue(nextArrayValue, attributePrefix, namespaces, getNamespaceSeparator(), isNamespaceAware()));
                        }

                    }
                    //Internally store each nested array it as JsonObject with name: "item"
                    if (valueType == nextArrayValue.getValueType()) {
                        JsonBuilderFactory factory = Json.createBuilderFactory(null);
                        JsonObjectBuilder jsonObjectBuilder = factory.createObjectBuilder();
                        jsonObjectBuilder.add("item", nextArrayValue);
                        nextArrayValue = jsonObjectBuilder.build();
                    }
                    parseValue(nextArrayValue);
                    if (!isTextValue) {
                        if (null != itemXPathFragment) {
                            contentHandler.endElement(itemXPathFragment.getNamespaceURI(), itemXPathFragment.getLocalName(), itemXPathFragment.getLocalName());
                        } else {
                            contentHandler.endElement(uri, parentLocalName, parentLocalName);
                        }
                    }
                }
            }
            if (null != groupingXPathFragment) {
                contentHandler.endElement(uri, groupingXPathFragment.getLocalName(), groupingXPathFragment.getLocalName());
            }
            endCollection();
        } else {
            if (attributePrefix != null && name.startsWith(attributePrefix)) {
                return;
            }
            String localName = name;
            String uri = Constants.EMPTY_STRING;
            if (isNamespaceAware() && namespaces != null) {
                if (localName.length() > 2) {
                    int nsIndex = localName.indexOf(getNamespaceSeparator(), 1);
                    String prefix = Constants.EMPTY_STRING;
                    if (nsIndex > -1) {
                        prefix = localName.substring(0, nsIndex);
                    }
                    uri = namespaces.resolveNamespacePrefix(prefix);
                    if (uri == null || uri == Constants.EMPTY_STRING) {
                        uri = namespaces.getDefaultNamespaceURI();
                    } else {
                        localName = localName.substring(nsIndex + 1);
                    }

                    if (localName.equals(Constants.SCHEMA_TYPE_ATTRIBUTE) && uri != null && uri.equals(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {
                        return;
                    }
                } else {
                    uri = namespaces.getDefaultNamespaceURI();
                }
            }
            if (contentHandler instanceof XMLRootRecord || contentHandler instanceof DeferredContentHandler) {
                if (jsonTypeCompatibility) {
                    // if its not namespaceAware don't report the "type" child as it
                    // is will be read by the xsi:type lookup
                    if (!isNamespaceAware() && localName.equals(Constants.SCHEMA_TYPE_ATTRIBUTE)) {
                        return;
                    }
                }
                if (textWrapper != null && textWrapper.equals(localName)) {
                    parseValue(jsonValue);
                    return;
                }
            } else if (contentHandler instanceof UnmarshalRecord && ((UnmarshalRecord) contentHandler).getXPathNode() != null) {
                if (jsonTypeCompatibility) {
                    if (!isNamespaceAware() && localName.equals(Constants.SCHEMA_TYPE_ATTRIBUTE) && !((UnmarshalRecord) contentHandler).getXPathNode().hasTypeChild()) {
                        return;
                    }
                }
                boolean isTextValue = isTextValue(localName, (UnmarshalRecord) contentHandler);
                if (isTextValue) {
                    parseValue(jsonValue);
                    return;
                }
                NodeValue nv = ((UnmarshalRecord) contentHandler).getAttributeChildNodeValue(uri, localName);
                if (attributePrefix == null && nv != null) {
                    return;
                }
            } else if (isUnmarshalRecordWithinAdapter()) {
                @SuppressWarnings("rawtypes") final UnmarshalRecord contentHandler_ = getUnmarshalRecordFromAdapter();
                if (jsonTypeCompatibility) {
                    if (!isNamespaceAware() && localName.equals(Constants.SCHEMA_TYPE_ATTRIBUTE) && !contentHandler_.getXPathNode().hasTypeChild()) {
                        return;
                    }
                }
                boolean isTextValue = isTextValue(localName, contentHandler_);
                if (isTextValue) {
                    parseValue(jsonValue);
                    return;
                }
                NodeValue nv = contentHandler_.getAttributeChildNodeValue(uri, localName);
                if (attributePrefix == null && nv != null) {
                    return;
                }
            }

            if (jsonValue.getValueType() == ValueType.NULL) {
                contentHandler.setNil(true);
            }

            contentHandler.startElement(uri, localName, localName, attributes.setValue(jsonValue, attributePrefix, namespaces, getNamespaceSeparator(), isNamespaceAware()));
            parseValue(jsonValue);
            contentHandler.endElement(uri, localName, localName);

        }

    }

    private UnmarshalRecord getUnmarshalRecordFromAdapter() {
        return (UnmarshalRecord) ((ValidatingContentHandler) ((ExtendedContentHandlerAdapter) contentHandler)
                .getContentHandler()).getContentHandler();
    }

    private boolean isUnmarshalRecordWithinAdapter() {
        return contentHandler instanceof ExtendedContentHandlerAdapter
                && ((ExtendedContentHandlerAdapter) contentHandler)
                .getContentHandler() instanceof ValidatingContentHandler
                && ((ValidatingContentHandler) ((ExtendedContentHandlerAdapter) contentHandler).getContentHandler())
                .getContentHandler() instanceof UnmarshalRecord;
    }

    public boolean isNullRepresentedByXsiNil(AbstractNullPolicy nullPolicy) {
        return true;
    }

    private void startCollection() {
        isInCollection = true;
    }

    private void endCollection() {
        isInCollection = false;
    }

    @Override
    public boolean isInCollection() {
        return isInCollection;
    }

    private boolean isTextValue(String localName, UnmarshalRecord contentHandler_) {
        XPathNode currentNode = ((UnmarshalRecord) contentHandler_).getXPathNode();
        if (currentNode == null) {
            return textWrapper != null && textWrapper.equals(localName);
        }

        return ((currentNode.getNonAttributeChildrenMap() == null
                || currentNode.getNonAttributeChildrenMap().size() == 0
                || (currentNode.getNonAttributeChildrenMap().size() == 1
                && currentNode.getTextNode() != null)
        ) && textWrapper != null && textWrapper.equals(localName)
        );
    }


    @Override
    public Object convertValueBasedOnSchemaType(Field xmlField, Object value, ConversionManager conversionManager, AbstractUnmarshalRecord record) {
        if (xmlField.getSchemaType() != null) {
            if (Constants.QNAME_QNAME.equals(xmlField.getSchemaType())) {
                String stringValue = (String) value;
                int indexOpen = stringValue.indexOf('{');
                int indexClose = stringValue.indexOf('}');
                String uri;
                String localName;
                if (indexOpen > -1 && indexClose > -1) {
                    uri = stringValue.substring(indexOpen + 1, indexClose);
                    localName = stringValue.substring(indexClose + 1);
                } else {
                    QName obj = (QName) xmlField.convertValueBasedOnSchemaType(stringValue, conversionManager, record);
                    localName = obj.getLocalPart();
                    uri = obj.getNamespaceURI();
                }
                if (uri != null) {
                    return new QName(uri, localName);
                } else {
                    return new QName(localName);
                }
            } else {
                Class fieldType = xmlField.getType();
                if (fieldType == null) {
                    fieldType = xmlField.getJavaClass(xmlField.getSchemaType(), conversionManager);
                }
                return conversionManager.convertObject(value, fieldType, xmlField.getSchemaType());
            }
        }
        return value;
    }

    /**
     * INTERNAL: The MediaType associated with this reader
     */
    @Override
    public MediaType getMediaType() {
        return Constants.APPLICATION_JSON;
    }

    private static class JsonAttributes extends IndexedAttributeList {

        private JsonValue value;
        private String attributePrefix;
        private char namespaceSeparator;
        private NamespaceResolver namespaces;
        private boolean namespaceAware;

        public JsonAttributes setValue(JsonValue value, String attributePrefix, NamespaceResolver nr, char namespaceSeparator, boolean namespaceAware) {
            reset();
            this.value = value;
            this.attributePrefix = attributePrefix;
            this.namespaces = nr;
            this.namespaceSeparator = namespaceSeparator;
            this.namespaceAware = namespaceAware;
            return this;
        }

        private void addSimpleAttribute(List<Attribute> attributes, String uri, String attributeLocalName, JsonValue childValue) {
            switch (childValue.getValueType()) {
                case STRING: {
                    String stringValue = ((JsonString) childValue).getString();
                    attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, stringValue));
                    break;
                }
                case NUMBER: {
                    attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, childValue.toString()));
                    break;
                }
                case FALSE: {
                    attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, FALSE));
                    break;
                }
                case TRUE: {
                    attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, TRUE));
                    break;
                }
                case ARRAY:
                case OBJECT:
                case NULL:
                    break; // noop
                default:
                    throw new IllegalStateException("Unhandled valueType: " + childValue.getValueType());
            }
        }

        @Override
        public int getIndex(String uri, String localName) {
            if (null == localName) {
                return -1;
            }
            int index = 0;
            for (Attribute attribute : attributes()) {
                if (namespaceAware) {
                    if (localName.equals(attribute.getLocalName()) && uri.equals(attribute.getUri())) {
                        return index;
                    }
                } else {
                    if (attribute.getName().equals(localName)) {
                        return index;
                    }
                }
                index++;
            }
            return -1;
        }

        @Override
        protected Attribute[] attributes() {
            if (null == attributes) {

                switch (value.getValueType()) {
                    case NULL: {
                        return NO_ATTRIBUTES;
                    }
                    case OBJECT: {
                        JsonObject jsonObject = (JsonObject) value;
                        ArrayList<Attribute> attributesList = new ArrayList<>(jsonObject.values().size());

                        for (Entry<String, JsonValue> nextEntry : jsonObject.entrySet()) {
                            String attributeLocalName = nextEntry.getKey();

                            if (attributePrefix != null) {
                                if (attributeLocalName.startsWith(attributePrefix)) {
                                    attributeLocalName = attributeLocalName.substring(attributePrefix.length());
                                } else {
                                    continue;
                                }
                            }

                            String uri = Constants.EMPTY_STRING;

                            if (namespaceAware && namespaces != null) {
                                if (attributeLocalName.length() > 2) {
                                    String prefix = Constants.EMPTY_STRING;
                                    int nsIndex = attributeLocalName.indexOf(namespaceSeparator, 1);
                                    if (nsIndex > -1) {
                                        prefix = attributeLocalName.substring(0, nsIndex);
                                    }
                                    uri = namespaces.resolveNamespacePrefix(prefix);
                                    if (uri == null) {
                                        uri = namespaces.getDefaultNamespaceURI();
                                    } else {
                                        attributeLocalName = attributeLocalName.substring(nsIndex + 1);
                                    }
                                } else {
                                    uri = namespaces.getDefaultNamespaceURI();
                                }
                            }

                            JsonValue nextValue = nextEntry.getValue();
                            if (nextValue.getValueType() == ValueType.ARRAY) {
                                JsonArray jsonArray = (JsonArray) nextValue;
                                if (jsonArray.size() == 0) {
                                    attributesList.add(new Attribute(uri, attributeLocalName, attributeLocalName, ""));
                                }
                                for (JsonValue nextChildValue : jsonArray) {
                                    addSimpleAttribute(attributesList, uri, attributeLocalName, nextChildValue);
                                }
                            } else {
                                addSimpleAttribute(attributesList, uri, attributeLocalName, nextValue);
                            }
                        }

                        attributes = attributesList.toArray(new Attribute[attributesList.size()]);
                        break;
                    }
                    default: {
                        attributes = NO_ATTRIBUTES;
                    }
                }
            }
            return attributes;
        }

    }

}
