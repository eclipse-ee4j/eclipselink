/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.collections.map;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.EntityDirectMapHolder;
import org.eclipse.persistence.testing.models.collections.map.EntityMapKey;

public class TestReadEntityDirectMapMapping extends TestCase {
    
    protected List holders = null;
    protected int fetchJoinRelationship = 0;
    protected int oldFetchJoinValue = 0;
    protected DirectCollectionMapping mapping = null;
    protected Expression holderExp;
    
    public TestReadEntityDirectMapMapping(){
        super();
    }
    
    public TestReadEntityDirectMapMapping(int fetchJoin){
        this();
        fetchJoinRelationship = fetchJoin;
        setName("TestReadEntityDirectMapMapping fetchJoin = " + fetchJoin);
    }
    
    public void setup(){
        mapping = (DirectCollectionMapping)getSession().getProject().getDescriptor(EntityDirectMapHolder.class).getMappingForAttributeName("entityToDirectMap");
        oldFetchJoinValue = mapping.getJoinFetch();
        mapping.setJoinFetch(fetchJoinRelationship);
        getSession().getProject().getDescriptor(EntityDirectMapHolder.class).reInitializeJoinedAttributes();
        
        UnitOfWork uow = getSession().acquireUnitOfWork();
        EntityDirectMapHolder holder = new EntityDirectMapHolder();
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(1);
        mapKey.setData("11");
        uow.registerObject(mapKey);
        holder.addEntityDirectMapItem(mapKey, new Integer(1));
        EntityMapKey mapKey2 = new EntityMapKey();
        mapKey2.setId(2);
        mapKey2.setData("22");
        uow.registerObject(mapKey2);
        holder.addEntityDirectMapItem(mapKey2, new Integer(2));
        uow.registerObject(holder);
        
        uow.commit();
        holderExp = (new ExpressionBuilder()).get("id").equal(holder.getId());
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        holders = getSession().readAllObjects(EntityDirectMapHolder.class, holderExp);
    }
    
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        EntityDirectMapHolder holder = (EntityDirectMapHolder)holders.get(0);
        
        if (!((IndirectMap)holder.getEntityToDirectMap()).getValueHolder().isInstantiated() && fetchJoinRelationship >0){
            throw new TestErrorException("Relationship was not properly joined.");
        }
        if (holder.getEntityToDirectMap().size() != 2){
            throw new TestErrorException("Incorrect Number of Map values was read.");
        }
        EntityMapKey mapKey = new EntityMapKey();
        mapKey.setId(1);
        Integer value = (Integer)holder.getEntityToDirectMap().get(mapKey);
        if (value.intValue() != 1){
            throw new TestErrorException("Incorrect map value was read.");
        }
    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Iterator i = holders.iterator();
        while (i.hasNext()){
            EntityDirectMapHolder holder = (EntityDirectMapHolder)i.next();
            Iterator j = holder.getEntityToDirectMap().keySet().iterator();
            while (j.hasNext()){
                uow.deleteObject(j.next());
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

