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

import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.DirectDirectMapHolder;

public class TestReadDirectDirectMapMapping extends TestCase {
    
    protected List holders = null;
    protected int fetchJoinRelationship = 0;
    protected int oldFetchJoinValue = 0;
    protected DirectCollectionMapping mapping = null;
    protected Expression holderExp;
    
    public TestReadDirectDirectMapMapping(){
        super();
    }
    
    public TestReadDirectDirectMapMapping(int fetchJoin){
        this();
        fetchJoinRelationship = fetchJoin;
        setName("TestReadDirectDirectMapMapping fetchJoin = " + fetchJoin);
    }
    
    public void setup(){
        mapping = (DirectCollectionMapping)getSession().getProject().getDescriptor(DirectDirectMapHolder.class).getMappingForAttributeName("directToDirectMap");
        oldFetchJoinValue = mapping.getJoinFetch();
        mapping.setJoinFetch(fetchJoinRelationship);
        getSession().getProject().getDescriptor(DirectDirectMapHolder.class).reInitializeJoinedAttributes();
        
        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectDirectMapHolder holder = new DirectDirectMapHolder();
        holder.addDirectToDirectMapItem(new Integer(1), new Integer(1));
        holder.addDirectToDirectMapItem(new Integer(2), new Integer(2));
        uow.registerObject(holder);
        uow.commit();
        holderExp = (new ExpressionBuilder()).get("id").equal(holder.getId());
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        holders = getSession().readAllObjects(DirectDirectMapHolder.class, holderExp);
    }
    
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        DirectDirectMapHolder holder = (DirectDirectMapHolder)holders.get(0);
        
        if (!((IndirectMap)holder.getDirectToDirectMap()).getValueHolder().isInstantiated() && fetchJoinRelationship >0){
            throw new TestErrorException("Relationship was not properly joined.");
        }
        if (holder.getDirectToDirectMap().size() != 2){
            throw new TestErrorException("Incorrect Number of Map values was read.");
        }
        Integer value = (Integer)holder.getDirectToDirectMap().get(1);
        if (value.intValue() != 1){
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
