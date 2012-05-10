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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.List;
import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.queries.ConstructorResult;

/**
 * INTERNAL:
 * Object to hold onto an entity result metadata.
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
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class ConstructorResultMetadata extends ORMetadata {
    private MetadataClass targetClass; 
    private String targetClassName;
    private List<ColumnResultMetadata> columnResults = new ArrayList<ColumnResultMetadata>();
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ConstructorResultMetadata() {
        super("<constructor-result>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ConstructorResultMetadata(MetadataAnnotation constructorResult, MetadataAccessor accessor) {
        super(constructorResult, accessor);
        
        targetClass = getMetadataClass((String) constructorResult.getAttribute("targetClass")); 
        
        for (Object columnResult : (Object[]) constructorResult.getAttributeArray("columns")) {
            columnResults.add(new ColumnResultMetadata((MetadataAnnotation) columnResult, accessor));
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ConstructorResultMetadata) {
            ConstructorResultMetadata constructorResult = (ConstructorResultMetadata) objectToCompare;
            
            if (! valuesMatch(getTargetClass(), constructorResult.getTargetClass())) {
                return false;
            }
            
            return valuesMatch(getColumnResults(), constructorResult.getColumnResults());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ColumnResultMetadata> getColumnResults() {
        return columnResults;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getTargetClass() {
        return targetClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getTargetClassName() {
        return targetClassName;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize lists of ORMetadata objects.
        initXMLObjects(getColumnResults(), accessibleObject);
        
        setTargetClass(initXMLClassName(getTargetClassName()));
    }
    
    /**
     * INTERNAL:
     * Process the constructor result for the caller.
     */
    public ConstructorResult process(ClassLoader loader) {
        // Create a new constructor result with the target class.
        ConstructorResult constructorResult = new ConstructorResult(MetadataHelper.getClassForName(getTargetClass().getName(), loader));
        
        // Process the column results.
        for (ColumnResultMetadata columnResult : getColumnResults()) {
            constructorResult.addColumnResult(columnResult.process(loader));
        }

        return constructorResult;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumnResults(List<ColumnResultMetadata> columnResults) {
        this.columnResults = columnResults; 
    }
    
    /**
     * INTERNAL:
     */
    public void setTargetClass(MetadataClass targetClass) {
        this.targetClass = targetClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }
}
