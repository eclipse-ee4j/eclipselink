/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     11/06/2009-2.0 Guy Pelletier
//       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     06/14/2010-2.2 Guy Pelletier
//       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.mappings;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.mappings.ContainerMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Object to hold onto map key metadata.
 * <p>
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.0
 */
public class MapKeyMetadata extends ORMetadata {
    private String m_name;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public MapKeyMetadata() {
        super("<map-key>");
    }

    /**
     * INTERNAL:
     * Used for defaulting.
     */
    public MapKeyMetadata(MetadataAccessor accessor) {
        super(null, accessor);
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public MapKeyMetadata(MetadataAnnotation mapKey, MetadataAccessor accessor) {
        super(mapKey, accessor);

        m_name = mapKey.getAttributeString("name");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof MapKeyMetadata mapKey) {
            return valuesMatch(m_name, mapKey.getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return m_name != null ? m_name.hashCode() : 0;
    }

    /**
     * INTERNAL:
     * Return true if a name has been specified.
     */
    public boolean hasName() {
        return m_name != null && !m_name.isEmpty();
    }

    /**
     * INTERNAL:
     * Process a map key for a 1-M or M-M mapping. Will return the map key
     * method name that should be used, null otherwise.
     */
    public String process(ContainerMapping mapping, MappingAccessor mappingAccessor) {
        MetadataDescriptor referenceDescriptor = mappingAccessor.getReferenceDescriptor();
        MetadataLogger logger = mappingAccessor.getLogger();

        if ((! hasName()) && referenceDescriptor.hasCompositePrimaryKey()) {
            // No persistent property or field name has been provided, and the
            // reference class has a composite primary key class.  Return null,
            // internally, EclipseLink will use an instance of the composite
            // primary key class as the map key.
            return null;
        } else {
            // A persistent property or field name may have have been provided.
            // If one has not we will default to the primary key of the reference
            // class. The primary key cannot be composite at this point.
            String fieldOrPropertyName = MetadataHelper.getName(m_name, referenceDescriptor.getIdAttributeName(), MetadataLogger.MAP_KEY_ATTRIBUTE_NAME, logger, mappingAccessor.getAnnotatedElementName());

            // Look up the referenceAccessor
            MetadataAccessor referenceAccessor = referenceDescriptor.getMappingAccessor(fieldOrPropertyName);
            if (referenceAccessor == null) {
                // 266912: relax validation for MappedSuperclass descriptors when the map key is an unresolved generic type
                if (referenceDescriptor.isMappedSuperclass()) {
                    return null;
                } else {
                    throw ValidationException.couldNotFindMapKey(fieldOrPropertyName, referenceDescriptor.getJavaClass(), (DatabaseMapping)mapping);
                }
            }

            return referenceAccessor.getAccessibleObjectName();
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
}
