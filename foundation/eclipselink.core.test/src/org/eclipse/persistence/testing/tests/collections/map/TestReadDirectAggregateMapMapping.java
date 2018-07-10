/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.AggregateMapValue;
import org.eclipse.persistence.testing.models.collections.map.DirectAggregateMapHolder;

public class TestReadDirectAggregateMapMapping extends TestCase {

    protected List holders = null;
    protected int fetchJoinRelationship = 0;
    protected int oldFetchJoinValue = 0;
    protected AggregateCollectionMapping mapping = null;
    protected Expression holderExp;

    public TestReadDirectAggregateMapMapping(){
        super();
    }

    public TestReadDirectAggregateMapMapping(int fetchJoin){
        this();
        fetchJoinRelationship = fetchJoin;
        setName("TestReadDirectAggregateMapMapping fetchJoin = " + fetchJoin);
    }

    public void setup(){
        mapping = (AggregateCollectionMapping)getSession().getProject().getDescriptor(DirectAggregateMapHolder.class).getMappingForAttributeName("directToAggregateMap");
        oldFetchJoinValue = mapping.getJoinFetch();
        mapping.setJoinFetch(fetchJoinRelationship);
        getSession().getProject().getDescriptor(DirectAggregateMapHolder.class).reInitializeJoinedAttributes();

        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectAggregateMapHolder holder = new DirectAggregateMapHolder();
        AggregateMapValue value = new AggregateMapValue();
        value.setValue(1);
        holder.addDirectToAggregateMapItem(new Integer(1), value);
        value = new AggregateMapValue();
        value.setValue(2);
        holder.addDirectToAggregateMapItem(new Integer(2), value);
        uow.registerObject(holder);
        uow.commit();
        holderExp = (new ExpressionBuilder()).get("id").equal(holder.getId());
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test(){
        holders = getSession().readAllObjects(DirectAggregateMapHolder.class, holderExp);
    }

    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        DirectAggregateMapHolder holder = (DirectAggregateMapHolder)holders.get(0);

        if (!((IndirectMap)holder.getDirectToAggregateMap()).getValueHolder().isInstantiated() && fetchJoinRelationship >0){
            throw new TestErrorException("Relationship was not properly joined.");
        }
        if (holder.getDirectToAggregateMap().size() != 2){
            throw new TestErrorException("Incorrect Number of Map values was read.");
        }
        AggregateMapValue value = (AggregateMapValue)holder.getDirectToAggregateMap().get(new Integer(1));
        if (value.getValue() != 1){
            throw new TestErrorException("Incorrect map value was read.");
        }
    }

    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteAllObjects(holders);
        uow.commit();
        if (!verifyDelete(holders.get(0))){
            throw new TestErrorException("Delete was unsuccessful.");
        }
        mapping.setJoinFetch(oldFetchJoinValue);
    }

}

