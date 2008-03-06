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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * A basic version accessor.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class VersionAccessor extends BasicAccessor {
    /**
     * INTERNAL:
     */
    public VersionAccessor() {}
    
    /**
     * INTERNAL:
     */
    public VersionAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
    }

    /**
     * INTERNAL:
     * Returns true if the given class is a valid timestamp locking type.
     */
    protected boolean isValidTimestampVersionLockingType(Class cls) {
        return cls.equals(java.sql.Timestamp.class);
    }
     
    /**
     * INTERNAL:
     * Returns true if the given class is a valid version locking type.
     */
    protected boolean isValidVersionLockingType(Class cls) {
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
    public void process() {
        // This will initialize the m_field variable.
        super.process();
    	
        // Process an @Version or version element if there is one.
        if (getDescriptor().usesOptimisticLocking()) {
            // Ignore the version locking if it is already set.
            getLogger().logWarningMessage(MetadataLogger.IGNORE_VERSION_LOCKING, this);
        } else {
            Class lockType = getRawClass();
            getField().setType(lockType);

            if (isValidVersionLockingType(lockType) || isValidTimestampVersionLockingType(lockType)) {
                VersionLockingPolicy policy = isValidVersionLockingType(lockType) ? new VersionLockingPolicy(getField()) : new TimestampLockingPolicy(getField());  
                policy.storeInObject();
                policy.setIsCascaded(getDescriptor().usesCascadedOptimisticLocking());
                getDescriptor().setOptimisticLockingPolicy(policy);
            } else {
                throw ValidationException.invalidTypeForVersionAttribute(getAttributeName(), lockType, getJavaClass());
            }
        }
    }
}
