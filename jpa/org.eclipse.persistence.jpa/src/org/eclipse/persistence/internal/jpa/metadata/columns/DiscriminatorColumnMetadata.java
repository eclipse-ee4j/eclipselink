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
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     04/05/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 3)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import javax.persistence.DiscriminatorType;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to process a JPA discriminator column into an EclipseLink database field.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class DiscriminatorColumnMetadata extends MetadataColumn {
    public static final String NAME_DEFAULT = "DTYPE";
    
    private Integer m_length;
    private String m_discriminatorType;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public DiscriminatorColumnMetadata() {
        super("<discriminator-column>");
    }
    
    /**
     * INTERNAL:
     * Used for defaulting.
     */
    public DiscriminatorColumnMetadata(MetadataAccessor accessor) {
        super(accessor);
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public DiscriminatorColumnMetadata(MetadataAnnotation discriminatorColumn, MetadataAccessor accessor) {
        super(discriminatorColumn, accessor);
        
        if (discriminatorColumn != null) {
            m_length = (Integer) discriminatorColumn.getAttribute("length");
            m_discriminatorType = (String) discriminatorColumn.getAttribute("discriminatorType");    
        }
    }
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected DiscriminatorColumnMetadata(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof DiscriminatorColumnMetadata) {
            DiscriminatorColumnMetadata discriminatorColumn = (DiscriminatorColumnMetadata) objectToCompare;
            
            if (! valuesMatch(m_length, discriminatorColumn.getLength())) {
                return false;
            }
            
            return valuesMatch(m_discriminatorType, discriminatorColumn.getDiscriminatorType());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDiscriminatorType() {
        return m_discriminatorType;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public DatabaseField getDatabaseField() {
        // Initialize the DatabaseField with values and defaults.
        DatabaseField field = super.getDatabaseField();
        
        field.setLength(MetadataHelper.getValue(m_length, 31));
        
        if (m_discriminatorType == null || m_discriminatorType.equals(DiscriminatorType.STRING.name())) {
            field.setType(String.class);
        } else if (m_discriminatorType.equals(DiscriminatorType.CHAR.name())) {
            field.setType(Character.class);
        } else {
            // Through annotation and XML validation, it must be 
            // DiscriminatorType.INTEGER and can't be anything else. 
            field.setType(Integer.class);
        }
        
        return field;
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
     * Process a discriminator column metadata into an EclipseLink 
     * DatabaseField. What is done with that field is up to the caller 
     * of this method.
     */
    public DatabaseField process(MetadataDescriptor descriptor, String loggingCtx) {
        DatabaseField field = getDatabaseField();
        
        // Set the field name. This will take care of any any delimited 
        // identifiers and casing defaults etc.
        setFieldName(field, NAME_DEFAULT, loggingCtx);

        // Set the table.
        field.setTable(descriptor.getPrimaryTable());

        // Return the field for the caller to handle.
        return field;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDiscriminatorType(String descriminatorType) {
        m_discriminatorType = descriminatorType;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setLength(Integer length) {
        m_length = length;
    }
}
