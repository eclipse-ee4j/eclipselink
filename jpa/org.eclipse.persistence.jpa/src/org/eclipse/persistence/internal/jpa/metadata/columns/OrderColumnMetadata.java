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
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
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
 * @since EclipseLink 2.0
 */
public class OrderColumnMetadata extends DirectColumnMetadata {
    private static final String _ORDER = "_ORDER";
    
    private String m_validationMode;    
    
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
    public OrderColumnMetadata(MetadataAnnotation orderColumn, MetadataAccessibleObject accessibleObject) {
        super(orderColumn, accessibleObject);
        
        // TODO: Once available, set the order column validation mode.
        //if (isAnnotationPresent(OrderColumnValidation.class)) {
          //  m_orderValidationMode = (String) getAnnotation(OrderColumnValidation.class).getAttribute("value");
        //}
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof OrderColumnMetadata) {
            OrderColumnMetadata orderColumn = (OrderColumnMetadata) objectToCompare;
            
            return valuesMatch(m_validationMode, orderColumn.getValidationMode());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getValidationMode() {
        return m_validationMode;
    }
    
    /**
     * INTERNAL:
     */
    public void process(CollectionMapping mapping, MetadataDescriptor descriptor) {
        if (((MetadataAnnotatedElement) getAccessibleObject()).getRawClass(descriptor).isList()) {
            // Get the database field with metadata applied.
            DatabaseField orderField = getDatabaseField();
            
            // Process and default is necessary the name.
            orderField.setName(MetadataHelper.getName(getName(), mapping.getAttributeName() + _ORDER, MetadataLogger.ORDER_COLUMN, descriptor.getLogger(), getAccessibleObject().toString()));
            
            // We don't set a table, the mapping will figure that out for us at runtime.
    
            // Set the oder field and validation mode on the mapping.
            mapping.setListOrderField(orderField);
            
            // TODO: Once available we will set the validation mode.
            //if (m_validationMode == null || m_validationMode.equals(OrderColumnValidationMode.NONE.name())) {
              //  mapping.setListOrderFieldValidationMode(OrderedListContainerPolicy.OrderValidationMode.NONE);
            //} else if (m_validationMode.equals(OrderColumnValidationMode.CORRECTION.name())) {
              //  mapping.setListOrderFieldValidationMode(OrderedListContainerPolicy.OrderValidationMode.CORRECTION);
            //} else if (m_validationMode.equals(OrderColumnValidationMode.EXCEPTION.name())) {
              //  mapping.setListOrderFieldValidationMode(OrderedListContainerPolicy.OrderValidationMode.EXCEPTION);
            //}
        } else {
            throw ValidationException.invalidAttributeTypeForOrderColumn(mapping.getAttributeName(), descriptor.getJavaClass());
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setValidationMode(String validationMode) {
        m_validationMode = validationMode;
    }
}
