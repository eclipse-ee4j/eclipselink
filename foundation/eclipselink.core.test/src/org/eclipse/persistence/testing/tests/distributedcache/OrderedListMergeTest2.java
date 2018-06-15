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
//     08/15/2008-1.0.1 Chris Delahunt
//       - 237545: List attribute types on OneToMany using @OrderBy does not work with attribute change tracking
package org.eclipse.persistence.testing.tests.distributedcache;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.employee.domain.Child;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

//Removes from the list of employee's children and verifies the order stays the same after synch
public class OrderedListMergeTest2 extends OrderedListMergeTest {

    Child childtoremove;

    protected void modifyCollection(UnitOfWork uow, Object objectToModify) {
        childtoremove = (Child)((Employee)objectToModify).children.remove(1);
        initialNumProjs = initialNumProjs-2;

    }
}
