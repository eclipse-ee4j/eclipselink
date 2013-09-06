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
package org.eclipse.persistence.internal.jpa.config.tables;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.tables.IndexMetadata;
import org.eclipse.persistence.jpa.config.Index;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class IndexImpl extends MetadataImpl<IndexMetadata> implements Index {
    
    public IndexImpl() {
        super(new IndexMetadata());
        getMetadata().setColumnNames(new ArrayList<String>());
    }
    
    public Index addColumnName(String columnName) {
        getMetadata().getColumnNames().add(columnName);
        return this;
    }

    public Index setCatalog(String catalog) {
        getMetadata().setCatalog(catalog);
        return this;
    }
    
    public Index setColumnList(String columnList) {
        getMetadata().setColumnList(columnList);
        return this;
    }
    
    public Index setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    public Index setSchema(String schema) {
        getMetadata().setSchema(schema);
        return this;
    }

    public Index setTable(String table) {
        getMetadata().setTable(table);
        return this;
    }

    public Index setUnique(Boolean unique) {
        getMetadata().setUnique(unique);
        return this;
    }

}
