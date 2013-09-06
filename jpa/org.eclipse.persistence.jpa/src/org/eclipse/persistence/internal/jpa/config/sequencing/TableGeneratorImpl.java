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
package org.eclipse.persistence.internal.jpa.config.sequencing;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.config.tables.IndexImpl;
import org.eclipse.persistence.internal.jpa.config.tables.UniqueConstraintImpl;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.IndexMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.UniqueConstraintMetadata;
import org.eclipse.persistence.jpa.config.Index;
import org.eclipse.persistence.jpa.config.TableGenerator;
import org.eclipse.persistence.jpa.config.UniqueConstraint;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class TableGeneratorImpl extends MetadataImpl<TableGeneratorMetadata> implements TableGenerator {

    public TableGeneratorImpl() {
        super(new TableGeneratorMetadata());
        
        getMetadata().setIndexes(new ArrayList<IndexMetadata>());
        getMetadata().setUniqueConstraints(new ArrayList<UniqueConstraintMetadata>());        
    }

    public Index addIndex() {
        IndexImpl index = new IndexImpl();
        getMetadata().getIndexes().add(index.getMetadata());
        return index;
    }
    
    public UniqueConstraint addUniqueConstraint() {
        UniqueConstraintImpl uniqueConstraint = new UniqueConstraintImpl();
        getMetadata().getUniqueConstraints().add(uniqueConstraint.getMetadata());
        return uniqueConstraint;
    }
    
    public TableGenerator setAllocationSize(Integer allocationSize) {
        getMetadata().setAllocationSize(allocationSize);
        return this;
    }
    
    public TableGenerator setCatalog(String catalog) {
        getMetadata().setCatalog(catalog);
        return this;
    }
    
    public TableGenerator setCreationSuffix(String creationSuffix) {
        getMetadata().setCreationSuffix(creationSuffix);
        return this;
    }
    
    public TableGenerator setInitialValue(Integer initialValue) {
        getMetadata().setInitialValue(initialValue);
        return this;
    }
    
    public TableGenerator setName(String name) {
        getMetadata().setGeneratorName(name);
        return this;
    }

    public TableGenerator setPKColumnName(String pkColumnName) {
        getMetadata().setPkColumnName(pkColumnName);
        return this;
    }
    
    public TableGenerator setPKColumnValue(String pkColumnValue) {
        getMetadata().setPkColumnValue(pkColumnValue);
        return this;
    }
    
    public TableGenerator setSchema(String schema) {
        getMetadata().setSchema(schema);
        return this;
    }

    public TableGenerator setTable(String table) {
        getMetadata().setName(table);
        return this;
    }

    public TableGenerator setValueColumnName(String valueColumnName) {
        getMetadata().setValueColumnName(valueColumnName);
        return this;
    }

}
