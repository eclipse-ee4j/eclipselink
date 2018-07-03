/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Gordon Yorke - initial API and implementation
package org.eclipse.persistence.internal.indirection;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;


/**
 * <p><b>Purpose</b>: provides a value holder type that can be used in Protected instances of Entities.
 * Similar to UnitOfWork Value Holder it wraps the original cache version.
 *
 * @author Gordon Yorke
 * @since EclipseLink 2.2
 */
public class ProtectedValueHolder extends DatabaseValueHolder implements WrappingValueHolder{

    protected transient ValueHolderInterface wrappedValueHolder;
    protected transient DatabaseMapping mapping;

    public ProtectedValueHolder(ValueHolderInterface attributeValue, DatabaseMapping mapping, AbstractSession cloningSession) {
        this.wrappedValueHolder = attributeValue;
        this.mapping = mapping;
        this.session = cloningSession;
    }

    @Override
    protected Object instantiate() throws DatabaseException {
        if (this.session == null){
            throw ValidationException.instantiatingValueholderWithNullSession();
        }
        //no need for original or cachekey here as the relationship must be cacheable and present in
        // wrapped valueholder or the ProtectedValueHolder would not have been created.
        Integer refreshCascade = null;
        if (wrappedValueHolder instanceof QueryBasedValueHolder){
            refreshCascade = ((QueryBasedValueHolder)getWrappedValueHolder()).getRefreshCascadePolicy();
        }
        Object clone = mapping.buildCloneForPartObject(this.wrappedValueHolder.getValue(),null, null, null, this.session, refreshCascade, true, true);
        // Bug 414801
        if (wrappedValueHolder.isInstantiated() && refreshCascade != null) {
            ((QueryBasedValueHolder)getWrappedValueHolder()).setRefreshCascadePolicy(null);
        }
        return clone;
    }

    @Override
    public Object instantiateForUnitOfWorkValueHolder(UnitOfWorkValueHolder unitOfWorkValueHolder) {
        throw ValidationException.operationNotSupported("instantiateForUnitOfWorkValueHolder");
    }

    @Override
    public boolean isPessimisticLockingValueHolder() {
        return false;
    }

    public ValueHolderInterface getWrappedValueHolder() {
        return wrappedValueHolder;
    }


}
