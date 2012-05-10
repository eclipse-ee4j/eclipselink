/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;
import java.lang.reflect.Modifier;
import java.util.Stack;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.SDOXMLDocument;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.StrBuffer;
import org.eclipse.persistence.internal.oxm.TreeObjectBuilder;
import org.eclipse.persistence.internal.oxm.record.namespaces.UnmarshalNamespaceResolver;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * <p><b>Purpose</b>: Called during XMLHelper load methods when there is unknown content in an XML document.  This
 * means when there is no corresponding SDO Type found.
 *
 * @see commonj.sdo.XMLHelper
 */
public class SDOUnmappedContentHandler implements UnmappedContentHandler {
    private UnmarshalRecord parentRecord;
    private SDOXMLDocument xmlDocument;
    private QName currentSchemaType;
    private StrBuffer currentBuffer;
    private Stack currentDataObjects;
    private Stack currentProperties;
    private boolean rootProcessed;
    private boolean isInCharacterBlock;
    private HelperContext aHelperContext;
    private int lastEvent = -1;
    private static final int START_ELEMENT = 0;
    private static final int END_ELEMENT = 1;
    private UnmarshalNamespaceResolver unmarshalNamespaceResolver;
    private static final String NO_NAMESPACE = null;
    private int depth = 0;
    
