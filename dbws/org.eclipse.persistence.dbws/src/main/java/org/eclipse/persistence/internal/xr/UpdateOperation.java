/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;

public class UpdateOperation extends Operation {

    /**
     * Execute <code>UPDATE</code> operation on the database
     * @param   xrService parent <code>XRService</code> that owns this <code>Operation</code>
     * @param   invocation contains runtime argument values to be bound to the list of
     *          {@link Parameter}'s.
     * @return  result - <code>null</code> as the underlying <code>UPDATE</code> operation on
     *          the database does not return a value.
     *
     * @see  Operation
     */
    @Override
    public Object invoke(XRServiceAdapter xrService, Invocation invocation) {
        XRDynamicEntity instance = (XRDynamicEntity)invocation.getParameters().toArray()[0];
        UnitOfWork uow = xrService.getORSession().acquireUnitOfWork();
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSelectionObject(instance);
        query.setIsExecutionClone(true);
        FetchGroup simpleFetchGroup = new FetchGroup();
        for (String propertyName : instance.fetchPropertiesManager().getPropertyNames()) {
            if (instance.isSet(propertyName)) {
                simpleFetchGroup.addAttribute(propertyName);
            }
        }
        query.setFetchGroup(simpleFetchGroup);
        // read the existing object into the uow
        uow.executeQuery(query);
        // merge in only properties that are set
        uow.mergeClone(instance);
        uow.commit();
        return null;
    }
}
