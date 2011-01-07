/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/08/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.additionalcriteria;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * Object to hold onto additional criteria metadata.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.2
 */
public class AdditionalCriteriaMetadata extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.
    
    protected String m_criteria;

    /**
     * INTERNAL:
     */
    public AdditionalCriteriaMetadata() {
        super("<additional-criteria>");
    }
    
    /**
     * INTERNAL:
     */
    public AdditionalCriteriaMetadata(MetadataAnnotation additionalCriteria, MetadataAccessibleObject accessibleObject) {
        super(additionalCriteria, accessibleObject);
        
        m_criteria = (String) additionalCriteria.getAttribute("value");
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof AdditionalCriteriaMetadata) {
            AdditionalCriteriaMetadata additionalCriteria = (AdditionalCriteriaMetadata) objectToCompare;
            return valuesMatch(m_criteria, additionalCriteria.getCriteria());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCriteria() {
        return m_criteria; 
    }
    
    /**
     * INTERNAL:
     * The unique identifier of additional criteria is the criteria itself.
     */
    @Override
    public String getIdentifier() {
        return m_criteria;
    }
    
    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor) {
        // Set the criteria (jpql fragment) on the descriptors query event 
        // manager. The JPQL fragment will be parsed during descriptor post
        // initialization.
        descriptor.getClassDescriptor().getQueryManager().setAdditionalCriteria(m_criteria);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCriteria(String criteria) {
        m_criteria = criteria; 
    }
}
