/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.unitofwork.referencesettings;

import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;

import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class ForceWeakReferenceTest extends AutoVerifyTestCase {
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork(ReferenceMode.FORCE_WEAK);
        int size = uow.readAllObjects(Employee.class).size();
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
            throw new TestErrorException("Did not release forced weak references.");
        }
    }
}
;
