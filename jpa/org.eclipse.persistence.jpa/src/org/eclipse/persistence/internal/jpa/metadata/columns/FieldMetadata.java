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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Synonym for Column for NoSql data.
 */
public class FieldMetadata extends ColumnMetadata {

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public FieldMetadata() {
        super("<field>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public FieldMetadata(MetadataAnnotation column, MetadataAccessor accessor) {
        super(column, accessor);
    }
}
