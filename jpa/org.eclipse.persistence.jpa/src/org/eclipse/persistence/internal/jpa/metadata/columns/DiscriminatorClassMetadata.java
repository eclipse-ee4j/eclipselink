/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import java.lang.annotation.Annotation;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;

/**
 * A discriminator class is used within a variable one to one mapping.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class DiscriminatorClassMetadata {
    private Class m_value;
    private String m_valueName;
    private String m_discriminator;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public DiscriminatorClassMetadata() {}
    
    /**
     * INTERNAL:
     */
    public DiscriminatorClassMetadata(Annotation discriminatorClass) {
        setDiscriminator((String) MetadataHelper.invokeMethod("discriminator", discriminatorClass));
        setValue((Class) MetadataHelper.invokeMethod("value", discriminatorClass));
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
    public Class getValue() {
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
    public void setValue(Class value) {
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

