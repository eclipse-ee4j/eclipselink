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

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.map.DirectDirectMapHolder;

public class TestUpdateDirectDirectMapMapping extends TestReadDirectDirectMapMapping {
    
    protected DirectDirectMapHolder changedHolder = null;
    
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holders = uow.readAllObjects(DirectDirectMapHolder.class, holderExp);
        changedHolder = (DirectDirectMapHolder)holders.get(0);
        changedHolder.removeDirectToDirectMapItem(new Integer(1));
        changedHolder.addDirectToDirectMapItem(new Integer(3), new Integer(3));
        uow.commit();
        Object holderForComparison = uow.readObject(changedHolder);
        if (!compareObjects(changedHolder, holderForComparison)){
            throw new TestErrorException("Objects do not match after write");
        }
    }
    
    public void verify(){
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Object initialHolder = holders.get(0);
        holders = getSession().readAllObjects(DirectDirectMapHolder.class, holderExp);
        DirectDirectMapHolder holder = (DirectDirectMapHolder)holders.get(0);
        if (!compareObjects(holder, changedHolder)){
            throw new TestErrorException("Objects do not match reinitialize");
        }
        if (holder.getDirectToDirectMap().containsKey(new Integer(1))){
            throw new TestErrorException("Item that was removed is still present in map.");
        }
        Integer value = (Integer)holder.getDirectToDirectMap().get(new Integer(3));
        if (value.intValue() != 3){
            throw new TestErrorException("Item was not correctly added to map");
        }
    }

}
