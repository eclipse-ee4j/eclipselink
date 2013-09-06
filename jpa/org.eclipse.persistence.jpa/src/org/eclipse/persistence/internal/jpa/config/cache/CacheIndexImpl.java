/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
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

    public CacheIndex addColumnName(String columnName) {
        getMetadata().getColumnNames().add(columnName);
        return this;
    }

    public CacheIndex setUpdateable(Boolean updateable) {
        getMetadata().setUpdateable(updateable);
        return this;
    }

}
