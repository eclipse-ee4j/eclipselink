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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.jpa.metadata.converters;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;

/**
 * INTERNAL:
 * Abstract metadata serializer.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author James Sutherland
 * @since EclipseLink 2.6
 */
public class SerializedConverterMetadata extends AbstractConverterMetadata {
    private String m_className;
    private String m_serializerPackage;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public SerializedConverterMetadata() {
        super("<serialized-converter>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public SerializedConverterMetadata(MetadataAnnotation serializer, MetadataAccessor accessor) {
        super(serializer, accessor);

        m_className = serializer.getAttributeString("serializerClass");
        m_serializerPackage = serializer.getAttributeString("serializerPackage");
    }

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected SerializedConverterMetadata(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof SerializedConverterMetadata) {
            SerializedConverterMetadata serializer = (SerializedConverterMetadata) objectToCompare;

            if (!valuesMatch(m_className, serializer.getClassName())) {
                return false;
            }
            if (!valuesMatch(m_serializerPackage, serializer.getSerializerPackage())) {
                return false;
            }

            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_className != null ? m_className.hashCode() : 0);
        result = 31 * result + (m_serializerPackage != null ? m_serializerPackage.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getClassName() {
        return m_className;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getSerializerPackage() {
        return m_serializerPackage;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        m_className = initXMLClassName(m_className).getName();
    }

    /**
     * INTERNAL:
     * Process this converter for the given mapping.
     */
    @Override
    public void process(DatabaseMapping mapping, MappingAccessor accessor, MetadataClass referenceClass, boolean isForMapKey) {
        SerializedObjectConverter converter = null;
        if ((m_className == null) || (m_className.length() == 0)) {
            converter = new SerializedObjectConverter(mapping);
        } else {
            converter = new SerializedObjectConverter(mapping, getClassName());
        }
        if ((m_serializerPackage != null) && (m_serializerPackage.length() > 0)) {
            converter.setSerializerPackage(m_serializerPackage);
        } else {
            // Default package to target classes package.
            converter.setSerializerPackage(referenceClass.getName().substring(0, referenceClass.getName().lastIndexOf('.')));
        }
        setConverter(mapping, converter, isForMapKey);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setClassName(String className) {
        m_className = className;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSerializerPackage(String serializerPackage) {
        m_serializerPackage = serializerPackage;
    }
}
