/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial implementation
package org.eclipse.persistence.testing.tests.collections.map;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapValue;
import org.eclipse.persistence.testing.models.collections.map.DirectAggregateMapHolder;

public class TestUpdateDirectAggregateMapMapping extends TestReadDirectAggregateMapMapping {

    protected DirectAggregateMapHolder changedHolder = null;

    @Override
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(DirectAggregateMapHolder.class, holderExp);
        changedHolder = (DirectAggregateMapHolder)holders.get(0);
        changedHolder.removeDirectToAggregateMapItem(1);
        AggregateMapValue mapValue = new AggregateMapValue();
        mapValue.setValue(3);
        changedHolder.addDirectToAggregateMapItem(3, mapValue);
        uow.commit();
        Object holderForComparison = uow.readObject(changedHolder);
        if (!compareObjects(changedHolder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }
    }

    @Override
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Object initialHolder = holders.get(0);
        holders = getSession().readAllObjects(DirectAggregateMapHolder.class, holderExp);
        DirectAggregateMapHolder holder = (DirectAggregateMapHolder)holders.get(0);
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        if (holder.getDirectToAggregateMap().containsKey(1)){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        AggregateMapValue value = (AggregateMapValue)holder.getDirectToAggregateMap().get(3);
        if (value.getValue() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
    }

}
