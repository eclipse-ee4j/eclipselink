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
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;

public class AggregateCollectionClearTest extends TransactionalTestCase {
    public Object object;
    public Class cls;

    public AggregateCollectionClearTest(Class cls) {
        this.cls = cls;
        setName(getName() + AgentBuilderHelper.getNameInBrackets(cls));
        setDescription("Verifies that when aggregate collections get cleared that the changes are merged.  CR 3013");
    }

    public void setup() {
        super.setup();
        java.util.Vector objects = getSession().readAllObjects(cls);
        int index = 0;
        while ((index < objects.size()) && ((object == null) || (AgentBuilderHelper.getCustomers(object).get(0) == null))) {
            object = objects.get(index);
            ++index;
        }
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Object objectClone = uow.readObject(object);
        AgentBuilderHelper.getCustomers(objectClone).clear();
        uow.commit();
    }

    public void verify() {
        Object object1 = getSession().readObject(object);
        if (!AgentBuilderHelper.getCustomers(object1).isEmpty()) {
            throw new TestErrorException("Did not merge change");
        }
    }

    public void reset() {
        super.reset();
    }
}
