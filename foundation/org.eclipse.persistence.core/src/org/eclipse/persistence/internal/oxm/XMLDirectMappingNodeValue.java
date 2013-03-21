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

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.DirectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Direct Mapping is handled when used
 * with the TreeObjectBuilder.</p>
 */
public class XMLDirectMappingNodeValue extends MappingNodeValue implements NullCapableValue {
    private DirectMapping xmlDirectMapping;

    public XMLDirectMappingNodeValue(DirectMapping xmlDirectMapping) {
        this.xmlDirectMapping = xmlDirectMapping;
    }

    public void setXPathNode(XPathNode xPathNode) {
        super.setXPathNode(xPathNode);
        xmlDirectMapping.getNullPolicy().xPathNode(xPathNode, this);
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return xPathFragment.hasAttribute || xPathFragment.nameIsText;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlDirectMapping.isReadOnly()) {
            return false;
        }
        Object objectValue = marshalContext.getAttributeValue(object, xmlDirectMapping);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        Object fieldValue = xmlDirectMapping.getFieldValue(objectValue, session, marshalRecord);
        
        // Check for a null value 
        if (null == fieldValue) {
            // Perform marshal operations based on the null policy
            return xmlDirectMapping.getNullPolicy().directMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
        } else {            
            QName schemaType = ((Field) xmlDirectMapping.getField()).getSchemaTypeForValue(fieldValue, session);
            XPathFragment groupingFragment = null;    
            boolean isQName = false;
            if(Constants.QNAME_QNAME.equals(schemaType)) {
                //if marshalling a QName, handle grouping elements here in case namespace adjustments need 
                //to happen
                groupingFragment = openGroupingElementsForQName((QName)fieldValue, marshalRecord);
                isQName = true;
            } 
            if(groupingFragment == null) {
                groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            }
            if (xPathFragment.hasAttribute) {
            	marshalRecord.attribute(xPathFragment, namespaceResolver, fieldValue, schemaType);
                marshalRecord.closeStartGroupingElements(groupingFragment);
            } else {
                marshalRecord.closeStartGroupingElements(groupingFragment);                             
                marshalRecord.characters(schemaType, fieldValue, null, xmlDirectMapping.isCDATA());                
            }
            if(isQName) {
                //check to see if the last grouping fragment was swapped
                XPathFragment fragment = getLastGroupingFragment();
                if(fragment != groupingFragment) {
                    marshalRecord.endElement(groupingFragment, namespaceResolver);
                    return false;
                }
            }
            return true;
        }
    }

    private XPathFragment getLastGroupingFragment() {
        XPathFragment fragment = ((Field)this.getMapping().getField()).getXPathFragment();
        if(fragment.hasAttribute || fragment.nameIsText) {
            return null;
        }
        while(fragment.getNextFragment() != null) {
            if(fragment.getNextFragment().nameIsText || fragment.getNextFragment().hasAttribute) {
                return fragment;
            }
            fragment = fragment.getNextFragment();
        }
        return fragment;
    }
    private XPathFragment openGroupingElementsForQName(QName fieldValue, MarshalRecord marshalRecord) {
        XPathFragment xPathFragment = null;
        ArrayList<XPathNode> groupingElements = marshalRecord.getGroupingElements();
        NamespaceResolver namespaceResolver = marshalRecord.getNamespaceResolver();
        if((fieldValue.getNamespaceURI() == null || fieldValue.getNamespaceURI().equals(Constants.EMPTY_STRING)) && marshalRecord.getNamespaceResolver().getDefaultNamespaceURI() != null) {
            //In this case, the last grouping element may need to have a new prefix generated. 
            for (int x = 0, groupingElementsSize = groupingElements.size(); x < groupingElementsSize; x++) {
                XPathNode xPathNode = groupingElements.get(x);
                xPathFragment = xPathNode.getXPathFragment();
                if(x == (groupingElements.size() - 1) && namespaceResolver.getDefaultNamespaceURI().equals(xPathFragment.getNamespaceURI()) && xPathFragment.getPrefix() == null) {
                    String prefix = namespaceResolver.generatePrefix();
                    String xPath = prefix +  Constants.COLON + xPathFragment.getShortName(); 
                    XPathFragment newFragment = new XPathFragment(xPath);
                    newFragment.setNamespaceURI(namespaceResolver.getDefaultNamespaceURI());
                    marshalRecord.openStartElement(newFragment, namespaceResolver);
                    marshalRecord.namespaceDeclaration(prefix,  namespaceResolver.getDefaultNamespaceURI());
                    marshalRecord.predicateAttribute(xPathFragment, namespaceResolver);
                    xPathFragment = newFragment;
                } else {
                    marshalRecord.openStartElement(xPathFragment, namespaceResolver);
                    marshalRecord.predicateAttribute(xPathFragment, namespaceResolver);
                    marshalRecord.closeStartElement();
                }
            }
            marshalRecord.setGroupingElement(null);
            
        }
        return xPathFragment;
        
        
    }

    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        unmarshalRecord.removeNullCapableValue(this);
        Field xmlField = (Field) xmlDirectMapping.getField();
        CoreAbstractSession session = unmarshalRecord.getSession();        
        Object realValue = unmarshalRecord.getXMLReader().convertValueBasedOnSchemaType(xmlField, value, (XMLConversionManager) session.getDatasourcePlatform().getConversionManager(), unmarshalRecord);

        // Perform operations on the object based on the null policy
        Object convertedValue = xmlDirectMapping.getAttributeValue(realValue, session, unmarshalRecord);
        xmlDirectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), convertedValue);
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        if(unmarshalRecord.isNil() && xmlDirectMapping.getNullPolicy().isNullRepresentedByXsiNil()){
            unmarshalRecord.resetStringBuffer();
            return;
        }        
        
        unmarshalRecord.removeNullCapableValue(this);
        Field xmlField = (Field) xmlDirectMapping.getField();
        if (!xmlField.getLastXPathFragment().nameIsText) {
            return;
        }
        Object value;
        CharSequence unmarshalRecordCharacters = unmarshalRecord.getCharacters();
        if (unmarshalRecordCharacters.length() == 0) {
            value = xmlDirectMapping.getNullValue();
        } else {
            value = unmarshalRecordCharacters.toString();
        }
        unmarshalRecord.resetStringBuffer();
        CoreAbstractSession session = unmarshalRecord.getSession();
        XMLConversionManager xmlConversionManager = (XMLConversionManager) session.getDatasourcePlatform().getConversionManager();
        QName typeQName = unmarshalRecord.getTypeQName(); 
        if (typeQName != null) {
            Class typeClass = xmlField.getJavaClass(typeQName);
            value = xmlConversionManager.convertObject(value, typeClass, typeQName);
        } else {
            value = unmarshalRecord.getXMLReader().convertValueBasedOnSchemaType(xmlField, value, xmlConversionManager, unmarshalRecord);
        }

        Object convertedValue = xmlDirectMapping.getAttributeValue(value, session, unmarshalRecord);
        unmarshalRecord.setAttributeValue(convertedValue, xmlDirectMapping);
    }

    public void setNullValue(Object object, CoreSession session) {
        Object value = xmlDirectMapping.getObjectValue(null, session);
        xmlDirectMapping.setAttributeValueInObject(object, value);
    }

    public boolean isNullCapableValue() {
        if(xmlDirectMapping.getAttributeAccessor().isInstanceVariableAttributeAccessor() && !xmlDirectMapping.hasConverter() && xmlDirectMapping.getNullValue() == null) {
             return false;
        }
        return xmlDirectMapping.getNullPolicy().getIsSetPerformedForAbsentNode();
    }

    public DirectMapping getMapping() {
        return xmlDirectMapping;
    }

    public boolean isWhitespaceAware() {
        return !xmlDirectMapping.getNullPolicy().isNullRepresentedByEmptyNode();
    }

}
