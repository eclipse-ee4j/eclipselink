/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import javax.persistence.DiscriminatorType;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to process a JPA discriminator column into an EclipseLink database field.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class DiscriminatorColumnMetadata extends MetadataColumn {
    private Integer m_length;
    private String m_discriminatorType;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public DiscriminatorColumnMetadata() {
        super("<discriminator-column>");
    }
    
    /**
     * INTERNAL:
     */
    public DiscriminatorColumnMetadata(MetadataAnnotation discriminatorColumn, MetadataAccessibleObject accessibleObject) {
        super(discriminatorColumn, accessibleObject);
        
        if (discriminatorColumn != null) {
            m_length = (Integer) discriminatorColumn.getAttribute("length");
            m_discriminatorType = (String) discriminatorColumn.getAttribute("discriminatorType");    
        }
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
    public DatabaseField process(MetadataDescriptor descriptor, String annotatedElementName, String loggingCtx) {     
        DatabaseField field = getDatabaseField();
        
        boolean useDelimitedIdentifier = (descriptor.getProject() != null) ? descriptor.getProject().useDelimitedIdentifier() : false;
        
        // Process the name
        field.setName(MetadataHelper.getName(field.getName(), "DTYPE", loggingCtx, descriptor.getLogger(), annotatedElementName), Helper.getDefaultStartDatabaseDelimiter(), Helper.getDefaultEndDatabaseDelimiter());
        if (useDelimitedIdentifier){
            field.setUseDelimiters(useDelimitedIdentifier);
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
