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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * Object to hold onto conversion values.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class ConversionValueMetadata extends ORMetadata {
    private String m_dataValue;
    private String m_objectValue;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ConversionValueMetadata() {
        super("<conversion-value");
    }
    
    /**
     * INTERNAL:
     */
    public ConversionValueMetadata(MetadataAnnotation conversionValue, MetadataAccessibleObject accessibleObject) {
        super(conversionValue, accessibleObject);
        
        m_dataValue = (String) conversionValue.getAttribute("dataValue"); 
        m_objectValue = (String) conversionValue.getAttribute("objectValue");  
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ConversionValueMetadata) {
            ConversionValueMetadata conversionValue = (ConversionValueMetadata) objectToCompare;
            
            if (! valuesMatch(m_dataValue, conversionValue.getDataValue())) {
                return false;
            }
            
            return valuesMatch(m_objectValue, conversionValue.getObjectValue());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDataValue() {
        return m_dataValue;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getObjectValue() {
        return m_objectValue;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDataValue(String dataValue) {
        m_dataValue = dataValue;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setObjectValue(String objectValue) {
        m_objectValue = objectValue;
    }
}
