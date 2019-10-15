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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.EntityMapValue;
import org.eclipse.persistence.testing.models.collections.map.EntityEntityU1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;

public class TestUpdateEntityEntityU1MMapMapping extends TestReadEntityEntityU1MMapMapping{

    protected OneToManyMapping mapping = null;
    private boolean usePrivateOwned = false;
    private boolean oldPrivateOwnedValue = false;
    protected ForeignReferenceMapping keyMapping = null;
    private boolean oldKeyPrivateOwnedValue = false;
    protected EntityEntityU1MMapHolder changedHolder = null;

    public TestUpdateEntityEntityU1MMapMapping(){
        super();
    }

    public TestUpdateEntityEntityU1MMapMapping(boolean usePrivateOwned){
        this();
        this.usePrivateOwned = usePrivateOwned;
        setName("TestUpdateDirectEntity1MMapMapping privateOwned=" + usePrivateOwned);
    }

    public void setup(){
        mapping = (OneToManyMapping)getSession().getProject().getDescriptor(EntityEntityU1MMapHolder.class).getMappingForAttributeName("entityToEntityMap");
        oldPrivateOwnedValue = mapping.isPrivateOwned();
        mapping.setIsPrivateOwned(usePrivateOwned);
        keyMapping = (ForeignReferenceMapping)((MappedKeyMapContainerPolicy)mapping.getContainerPolicy()).getKeyMapping();
        oldKeyPrivateOwnedValue = keyMapping.isPrivateOwned();
        keyMapping.setIsPrivateOwned(usePrivateOwned);
        super.setup();
    }

    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(EntityEntityU1MMapHolder.class, holderExp);
        changedHolder = (EntityEntityU1MMapHolder)holders.get(0);
        EntityMapKey key = new EntityMapKey();
        key.setId(11);
        changedHolder.removeEntityToEntityMapItem(key);
        EntityMapValue mapValue = new EntityMapValue();
        mapValue.setId(3);
        mapValue = (EntityMapValue)uow.registerObject(mapValue);
        key = new EntityMapKey();
        key.setId(33);
        key = (EntityMapKey)uow.registerObject(key);
        changedHolder.addEntityToEntityMapItem(key, mapValue);
        uow.commit();
        Object holderForComparison = uow.readObject(changedHolder);
        if (!compareObjects(changedHolder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }
    }

    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        holders = getSession().readAllObjects(EntityEntityU1MMapHolder.class, holderExp);
        EntityEntityU1MMapHolder holder = (EntityEntityU1MMapHolder)holders.get(0);
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        EntityMapKey key = new EntityMapKey();
        key.setId(11);
        if (holder.getEntityToEntityMap().containsKey(key)){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        key = new EntityMapKey();
        key.setId(33);
        EntityMapValue value = (EntityMapValue)holder.getEntityToEntityMap().get(key);
        if (value.getId() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
        if (mapping.isPrivateOwned()){
            ReadObjectQuery query = new ReadObjectQuery(EntityMapValue.class);
            ExpressionBuilder values = new ExpressionBuilder();
            Expression criteria = values.get("id").equal(1);
            query.setSelectionCriteria(criteria);
            value = (EntityMapValue)getSession().executeQuery(query);
            if (value != null){
                throw new TestErrorException("PrivateOwned EntityMapValue was not deleted.");
            }
            query = new ReadObjectQuery(EntityMapKey.class);
            ExpressionBuilder keys = new ExpressionBuilder();
            Expression keycriteria = keys.get("id").equal(11);
            query.setSelectionCriteria(keycriteria);
            key = (EntityMapKey)getSession().executeQuery(query);
            if (key != null){
                throw new TestErrorException("PrivateOwned EntityMapKey was not deleted.");
            }
        }
    }

    public void reset(){
        super.reset();
        mapping.setIsPrivateOwned(oldPrivateOwnedValue);
        keyMapping.setIsPrivateOwned(oldKeyPrivateOwnedValue);
    }

}
