/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.metadata.structures;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.mappings.structures.StructureMapping;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_COLUMN;

/**
 * Defines a StructureMapping metadata.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
public class StructureAccessor extends MappingAccessor {
    private ColumnMetadata m_column;
    private DatabaseField m_field;
    private MetadataClass m_referenceClass;
    private MetadataClass m_targetClass;
    private String m_targetClassName;

    /**
     * Used for OX mapping.
     */
    public StructureAccessor() {
        super("<structure>");
    }
    
    /**
     * INTERNAL:
     */
    public StructureAccessor(MetadataAnnotation embedded, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(embedded, accessibleObject, classAccessor);
        
        // Set the column metadata if one if present.
        m_column = new ColumnMetadata(getAnnotation(JPA_COLUMN), this);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && (objectToCompare instanceof StructureAccessor)) {
            StructureAccessor accessor = (StructureAccessor) objectToCompare;
                        
            return valuesMatch(m_column, accessor.getColumn());
        }
        
        return false;
    }

    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Default a column if necessary.
        if (m_column == null) {
            m_column = new ColumnMetadata(this);
        } else {
            // Initialize single objects.
            initXMLObject(m_column, accessibleObject);
        }
    }
    
    /**
     * Build and structure mapping and add it to the descriptor.
     */
    @Override
    public void process() {
        StructureMapping mapping = new StructureMapping();
        setMapping(mapping);
        
        // Process the @Column or column element if there is one.
        // A number of methods depend on this field so it must be
        // initialized before any further processing can take place.
        m_field = new ObjectRelationalDatabaseField(getDatabaseField(getDescriptor().getPrimaryTable(), MetadataLogger.COLUMN));
                
        mapping.setField(m_field);
        mapping.setIsReadOnly(m_field.isReadOnly());
        
        mapping.setReferenceClassName(getReferenceClassName());
        mapping.setAttributeName(getAttributeName());    
        
        // Will check for PROPERTY access
        setAccessorMethods(mapping);
                
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
    }

    public ColumnMetadata getColumn() {
        return m_column;
    }

    public void setColumn(ColumnMetadata column) {
        m_column = column;
    }
    
    /**
     * Must return true as an embedded is referenced and needs to be correctly initialized.
     */
    public boolean isEmbedded() {
        return true;
    }
   
    /**
     * If a target class is specified in metadata, it will be set as the 
     * reference class, otherwise we will use the raw class.
     */
    @Override
    public MetadataClass getReferenceClass() {
        if (m_referenceClass == null) {
            m_referenceClass = getTargetClass();
        
            if (m_referenceClass == null) {
                // Get the reference class from the accessible object.
                m_referenceClass = super.getReferenceClass();
            } 
        }
        
        return m_referenceClass;
    }
    
    /**
     * Return the target class for this accessor.
     */
    protected MetadataClass getTargetClass() {
        return m_targetClass;
    }

    protected String getTargetClassName() {
        return m_targetClassName;
    }

    public void setTargetClassName(String targetClassName) {
        m_targetClassName = targetClassName;
    }
}
