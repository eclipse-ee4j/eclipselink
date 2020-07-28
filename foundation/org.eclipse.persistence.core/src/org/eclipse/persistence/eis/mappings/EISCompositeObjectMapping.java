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
package org.eclipse.persistence.eis.mappings;

import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangeTrackingPolicy;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.internal.oxm.XMLObjectBuilder;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;

/**
 * <p>EIS Composite Object Mappings map a Java object to a privately owned, one-to-one
 * relationship to an EIS Record according to its descriptor's record type.
 *
 * <table summary="" border="1">
 * <tr>
 * <th id="c1" align="left">Record Type</th>
 * <th id="c2" align="left">Description</th>
 * </tr>
 * <tr>
 * <td headers="c1">Indexed</td>
 * <td headers="c2">Ordered collection of record elements.  The indexed record EIS format
 * enables Java class attribute values to be retreived by position or index.</td>
 * </tr>
 * <tr>
 * <td headers="c1">Mapped</td>
 * <td headers="c2">Key-value map based representation of record elements.  The mapped record
 * EIS format enables Java class attribute values to be retreived by an object key.</td>
 * </tr>
 * <tr>
 * <td headers="c1">XML</td>
 * <td headers="c2">Record/Map representation of an XML DOM element.</td>
 * </tr>
 * </table>
 *
 * @see org.eclipse.persistence.eis.EISDescriptor#useIndexedRecordFormat
 * @see org.eclipse.persistence.eis.EISDescriptor#useMappedRecordFormat
 * @see org.eclipse.persistence.eis.EISDescriptor#useXMLRecordFormat
 *
 * @since Oracle TopLink 10<i>g</i> Release 2 (10.1.3)
 */
public class EISCompositeObjectMapping extends AbstractCompositeObjectMapping implements EISMapping {
    public EISCompositeObjectMapping() {
        super();
    }

    /**
     * INTERNAL:
     */
    public boolean isEISMapping() {
        return true;
    }

    /**
     * Get the XPath String
     *
     * @return String the XPath String associated with this Mapping
     *
     */
    public String getXPath() {
        return getField().getName();
    }

    /**
     * Set the Mapping field name attribute to the given XPath String
     *
     * @param xpathString String
     *
     */
    public void setXPath(String xpathString) {
        this.setField(new XMLField(xpathString));
    }

    /**
     * PUBLIC:
     * Return the name of the field mapped by the mapping.
     */
    public String getFieldName() {
        return this.getField().getName();
    }

    /**
     * PUBLIC:
     * Set the name of the field mapped by the mapping.
     */
    public void setFieldName(String fieldName) {
        this.setField(new DatabaseField(fieldName));
    }

    @Override
    protected Object buildCompositeRow(Object attributeValue, AbstractSession session, AbstractRecord record, WriteType writeType) {
        if (((EISDescriptor)getDescriptor()).isXMLFormat()) {
            XMLObjectBuilder objectBuilder = (XMLObjectBuilder)getReferenceDescriptor(attributeValue, session).getObjectBuilder();
            return objectBuilder.buildRow(attributeValue, session, getField(), (XMLRecord)record);
        } else {
            AbstractRecord nestedRow = this.getObjectBuilder(attributeValue, session).buildRow(attributeValue, session, writeType);
            return this.getReferenceDescriptor(attributeValue, session).buildFieldValueFromNestedRow(nestedRow, session);
        }
    }

    protected Object buildCompositeObject(ObjectBuilder objectBuilder, AbstractRecord nestedRow, ObjectBuildingQuery query, CacheKey parentCacheKey, JoinedAttributeManager joinManager, AbstractSession targetSession) {
        if (((EISDescriptor)getDescriptor()).isXMLFormat()) {
            return objectBuilder.buildObject(query, nestedRow, joinManager);
        } else {
            Object aggregateObject = objectBuilder.buildNewInstance();
            objectBuilder.buildAttributesIntoObject(aggregateObject, parentCacheKey, nestedRow, query, joinManager, query.getExecutionFetchGroup(objectBuilder.getDescriptor()), false, targetSession);
            return aggregateObject;

        }
    }

    /**
     * INTERNAL:
     * Build the value for the database field and put it in the
     * specified database row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord record, AbstractSession session, WriteType writeType) throws DescriptorException {
        if (this.isReadOnly()) {
            return;
        }

        Object attributeValue = this.getAttributeValueFromObject(object);

        // handle "." xpath - condition: xml data format AND xml field is "self"
        if ((((EISDescriptor)getDescriptor()).isXMLFormat()) && ((XMLField)getField()).isSelfField()) {
            XMLObjectBuilder objectBuilder = (XMLObjectBuilder)getReferenceDescriptor(attributeValue, session).getObjectBuilder();
            objectBuilder.buildIntoNestedRow(record, attributeValue, session);
        } else {
            Object fieldValue = null;

            if (attributeValue != null) {
                fieldValue = buildCompositeRow(attributeValue, session, record, writeType);
            }

            record.put(this.getField(), fieldValue);
        }
    }

    /**
     * Fix field names for XML data descriptors.
     * Since fields are fixed to use text() by default in descriptor, ensure the correct non text field is used here.
     */
    @Override
    public void preInitialize(AbstractSession session) {
        super.preInitialize(session);
        if (((EISDescriptor)this.descriptor).isXMLFormat()) {
            if (!(this.field instanceof XMLField)) {
                XMLField newField = new XMLField(this.field.getName());
                this.field = newField;
            }
        }
    }

    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    @Override
    public void postInitialize(AbstractSession session) throws DescriptorException {
        super.postInitialize(session);

        if (getReferenceDescriptor() != null) {
            getReferenceDescriptor().getCachePolicy().setCacheIsolation(this.descriptor.getCachePolicy().getCacheIsolation());
            // Changed as part of fix for bug#4410581 aggregate mapping can not be set to use change tracking if owning descriptor does not use it.
            // Basically the policies should be the same, but we also allow deferred with attribute for CMP2 (courser grained).
            if (getDescriptor().getObjectChangePolicy().getClass().equals(DeferredChangeDetectionPolicy.class)) {
                getReferenceDescriptor().setObjectChangePolicy(new DeferredChangeDetectionPolicy());
            } else if (getDescriptor().getObjectChangePolicy().getClass().equals(ObjectChangeTrackingPolicy.class)
                    && getReferenceDescriptor().getObjectChangePolicy().getClass().equals(AttributeChangeTrackingPolicy.class)) {
                getReferenceDescriptor().setObjectChangePolicy(new ObjectChangeTrackingPolicy());
            }
        }
    }

}
