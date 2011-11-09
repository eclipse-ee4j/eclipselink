/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.sessions.Session;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Direct Mapping is handled when used
 * with the TreeObjectBuilder.</p>
 */
public class XMLDirectMappingNodeValue extends MappingNodeValue implements NullCapableValue {
    private XMLDirectMapping xmlDirectMapping;

    public XMLDirectMappingNodeValue(XMLDirectMapping xmlDirectMapping) {
        super();
        this.xmlDirectMapping = xmlDirectMapping;
    }

    public void setXPathNode(XPathNode xPathNode) {
        super.setXPathNode(xPathNode);
        xmlDirectMapping.getNullPolicy().xPathNode(xPathNode, this);
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return xPathFragment.isAttribute() || xPathFragment.nameIsText();
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlDirectMapping.isReadOnly()) {
            return false;
        }
        Object objectValue = marshalContext.getAttributeValue(object, xmlDirectMapping);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        Object fieldValue = xmlDirectMapping.getFieldValue(objectValue, session, marshalRecord);
        XPathFragment textXPathFragment = null; 

        if(xPathFragment.nameIsText() && marshalRecord.getTextWrapperFragment() != null && xPathFragment.getXMLField().getXPathFragment() == xPathFragment){
        	textXPathFragment = marshalRecord.getTextWrapperFragment();   
        }
        
        // Check for a null value 
        if (null == fieldValue) {
            // Perform marshal operations based on the null policy
            return xmlDirectMapping.getNullPolicy().directMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
        } else {
            QName schemaType = getSchemaType((XMLField) xmlDirectMapping.getField(), fieldValue, session);
            XPathFragment groupingFragment = null;    
            boolean isQName = false;
            if(XMLConstants.QNAME_QNAME.equals(schemaType)) {
                //if marshalling a QName, handle grouping elements here in case namespace adjustments need 
                //to happen
                groupingFragment = openGroupingElementsForQName((QName)fieldValue, marshalRecord);
                isQName = true;
            } 
            if(groupingFragment == null) {
                groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            }
            if (xPathFragment.isAttribute()) {
            	marshalRecord.attribute(xPathFragment, namespaceResolver, fieldValue, schemaType);
                marshalRecord.closeStartGroupingElements(groupingFragment);
            } else {
                marshalRecord.closeStartGroupingElements(groupingFragment); 
                
                if(textXPathFragment !=null){
                	marshalRecord.openStartElement(textXPathFragment, namespaceResolver);
                }
                marshalRecord.characters(schemaType, fieldValue, null, xmlDirectMapping.isCDATA());
                if(textXPathFragment!=null){
                	marshalRecord.endElement(textXPathFragment, namespaceResolver);
                }
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
        XPathFragment fragment = ((XMLField)this.getMapping().getField()).getXPathFragment();
        if(fragment.isAttribute() || fragment.nameIsText()) {
            return null;
        }
        while(fragment.getNextFragment() != null) {
            if(fragment.getNextFragment().nameIsText() || fragment.getNextFragment().isAttribute()) {
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
        if((fieldValue.getNamespaceURI() == null || fieldValue.getNamespaceURI().equals("")) && marshalRecord.getNamespaceResolver().getDefaultNamespaceURI() != null) {
            //In this case, the last grouping element may need to have a new prefix generated. 
            for (int x = 0, groupingElementsSize = groupingElements.size(); x < groupingElementsSize; x++) {
                XPathNode xPathNode = groupingElements.get(x);
                xPathFragment = xPathNode.getXPathFragment();
                if(x == (groupingElements.size() - 1) && namespaceResolver.getDefaultNamespaceURI().equals(xPathFragment.getNamespaceURI()) && xPathFragment.getPrefix() == null) {
                    String prefix = namespaceResolver.generatePrefix();
                    String xPath = prefix + ":" + xPathFragment.getShortName(); 
                    XPathFragment newFragment = new XPathFragment(xPath);
                    newFragment.setNamespaceURI(namespaceResolver.getDefaultNamespaceURI());
                    marshalRecord.openStartElement(newFragment, namespaceResolver);
                    marshalRecord.attribute(XMLConstants.XMLNS_URL, prefix, XMLConstants.XMLNS + ":" + prefix, namespaceResolver.getDefaultNamespaceURI());
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
        XMLField xmlField = (XMLField) xmlDirectMapping.getField();
        Object realValue = xmlField.convertValueBasedOnSchemaType(value, (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager(), unmarshalRecord);
        // Perform operations on the object based on the null policy
        Object convertedValue = xmlDirectMapping.getAttributeValue(realValue, unmarshalRecord.getSession(), unmarshalRecord);
        xmlDirectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), convertedValue);
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        unmarshalRecord.removeNullCapableValue(this);
        XMLField xmlField = (XMLField) xmlDirectMapping.getField();
        if (!xmlField.getLastXPathFragment().nameIsText()) {
            return;
        }
        Object value;
        if(unmarshalRecord.getCharacters().length() == 0) {
            value = this.getMapping().getNullValue();
        } else {
            value = unmarshalRecord.getCharacters().toString();
        }
        unmarshalRecord.resetStringBuffer();

        XMLConversionManager xmlConversionManager = (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager();
        if (unmarshalRecord.getTypeQName() != null) {
            Class typeClass = xmlField.getJavaClass(unmarshalRecord.getTypeQName());
            value = xmlConversionManager.convertObject(value, typeClass, unmarshalRecord.getTypeQName());
        } else {
            value = xmlField.convertValueBasedOnSchemaType(value, xmlConversionManager,unmarshalRecord);
        }

        Object convertedValue = xmlDirectMapping.getAttributeValue(value, unmarshalRecord.getSession(), unmarshalRecord);
        unmarshalRecord.setAttributeValue(convertedValue, xmlDirectMapping);
    }

    public void setNullValue(Object object, Session session) {
        Object value = xmlDirectMapping.getAttributeValue(null, session);
        xmlDirectMapping.setAttributeValueInObject(object, value);
    }

    public boolean isNullCapableValue() {
        return xmlDirectMapping.getNullPolicy().getIsSetPerformedForAbsentNode();
    }

    public XMLDirectMapping getMapping() {
        return xmlDirectMapping;
    }

    public boolean isWhitespaceAware() {
        return !xmlDirectMapping.getNullPolicy().isNullRepresentedByEmptyNode();
    }

}
