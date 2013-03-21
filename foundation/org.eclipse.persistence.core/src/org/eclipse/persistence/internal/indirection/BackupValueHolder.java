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
package org.eclipse.persistence.internal.indirection;

import org.eclipse.persistence.indirection.*;

/**
 * Used as the backup value holder in the unit of work for transparent indirection.
 * This ensure that a reference to the original value holder is held in case the
 * transparent collection or proxy is replace without first instantiating the original.
 *
 * @since 10.1.3
 * @author James Sutherland
 */
public class BackupValueHolder extends ValueHolder {

    /** Stores the original uow clone's value holder. */
    protected ValueHolderInterface unitOfWorkValueHolder;

    public BackupValueHolder(ValueHolderInterface unitOfWorkValueHolder) {
        this.unitOfWorkValueHolder = unitOfWorkValueHolder;
    }
    
    /**
     * If the original value holder was not instantiated,
     * then first instantiate it to obtain the backup value.
     */
    public Object getValue() {
        // Ensures instantiation of the original, and setting of this back value holder's value.
        getUnitOfWorkValueHolder().getValue();
        return value;
    }

    /**
     * Return the original uow clone's value holder.
     */
    public ValueHolderInterface getUnitOfWorkValueHolder() {
        return unitOfWorkValueHolder;
    }
}
