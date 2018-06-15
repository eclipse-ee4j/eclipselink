/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.tests.unitofwork.referencesettings;

import java.math.BigDecimal;

import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.tests.unitofwork.changeflag.model.ALCTEmployee;

import org.eclipse.persistence.testing.framework.TestErrorException;

public class WeakReferenceTest extends AutoVerifyTestCase {
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork(ReferenceMode.WEAK);
        int size = uow.readAllObjects(ALCTEmployee.class).size();
        for (int i = 0; i < 200;  ++i){
            //force cacheKey cleanup
            uow.setShouldNewObjectsBeCached(true);
            ALCTEmployee emp = new ALCTEmployee();
            emp.setId(new BigDecimal(i));
            uow.registerObject(emp);
        }
        try{
            Long[] arr = new Long[10000000];
            for (int i = 0; i< 10000000; ++i){
                arr[i] = new Long(i);
            }
            System.gc();
            try{
                Thread.currentThread().sleep(200);
            }catch (InterruptedException ex){
            }
            System.gc();
        }catch (Error er){
            //ignore
        }
        if (((UnitOfWorkImpl)uow).getCloneMapping().size() == size){
            throw new TestErrorException("Did not release weak references.");
        }
    }
}
;
