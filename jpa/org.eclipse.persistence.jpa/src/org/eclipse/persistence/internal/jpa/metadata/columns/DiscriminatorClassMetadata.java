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
 *     03/26/2008-1.0M6 Guy Pelletier 
 *       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files   
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
    private MetadataClass m_value;
    private String m_valueName;
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
        setValue(getMetadataClass((String) discriminatorClass.getAttribute("value")));
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
    public MetadataClass getValue() {
        return m_value;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getValueName() {
        return m_valueName;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
    
        m_value = initXMLClassName(m_valueName);
    }
    
    /**
     * INTERNAL:
     * Process a discriminator class for the given variable one to one mapping.
     */
    public void process(VariableOneToOneMapping mapping) {
        if (mapping.getTypeIndicatorNameTranslation().containsValue(m_discriminator)) {
            throw ValidationException.multipleClassesForTheSameDiscriminator(m_discriminator, mapping.getAttributeName());
        }
        
        mapping.addClassNameIndicator(m_value.getName(), m_discriminator);
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
    public void setValue(MetadataClass value) {
        m_value = value;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setValueName(String valueName) {
        m_valueName = valueName;
    }    
}

