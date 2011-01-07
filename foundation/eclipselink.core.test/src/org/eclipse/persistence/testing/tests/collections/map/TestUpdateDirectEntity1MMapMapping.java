/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.DEOTMMapValue;
import org.eclipse.persistence.testing.models.collections.map.DirectEntity1MMapHolder;

public class TestUpdateDirectEntity1MMapMapping extends TestReadDirectEntity1MMapMapping{
    
    protected OneToManyMapping mapping = null;
    private boolean usePrivateOwned = false;
    private boolean oldPrivateOwnedValue = false;
    protected DirectEntity1MMapHolder changedHolder = null;
    
    public TestUpdateDirectEntity1MMapMapping(){
        super();
    }
    
    public TestUpdateDirectEntity1MMapMapping(boolean usePrivateOwned){
        this();
        this.usePrivateOwned = usePrivateOwned;
        setName("TestUpdateDirectEntity1MMapMapping privateOwned=" + usePrivateOwned);
    }
    
    public void setup(){
        mapping = (OneToManyMapping)getSession().getProject().getDescriptor(DirectEntity1MMapHolder.class).getMappingForAttributeName("directToEntityMap");
        oldPrivateOwnedValue = mapping.isPrivateOwned();
        mapping.setIsPrivateOwned(usePrivateOwned);
        
        super.setup();
    }
    
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(DirectEntity1MMapHolder.class, holderExp);
        changedHolder = (DirectEntity1MMapHolder)holders.get(0);
        changedHolder.removeDirectToEntityMapItem(new Integer(11));
        DEOTMMapValue mapValue = new DEOTMMapValue();
        mapValue.setId(3);
        mapValue = (DEOTMMapValue)uow.registerObject(mapValue);
        mapValue.getHolder().setValue(changedHolder);
        changedHolder.addDirectToEntityMapItem(new Integer(33), mapValue);
        uow.commit();
    }
        
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        holders = getSession().readAllObjects(DirectEntity1MMapHolder.class, holderExp);
        DirectEntity1MMapHolder holder = (DirectEntity1MMapHolder)holders.get(0);
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        if (holder.getDirectToEntityMap().containsKey(new Integer(1))){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        DEOTMMapValue value = (DEOTMMapValue)holder.getDirectToEntityMap().get(new Integer(33));
        if (value.getId() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
        if (mapping.isPrivateOwned()){
            ReadObjectQuery query = new ReadObjectQuery(DEOTMMapValue.class);
            ExpressionBuilder values = new ExpressionBuilder();
            Expression criteria = values.get("id").equal(1);
            query.setSelectionCriteria(criteria);
            value = (DEOTMMapValue)getSession().executeQuery(query);
            if (value != null){
                throw new TestErrorException("PrivateOwned DEOTMMapValue was not deleted.");
            }
        }
    }
        
    public void reset(){
        super.reset();
        mapping.setIsPrivateOwned(oldPrivateOwnedValue);
    }
        
}
