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
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.EEOTMMapValue;
import org.eclipse.persistence.testing.models.collections.map.EntityEntity1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;

import java.util.List;

public class TestReadEntityEntity1MMapMapping extends TestCase {

    protected List holders = null;
    protected int fetchJoinRelationship = 0;
    protected int oldFetchJoinValue = 0;
    protected OneToManyMapping mapping = null;
    protected Expression holderExp;


    public TestReadEntityEntity1MMapMapping(){
        super();
    }

    public TestReadEntityEntity1MMapMapping(int fetchJoin){
        this();
        fetchJoinRelationship = fetchJoin;
        setName("TestReadEntityEntity1MMapMapping fetchJoin = " + fetchJoin);
    }

    @Override
    public void setup(){
        mapping = (OneToManyMapping)getSession().getProject().getDescriptor(EntityEntity1MMapHolder.class).getMappingForAttributeName("entityToEntityMap");
        oldFetchJoinValue = mapping.getJoinFetch();
        mapping.setJoinFetch(fetchJoinRelationship);
        getSession().getProject().getDescriptor(EntityEntity1MMapHolder.class).reInitializeJoinedAttributes();

        UnitOfWork uow = getSession().acquireUnitOfWork();
        EntityEntity1MMapHolder holder = new EntityEntity1MMapHolder();
        EEOTMMapValue value = new EEOTMMapValue();
        value.setId(1);
        value.getHolder().setValue(holder);
        EntityMapKey key = new EntityMapKey();
        key.setId(11);
        key.setData("data1");
        holder.addEntityToEntityMapItem(key, value);
        uow.registerObject(key);

        EEOTMMapValue value2 = new EEOTMMapValue();
        value2.setId(2);
        value2.getHolder().setValue(holder);
        key = new EntityMapKey();
        key.setId(22);
        holder.addEntityToEntityMapItem(key, value2);
        uow.registerObject(holder);
        uow.registerObject(key);
        uow.registerObject(value);
        uow.registerObject(value2);
        uow.commit();
        holderExp = (new ExpressionBuilder()).get("id").equal(holder.getId());
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void test(){
        holders = getSession().readAllObjects(EntityEntity1MMapHolder.class, holderExp);
    }

    @Override
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        EntityEntity1MMapHolder holder = (EntityEntity1MMapHolder)holders.get(0);

        if (!((IndirectMap)holder.getEntityToEntityMap()).getValueHolder().isInstantiated() && fetchJoinRelationship > 0){
            throw new TestErrorException("Relationship was not properly joined.");
        }

        if (holder.getEntityToEntityMap().size() != 2){
            throw new TestErrorException("Incorrect Number of MapEntityValues was read.");
        }
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(11);
        EEOTMMapValue value = (EEOTMMapValue)holder.getEntityToEntityMap().get(mapKey);
        if (value.getId() != 1){
            throw new TestErrorException("Incorrect MapEntityValues was read.");
        }
        mapKey = (EntityMapKey)getSession().readObject(mapKey);
        if (!mapKey.getData().equals("data1")){
            throw new TestErrorException("EntityMapKey had wrong data");
        }
    }

    @Override
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        for (Object o : holders) {
            EntityEntity1MMapHolder holder = (EntityEntity1MMapHolder) o;
            for (Object key : holder.getEntityToEntityMap().keySet()) {
                uow.deleteObject(holder.getEntityToEntityMap().get(key));
                uow.deleteObject(key);
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

