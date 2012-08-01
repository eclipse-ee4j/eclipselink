/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     06/20/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.queries.ColumnResult;

/**
 * INTERNAL:
 * Object to hold onto a column result metadata.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - all metadata mapped from XML must be initialized in the initXMLObject 
 *   method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since Eclipselink 2.4
 */
public class ColumnResultMetadata extends ORMetadata {
    private MetadataClass type;
    private String name;
    private String typeName;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ColumnResultMetadata() {
        super("<column-result>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ColumnResultMetadata(MetadataAnnotation columnResult, MetadataAccessor accessor) {
        super(columnResult, accessor);
        
        name = (String) columnResult.getAttribute("name");
        type = getMetadataClass((String) columnResult.getAttribute("type")); 
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ColumnResultMetadata) {
            ColumnResultMetadata columnResult = (ColumnResultMetadata) objectToCompare;
            
            if (! valuesMatch(getName(), columnResult.getName())) {
                return false;
            }
            
            return valuesMatch(getType(), columnResult.getType());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return name;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getType() {
        return type;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getTypeName() {
        return typeName;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        setType(initXMLClassName(getTypeName()));
    }
    
    /**
     * INTERNAL:
     * Process the column result for the caller.
     */
    public ColumnResult process() {
        DatabaseField field = new DatabaseField();
        
        // Process the name (taking into consideration delimiters etc.)
        setFieldName(field, getName());
        
        // Set the type name.
        if (! getType().isVoid()) {
            field.setTypeName(getJavaClassName(getType()));
        }
        
        // Return a column result to the mapping.
        return new ColumnResult(field);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * INTERNAL:
     */
    public void setType(MetadataClass type) {
        this.type = type;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}

