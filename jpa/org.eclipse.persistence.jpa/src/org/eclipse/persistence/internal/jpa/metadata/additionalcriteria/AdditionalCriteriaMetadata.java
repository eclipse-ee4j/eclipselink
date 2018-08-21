/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     10/08/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.additionalcriteria;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * Object to hold onto additional criteria metadata.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.2
 */
public class AdditionalCriteriaMetadata extends ORMetadata {
    protected String m_criteria;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public AdditionalCriteriaMetadata() {
        super("<additional-criteria>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public AdditionalCriteriaMetadata(MetadataAnnotation additionalCriteria, MetadataAccessor accessor) {
        super(additionalCriteria, accessor);

        m_criteria = additionalCriteria.getAttributeString("value");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof AdditionalCriteriaMetadata) {
            AdditionalCriteriaMetadata additionalCriteria = (AdditionalCriteriaMetadata) objectToCompare;
            return valuesMatch(m_criteria, additionalCriteria.getCriteria());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return m_criteria != null ? m_criteria.hashCode() : 0;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCriteria() {
        return m_criteria;
    }

    /**
     * INTERNAL:
     * The unique identifier of additional criteria is the criteria itself.
     */
    @Override
    public String getIdentifier() {
        return m_criteria;
    }

    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor) {
        // Set the criteria (jpql fragment) on the descriptors query event
        // manager. The JPQL fragment will be parsed during descriptor post
        // initialization.
        descriptor.getClassDescriptor().getQueryManager().setAdditionalCriteria(m_criteria);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCriteria(String criteria) {
        m_criteria = criteria;
    }
}
