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

import org.eclipse.persistence.annotations.OrderCorrection;
import org.eclipse.persistence.annotations.OrderCorrectionType;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.mappings.CollectionMapping;

/**
 * INTERNAL:
 * Object to process a JPA order column into an EclipseLink database field.
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
 * @since EclipseLink 1.2
 */
public class OrderColumnMetadata extends DirectColumnMetadata {
    private static final String _ORDER = "_ORDER";
    
    private String m_correctionType;    
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public OrderColumnMetadata() {
        super("<order-column>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public OrderColumnMetadata(MetadataAnnotation orderColumn, MetadataAccessor accessor) {
        super(orderColumn, accessor);
        
        if (accessor.isAnnotationPresent(OrderCorrection.class)) {
            m_correctionType = (String) accessor.getAnnotation(OrderCorrection.class).getAttribute("value");
        }
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
            
            // Set the field name. This will take care of any any delimited 
            // identifiers and casing defaults etc.
            setFieldName(orderField, mapping.getAttributeName() + _ORDER, MetadataLogger.ORDER_COLUMN);

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
