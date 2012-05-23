/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.collections.map.EntityMapValue;
import org.eclipse.persistence.testing.models.collections.map.DirectEntityMapHolder;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.util.Iterator;
import java.util.List;

public class TestReadDirectEntityMapMapping extends TestCase {
    
    private List holders = null;
    protected int fetchJoinRelationship = 0;
    protected int oldFetchJoinValue = 0;
    protected ManyToManyMapping mapping = null;
    protected Expression holderExp;
    
    
    public TestReadDirectEntityMapMapping(){
        super();
    }
    
    public TestReadDirectEntityMapMapping(int fetchJoin){
        this();
        fetchJoinRelationship = fetchJoin;
        setName("TestReadDirectEntityMapMapping fetchJoin = " + fetchJoin);
    }
    
    public void setup(){
        mapping = (ManyToManyMapping)getSession().getProject().getDescriptor(DirectEntityMapHolder.class).getMappingForAttributeName("directToEntityMap");
        oldFetchJoinValue = mapping.getJoinFetch();
        mapping.setJoinFetch(fetchJoinRelationship);
        getSession().getProject().getDescriptor(DirectEntityMapHolder.class).reInitializeJoinedAttributes();

        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectEntityMapHolder holder = new DirectEntityMapHolder();
        EntityMapValue value = new EntityMapValue();
        value.setId(1);
        holder.addDirectToEntityMapItem(new Integer(11), value);

        
        EntityMapValue value2 = new EntityMapValue();
        value2.setId(2);
        holder.addDirectToEntityMapItem(new Integer(22), value2);
        uow.registerObject(holder);
        uow.registerObject(value);
        uow.registerObject(value2);
        uow.commit();
        holderExp = (new ExpressionBuilder()).get("id").equal(holder.getId());
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        holders = getSession().readAllObjects(DirectEntityMapHolder.class, holderExp);
    }
    
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        DirectEntityMapHolder holder = (DirectEntityMapHolder)holders.get(0);
        
        if (!((IndirectMap)holder.getDirectToEntityMap()).getValueHolder().isInstantiated() && fetchJoinRelationship > 0){
            throw new TestErrorException("Relationship was not properly joined.");
        }
        
        if (holder.getDirectToEntityMap().size() != 2){
            throw new TestErrorException("Incorrect Number of MapEntityValues was read.");
        }
        EntityMapValue value = (EntityMapValue)holder.getDirectToEntityMap().get(new Integer(11));
        if (value.getId() != 1){
            throw new TestErrorException("Incorrect MapEntityValues was read.");
        }
    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Iterator i = holders.iterator();
        while (i.hasNext()){
            DirectEntityMapHolder holder = (DirectEntityMapHolder)i.next();
            Iterator j = holder.getDirectToEntityMap().keySet().iterator();
            while (j.hasNext()){
                uow.deleteObject(holder.getDirectToEntityMap().get(j.next()));
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
