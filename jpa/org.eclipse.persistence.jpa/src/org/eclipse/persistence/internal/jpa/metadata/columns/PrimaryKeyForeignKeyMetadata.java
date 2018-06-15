/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to process JPA primary key foreign key metadata.
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
 * @since EclipseLink 2.5
 */
public class PrimaryKeyForeignKeyMetadata extends ForeignKeyMetadata {
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public PrimaryKeyForeignKeyMetadata() {
        super("<primary-key-foreign-key>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public PrimaryKeyForeignKeyMetadata(ForeignKeyMetadata foreignKey) {
        super(foreignKey);
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public PrimaryKeyForeignKeyMetadata(MetadataAnnotation primaryKeyForeignKey, MetadataAccessor accessor) {
        super(primaryKeyForeignKey, accessor);
    }
}
