/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.descriptors;

import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * <p> <b>Description</b>: This policy is used to configure bean level pessimistic locking feature.
 * It is set on the CMPPolicy instance of the ClassDescriptor
 *
 * Note that bean is not pessimistic locked in the following scenarios:
 * <ul>
 * <li> No presence of a JTA transaction
 * <li> The current transaction is created and started by the Container for the invoking entity bean's method only. (i.e. invoke a business method without a client transaction)
 * <li> The bean has already been pessimistic locked in the current transaction
 * <li> Execution of ejbSelect
 * <li> Traversing relationship does not lock the returned result.
 * </ul>
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Configure locking modes of WAIT or NO_WAIT
 * <li> Provide utility to configure an ObjectLevelReadQuery with pessimistic locking based on this policy.
 * </ul>
 *
 * @see org.eclipse.persistence.descriptors.CMPPolicy
 *
 * @since TopLink 10.1.3
 */
public class PessimisticLockingPolicy implements Cloneable, java.io.Serializable {
    protected short lockingMode;

    public PessimisticLockingPolicy() {
        lockingMode = ObjectLevelReadQuery.LOCK;
    }

    /**
     * PUBLIC:
     * Return locking mode.  Default locking mode is ObjectLevelReadQuery.LOCK.
     * @return short locking mode value of ObjectLevelReadQuery.LOCK or ObjectLevelReadQuery.LOCK_NOWAIT
     */
    public short getLockingMode() {
        return lockingMode;
    }

    /**
     * PUBLIC:
     * Set locking mode.  If the mode is not a valid value, the locking mode is unchanged.
     * @param short mode must be value of ObjectLevelReadQuery.LOCK or ObjectLevelReadQuery.LOCK_NOWAIT
     */
    public void setLockingMode(short mode) {
        if ((mode == ObjectLevelReadQuery.LOCK) || (mode == ObjectLevelReadQuery.LOCK_NOWAIT)) {
            lockingMode = mode;
        } else {
            throw ValidationException.invalidMethodArguments();
        }
    }

    /**
     * INTERNAL:
     * Clone the policy
     */
    public Object clone() {
        PessimisticLockingPolicy clone = new PessimisticLockingPolicy();
        clone.setLockingMode(this.lockingMode);
        return clone;
    }
}
