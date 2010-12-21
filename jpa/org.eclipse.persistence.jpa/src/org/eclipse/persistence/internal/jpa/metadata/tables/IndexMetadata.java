/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to hold onto database index metadata.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class IndexMetadata extends ORMetadata {
    private String name;
    private String schema;
    private String catalog;
    private String table;
    private Boolean unique;

    private List<String> columnNames = new ArrayList();

    public IndexMetadata() {
        super("<index>");
    }

    protected IndexMetadata(String xmlElement) {
        super(xmlElement);
    }

    public IndexMetadata(MetadataAnnotation index, MetadataAccessibleObject accessibleObject) {
        super(index, accessibleObject);
        
        if (index != null) {
            this.name = (String) index.getAttribute("name"); 
            this.schema = (String) index.getAttribute("schema"); 
            this.catalog = (String) index.getAttribute("catalog");
            this.table = (String) index.getAttribute("table");
            this.unique = (Boolean) index.getAttribute("unique");
            
            for (Object uniqueConstraint : (Object[]) index.getAttributeArray("columnNames")) {
                this.columnNames.add((String)uniqueConstraint);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Sub classed must that can uniquely be identified must override this
     * message to allow the overriding and merging to uniquely identify objects.
     * It will also be used when logging messages (that is provide a more
     * detailed message).
     * 
     * @see shouldOverride
     * @see mergeListsAndOverride
     */
    @Override
    protected String getIdentifier() {
        return getName();
    }

    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof IndexMetadata) {
            IndexMetadata table = (IndexMetadata) objectToCompare;
            
            if (! valuesMatch(this.name, table.getName())) {
                return false;
            }
            
            if (! valuesMatch(this.schema, table.getSchema())) {
                return false;
            }
            
            if (! valuesMatch(this.catalog, table.getCatalog())) {
                return false;
            }
            
            return this.columnNames.equals(table.getColumnNames());
        }
        
        return false;
    }
    
    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }
}
