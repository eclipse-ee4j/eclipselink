/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.cache;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheIndexMetadata;
import org.eclipse.persistence.jpa.config.CacheIndex;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class CacheIndexImpl extends MetadataImpl<CacheIndexMetadata> implements CacheIndex {

    public CacheIndexImpl() {
        super(new CacheIndexMetadata());
        getMetadata().setColumnNames(new ArrayList<String>());
    }

    @Override
    public CacheIndex addColumnName(String columnName) {
        getMetadata().getColumnNames().add(columnName);
        return this;
    }

    @Override
    public CacheIndex setUpdateable(Boolean updateable) {
        getMetadata().setUpdateable(updateable);
        return this;
    }

}
