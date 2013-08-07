/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record.json;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.CollectionGroupingElementNodeValue;
import org.eclipse.persistence.internal.oxm.ConversionManager;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.MappingNodeValue;
import org.eclipse.persistence.internal.oxm.MediaType;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.NodeValue;
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
import org.xml.sax.ErrorHandler;
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


    public JsonStructureReader(Unmarshaller u) {
        this(u, null);        
    }
    
    public JsonStructureReader(Unmarshaller u, Class clazz) {
        this(u.getAttributePrefix(), u.getNamespaceResolver(), u.getNamespaceResolver() != null, u.isIncludeRoot(), u.getNamespaceSeparator(), u.getErrorHandler(), u.getValueWrapper(), clazz);
    }

    private JsonStructureReader(String attrPrefix, NamespaceResolver nr,boolean namespaceAware, boolean includeRoot,Character namespaceSeparator, ErrorHandler errorHandler, String textWrapper, Class unmarshalClass) {
        this.attributePrefix = attrPrefix;
        if (attributePrefix == Constants.EMPTY_STRING) {
            attributePrefix = null;
        }
        namespaces = nr;
        this.namespaceAware = namespaceAware;
        if (namespaceSeparator == null) {
            this.namespaceSeparator = Constants.DOT;
        } else {
            this.namespaceSeparator = namespaceSeparator;
        }
        this.includeRoot = includeRoot;
        this.setErrorHandler(errorHandler);
        this.textWrapper = textWrapper;
        this.unmarshalClass = unmarshalClass;
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
            JsonReader jsonReader = null;
            if (null != input.getByteStream()) {
                inputStream = input.getByteStream();
                jsonReader = Json.createReader(new InputStreamReader(inputStream));
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
                jsonReader = Json.createReader(new InputStreamReader(inputStream));
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
        } catch (IOException e) {
            throw XMLMarshalException.unmarshalException(e);
        } catch (SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public void parseRoot(JsonValue jsonValue) throws SAXException {
        if (namespaces != null) {
            Map<String, String> namespacePairs = namespaces.getPrefixesToNamespaces();
            Iterator<String> keys = namespacePairs.keySet().iterator();
            while (keys.hasNext()) {
                String nextKey = keys.next();
                contentHandler.startPrefixMapping(nextKey, namespacePairs.get(nextKey));
            }
        }

        if (jsonValue.getValueType() == ValueType.OBJECT) {
            contentHandler.startDocument();
            JsonObject jsonObject = (JsonObject) jsonValue;

            Set<Entry<String, JsonValue>> children = jsonObject.entrySet();
            if (children == null || children.size() == 0 && unmarshalClass == null) {
                return;
            }

            Iterator<Entry<String, JsonValue>> iter = children.iterator();

            if (includeRoot) {
                if (children.size() > 0) {
                    Entry<String, JsonValue> nextEntry = iter.next();
                    parsePair(nextEntry.getKey(), nextEntry.getValue());
                }

            } else {
        
                contentHandler.startElement(Constants.EMPTY_STRING, Constants.EMPTY_STRING, null, attributes.setValue(jsonValue, attributePrefix, namespaces, namespaceSeparator, namespaceAware));

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
            int size = jsonArray.size();

            List list = new ArrayList(size);
            for (int x = 0; x < size; x++) {
                parseRoot(jsonArray.get(x));
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

        }
    }

    private void parseValue(JsonValue jsonValue) throws SAXException {
        ValueType valueType = jsonValue.getValueType();
        if (valueType == ValueType.STRING) {
            String string = ((JsonString) jsonValue).getString();
            contentHandler.characters(string);
        } else if (valueType == ValueType.FALSE) {
            contentHandler.characters(FALSE);
        } else if (valueType == ValueType.TRUE) {
            contentHandler.characters(TRUE);
        } else if (valueType == ValueType.NULL) {
            // do nothing
        } else if (valueType == ValueType.NUMBER) {
            JsonNumber number = ((JsonNumber)jsonValue);
            contentHandler.characters(number.toString());    		    	   
        } else if (valueType == ValueType.OBJECT) {
            JsonObject childObject = (JsonObject) jsonValue;
            Iterator<Entry<String, JsonValue>> iter = childObject.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, JsonValue> nextEntry = iter.next();
                parsePair(nextEntry.getKey(), nextEntry.getValue());
            }
        } else if(valueType == ValueType.ARRAY) {
            JsonArray childArray = (JsonArray)jsonValue;
            int size = childArray.size();
         	    
            List list = new ArrayList(size);            
            for(int x=0; x<size; x++) {
                JsonValue nextArrayValue = childArray.get(x);
                parseValue(nextArrayValue);
            }
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
            if (namespaceAware && namespaces != null) {
                if (parentLocalName.length() > 2) {
                    int nsIndex = parentLocalName.indexOf(namespaceSeparator, 1);
                    if (nsIndex > -1) {
                        String prefix = parentLocalName.substring(0, nsIndex);
                        uri = namespaces.resolveNamespacePrefix(prefix);
                    }
                    if (uri == null) {
                        uri = namespaces.getDefaultNamespaceURI();
                    } else {
                        parentLocalName = parentLocalName.substring(nsIndex + 1);
                    }
                } else {
                    uri = namespaces.getDefaultNamespaceURI();
                }
            }

            boolean isTextValue = isTextValue(parentLocalName);
            int arraySize = jsonArray.size();
            if (arraySize == 0) {
                if (contentHandler instanceof UnmarshalRecord) {
                    UnmarshalRecord ur = (UnmarshalRecord) contentHandler;
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
            if (contentHandler instanceof UnmarshalRecord) {
                UnmarshalRecord unmarshalRecord = (UnmarshalRecord) contentHandler;
                if (unmarshalRecord.getUnmarshaller().isWrapperAsCollectionName()) {
                    XPathNode unmarshalRecordXPathNode = unmarshalRecord.getXPathNode();
                    if (null != unmarshalRecordXPathNode) {
                        XPathFragment currentFragment = new XPathFragment();
                        currentFragment.setLocalName(parentLocalName);
                        currentFragment.setNamespaceURI(uri);
                        currentFragment.setNamespaceAware(namespaceAware);
                        XPathNode groupingXPathNode = unmarshalRecordXPathNode.getNonAttributeChildrenMap().get(currentFragment);
                        if (groupingXPathNode != null) {
                            if (groupingXPathNode.getUnmarshalNodeValue() instanceof CollectionGroupingElementNodeValue) {
                                groupingXPathFragment = groupingXPathNode.getXPathFragment();
                                contentHandler.startElement(uri, parentLocalName, parentLocalName, new AttributesImpl());
                                XPathNode itemXPathNode = groupingXPathNode.getNonAttributeChildren().get(0);
                                itemXPathFragment = itemXPathNode.getXPathFragment();
                            } else if (groupingXPathNode.getUnmarshalNodeValue() == null) {
                                XPathNode itemXPathNode = groupingXPathNode.getNonAttributeChildren().get(0);
                                if (itemXPathNode != null) {
                                    if (((MappingNodeValue) itemXPathNode.getUnmarshalNodeValue()).isContainerValue()) {
                                        groupingXPathFragment = groupingXPathNode.getXPathFragment();
                                        contentHandler.startElement(uri, parentLocalName, parentLocalName, new AttributesImpl());
                                        itemXPathFragment = itemXPathNode.getXPathFragment();
                                    }
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < arraySize; i++) {
                    JsonValue nextArrayValue = jsonArray.get(i);
                    if (nextArrayValue.getValueType() == ValueType.NULL) {
                        ((UnmarshalRecord) contentHandler).setNil(true);
                    }

                    if (!isTextValue) {
                        if (null != itemXPathFragment) {
                            contentHandler.startElement(itemXPathFragment.getNamespaceURI(), itemXPathFragment.getLocalName(), itemXPathFragment.getLocalName(), attributes.setValue(nextArrayValue, attributePrefix,namespaces, namespaceSeparator,namespaceAware));
                        } else {
                            contentHandler.startElement(uri, parentLocalName,parentLocalName, attributes.setValue(nextArrayValue, attributePrefix,namespaces, namespaceSeparator,namespaceAware));
                        }

                    }
                    parseValue(nextArrayValue);
                    if (!isTextValue) {
                        if (null != itemXPathFragment) {
                            contentHandler.endElement(itemXPathFragment.getNamespaceURI(),itemXPathFragment.getLocalName(),itemXPathFragment.getLocalName());
                        } else {
                            contentHandler.endElement(uri, parentLocalName,parentLocalName);
                        }
                    }
                }
            }
            if (null != groupingXPathFragment) {
                contentHandler.endElement(uri,groupingXPathFragment.getLocalName(),groupingXPathFragment.getLocalName());
            }
            endCollection();
        } else {
            String qualifiedName = name;
            if (attributePrefix != null && qualifiedName.startsWith(attributePrefix)) {
                return;
            }
            String localName = qualifiedName;
            String uri = Constants.EMPTY_STRING;
            if (namespaceAware && namespaces != null) {
                if (localName.length() > 2) {
                    int nsIndex = localName.indexOf(namespaceSeparator, 1);
                    String prefix = Constants.EMPTY_STRING;
                    if (nsIndex > -1) {
                        prefix = localName.substring(0, nsIndex);
                    }
                    uri = namespaces.resolveNamespacePrefix(prefix);
                    if (uri == null) {
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
                // if its not namespaceAware don't report the "type" child as it
                // is will be read by the xsi:type lookup
                if (!namespaceAware && localName.equals(Constants.SCHEMA_TYPE_ATTRIBUTE)) {
                    return;
                }
                if (textWrapper != null && textWrapper.equals(localName)) {
                    parseValue(jsonValue);
                    return;
                }
            } else if (contentHandler instanceof UnmarshalRecord && ((UnmarshalRecord) contentHandler).getXPathNode() != null) {
                if (!namespaceAware && localName.equals(Constants.SCHEMA_TYPE_ATTRIBUTE) && !((UnmarshalRecord) contentHandler).getXPathNode().hasTypeChild()) {
                    return;
                }
                boolean isTextValue = isTextValue(localName);
                if (isTextValue) {
                    parseValue(jsonValue);
                    return;
                }
            }
            if (jsonValue != null && jsonValue.getValueType() == valueType.NULL) {
                contentHandler.setNil(true);
            }

            contentHandler.startElement(uri, localName, localName, attributes.setValue(jsonValue, attributePrefix, namespaces,namespaceSeparator, namespaceAware));
            parseValue(jsonValue);
            contentHandler.endElement(uri, localName, localName);

        }

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

    public boolean isInCollection() {
        return isInCollection;
    }

    private boolean isTextValue(String localName) {
        XPathNode currentNode = ((UnmarshalRecord) contentHandler).getXPathNode();
        if (currentNode == null) {
            return textWrapper != null && textWrapper.equals(localName);
        }

        return ((currentNode.getNonAttributeChildrenMap() == null || currentNode.getNonAttributeChildrenMap().size() == 0 || (currentNode.getNonAttributeChildrenMap().size() == 1 && currentNode.getTextNode() != null))&& textWrapper != null && textWrapper.equals(localName));
    }

    @Override
    public Object convertValueBasedOnSchemaType(Field xmlField, Object value, ConversionManager conversionManager, AbstractUnmarshalRecord record) {
        if (xmlField.getSchemaType() != null) {
            if (Constants.QNAME_QNAME.equals(xmlField.getSchemaType())) {
                String stringValue = (String) value;
                int indexOpen = stringValue.indexOf('{');
                int indexClose = stringValue.indexOf('}');
                String uri = null;
                String localName = null;
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
     * 
     * @return
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

        private void addSimpleAttribute(List attributes, String uri, String attributeLocalName, JsonValue childValue) {
            if (childValue.getValueType() == ValueType.STRING) {
                String stringValue = ((JsonString) childValue).getString();
                attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, stringValue));
            } else if (childValue.getValueType() == ValueType.NUMBER) {
                attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, ((JsonNumber) childValue).toString()));
            } else if (childValue.getValueType() == ValueType.NULL) {
                attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, Constants.EMPTY_STRING));
            } else if (childValue.getValueType() == ValueType.FALSE) {
                attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, FALSE));
            } else if (childValue.getValueType() == ValueType.TRUE) {
                attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, TRUE));
            }
        }

        public int getIndex(String uri, String localName) {
            if (null == localName) {
                return -1;
            }
            int index = 0;
            for (Attribute attribute : attributes()) {
                if (namespaceAware) {
                    QName testQName = new QName(uri, localName);
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

                if (value.getValueType() == ValueType.NULL) {
                    return NO_ATTRIBUTES;
                }

                if (value.getValueType() == ValueType.OBJECT) {
                    JsonObject jsonObject = (JsonObject) value;
                    ArrayList<Attribute> attributesList = new ArrayList<Attribute>(jsonObject.values().size());

                    Iterator<Entry<String, JsonValue>> iter = jsonObject.entrySet().iterator();
                    while (iter.hasNext()) {
                        Entry<String, JsonValue> nextEntry = iter.next();
                        JsonValue nextValue = nextEntry.getValue();
                        String attributeLocalName = nextEntry.getKey();                       

                        if (attributePrefix != null) {
                            if (attributeLocalName.startsWith(attributePrefix)) {
                                attributeLocalName = attributeLocalName.substring(attributePrefix.length());
                            } else {
                                break;
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

                        if (nextValue.getValueType() == ValueType.ARRAY) {
                            JsonArray jsonArray = (JsonArray) nextValue;
                            if (jsonArray.size() == 0) {
                                attributesList.add(new Attribute(uri, attributeLocalName, attributeLocalName, ""));
                            }
                            for (int y = 0; y < jsonArray.size(); y++) {
                                JsonValue nextChildValue = jsonArray.get(y);
                                addSimpleAttribute(attributesList, uri, attributeLocalName, nextChildValue);
                            }
                        } else {
                            addSimpleAttribute(attributesList, uri, attributeLocalName, nextValue);
                        }
                    }

                    attributes = attributesList.toArray(new Attribute[attributesList.size()]);
                } else {
                    attributes = NO_ATTRIBUTES;
                }
            }
            return attributes;
        }

    }

}