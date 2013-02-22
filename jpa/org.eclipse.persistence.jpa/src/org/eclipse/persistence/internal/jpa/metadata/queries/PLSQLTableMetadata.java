/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection;

/**
 * INTERNAL:
 * Object to hold onto a PLSQL table meta-data.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - all metadata mapped from XML should be initialized in the initXMLObject 
 *   method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
public class PLSQLTableMetadata extends PLSQLComplexTypeMetadata {
    private String nestedType;
    private Boolean isNestedTable;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public PLSQLTableMetadata() {
        super("<plsql-table>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public PLSQLTableMetadata(MetadataAnnotation record, MetadataAccessor accessor) {
        super(record, accessor);
                
        this.nestedType = record.getAttributeString("nestedType");
        this.isNestedTable = record.getAttributeBooleanDefaultFalse("isNestedTable");
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof PLSQLTableMetadata) {
            PLSQLTableMetadata parameter = (PLSQLTableMetadata) objectToCompare;            
            
            if (! valuesMatch(this.isNestedTable, parameter.getNestedTable())) {
                return false;
            }
            if (! valuesMatch(this.nestedType, parameter.getNestedType())) {
                return false;
            }
        }
        
        return super.equals(objectToCompare);
    }
    
    /**
     * Indicates if the instance represents a Nested Table (as opposed to Varray).
     * Defaults to false, i.e. Varray.
     */
    public Boolean getNestedTable() {
        return isNestedTable;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getNestedType() {
        return nestedType;
    }
    
    /**
     * Indicates if the instance represents a Nested Table (as opposed to Varray).
     * Defaults to false, i.e. Varray.
     */
    public boolean isNestedTable() {
        return getNestedTable() != null && getNestedTable();
    }
    
    /**
     * INTERNAL:
     * Build a runtime record type from the meta-data.
     */
    public PLSQLCollection process() {
        PLSQLCollection table = new PLSQLCollection();
        super.process(table);
        table.setIsNestedTable(isNestedTable());
        table.setNestedType(getDatabaseTypeEnum(getNestedType()));
        return table;
    }

    /**
     * Set boolean that indicates if the instance represents a Nested Table 
     * (as opposed to Varray)
     */
    public void setNestedTable(Boolean isNestedTable) {
        this.isNestedTable = isNestedTable;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setNestedType(String nestedType) {
        this.nestedType = nestedType;
    }

    /**
     * Indicates an instance of PLSQLTableMetadata.
     */
    public boolean isPLSQLTableMetadata() {
        return true;
    }
}
