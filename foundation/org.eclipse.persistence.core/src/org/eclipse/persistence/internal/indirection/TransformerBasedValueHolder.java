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

import java.lang.reflect.InvocationTargetException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.transformers.*;

/**
 *  @version $Header: TransformerBasedValueHolder.java 30-aug-2006.11:32:36 gyorke Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 *  This class is to replace the MethodBasedValueHolder for TransformationMappings
 *  Holds on to an AttributeTransformer and uses it to generate the attribute value when triggered.
 *  That Transformer may be a MethodBasedAttributeTransformer or a user defined implementation.
 *
 */
public class TransformerBasedValueHolder extends DatabaseValueHolder {

    /**
     * Stores the method to be executed. The method can take
     * one (the row) or two parameters (the row and session).
     */
    protected transient AttributeTransformer transformer;

    /**
     * Stores the object which owns this attribute.
     */
    protected transient Object object;

    /**
     * Initialize the method-based value holder.
     * @param theMethod The method that returns the object when executed.
     * @param object the Object which owns this attribute.
     * @param theRow The row representation of the object.
     * @param theSession The session to the database that stores the object.
     */
    public TransformerBasedValueHolder(AttributeTransformer theTransformer, Object theObject, AbstractRecord theRow, AbstractSession theSession) {
        super();
        row = theRow;
        session = theSession;

        // Make sure not to put a ClientSession or IsolatedClientSession in
        // the shared cache (indirectly).
        // Skip this if unitOfWork, for we use session.isUnitOfWork() to implement
        // isTransactionalValueholder(), saving us from needing a boolean instance variable.
        // If unitOfWork this safety measure is deferred until merge time with
        // releaseWrappedValuehHolder.
        // Note that if isolated session & query will return itself, which is safe
        // for if isolated this valueholder is not in the shared cache.
        if (!session.isUnitOfWork()) {
            this.session = session.getRootSession(null);
        }
        transformer = theTransformer;
        object = theObject;
    }

    /**
     * Return the method.
     */
    protected AttributeTransformer getTransformer() {
        return transformer;
    }

    /**
    * Return the receiver.
    */
    protected Object getObject() {
        return object;
    }

    /**
     * Instantiate the object by executing the method on the transformer.
     */
    protected Object instantiate() throws DescriptorException {
        return instantiate(getObject(), getSession());
    }

    protected Object instantiate(Object object, AbstractSession session) throws DescriptorException {
        try {
            return transformer.buildAttributeValue(getRow(), object, session);
        } catch (DescriptorException ex) {
            Throwable nestedException = ex.getInternalException();
            if (nestedException instanceof IllegalAccessException) {
                throw DescriptorException.illegalAccessWhileInstantiatingMethodBasedProxy(nestedException);
            } else if (nestedException instanceof IllegalArgumentException) {
                throw DescriptorException.illegalArgumentWhileInstantiatingMethodBasedProxy(nestedException);
            } else if (nestedException instanceof InvocationTargetException) {
                throw DescriptorException.targetInvocationWhileInstantiatingMethodBasedProxy(nestedException);
            } else {
                throw ex;
            }
        }
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
        return instantiate(getObject(), unitOfWorkValueHolder.getUnitOfWork());
    }

    /**
     * INTERNAL:
     * Answers if this valueholder is a pessimistic locking one.  Such valueholders
     * are special in that they can be triggered multiple times by different
     * UnitsOfWork.  Each time a lock query will be issued.  Hence even if
     * instantiated it may have to be instantiated again, and once instantiated
     * all fields can not be reset.
     * Note: This method is not thread-safe.  It must be used in a synchronized manner
     */
    public boolean isPessimisticLockingValueHolder() {
        // there is no way to tell, as a transformation mapping may have
        // a reference class or query to check, but by design there is no
        // way we can access at it.
        return false;
    }

    /**
     * Set the transformer.
     */
    protected void setTransformer(AttributeTransformer theTransformer) {
        transformer = theTransformer;
    }

    /**
     * Set the receiver.
     */
    protected void setObject(Object theObject) {
        object = theObject;
    }
}
