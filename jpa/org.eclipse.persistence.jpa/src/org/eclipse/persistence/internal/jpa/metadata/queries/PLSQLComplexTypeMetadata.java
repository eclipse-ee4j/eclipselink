/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to hold onto a PLSQL complex type meta-data.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
public abstract class PLSQLComplexTypeMetadata extends ORMetadata {
    private String name;
    private String compatibleType;
    private String javaType;
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public PLSQLComplexTypeMetadata(MetadataAnnotation record, MetadataAccessor accessor) {
        super(record, accessor);
        
        this.name = (String) record.getAttribute("name");
        this.compatibleType = (String) record.getAttribute("compatibleType");
        this.javaType = (String) record.getAttribute("javaType");        
    }
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public PLSQLComplexTypeMetadata(String element) {
        super(element);
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof PLSQLRecordMetadata) {
            PLSQLRecordMetadata parameter = (PLSQLRecordMetadata) objectToCompare;
            
            if (! valuesMatch(this.name, parameter.getName())) {
                return false;
            }
            
            if (! valuesMatch(this.compatibleType, parameter.getCompatibleType())) {
                return false;
            }
            
            if (! valuesMatch(this.javaType, parameter.getJavaType())) {
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCompatibleType() {
        return compatibleType;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getJavaType() {
        return javaType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Build a runtime type from the meta-data.
     */
    public abstract ComplexDatabaseType process(MetadataProject project);
    
    /**
     * Build a runtime record type from the meta-data.
     */
    public void process(ComplexDatabaseType type, MetadataProject project) {
        type.setTypeName(this.name);
        type.setCompatibleType(this.compatibleType);
        if (this.javaType != null) {
            type.setJavaType(getJavaClass(getMetadataClass(this.javaType)));
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCompatibleType(String compatibleType) {
        this.compatibleType = compatibleType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        this.name = name;
    }
}
