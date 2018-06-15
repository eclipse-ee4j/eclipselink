/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.sessions.UnitOfWork;

/**
* <p><b>INTERNAL:</b>An InsertOperation is an executable representation of an <tt>INSERT</tt>
* operation on the database.
*
* @author Mike Norman - michael.norman@oracle.com
* @since EclipseLink 1.x
*/
public class InsertOperation extends Operation {

    /**
     * Execute <tt>INSERT</tt> operation on the database
     * @param   xrService parent <code>XRService</code> that owns this <code>Operation</code>
     * @param   invocation contains runtime argument values to be bound to the list of
     *          {@link Parameter}'s.
     * @return  result - <code>null</code> as the underlying <tt>INSERT</tt> operation on
     *          the database does not return a value.
     *
     * @see  Operation
     */
    @Override
    public Object invoke(XRServiceAdapter xrService, Invocation invocation) {
        Object instance = invocation.getParameters().toArray()[0];
        UnitOfWork uow = xrService.getORSession().acquireUnitOfWork();
        uow.registerNewObject(instance);
        uow.commit();
        return null;
    }
}
