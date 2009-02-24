/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.DirectEntity1MMapHolder;
import org.eclipse.persistence.testing.models.collections.map.DEOTMMapValue;

public class TestReadDirectEntity1MMapMapping extends TestCase {
    
    protected List holders = null;
    protected int fetchJoinRelationship = 0;
    protected int oldFetchJoinValue = 0;
    protected OneToManyMapping mapping = null;
    
    public TestReadDirectEntity1MMapMapping(){
        super();
    }
    
    public TestReadDirectEntity1MMapMapping(int fetchJoin){
        this();
        fetchJoinRelationship = fetchJoin;
        setName("TestReadDirectEntity1MMapMapping fetchJoin = " + fetchJoin);
    }
    
    public void setup(){
        mapping = (OneToManyMapping)getSession().getProject().getDescriptor(DirectEntity1MMapHolder.class).getMappingForAttributeName("directToEntityMap");
        oldFetchJoinValue = mapping.getJoinFetch();
        mapping.setJoinFetch(fetchJoinRelationship);
        getSession().getProject().getDescriptor(DirectEntity1MMapHolder.class).reInitializeJoinedAttributes();
        
        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectEntity1MMapHolder holder = new DirectEntity1MMapHolder();
        DEOTMMapValue value = new DEOTMMapValue();
        value.setId(1);
        value.getHolder().setValue(holder);
        holder.addDirectToEntityMapItem(new Integer(11), value);
        
        DEOTMMapValue value2 = new DEOTMMapValue();
        value2.setId(2);
        value2.getHolder().setValue(holder);
        holder.addDirectToEntityMapItem(new Integer(22), value2);
        uow.registerObject(holder);
        uow.registerObject(value);
        uow.registerObject(value2);
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
        holders = getSession().readAllObjects(DirectEntity1MMapHolder.class);
    }
    
    public void verify(){
        if (holders == null || holders.size() != 1){
            throw new TestErrorException("Incorrect number of MapHolders was read.");
        }
        DirectEntity1MMapHolder holder = (DirectEntity1MMapHolder)holders.get(0);
        
        if (!((IndirectMap)holder.getDirectToEntityMap()).getValueHolder().isInstantiated() && fetchJoinRelationship >0){
            throw new TestErrorException("Relationship was not properly joined.");
        }
        if (holder.getDirectToEntityMap().size() != 2){
            throw new TestErrorException("Incorrect Number of MapEntityValues was read.");
        }
        DEOTMMapValue value = (DEOTMMapValue)holder.getDirectToEntityMap().get(new Integer(11));
        if (value.getId() != 1){
            throw new TestErrorException("Incorrect MapEntityValues was read.");
        }
    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteAllObjects(holders);
        List keys = uow.readAllObjects(DEOTMMapValue.class);
        uow.deleteAllObjects(keys);
        uow.commit();
        mapping.setJoinFetch(oldFetchJoinValue);
    }

}
