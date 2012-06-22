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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to hold onto common attribute/association override metadata.
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
 * @since EclipseLink 1.0
 */
public abstract class OverrideMetadata extends ORMetadata {
    private String m_name;
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    protected OverrideMetadata(MetadataAnnotation annotation, MetadataAccessor accessor) {
        super(annotation, accessor);
        
        m_name = (String) annotation.getAttribute("name");
    }
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected OverrideMetadata(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof OverrideMetadata) {
            return valuesMatch(m_name, ((OverrideMetadata) objectToCompare).getName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * To satisfy the MetadataMergeable interface for subclasses and provide a 
     * means of uniquely identifying objects.
     */
    public String getIdentifier() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     */
    public abstract String getIgnoreMappedSuperclassContext();
    
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
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * INTERNAL:
     * Method to determine if this ORMetadata should override another. Assumes
     * all ORMetadata have correctly implemented their equals method and will
     * return their correct logging context for any ignore messages. This is 
     * called on class level override metadata (and not those from an embedded
     * mapping).
     */
    public boolean shouldOverride(OverrideMetadata existing, MetadataLogger logger, String descriptorClass) {
        if (existing == null) {
            // There is no existing, no override occurs, just use it!
            return true;
        } else if (existing.getLocation().equals(getLocation())) {
            // Both were loaded from the same class, check if we need an 
            // override.
            return shouldOverride(existing);
        } else {
            // We already have an attribute override specified and the 
            // java class names are different. We must be processing
            // a mapped superclass therefore, ignore and log a message.
            logger.logConfigMessage(getIgnoreMappedSuperclassContext(), getName(), getLocation(), descriptorClass);
            return false;
        }
    }
}
