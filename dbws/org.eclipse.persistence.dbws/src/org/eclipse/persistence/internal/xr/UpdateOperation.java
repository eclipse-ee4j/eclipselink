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
 ******************************************************************************/

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.sessions.UnitOfWork;

public class UpdateOperation extends Operation {

    /**
     * Execute <tt>UPDATE</tt> operation on the database
     * @param   xrService parent <code>XRService</code> that owns this <code>Operation</code>
     * @param   invocation contains runtime argument values to be bound to the list of
     *          {@link Parameter}'s.
     * @return  result - <code>null</code> as the underlying <tt>UPDATE</tt> operation on
     *          the database does not return a value.
     *
     * @see  {@link Operation}
     */
    @Override
    public Object invoke(XRServiceAdapter xrService, Invocation invocation) {
        Object instance = invocation.getParameters().toArray()[0];
        UnitOfWork uow = xrService.getORSession().acquireUnitOfWork();
        // read the existing object into the uow
        uow.readObject(instance);
        // overwrite it
        uow.mergeClone(instance);
        uow.commit();
        return null;
    }
}
