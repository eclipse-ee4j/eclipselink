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

import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.XMLTransformationRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the Field Transformer is handled when used 
 * with the TreeObjectBuilder.  Field Transformers are used with the XML 
 * Transformation Mapping.</p>
 */

public class FieldTransformerNodeValue extends NodeValue {
    private FieldTransformer fieldTransformer;
    private XMLField xmlField;

    public FieldTransformer getFieldTransformer() {
        return fieldTransformer;
    }

    public void setFieldTransformer(FieldTransformer fieldTransformer) {
        this.fieldTransformer = fieldTransformer;
    }

    public XMLField getXMLField() {
        return xmlField;
    }

    public void setXMLField(XMLField xmlField) {
        this.xmlField = xmlField;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        Object value = fieldTransformer.buildFieldValue(object, getXMLField().getXPath(), session);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, value, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if(value == null) {
            return false;
        }
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        if (getXMLField().getLastXPathFragment().isAttribute()) {
            marshalRecord.add(getXMLField(), value);
            marshalRecord.closeStartGroupingElements(groupingFragment);
        } else {
            marshalRecord.closeStartGroupingElements(groupingFragment);
            marshalRecord.add(getXMLField(), value);
        }
        return true;
    }

    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        XMLConversionManager xmlConversionManager = (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager();
        Object objectValue = xmlField.convertValueBasedOnSchemaType(value, xmlConversionManager, unmarshalRecord);

        // PUT VALUE INTO A RECORD KEYED ON XMLFIELD
        if (null == unmarshalRecord.getTransformationRecord()) {
            unmarshalRecord.setTransformationRecord(new XMLTransformationRecord("ROOT", unmarshalRecord));
        }
        unmarshalRecord.getTransformationRecord().put(xmlField, objectValue);
    }    
    
    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        Object value = unmarshalRecord.getStringBuffer().toString();
        boolean isCDATA = unmarshalRecord.isBufferCDATA();
        unmarshalRecord.resetStringBuffer();
        XMLField toWrite = xmlField;
        if(xmlField.isCDATA() != isCDATA) {
            toWrite = new XMLField(xmlField.getName());
            toWrite.setNamespaceResolver(xmlField.getNamespaceResolver());
            toWrite.setIsCDATA(isCDATA);
        }
        //xmlField.setIsCDATA(isCDATA);
        XMLConversionManager xmlConversionManager = (XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager();
        if (unmarshalRecord.getTypeQName() != null) {
            Class typeClass = xmlField.getJavaClass(unmarshalRecord.getTypeQName());
            value = xmlConversionManager.convertObject(value, typeClass, unmarshalRecord.getTypeQName());
        } else {
            value = xmlField.convertValueBasedOnSchemaType(value, xmlConversionManager, unmarshalRecord);
        }

        // PUT VALUE INTO A RECORD KEYED ON XMLFIELD
        if (null == unmarshalRecord.getTransformationRecord()) {
            unmarshalRecord.setTransformationRecord(new XMLTransformationRecord("ROOT", unmarshalRecord));
        }
        unmarshalRecord.getTransformationRecord().put(toWrite, value);
    }
}
