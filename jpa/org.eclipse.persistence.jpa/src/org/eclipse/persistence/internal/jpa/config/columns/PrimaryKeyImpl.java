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

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyMetadata;
import org.eclipse.persistence.jpa.config.Column;
import org.eclipse.persistence.jpa.config.PrimaryKey;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class PrimaryKeyImpl extends MetadataImpl<PrimaryKeyMetadata> implements PrimaryKey {

    public PrimaryKeyImpl() {
        super(new PrimaryKeyMetadata());

        getMetadata().setColumns(new ArrayList<ColumnMetadata>());
    }

    public Column addColumn() {
        ColumnImpl column = new ColumnImpl();
        getMetadata().getColumns().add(column.getMetadata());
        return column;
    }

    public PrimaryKey setCacheKeyType(String cacheKeyType) {
        getMetadata().setCacheKeyType(cacheKeyType);
        return this;
    }

    public PrimaryKey setValidation(String validation) {
        getMetadata().setValidation(validation);
        return this;
    }

}
