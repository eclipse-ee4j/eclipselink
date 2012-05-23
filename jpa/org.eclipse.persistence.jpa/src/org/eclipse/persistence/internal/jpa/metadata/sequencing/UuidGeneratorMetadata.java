/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.sequencing;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.sequencing.UUIDSequence;

/**
 * A wrapper class to the @UuidGenerator for its metadata values.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
public class UuidGeneratorMetadata extends ORMetadata {
    private String m_name;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public UuidGeneratorMetadata() {
        super("<uuid-generator>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public UuidGeneratorMetadata(MetadataAnnotation uuidGenerator, MetadataAccessor accessor) {
        super(uuidGenerator, accessor);
        m_name = (String) uuidGenerator.getAttributeString("name");
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof UuidGeneratorMetadata) {
            return true;
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
     */
    public UUIDSequence process(MetadataLogger logger) {
        UUIDSequence sequence = new UUIDSequence();        
        return sequence;
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
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + m_name + "]";
    }
}
