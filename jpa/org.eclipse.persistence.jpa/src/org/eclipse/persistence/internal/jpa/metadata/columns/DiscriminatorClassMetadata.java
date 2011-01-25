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
 *     03/26/2008-1.0M6 Guy Pelletier 
 *       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     01/25/2011-2.3 Guy Pelletier 
 *       - 333913: @OrderBy and <order-by/> without arguments should order by primary
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.mappings.VariableOneToOneMapping;

/**
 * INTERNAL:
 * A discriminator class is used within a variable one to one mapping.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class DiscriminatorClassMetadata extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    private MetadataClass m_valueClass;
    private String m_value;
    private String m_discriminator;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public DiscriminatorClassMetadata() {
        super("<discriminator-class>");
    }
    
    /**
     * INTERNAL:
     */
    public DiscriminatorClassMetadata(MetadataAnnotation discriminatorClass, MetadataAccessibleObject accessibleObject) {
        super(discriminatorClass, accessibleObject);
        
        setDiscriminator((String) discriminatorClass.getAttribute("discriminator"));
        setValueClass(getMetadataClass((String) discriminatorClass.getAttribute("value")));
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof DiscriminatorClassMetadata) {
            DiscriminatorClassMetadata discriminatorClass = (DiscriminatorClassMetadata) objectToCompare;
            
            if (! valuesMatch(m_value, discriminatorClass.getValue())) {
                return false;
            }
            
            return valuesMatch(m_discriminator, discriminatorClass.getDiscriminator());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDiscriminator() {
        return m_discriminator;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getValueClass() {
        return m_valueClass;
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
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
    
        m_valueClass = initXMLClassName(m_value);
    }
    
    /**
     * INTERNAL:
     * Process a discriminator class for the given variable one to one mapping.
     */
    public void process(VariableOneToOneMapping mapping) {
        if (mapping.getTypeIndicatorNameTranslation().containsValue(m_discriminator)) {
            throw ValidationException.multipleClassesForTheSameDiscriminator(m_discriminator, mapping.getAttributeName());
        }
        
        mapping.addClassNameIndicator(m_valueClass.getName(), m_discriminator);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDiscriminator(String discriminator) {
        m_discriminator= discriminator;
    }
    
    /**
     * INTERNAL:
     */
    public void setValueClass(MetadataClass value) {
        m_valueClass = value;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setValue(String valueName) {
        m_value = valueName;
    }    
}

