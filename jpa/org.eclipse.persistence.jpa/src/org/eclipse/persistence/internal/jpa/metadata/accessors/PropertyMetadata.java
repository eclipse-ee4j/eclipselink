/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     Andrei Ilitchev (Oracle), April 8, 2008 
 *        - New file introduced for bug 217168.
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     25/05/2012-2.4 Guy Pelletier  
 *       - 354678: Temp classloader is still being used during metadata processing
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * INTERNAL:
 * PropertyMetadata. Each mapping may be assigned user-defined properties.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Andrei Ilitchev
 * @since EclipseLink 1.0 
 */
public class PropertyMetadata extends ORMetadata {
    private String m_name;
    private String m_value;
    private MetadataClass m_valueType;
    private String m_valueTypeName;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public PropertyMetadata() {
        super("<property>");
    }
    
    /**
     * INTERNAL:
     */
    public PropertyMetadata(MetadataAnnotation property, MetadataAccessor accessor) {
        super(property, accessor);
        
        m_name = (String) property.getAttributeString("name");
        m_value = (String) property.getAttributeString("value");
        m_valueType = getMetadataClass((String) property.getAttributeClass("valueType", String.class));
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof PropertyMetadata) {
            PropertyMetadata property = (PropertyMetadata) objectToCompare;
            
            if (! valuesMatch(m_name, property.getName())) {
                return false;
            }
            
            if (! valuesMatch(m_value, property.getValue())) {
                return false;
            }
            
            return valuesMatch(m_valueTypeName, property.getValueTypeName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * To satisfy the abstract getIdentifier() method from ORMetadata.
     */
    @Override
    public String getIdentifier() {
        return getName();
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
     * Used for OX mapping.
     */
    public String getValue() {
        return m_value;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getValueType() {
        return m_valueType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getValueTypeName() {
        return m_valueTypeName;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        if (m_valueTypeName != null) {
            m_valueType = initXMLClassName(m_valueTypeName);
        } else {
            m_valueType = getMetadataClass(String.class);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setValue(String value) {
        m_value = value;
    }
    
    /**
     * INTERNAL:
     */
    public void setValueType(MetadataClass valueType) {
        m_valueType = valueType;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setValueTypeName(String valueTypeName) {
        m_valueTypeName = valueTypeName;
    }
}
