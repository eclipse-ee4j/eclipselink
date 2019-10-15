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
//     James Sutherland - initial API and implementation
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.partitioning;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Define JPA meta-data for partitioning policy.
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
 * @since EclipseLink 2.2
 */
public abstract class AbstractPartitioningMetadata extends ORMetadata {
    protected String name;

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public AbstractPartitioningMetadata(MetadataAnnotation annotation, MetadataAccessor accessor) {
        super(annotation, accessor);

        this.name = annotation.getAttributeString("name");
    }

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected AbstractPartitioningMetadata(String elementName) {
        super(elementName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Equals is used for processing overrides from XML.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof AbstractPartitioningMetadata) {
            AbstractPartitioningMetadata policy = (AbstractPartitioningMetadata) objectToCompare;

            return valuesMatch(this.name, policy.getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    /**
     * Require subclass to build policy.
     */
    public abstract PartitioningPolicy buildPolicy();

    /**
     * Set common fields into policy.
     * Should be called by subclasses.
     */
    public void buildPolicy(PartitioningPolicy policy) {
        policy.setName(this.name);
    }
}
