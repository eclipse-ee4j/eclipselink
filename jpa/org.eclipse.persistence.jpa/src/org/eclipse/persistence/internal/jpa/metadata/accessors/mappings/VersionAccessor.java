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
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

/**
 * INTERNAL:
 * A basic version accessor.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class VersionAccessor extends BasicAccessor {
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public VersionAccessor() {
        super("<version>");
    }
    
    /**
     * INTERNAL:
     */
    public VersionAccessor(MetadataAnnotation version, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(version, accessibleObject, classAccessor);
    }

    /**
     * INTERNAL:
     * Returns true if the given class is a valid timestamp locking type.
     */
    protected boolean isValidTimestampVersionLockingType(MetadataClass cls) {
        return cls.equals(java.sql.Timestamp.class);
    }
     
    /**
     * INTERNAL:
     * Returns true if the given class is a valid version locking type.
     */
    protected boolean isValidVersionLockingType(MetadataClass cls) {
        return (cls.equals(int.class) ||
                cls.equals(Integer.class) ||
                cls.equals(short.class) ||
                cls.equals(Short.class) ||
                cls.equals(long.class) ||
                cls.equals(Long.class));
    }
    
    /**
     * INTERNAL:
     * Process a version accessor.
     */
    @Override
    public void process() {
        // This will initialize the m_field variable. Accessible through getField().
        super.process();
        
        // Process an @Version or version element if there is one.
        if (getDescriptor().usesOptimisticLocking()) {
            // Ignore the version locking if it is already set.
            getLogger().logConfigMessage(MetadataLogger.IGNORE_VERSION_LOCKING, this);
        } else {
            MetadataClass lockType = getRawClass();
            getField().setType(getJavaClass(lockType));

            if (isValidVersionLockingType(lockType) || isValidTimestampVersionLockingType(lockType)) {
                for (MetadataDescriptor owningDescriptor : getOwningDescriptors()) {
                    VersionLockingPolicy policy = isValidVersionLockingType(lockType) ? new VersionLockingPolicy(getField()) : new TimestampLockingPolicy(getField());  
                    policy.storeInObject();
                    policy.setIsCascaded(getDescriptor().usesCascadedOptimisticLocking());
                    owningDescriptor.setOptimisticLockingPolicy(policy);
                }
            } else {
                throw ValidationException.invalidTypeForVersionAttribute(getAttributeName(), lockType, getJavaClass());
            }
        }
    }
}
