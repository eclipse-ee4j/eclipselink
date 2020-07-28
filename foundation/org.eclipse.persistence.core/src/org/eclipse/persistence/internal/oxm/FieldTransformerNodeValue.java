/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.core.mappings.transformers.CoreFieldTransformer;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.TransformationMapping;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the Field Transformer is handled when used
 * with the TreeObjectBuilder.  Field Transformers are used with the XML
 * Transformation Mapping.</p>
 */

public class FieldTransformerNodeValue extends NodeValue {
    private CoreFieldTransformer fieldTransformer;
    private TransformationMapping transformationMapping;
    private Field xmlField;

    public FieldTransformerNodeValue(TransformationMapping transformationMapping) {
        this.transformationMapping = transformationMapping;
    }

    public CoreFieldTransformer getFieldTransformer() {
        return fieldTransformer;
    }

    public void setFieldTransformer(CoreFieldTransformer fieldTransformer) {
        this.fieldTransformer = fieldTransformer;
    }

    public Field getXMLField() {
        return xmlField;
    }

    public void setXMLField(Field xmlField) {
        this.xmlField = xmlField;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        Object value = fieldTransformer.buildFieldValue(object, getXMLField().getXPath(), session);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, value, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
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
        ConversionManager conversionManager = (ConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager();
        Object objectValue = unmarshalRecord.getXMLReader().convertValueBasedOnSchemaType(xmlField, value, conversionManager, unmarshalRecord);
        transformationMapping.writeFromAttributeIntoRow(unmarshalRecord, xmlField, objectValue, false);
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        Object value = unmarshalRecord.getCharacters().toString();
        unmarshalRecord.resetStringBuffer();
        ConversionManager conversionManager = unmarshalRecord.getConversionManager();
        if (unmarshalRecord.getTypeQName() != null) {
            Class typeClass = xmlField.getJavaClass(unmarshalRecord.getTypeQName(), conversionManager);
            value = conversionManager.convertObject(value, typeClass, unmarshalRecord.getTypeQName());
        } else {
            value = unmarshalRecord.getXMLReader().convertValueBasedOnSchemaType(xmlField, value, conversionManager, unmarshalRecord);
        }
        transformationMapping.writeFromAttributeIntoRow(unmarshalRecord, xmlField, value, true);
    }
}
