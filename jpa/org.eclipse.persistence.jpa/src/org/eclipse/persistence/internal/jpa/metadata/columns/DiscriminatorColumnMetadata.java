/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import java.lang.annotation.Annotation;

import javax.persistence.DiscriminatorType;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * Object to hold onto discriminator column metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class DiscriminatorColumnMetadata extends ORMetadata {
    private Enum m_discriminatorType;
    private Integer m_length;
    private String m_columnDefinition;
    private String m_name;
    
    /**
     * INTERNAL:
     */
    public DiscriminatorColumnMetadata() {
        super("<discriminator-column>");
    }
    
    /**
     * INTERNAL:
     */
    public DiscriminatorColumnMetadata(Annotation discriminatorColumn, MetadataAccessibleObject accessibleObject) {
        super(discriminatorColumn, accessibleObject);
        
        if (discriminatorColumn != null) {
            m_columnDefinition =  (String) MetadataHelper.invokeMethod("columnDefinition", discriminatorColumn);
            m_discriminatorType = (Enum) MetadataHelper.invokeMethod("discriminatorType", discriminatorColumn); 
            m_length = (Integer) MetadataHelper.invokeMethod("length", discriminatorColumn);
            m_name = (String) MetadataHelper.invokeMethod("name", discriminatorColumn); 
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getColumnDefinition() {
        return m_columnDefinition;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Enum getDiscriminatorType() {
        return m_discriminatorType;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getLength() {
        return m_length;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     * Process a discriminator column metadata into an EclipseLink 
     * DatabaseField. What is done with that field is up to the caller 
     * of this method.
     */
    public DatabaseField process(MetadataDescriptor descriptor, String annotatedElementName, String loggingCtx) {     
        DatabaseField field = new DatabaseField();

        // Process the name
        field.setName(MetadataHelper.getName(m_name, "DTYPE", loggingCtx, descriptor.getLogger(), annotatedElementName));
        
        // Process the length.
        field.setLength(MetadataHelper.getValue(m_length, 31));
        
        // Process the column definition.
        field.setColumnDefinition(MetadataHelper.getValue(m_columnDefinition, ""));
        
        // Process the type.
        if (m_discriminatorType == null || m_discriminatorType.name().equals(DiscriminatorType.STRING.name())) {
            field.setType(String.class);
        } else if (m_discriminatorType.name().equals(DiscriminatorType.CHAR.name())) {
            field.setType(Character.class);
        } else {
            // Through annotation and XML validation, it must be 
            // DiscriminatorType.INTEGER and can't be anything else. 
            field.setType(Integer.class);
        }
        
        // Set the table.
        field.setTable(descriptor.getPrimaryTable());

        // Return the field for the caller to handle.
        return field;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumnDefinition(String columnDefinition) {
        m_columnDefinition = columnDefinition;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDiscriminatorType(Enum descriminatorType) {
        m_discriminatorType = descriminatorType;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setLength(Integer length) {
        m_length = length;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
}
