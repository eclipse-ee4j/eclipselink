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
 *     Guy Pelletier (Oracle), February 28, 2007 
 *        - New file introduced for bug 217880.
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files     
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.inheritance;

import java.lang.annotation.Annotation;

import javax.persistence.InheritanceType;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * Object to represent an inheritance root defined in XML.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class InheritanceMetadata extends ORMetadata {
    private Enum m_strategy;
    
    /**
     * INTERNAL:
     */
    public InheritanceMetadata() {
        super("<inheritance>");
    }
    
    /**
     * INTERNAL:
     */
    public InheritanceMetadata(Annotation inheritance, MetadataAccessibleObject accessibleObject) {
        super(inheritance, accessibleObject);
        
        if (inheritance != null) {
            m_strategy = (Enum)MetadataHelper.invokeMethod("strategy", inheritance);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Enum getStrategy() {
        return m_strategy;
    }
    
    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor) {
        // Process the cache metadata.
        ClassDescriptor classDescriptor = descriptor.getClassDescriptor();
        
        if (m_strategy == null || m_strategy.equals(InheritanceType.SINGLE_TABLE)) {
            // TODO: Log a defaulting message if strategy is null.
            classDescriptor.getInheritancePolicy().setSingleTableStrategy();
        } else if (m_strategy.equals(InheritanceType.JOINED)) {
            classDescriptor.getInheritancePolicy().setJoinedStrategy();
        } else if (m_strategy.equals(InheritanceType.TABLE_PER_CLASS)) {
            throw ValidationException.tablePerClassInheritanceNotSupported(getLocation());
        }        
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setStrategy(Enum strategy) {
        m_strategy = strategy;
    }
}
    