/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - added with fix for bug 300236
package org.eclipse.persistence.testing.tests.identitymaps;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

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

