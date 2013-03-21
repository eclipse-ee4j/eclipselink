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
package org.eclipse.persistence.internal.oxm;

import java.util.StringTokenizer;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.DirectCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.xml.sax.Attributes;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Composite Direct Collection Mapping is
 * handled when used with the TreeObjectBuilder.</p>
 */

public class XMLCompositeDirectCollectionMappingNodeValue extends MappingNodeValue implements ContainerValue {
    private static final String SPACE = " ";
    private DirectCollectionMapping xmlCompositeDirectCollectionMapping;
    private int index = -1;

    public XMLCompositeDirectCollectionMappingNodeValue(DirectCollectionMapping xmlCompositeDirectCollectionMapping) {
        super();
        this.xmlCompositeDirectCollectionMapping = xmlCompositeDirectCollectionMapping;
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
         XPathFragment nextFragment = xPathFragment.getNextFragment();
         if (nextFragment == null || xmlCompositeDirectCollectionMapping.usesSingleNode()) {
             return xPathFragment.isAttribute() || xPathFragment.nameIsText();
         } else {
             return  (nextFragment != null) && (nextFragment.nameIsText() || nextFragment.isAttribute());
         }
    }

    /**
     * Override the method in XPathNode such that the marshaller can be set on the
     * marshalRecord - this is required for XMLConverter usage.
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlCompositeDirectCollectionMapping.isReadOnly()) {
            return false;
        }

        CoreContainerPolicy cp = getContainerPolicy();
        Object collection = xmlCompositeDirectCollectionMapping.getAttributeAccessor().getAttributeValueFromObject(object);
        if (null == collection) {
            AbstractNullPolicy wrapperNP = xmlCompositeDirectCollectionMapping.getWrapperNullPolicy();
            if (wrapperNP != null && wrapperNP.getMarshalNullRepresentation() == XMLNullRepresentationType.XSI_NIL) {
                marshalRecord.nilSimple(namespaceResolver);
                return true;
            } else {
                return false;
            }
        }

        Object iterator = cp.iteratorFor(collection);
        Field xmlField = (Field) xmlCompositeDirectCollectionMapping.getField();
        if (null != iterator && cp.hasNext(iterator)) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            marshalRecord.closeStartGroupingElements(groupingFragment);
        } else {
            if (xmlField.usesSingleNode() && !xmlCompositeDirectCollectionMapping.isDefaultEmptyContainer()) {
                XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
                marshalRecord.closeStartGroupingElements(groupingFragment);
            } else {
                return marshalRecord.emptyCollection(xPathFragment, namespaceResolver, xmlCompositeDirectCollectionMapping.getWrapperNullPolicy() != null);
            }
        }
        
        Object objectValue;
        if (xmlField.usesSingleNode()) {        	
            StringBuilder stringValueStringBuilder = new StringBuilder();
            String newValue;
            QName schemaType = null;
            if (xPathFragment != null && !xPathFragment.isAttribute() && !xPathFragment.nameIsText) {
                marshalRecord.openStartElement(xPathFragment, namespaceResolver);
            }

            while (cp.hasNext(iterator)) {
                objectValue = cp.next(iterator, session);
                objectValue = xmlCompositeDirectCollectionMapping.convertObjectValueToDataValue(objectValue, session, marshalRecord.getMarshaller());
                schemaType = xmlField.getSchemaTypeForValue(objectValue, session);

                newValue = marshalRecord.getValueToWrite(schemaType, objectValue, (XMLConversionManager) session.getDatasourcePlatform().getConversionManager());
                if (null != newValue) {
                    stringValueStringBuilder.append(newValue);
                    if (cp.hasNext(iterator)) {
                        stringValueStringBuilder.append(SPACE);
                    }
                }
            }

            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            if (xPathFragment != null && xPathFragment.isAttribute()) {
                marshalRecord.attribute(xPathFragment, namespaceResolver, stringValueStringBuilder.toString());
                marshalRecord.closeStartGroupingElements(groupingFragment);
            } else {
                marshalRecord.closeStartGroupingElements(groupingFragment);
                if (xmlCompositeDirectCollectionMapping.isCDATA()) {
                    marshalRecord.cdata(stringValueStringBuilder.toString());
                } else {
                    marshalRecord.characters(stringValueStringBuilder.toString());
                    if (xPathFragment != null && !xPathFragment.isAttribute() && !xPathFragment.nameIsText) {
                        marshalRecord.endElement(xPathFragment, namespaceResolver);
                    }
                }
            }
        } else {
            marshalRecord.startCollection();

            while (cp.hasNext(iterator)) {
                objectValue = cp.next(iterator, session);
                marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
            }
            marshalRecord.endCollection();
        }
        return true;
    }

    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        Object collection = unmarshalRecord.getContainerInstance(this);
        if (xmlCompositeDirectCollectionMapping.usesSingleNode()) {
            StringTokenizer stringTokenizer = new StringTokenizer(value);
            while (stringTokenizer.hasMoreTokens()) {
                addUnmarshalValue(unmarshalRecord, stringTokenizer.nextToken(), collection);
            }
        } else {
            addUnmarshalValue(unmarshalRecord, value, collection);
        }
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
    	Field xmlField = (Field) xmlCompositeDirectCollectionMapping.getField();
        XPathFragment lastXPathFragment = xmlField.getLastXPathFragment();
        if (lastXPathFragment.nameIsText()) {
            String type = atts.getValue(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_TYPE_ATTRIBUTE);
            if (null != type) {
                String namespaceURI = null;
                int colonIndex = type.indexOf(Constants.COLON);
                if (colonIndex > -1) {
                    String prefix = type.substring(0, colonIndex);
                    namespaceURI = unmarshalRecord.resolveNamespacePrefix(prefix);
                    type = type.substring(colonIndex + 1);
                }
                unmarshalRecord.setTypeQName(new QName(namespaceURI, type));
            }
        } else if (lastXPathFragment.isAttribute()) {
            if (!xmlField.usesSingleNode()) {
                String namespaceURI = lastXPathFragment.getNamespaceURI();
                if (namespaceURI == null) {
                    namespaceURI = Constants.EMPTY_STRING;
                }
                String value = atts.getValue(namespaceURI, lastXPathFragment.getLocalName());
                Object collection = unmarshalRecord.getContainerInstance(this);
                addUnmarshalValue(unmarshalRecord, value, collection);
            }
        }
        return true;
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
    	Field xmlField = (Field) xmlCompositeDirectCollectionMapping.getField();
        XPathFragment lastXPathFragment = xmlField.getLastXPathFragment();
        if (!lastXPathFragment.nameIsText()) {
            return;
        }

        String value = unmarshalRecord.getCharacters().toString();

        unmarshalRecord.resetStringBuffer();

        if (xmlField.usesSingleNode()) {
            StringTokenizer stringTokenizer = new StringTokenizer(value);
            Object collection = unmarshalRecord.getContainerInstance(this);
            while (stringTokenizer.hasMoreTokens()) {
                addUnmarshalValue(unmarshalRecord, stringTokenizer.nextToken(), collection);
            }
        } else {
            if(!unmarshalRecord.getXMLReader().isInCollection() && unmarshalRecord.isNil() ){
        		unmarshalRecord.setAttributeValueNull(this);	
            }else{
                Object collection = unmarshalRecord.getContainerInstance(this);
                addUnmarshalValue(unmarshalRecord, value, collection);
            }  
        }    
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Object collection) {
        String value = unmarshalRecord.getCharacters().toString();
        unmarshalRecord.resetStringBuffer();

        Field xmlField = (Field) xmlCompositeDirectCollectionMapping.getField();
        if (xmlField.usesSingleNode()) {
            StringTokenizer stringTokenizer = new StringTokenizer(value);
            while (stringTokenizer.hasMoreTokens()) {
                addUnmarshalValue(unmarshalRecord, stringTokenizer.nextToken(), collection);
            }
        } else {            
            if (xmlField.getLastXPathFragment().nameIsText()) {            	
            	if(!unmarshalRecord.getXMLReader().isInCollection() && unmarshalRecord.isNil() ){
            		unmarshalRecord.setAttributeValueNull(this);
            	} else{
                    addUnmarshalValue(unmarshalRecord, value, collection);
            	}  
            }
        }
    }

    private void addUnmarshalValue(UnmarshalRecord unmarshalRecord, Object value, Object collection) {
      
        if (unmarshalRecord.isNil() && unmarshalRecord.getXMLReader().isNullRepresentedByXsiNil(xmlCompositeDirectCollectionMapping.getNullPolicy())){
            value = null;           
        } else if (!isWhitespaceAware() && Constants.EMPTY_STRING.equals(value)) {
            value = null;
        }

        Field xmlField = (Field) xmlCompositeDirectCollectionMapping.getField();

        XMLConversionManager xmlConversionManager = (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager();
        if (unmarshalRecord.getTypeQName() != null) {
            Class typeClass = xmlField.getJavaClass(unmarshalRecord.getTypeQName());
            value = xmlConversionManager.convertObject(value, typeClass, unmarshalRecord.getTypeQName());
        } else {
            value = unmarshalRecord.getXMLReader().convertValueBasedOnSchemaType(xmlField, value, xmlConversionManager, unmarshalRecord);
        }

        value = xmlCompositeDirectCollectionMapping.convertDataValueToObjectValue(value, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());

        if (value != null && value.getClass() == CoreClassConstants.STRING) {
            if (xmlCompositeDirectCollectionMapping.isCollapsingStringValues()) {
                value = XMLConversionManager.getDefaultXMLManager().collapseStringValue((String)value);
            } else if (xmlCompositeDirectCollectionMapping.isNormalizingStringValues()) {
                value = XMLConversionManager.getDefaultXMLManager().normalizeStringValue((String)value);
            }
        }
        unmarshalRecord.addAttributeValue(this, value, collection);
    }

    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    public void setContainerInstance(Object object, Object containerInstance) {
        xmlCompositeDirectCollectionMapping.setAttributeValueInObject(object, containerInstance);
    }

    public CoreContainerPolicy getContainerPolicy() {
        return xmlCompositeDirectCollectionMapping.getContainerPolicy();
    }

    public boolean isContainerValue() {
        return true;
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        value = xmlCompositeDirectCollectionMapping.convertObjectValueToDataValue(value, session, marshalRecord.getMarshaller());

        if (null != value) {
        	Field xmlField = (Field) xmlCompositeDirectCollectionMapping.getField();
            QName schemaType = xmlField.getSchemaTypeForValue(value, session);
            boolean isElementOpen = false;
            if (Constants.QNAME_QNAME.equals(schemaType)) {
                QName fieldValue = (QName) value;
                if ((fieldValue.getNamespaceURI() == null || fieldValue.getNamespaceURI().equals("")) && marshalRecord.getNamespaceResolver().getDefaultNamespaceURI() != null) {
                    // In this case, an extra xmlns="" declaration is going to be added. This may
                    // require adjusting the namespace of the current fragment.
                    String defaultNamespaceURI = namespaceResolver.getDefaultNamespaceURI();
                    if (defaultNamespaceURI.equals(xPathFragment.getNamespaceURI()) && xPathFragment.getPrefix() == null) {
                        String prefix = namespaceResolver.generatePrefix();
                        String xPath = prefix + Constants.COLON + xPathFragment.getShortName();
                        XPathFragment newFragment = new XPathFragment(xPath);
                        newFragment.setNamespaceURI(defaultNamespaceURI);
                        newFragment.setNextFragment(xPathFragment.getNextFragment());
                        marshalRecord.openStartElement(newFragment, namespaceResolver);
                        isElementOpen = true;
                        marshalRecord.namespaceDeclaration(prefix, defaultNamespaceURI);
                        marshalRecord.predicateAttribute(xPathFragment, namespaceResolver);
                        xPathFragment = newFragment;
                    }
                }
            }

            if (!isElementOpen) {
                 marshalRecord.openStartElement(xPathFragment, namespaceResolver);
            }

            XPathFragment nextFragment = xPathFragment.getNextFragment();
            if (nextFragment != null && nextFragment.isAttribute()) {
                marshalRecord.predicateAttribute(xPathFragment, namespaceResolver);
                marshalRecord.attribute(nextFragment, namespaceResolver, value,schemaType);
                marshalRecord.closeStartElement();
            } else {
                if (xmlField.isTypedTextField()) {
                    updateNamespaces(schemaType, marshalRecord, xmlField);
                }
                marshalRecord.closeStartElement();
                marshalRecord.predicateAttribute(xPathFragment, namespaceResolver);
                marshalRecord.characters(schemaType, value, null, xmlCompositeDirectCollectionMapping.isCDATA());
            }
            marshalRecord.endElement(xPathFragment, namespaceResolver);
            return true;
        } else {
            AbstractNullPolicy nullPolicy = xmlCompositeDirectCollectionMapping.getNullPolicy();
            if (nullPolicy.getMarshalNullRepresentation() != XMLNullRepresentationType.ABSENT_NODE) {
                marshalRecord.openStartElement(xPathFragment, namespaceResolver);
                XPathFragment nextFragment = xPathFragment.getNextFragment();
                nullPolicy.directMarshal(nextFragment, marshalRecord, object, session, namespaceResolver);
                marshalRecord.endElement(xPathFragment, namespaceResolver);
                return true;
            }
        }
        return false;
    }

    public DirectCollectionMapping getMapping() {
        return xmlCompositeDirectCollectionMapping;
    }

    public boolean isWhitespaceAware() {
        return !xmlCompositeDirectCollectionMapping.getNullPolicy().isNullRepresentedByEmptyNode();
    }

    public boolean getReuseContainer() {
        return getMapping().getReuseContainer();
    }

    /**
     *  INTERNAL:
     *  Used to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord
     */
    public void setIndex(int index){
        this.index = index;
    }

    /**
     * INTERNAL:
     * Set to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord
     * Set during TreeObjectBuilder initialization
     */
    public int getIndex(){
        return index;
    }

    /**
     * INTERNAL
     * Return true if an empty container should be set on the object if there
     * is no presence of the collection in the XML document.
     * @since EclipseLink 2.3.3
     */
    public boolean isDefaultEmptyContainer() {
        return getMapping().isDefaultEmptyContainer();
    }

    @Override
    public boolean isWrapperAllowedAsCollectionName() {
        return true;
    }

}