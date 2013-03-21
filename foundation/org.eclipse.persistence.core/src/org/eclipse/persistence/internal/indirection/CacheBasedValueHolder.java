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
 *     Gordon Yorke - initial API and implementation
 ******************************************************************************/  

package org.eclipse.persistence.internal.indirection;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * <p>
 * <b>Purpose:</b> In certain cases the contents of a relationship may be
 * retrievable from a cache. This ValueHolder instance provides the mechanism to
 * store a cached relationship and to load that relationship from a cache. This
 * functionality requires that the persistent identities of the targets can be
 * collected as database type foreign key queries are unavailable.
 * 
 * @author gyorke
 * @since EclipseLink 1.1
 */
public class CacheBasedValueHolder extends DatabaseValueHolder {
    
    protected transient ForeignReferenceMapping mapping;
    protected Object[] references;
    /** Setting to force the instantiation of the Collection on modification */
    protected boolean shouldAllowInstantiationDeferral = true;

    public CacheBasedValueHolder(Object[] pks, AbstractRecord foreignKeys, AbstractSession session, ForeignReferenceMapping mapping){
        super();
        this.references = pks;
        this.mapping = mapping;
        this.session = session;
        this.row = foreignKeys;
    }
    
    public Object[] getCachedPKs(){
        return this.references;
    }
    

    protected Object instantiate() throws DatabaseException {
        return instantiate(this.session);
    }

    protected Object instantiate(AbstractSession localSession) throws DatabaseException {
        if (session == null){
            throw ValidationException.instantiatingValueholderWithNullSession();
        }
        return mapping.valueFromPKList(references, row, localSession);
    }

    /**
     * Triggers UnitOfWork valueholders directly without triggering the wrapped
     * valueholder (this).
     * <p>
     * When in transaction and/or for pessimistic locking the UnitOfWorkValueHolder
     * needs to be triggered directly without triggering the wrapped valueholder.
     * However only the wrapped valueholder knows how to trigger the indirection,
     * i.e. it may be a batchValueHolder, and it stores all the info like the row
     * and the query.
     * Note: This method is not thread-safe.  It must be used in a synchronized manner
     */
    public Object instantiateForUnitOfWorkValueHolder(UnitOfWorkValueHolder unitOfWorkValueHolder) {
        return instantiate(unitOfWorkValueHolder.getUnitOfWork());
    }

    @Override
    public boolean isPessimisticLockingValueHolder() {
        return false;
    }

    /**
     * Set if instantiation deferral on modification should be available.
     */
    public void setShouldAllowInstantiationDeferral(boolean shouldAllowInstantiationDeferral){
        this.shouldAllowInstantiationDeferral = shouldAllowInstantiationDeferral;
    }
    
    /**
     * INTERNAL:
     * Return if add/remove should trigger instantiation or avoid.
     * Current instantiation is avoided is using change tracking.
     */
    public boolean shouldAllowInstantiationDeferral() {
        return this.shouldAllowInstantiationDeferral;
    }

    

}
