/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Contributors:
//     24 April 2013-2.5.1 ailitche
//       SerializedObjectPolicy initial API and implementation
package org.eclipse.persistence.internal.jpa.metadata.sop;

import org.eclipse.persistence.internal.descriptors.SerializedObjectPolicyWrapper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * Object to hold onto serialized object policy metadata.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author Andrei Ilitchev
 * @since EclipseLink 2.5.1
 */
public class SerializedObjectPolicyMetadata extends ORMetadata {
    protected MetadataClass m_class;
    protected String m_className;
    protected ColumnMetadata m_column;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public SerializedObjectPolicyMetadata() {
        super("<serialized-object>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public SerializedObjectPolicyMetadata(MetadataAnnotation serializedObject, MetadataAccessor accessor) {
        super(serializedObject, accessor);

        m_class = getMetadataClass(serializedObject.getAttributeString("value"));
        MetadataAnnotation column = serializedObject.getAttributeAnnotation("column");
        m_column = new ColumnMetadata(column, accessor);
        // This should be kept in sync with SerializedObject.column default
        if (column == null) {
            m_column.setName("SOP");
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof SerializedObjectPolicyMetadata) {
            SerializedObjectPolicyMetadata serializedObjectToCompare = (SerializedObjectPolicyMetadata) objectToCompare;
            return valuesMatch(m_className, serializedObjectToCompare.getClassName()) && valuesMatch(m_column, serializedObjectToCompare.getColumn());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return m_className != null ? m_className.hashCode() : 0;
    }

    /**
     * INTERNAL
     * Used for OX mapping.
     */
    public String getClassName() {
        return m_className;
    }

    /**
     * INTERNAL
     * Used for OX mapping.
     */
    public ColumnMetadata getColumn() {
        return m_column;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        m_class = initXMLClassName(m_className);
        initXMLObject(m_column, accessibleObject);

        if (m_column == null) {
            // This should be kept in sync with SerializedObject.column default
            m_column = new ColumnMetadata();
            m_column.setName("SOP");
        }
    }

    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor) {
        // Set the cache flag on the metadata Descriptor.
        descriptor.setHasSerializedObjectPolicy();

        // Process the cache metadata.
        SerializedObjectPolicyWrapper sop = new SerializedObjectPolicyWrapper(m_class.getName());
        sop.setField(m_column.getDatabaseField());
        descriptor.getClassDescriptor().setSerializedObjectPolicy(sop);
    }

    /**
     * INTERNAL
     * Used for OX mapping.
     */
    public void setClassName(String className) {
        m_className = className;
    }

    /**
     * INTERNAL
     * Used for OX mapping.
     */
    public void setColumn(ColumnMetadata column) {
        m_column = column;
    }
}
