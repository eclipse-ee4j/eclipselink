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

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.ReadObjectTest;

public class BatchReadingWithAggregateCollectionMapping extends ReadObjectTest {

    Class cls;
    // Must be Agent or Builder
    public BatchReadingWithAggregateCollectionMapping(Class cls) {
        super();
        this.cls = cls;
        setName(getName() + AgentBuilderHelper.getNameInBrackets(cls));
    }

    // Must be Agent or Builder
    public BatchReadingWithAggregateCollectionMapping(Object originalObject) {
        super(originalObject);
    }

    public void reset() {

    }

    public void setup() {
    }

    public void test() {
        if(cls == null) {
            cls = originalObject.getClass();
        }
        ReadAllQuery query = new ReadAllQuery(cls);
        query.addBatchReadAttribute("houses");
        query.setSelectionCriteria(new ExpressionBuilder().get("lastName").equal("Jordan"));
        List objects = (List)getSession().executeQuery(query);
        Object object = objects.get(0);
        List houses = AgentBuilderHelper.getHouses(object);
    }

    public void verify() {
    }
}

