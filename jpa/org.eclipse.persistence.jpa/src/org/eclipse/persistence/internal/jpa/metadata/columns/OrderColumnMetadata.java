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
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.annotations.OrderCorrectionType;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.mappings.CollectionMapping;

/**
 * INTERNAL:
 * Object to process a JPA order column into an EclipseLink database field.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public class OrderColumnMetadata extends DirectColumnMetadata {
    private static final String _ORDER = "_ORDER";
    
    private String m_correctionType;    
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public OrderColumnMetadata() {
        super("<order-column>");
    }
    
    /**
     * INTERNAL:
     */
    public OrderColumnMetadata(MetadataAnnotation orderColumn, MetadataAccessibleObject accessibleObject, String correctionType) {
        super(orderColumn, accessibleObject);
        m_correctionType = correctionType;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof OrderColumnMetadata) {
            OrderColumnMetadata orderColumn = (OrderColumnMetadata) objectToCompare;
            
            return valuesMatch(m_correctionType, orderColumn.getCorrectionType());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCorrectionType() {
        return m_correctionType;
    }
    
    /**
     * INTERNAL:
     */
    public void process(CollectionMapping mapping, MetadataDescriptor descriptor) {
        if (((MetadataAnnotatedElement) getAccessibleObject()).getRawClass(descriptor).isList()) {
            // Get the database field with metadata applied.
            DatabaseField orderField = getDatabaseField();
            
            boolean useDelimitedIdentifier = (descriptor.getProject() != null) ? descriptor.getProject().useDelimitedIdentifier() : false;

            // Process and default is necessary the name.
            orderField.setName(MetadataHelper.getName(getName(), mapping.getAttributeName() + _ORDER, MetadataLogger.ORDER_COLUMN, descriptor.getLogger(), getAccessibleObject().toString()), Helper.getDefaultStartDatabaseDelimiter(), Helper.getDefaultEndDatabaseDelimiter());
            if (useDelimitedIdentifier){
                orderField.setUseDelimiters(useDelimitedIdentifier);
            } else if (descriptor.getProject().getShouldForceFieldNamesToUpperCase() && !orderField.shouldUseDelimiters()) {
                //done directly as this field's name should be in uppercase.
                orderField.setName(orderField.getName().toUpperCase());
            }

            // We don't set a table, the mapping will figure that out for us at runtime.

            // Set the oder field and validation mode on the mapping.
            mapping.setListOrderField(orderField);
            
            if(m_correctionType != null) {
                OrderCorrectionType[] values = OrderCorrectionType.values();
                for(int i=0; i < values.length; i++) {
                    if(values[i].name().equals(m_correctionType)) {
                        mapping.setOrderCorrectionType(values[i]);
                        break;
                    }
                }
            }
        } else {
            throw ValidationException.invalidAttributeTypeForOrderColumn(mapping.getAttributeName(), descriptor.getJavaClass());
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCorrectionType(String correctionType) {
        m_correctionType = correctionType;
    }
}
