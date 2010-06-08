/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - added with fix for bug 300236
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.identitymaps;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import junit.framework.TestFailure;

import org.eclipse.persistence.internal.helper.IdentityWeakHashMap;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Test that gc does not cause a ConcurrentModificationException when using IdentityWeakHashMap
 * @author tware
 *
 */
public class IdentityWeakHashMapConcurrentModTest extends AutoVerifyTestCase{

    protected IdentityWeakHashMap<Integer, Integer> map = null;
    protected ConcurrentModificationException exception = null;
    
    public void setup(){
        map = new IdentityWeakHashMap<Integer, Integer>(100);

        for (int i=0;i<1000;i++){
            map.put(i, i);
        }
    }
    
    public void test(){
        try{
            Iterator i = map.keySet().iterator();
            int count=0;
            while (i.hasNext()){
                count ++;
                i.next();
                map.get(10);
                System.gc();
            }
        } catch (ConcurrentModificationException e){
            exception = e;
        }
    }
    
    public void verify(){
        if (exception != null){
            throw new TestErrorException("ConcurrentModificationException thrown in IdentityWeakHashMap because of System GC", exception);
        }

    }
}

