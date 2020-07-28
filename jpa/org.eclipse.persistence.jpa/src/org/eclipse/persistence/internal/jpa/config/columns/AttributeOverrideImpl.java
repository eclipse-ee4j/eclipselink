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
package org.eclipse.persistence.internal.jpa.config.columns;

import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.jpa.config.AttributeOverride;
import org.eclipse.persistence.jpa.config.Column;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class AttributeOverrideImpl extends AbstractOverrideImpl<AttributeOverrideMetadata, AttributeOverride> implements AttributeOverride {

    public AttributeOverrideImpl() {
        super(new AttributeOverrideMetadata());
    }

    public Column setColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().setColumn(column.getMetadata());
        return column;
    }

}
