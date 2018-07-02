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
//     tware - March 28/2008 - 1.0M7 - Initial implementation
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.copypolicy;

import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Used to store information about InstantiationCopyPolicy as it is read from
 * XML or annotations
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @see org.eclipse.persistence.annotations.InstantiationCopyPolicy
 * @author tware
 */
public class InstantiationCopyPolicyMetadata extends CopyPolicyMetadata {
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public InstantiationCopyPolicyMetadata() {
        super("<instantiation-copy-policy>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public InstantiationCopyPolicyMetadata(MetadataAnnotation copyPolicy, MetadataAccessor accessor) {
        super(copyPolicy, accessor);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        return super.equals(objectToCompare) && objectToCompare instanceof InstantiationCopyPolicyMetadata;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * INTERNAL:
     */
    public CopyPolicy getCopyPolicy() {
        return new InstantiationCopyPolicy();
    }
}
