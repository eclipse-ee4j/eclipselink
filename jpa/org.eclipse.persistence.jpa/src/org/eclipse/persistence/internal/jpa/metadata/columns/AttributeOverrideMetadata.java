/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * INTERNAL:
 * Object to hold onto an attribute override meta data.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class AttributeOverrideMetadata extends OverrideMetadata {
    private ColumnMetadata m_column;

    /**
     * INTERNAL:
     */
    public AttributeOverrideMetadata() {
        super("<attribute-override>");
    }
    
    /**
     * INTERNAL:
     */
    public AttributeOverrideMetadata(MetadataAnnotation attributeOverride, MetadataAccessibleObject accessibleObject) {
        super(attributeOverride, accessibleObject);

        m_column = new ColumnMetadata((MetadataAnnotation) attributeOverride.getAttribute("column"), accessibleObject);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof AttributeOverrideMetadata) {
            return valuesMatch(m_column, ((AttributeOverrideMetadata) objectToCompare).getColumn());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ColumnMetadata getColumn() {
        return m_column;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String getIgnoreMappedSuperclassContext() {
        return MetadataLogger.IGNORE_MAPPED_SUPERCLASS_ATTRIBUTE_OVERRIDE;
    }
    
    /**
     * INTERNAL:
     * Return the database field for this override.
     */
    public DatabaseField getOverrideField() {
        return m_column.getDatabaseField();
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize single objects.
        initXMLObject(m_column, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumn(ColumnMetadata column) {
        m_column = column;
    }
}
