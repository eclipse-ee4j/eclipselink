/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.locking;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.config.columns.ColumnImpl;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.locking.OptimisticLockingMetadata;
import org.eclipse.persistence.jpa.config.Column;
import org.eclipse.persistence.jpa.config.OptimisticLocking;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class OptimisticLockingImpl extends MetadataImpl<OptimisticLockingMetadata> implements OptimisticLocking {

    public OptimisticLockingImpl() {
        super(new OptimisticLockingMetadata());

        getMetadata().setSelectedColumns(new ArrayList<ColumnMetadata>());
    }

    public Column addSelectedColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().getSelectedColumns().add(column.getMetadata());
        return column;
    }

    public OptimisticLocking setCascade(Boolean cascade) {
        getMetadata().setCascade(cascade);
        return this;
    }

    public OptimisticLocking setType(String type) {
        getMetadata().setType(type);
        return this;
    }

}
