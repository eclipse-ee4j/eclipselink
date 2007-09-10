/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
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
public class XMLDirectMappingNodeValue extends XMLSimpleMappingNodeValue implements NullCapableValue {
    private XMLDirectMapping xmlDirectMapping;

    public XMLDirectMappingNodeValue(XMLDirectMapping xmlDirectMapping) {
        super();
        this.xmlDirectMapping = xmlDirectMapping;
    }

    public void setXPathNode(XPathNode xPathNode) {
        super.setXPathNode(xPathNode);
        xmlDirectMapping.getNodeNullPolicy().xPathNode(xPathNode, this);
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return xPathFragment.isAttribute() || xPathFragment.nameIsText();
    }

    /**
     * Override the method in XPathNode such that the marshaller can be set on the
     * marshalRecord - this is required for XMLConverter usage.
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, org.eclipse.persistence.oxm.XMLMarshaller marshaller) {
        marshalRecord.setMarshaller(marshaller);
        return this.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
    }
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlDirectMapping.isReadOnly()) {
            return false;
        }
        Object objectValue = xmlDirectMapping.getAttributeValueFromObject(object);
        Object fieldValue = xmlDirectMapping.getFieldValue(objectValue, session, marshalRecord);
        if (null == fieldValue) {
            return xmlDirectMapping.getNodeNullPolicy().directMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
        } else {
            QName schemaType = getSchemaType((XMLField)xmlDirectMapping.getField(), fieldValue);
            XMLConversionManager xmlConversionManager = (XMLConversionManager) session.getDatasourcePlatform().getConversionManager();
            String stringValue = getValueToWrite(schemaType, fieldValue, xmlConversionManager);
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            if (xPathFragment.isAttribute()) {
                marshalRecord.attribute(xPathFragment, namespaceResolver, stringValue);
                marshalRecord.closeStartGroupingElements(groupingFragment);
            } else {
                marshalRecord.closeStartGroupingElements(groupingFragment);
                if(xmlDirectMapping.isCDATA()) {
                    marshalRecord.cdata(stringValue);
                } else {
                    marshalRecord.characters(stringValue);
                }
            }
            return true;
        }
    }
    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        unmarshalRecord.removeNullCapableValue(this);
        XMLField xmlField = (XMLField)xmlDirectMapping.getField();
        XMLConversionManager xmlConversionManager = (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager();
        Object realValue = xmlField.convertValueBasedOnSchemaType(value, xmlConversionManager);
        Object convertedValue = xmlDirectMapping.getAttributeValue(realValue, unmarshalRecord.getSession(), unmarshalRecord);
        xmlDirectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), convertedValue);
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        unmarshalRecord.removeNullCapableValue(this);
        XMLField xmlField = (XMLField)xmlDirectMapping.getField();
        if (!xmlField.getLastXPathFragment().nameIsText()) {
            return;
        }
        Object value = unmarshalRecord.getStringBuffer().toString();
        if (value.equals(EMPTY_STRING)) {
            value = null;
        }
        unmarshalRecord.resetStringBuffer();

        XMLConversionManager xmlConversionManager = (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager();
        if (unmarshalRecord.getTypeQName() != null) {
            Class typeClass = xmlField.getJavaClass(unmarshalRecord.getTypeQName());
            value = xmlConversionManager.convertObject(value, typeClass, unmarshalRecord.getTypeQName());
        } else {
            value = xmlField.convertValueBasedOnSchemaType(value, xmlConversionManager);
        }

        Object convertedValue = xmlDirectMapping.getAttributeValue(value, unmarshalRecord.getSession(), unmarshalRecord);
        xmlDirectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), convertedValue);
    }

    public void setNullValue(Object object, Session session) {
        Object value = xmlDirectMapping.getAttributeValue(null, (org.eclipse.persistence.internal.sessions.AbstractSession)session);
        xmlDirectMapping.setAttributeValueInObject(object, value);
    }

    public boolean isNullCapableValue() {
        return xmlDirectMapping.getNodeNullPolicy().isNullCapabableValue();
    }
}
