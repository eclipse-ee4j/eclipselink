/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Gordon Yorke - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.indirection;

import org.eclipse.persistence.indirection.ValueHolderInterface;

/**
 * WrappingValueHolder is an interface type that implementors use when they will be
 * wrapping another ValueHolder that has the original value.
 *
 * @see UnitOfWorkValueHolder, ProtectedValueHolder
 * @author    Gordon Yorke
 */
public interface WrappingValueHolder {

    /**
     * Returns the valueholder that is wrapped by this ValueHolder
     */
    public ValueHolderInterface getWrappedValueHolder();


}