    public SDOUnmappedContentHandler() {
        isInCharacterBlock = false;
        currentDataObjects = new Stack();
        currentProperties = new Stack();
        currentBuffer = new StrBuffer();
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        unmarshalNamespaceResolver.push(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        unmarshalNamespaceResolver.pop(prefix);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (isInCharacterBlock) {
            if (!currentBuffer.toString().trim().equals("")) {
                DataObject dObj = (DataObject) currentDataObjects.peek();
                dObj.getSequence().addText(currentBuffer.toString());
            }
            currentBuffer.reset();
        }
        if (!rootProcessed) {
            processRoot(namespaceURI, localName, qName, atts);
        } else {
            processNonRoot(namespaceURI, localName, qName, atts);
        }
        lastEvent = START_ELEMENT;
    }

    /**
     * Return a QName representing the value of the xsi:type attribute or null if one is not present
     */
    private QName getTypeAttributeQName(Attributes atts) {
        int attributeSize = atts.getLength();
        for (int i = 0; i < attributeSize; i++) {
            String stringValue = atts.getValue(i);
            String uri = atts.getURI(i);
            String attrName = atts.getLocalName(i);
            if (XMLConstants.SCHEMA_INSTANCE_URL.equals(uri) && XMLConstants.SCHEMA_TYPE_ATTRIBUTE.equals(attrName)) {
                int colonIndex = stringValue.indexOf(':');
                String localPrefix = stringValue.substring(0, colonIndex);
                String localURI = unmarshalNamespaceResolver.getNamespaceURI(localPrefix);
                if (localURI != null) {
                    String localName = stringValue.substring(colonIndex + 1, stringValue.length());
                    QName theQName = new QName(localURI, localName);
                    currentSchemaType = theQName;
                    return theQName;
                } else {
                    throw XMLMarshalException.namespaceNotFound(localPrefix);
                }
            }
        }

        return null;
    }

    private void processAttributes(Attributes atts, DataObject dataObject, boolean isRoot) {
        int attributeSize = atts.getLength();
        for (int i = 0; i < attributeSize; i++) {
            String stringValue = atts.getValue(i);
            String uri = atts.getURI(i);
            String attrName = atts.getLocalName(i);

            if ((atts.getQName(i) != null) && atts.getQName(i).startsWith(XMLConstants.XMLNS + ":")) {
                //namespace declaration - do nothing because namespaces were already handled                             
            } else if (isRoot && XMLConstants.SCHEMA_LOCATION.equals(attrName)) {
                getXmlDocument().setSchemaLocation(stringValue);
            } else if (isRoot && XMLConstants.NO_NS_SCHEMA_LOCATION.equals(attrName)) {
                getXmlDocument().setNoNamespaceSchemaLocation(stringValue);
            } else if (XMLConstants.SCHEMA_INSTANCE_URL.equals(uri) && XMLConstants.SCHEMA_TYPE_ATTRIBUTE.equals(attrName)) {
                //do nothing
            } else if (SDOConstants.CHANGESUMMARY_REF.equals(attrName) && SDOConstants.SDO_URL.equals(uri)) {
                ((SDODataObject)dataObject)._setSdoRef(stringValue);
            } else {
                HelperContext aHelperContext = ((SDOType)dataObject.getType()).getHelperContext();
                Property prop = aHelperContext.getXSDHelper().getGlobalProperty(uri, attrName, false);
                if (prop != null) {
                    Object convertedValue = ((SDODataHelper)aHelperContext.getDataHelper()).convertFromStringValue(stringValue, prop.getType());
                    dataObject.set(prop, convertedValue);
                } else {
                    Object convertedValue = ((SDODataHelper)aHelperContext.getDataHelper()).convertFromStringValue(stringValue, SDOConstants.SDO_STRING);

                    //can't use create on demand property via a set by string name operation because that would create an element
                    prop = defineNewSDOProperty(uri, attrName, false, SDOConstants.SDO_STRING);
                    dataObject.set(prop, convertedValue);
                }
            }
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if ((currentDataObjects.size() == 0) && (currentProperties.size() == 0)) {
            return;
        }

        if ((currentDataObjects.size() == 1) && (currentProperties.size() == 0)) {
            parentRecord.getUnmarshaller().getUnmarshalListener().afterUnmarshal(currentDataObjects.peek(), null);
            currentDataObjects.pop();
            depth--;
            return;
        } else {
            setElementPropertyValue();
            return;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!isInCharacterBlock) {
            currentBuffer.reset();
            isInCharacterBlock = true;
        }
        currentBuffer.append(ch, start, length);
    }

    private void setElementPropertyValue() {
        Property currentProperty = (Property)currentProperties.pop();
        boolean simple = true;
        
        if (lastEvent == END_ELEMENT) {
            simple = false;    
        } else {
            //last event was start element so is usually a simple thing
            //if depth is greater than the stack size it means dataType == true
            if (depth > currentDataObjects.size()) {
                simple = true;                
            } else {
                //if depth and stack size are the same it means complex or simple.
                DataObject nextDO = (DataObject)currentDataObjects.peek();

                if (nextDO.getInstanceProperties().size() > 0) {
                    simple = false;
                    if (!currentBuffer.toString().trim().equals("")) {
                        DataObject dObj = (DataObject) currentDataObjects.peek();
                        dObj.getSequence().addText(currentBuffer.toString());
                    }
                } else {
                    currentDataObjects.pop();
                }
            }
            depth--;         
        }

        lastEvent = END_ELEMENT;
        if (simple && (!isInCharacterBlock || (currentBuffer.length() == 0))) {
            return;
        }

        DataObject currentDataObject = (DataObject)currentDataObjects.peek();

        if (currentProperty != null) {
            Object value = null;
            if (simple) {
                value = currentBuffer.toString();
                ((SDOProperty)currentProperty).setType(SDOConstants.SDO_STRING);
                ((SDOProperty)currentProperty).setContainment(false);
            } else {
                value = currentDataObject;
                currentDataObjects.pop();
                depth--;
                if (currentDataObjects.isEmpty()) {
                    currentDataObject = null;
                } else {
                    currentDataObject = (DataObject)currentDataObjects.peek();
                }
            }
            HelperContext aHelperContext = ((SDOType)currentDataObject.getType()).getHelperContext();
            if (currentSchemaType != null) {
                Type sdoType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getSDOTypeFromXSDType(currentSchemaType);

                if (sdoType != null) {
                    ((SDOProperty)currentProperty).setType(sdoType);
                }

                if ((currentProperty.getType() != null) && simple) {
                    value = ((SDODataHelper)aHelperContext.getDataHelper()).convertFromStringValue((String)value, currentProperty.getType(), currentSchemaType);
                }
                currentSchemaType = null;
            } else if ((currentProperty.getType() != null) && ((SDOType)currentProperty.getType()).isDataType()) {
                value = ((SDODataHelper)aHelperContext.getDataHelper()).convertFromStringValue((String)value, currentProperty.getType());
            }

            if (currentDataObject != null) {
                if (!simple) {
                    parentRecord.getUnmarshaller().getUnmarshalListener().afterUnmarshal(value, currentDataObject);
                }

                //newly defined Properties will be many=true
                if (currentProperty.isMany()) {
                    currentDataObject.getList(currentProperty).add(value);
                } else {
                    currentDataObject.set(currentProperty, value);
                }
            }
            currentBuffer.reset();
        }
    }

    private void processNonRoot(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        DataObject owner = (DataObject)currentDataObjects.peek();

        if ((owner != null) && !owner.getType().isOpen()) {
            return;
        }
        Property globalProperty = aHelperContext.getXSDHelper().getGlobalProperty(namespaceURI, localName, true);
        if (globalProperty != null) {
            currentProperties.push(globalProperty);
            SDOType theType = ((SDOType)globalProperty.getType());

            if (((SDOType)globalProperty.getType()).isDataType()) {                
                depth++;                
            } else {
                XMLDescriptor xmlDescriptor = theType.getXmlDescriptor();
                giveToOXToProcess(namespaceURI, localName, qName, atts, xmlDescriptor);
            }
        } else {
            String typeName = localName;
            String typeUri = namespaceURI;
            QName typeAttribute = getTypeAttributeQName(atts);
            Type newType = null;
            if (typeAttribute != null) {
                typeName = typeAttribute.getLocalPart();
                typeUri = typeAttribute.getNamespaceURI();
                newType = aHelperContext.getTypeHelper().getType(typeUri, typeName);
            }
            if (newType == null) {
                newType = aHelperContext.getTypeHelper().getType(SDOConstants.ORACLE_SDO_URL, "OpenSequencedType");
                Type dataObjectType = aHelperContext.getTypeHelper().getType(SDOConstants.SDO_URL, "DataObject");
                Property property = defineNewSDOProperty(namespaceURI, localName, true, dataObjectType);
                DataObject newDO = aHelperContext.getDataFactory().create(newType);
                processAttributes(atts, newDO, false);
                currentDataObjects.push(newDO);
                depth++;
                parentRecord.setCurrentObject(newDO);

                currentProperties.push(property);
            } else {
                //this means that type is a known type which will have a descriptor                      
                XMLDescriptor xmlDescriptor = ((SDOType)newType).getXmlDescriptor();
                giveToOXToProcess(namespaceURI, localName, qName, atts, xmlDescriptor);
                Property property = defineNewSDOProperty(namespaceURI, localName, true, newType);
                currentProperties.push(property);
                return;
            }            
        }
    }

    private void processRoot(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        DataFactory dataFactory = aHelperContext.getDataFactory();
        SDOTypeHelper typeHelper = (SDOTypeHelper)aHelperContext.getTypeHelper();

        getXmlDocument().setRootElementName(localName);
        getXmlDocument().setRootElementURI(namespaceURI);
        Property rootElementProperty = aHelperContext.getXSDHelper().getGlobalProperty(namespaceURI, localName, true);
        Type rootObjectType = null;

        if (rootElementProperty != null) {
            rootObjectType = rootElementProperty.getType();
        } else {
            QName typeQName = getTypeAttributeQName(atts);
            if (typeQName != null) {
                String typeName = typeQName.getLocalPart();
                String typeUri = null;
                String prefix = typeQName.getPrefix();
                if (prefix == null) {
                    typeUri = null;
                } else {
                    typeUri = unmarshalNamespaceResolver.getNamespaceURI(prefix);
                }
            rootObjectType = typeHelper.getType(typeUri, typeName);
            }            
        }

        DataObject rootObject = null;
        if (rootObjectType != null) {
            giveToOXToProcess(namespaceURI, localName, qName, atts, ((SDOType)rootObjectType).getXmlDescriptor());
            return;
        } else {
            Type rootType = aHelperContext.getTypeHelper().getType(SDOConstants.ORACLE_SDO_URL, "OpenSequencedType");
            rootObject = dataFactory.create(rootType);
        }
        currentDataObjects.push(rootObject);
        depth++;
        processAttributes(atts, rootObject, true);

        getXmlDocument().setRootObject(rootObject);
        rootProcessed = true;
        parentRecord.setCurrentObject(getXmlDocument());
    }

    private SDOProperty defineNewSDOProperty(String uri, String localName, boolean isElement, Type type) {        
        DataObject currentDataObject = (DataObject)currentDataObjects.peek();

        if ((uri != null) && uri.equals("")) {
            uri = NO_NAMESPACE;
        }

        //Check if the current DataObject has a property by this name and if so return it, 
        //otherwise create a new property
        SDOProperty lookedUp = (SDOProperty)currentDataObject.getInstanceProperty(localName);
        if ((lookedUp != null) && equalStrings(lookedUp.getUri(), uri)) {
            if (isElement && aHelperContext.getXSDHelper().isElement(lookedUp)) {
                return lookedUp;
            }
            if (!isElement && aHelperContext.getXSDHelper().isAttribute(lookedUp)) {
                return lookedUp;
            }
        }

        //This Property will not be registered with XSDHelper or TypeHelper
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(localName);
        property.setMany(isElement);
        property.setContainment(!((SDOType)type).isDataType());
        property.setType(type);
        property.setUri(uri);
        property.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, isElement);

        return property;
    }

    private void giveToOXToProcess(String namespaceURI, String localName, String qName, Attributes atts, XMLDescriptor xmlDescriptor) throws SAXException {
        AbstractSession session = ((SDOXMLHelper)aHelperContext.getXMLHelper()).getXmlContext().getReadSession(xmlDescriptor);

        //taken from SAXUnmarshallerHandler start Element
        UnmarshalRecord unmarshalRecord;
        if (xmlDescriptor.hasInheritance()) {
            unmarshalRecord = new UnmarshalRecord((TreeObjectBuilder)xmlDescriptor.getObjectBuilder());
            unmarshalRecord.setUnmarshalNamespaceResolver(unmarshalNamespaceResolver);
            unmarshalRecord.setAttributes(atts);
            if(parentRecord != null){
                unmarshalRecord.setXMLReader(parentRecord.getXMLReader());
            }
            Class classValue = xmlDescriptor.getInheritancePolicy().classFromRow(unmarshalRecord, session);
            if (classValue == null) {
                // no xsi:type attribute - look for type indicator on the default root element
                QName leafElementType = xmlDescriptor.getDefaultRootElementType();

                // if we have a user-set type, try to get the class from the inheritance policy
                if (leafElementType != null) {
                    Object indicator = xmlDescriptor.getInheritancePolicy().getClassIndicatorMapping().get(leafElementType);
                    if(indicator != null) {
                        classValue = (Class)indicator;
                    }
                }
            }
            if (classValue != null) {
                xmlDescriptor = (XMLDescriptor)session.getDescriptor(classValue);
            } else {
                // since there is no xsi:type attribute, we'll use the descriptor
                // that was retrieved based on the rootQName -  we need to make 
                // sure it is non-abstract
                if (Modifier.isAbstract(xmlDescriptor.getJavaClass().getModifiers())) {
                    // need to throw an exception here
                    throw DescriptorException.missingClassIndicatorField(unmarshalRecord, xmlDescriptor.getInheritancePolicy().getDescriptor());
                }
            }
        }

        unmarshalRecord = (UnmarshalRecord)xmlDescriptor.getObjectBuilder().createRecord(session);
        unmarshalRecord.setParentRecord(parentRecord);
        unmarshalRecord.setUnmarshaller(parentRecord.getUnmarshaller());
        unmarshalRecord.setXMLReader(parentRecord.getXMLReader());
        unmarshalRecord.startDocument();
        unmarshalRecord.setUnmarshalNamespaceResolver(unmarshalNamespaceResolver);
        unmarshalRecord.startElement(namespaceURI, localName, qName, atts);

        parentRecord.getXMLReader().setContentHandler(unmarshalRecord);

        try {
            unmarshalRecord.getXMLReader().setProperty("http://xml.org/sax/properties/lexical-handler", unmarshalRecord);
        } catch (SAXNotRecognizedException ex) {
        } catch (SAXNotSupportedException ex) {
            //if lexical handling is not supported by this parser, just ignore. 
        }

        currentDataObjects.push(unmarshalRecord.getCurrentObject());
        depth++;
    }

    private SDOXMLDocument getXmlDocument() {
        if (xmlDocument == null) {
            xmlDocument = new SDOXMLDocument();
        }
        return xmlDocument;
    }

    public void setUnmarshalRecord(UnmarshalRecord unmarshalRecord) {
        parentRecord = unmarshalRecord;
        aHelperContext = (HelperContext)unmarshalRecord.getUnmarshaller().getProperty(SDOConstants.SDO_HELPER_CONTEXT);

        if (parentRecord.getParentRecord() == null) {
            rootProcessed = false;
        } else {
            rootProcessed = true;
            if (parentRecord.getParentRecord().getCurrentObject() instanceof DataObject) {
                currentDataObjects.push(parentRecord.getParentRecord().getCurrentObject());
                depth++;
            }
        }
        unmarshalNamespaceResolver = parentRecord.getUnmarshalNamespaceResolver();
    }

    private boolean equalStrings(String string1, String string2) {
        if (string1 == null) {
            if (string2 == null) {
                return true;
            } else {
                return false;
            }
        } else {
            if (string2 == null) {
                return false;
            } else {
                return string1.equals(string2);
            }
        }
    }
}
