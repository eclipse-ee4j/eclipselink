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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.UnidirectionalOneToManyMapping;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.DirectEntityU1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapValue;

import java.util.List;

public class TestReadDirectEntityU1MMapMapping extends TestCase {

    protected List holders = null;
    protected int fetchJoinRelationship = 0;
    protected int oldFetchJoinValue = 0;
    protected OneToManyMapping mapping = null;
    protected Expression holderExp;


    public TestReadDirectEntityU1MMapMapping(){
        super();
    }

    public TestReadDirectEntityU1MMapMapping(int fetchJoin){
        this();
        fetchJoinRelationship = fetchJoin;
        setName("TestReadDirectEntityU1MMapMapping fetchJoin = " + fetchJoin);
    }

    @Override
    public void setup(){
        mapping = (UnidirectionalOneToManyMapping)getSession().getProject().getDescriptor(DirectEntityU1MMapHolder.class).getMappingForAttributeName("directToEntityMap");
        oldFetchJoinValue = mapping.getJoinFetch();
        mapping.setJoinFetch(fetchJoinRelationship);
        getSession().getProject().getDescriptor(DirectEntityU1MMapHolder.class).reInitializeJoinedAttributes();

        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectEntityU1MMapHolder holder = new DirectEntityU1MMapHolder();
        EntityMapValue value = new EntityMapValue();
        value.setId(1);
        holder.addDirectToEntityMapItem(11, value);


        EntityMapValue value2 = new EntityMapValue();
        value2.setId(2);
        holder.addDirectToEntityMapItem(22, value2);
        uow.registerObject(holder);
        uow.registerObject(value);
        uow.registerObject(value2);
        uow.commit();
        holderExp = (new ExpressionBuilder()).get("id").equal(holder.getId());
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void test(){
        holders = getSession().readAllObjects(DirectEntityU1MMapHolder.class, holderExp);
    }

    @Override
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        DirectEntityU1MMapHolder holder = (DirectEntityU1MMapHolder)holders.get(0);

        if (!((IndirectMap)holder.getDirectToEntityMap()).getValueHolder().isInstantiated() && fetchJoinRelationship > 0){
            throw new TestErrorException("Relationship was not properly joined.");
        }

        if (holder.getDirectToEntityMap().size() != 2){
            throw new TestErrorException("Incorrect Number of MapEntityValues was read.");
        }
        EntityMapValue value = (EntityMapValue)holder.getDirectToEntityMap().get(11);
        if (value.getId() != 1){
            throw new TestErrorException("Incorrect MapEntityValues was read.");
        }
    }

    @Override
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        for (Object object : holders) {
            DirectEntityU1MMapHolder holder = (DirectEntityU1MMapHolder) object;
            for (Object o : holder.getDirectToEntityMap().keySet()) {
                uow.deleteObject(holder.getDirectToEntityMap().get(o));
            }
        }
        uow.deleteAllObjects(holders);
        uow.commit();
        if (!verifyDelete(holders.get(0))){
            throw new TestErrorException("Delete was unsuccessful.");
        }
        mapping.setJoinFetch(oldFetchJoinValue);
    }

}
