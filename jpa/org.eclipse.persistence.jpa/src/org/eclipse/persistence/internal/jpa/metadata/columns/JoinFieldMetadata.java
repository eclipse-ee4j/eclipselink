/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Synonym for JoinColumn for NoSql data.
 */
public class JoinFieldMetadata extends JoinColumnMetadata {

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public JoinFieldMetadata() {
        super("<join-field>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public JoinFieldMetadata(MetadataAnnotation joinColumn, MetadataAccessor accessor) {
        super(joinColumn, accessor);
    }
}
