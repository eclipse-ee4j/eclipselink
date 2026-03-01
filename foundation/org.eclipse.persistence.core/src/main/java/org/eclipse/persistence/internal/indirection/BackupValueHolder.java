/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.indirection;

import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Used as the backup value holder in the unit of work for transparent indirection.
 * This ensure that a reference to the original value holder is held in case the
 * transparent collection or proxy is replace without first instantiating the original.
 *
 * @since 10.1.3
 * @author James Sutherland
 */
public class BackupValueHolder<T> extends DatabaseValueHolder<T> {

    /** Stores the original uow clone's value holder. */
    protected ValueHolderInterface<T> unitOfWorkValueHolder;

    public BackupValueHolder(ValueHolderInterface<T> unitOfWorkValueHolder) {
        this.unitOfWorkValueHolder = unitOfWorkValueHolder;
    }

    @Override
    public boolean isPessimisticLockingValueHolder() {
        return ((DatabaseValueHolder<T>)this.unitOfWorkValueHolder).isPessimisticLockingValueHolder();
    }

    @Override
    public T instantiateForUnitOfWorkValueHolder(UnitOfWorkValueHolder<T> unitOfWorkValueHolder) {
        return ((DatabaseValueHolder<T>)this.unitOfWorkValueHolder).instantiateForUnitOfWorkValueHolder(unitOfWorkValueHolder);
    }

    @Override
    public AbstractRecord getRow() {
        return ((DatabaseValueHolder<T>)this.unitOfWorkValueHolder).getRow();
    }

    @Override
    public AbstractSession getSession() {
        return ((DatabaseValueHolder<T>)this.unitOfWorkValueHolder).getSession();
    }

    /**
     * If the original value holder was not instantiated,
     * then first instantiate it to obtain the backup value.
     */
    @Override
    public T instantiate() {
        // Ensures instantiation of the original, and setting of this back value holder's value.
        return this.unitOfWorkValueHolder.getValue();
    }

    /**
     * Return the original uow clone's value holder.
     */
    public ValueHolderInterface<?> getUnitOfWorkValueHolder() {
        return unitOfWorkValueHolder;
    }
}
