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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.tests.unitofwork.changeflag.model.ALCTEmployee;

import org.eclipse.persistence.testing.framework.TestErrorException;

public class ChangeTrackedWeakReferenceTest extends AutoVerifyTestCase {
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork(ReferenceMode.FORCE_WEAK);
        Collection collection = uow.readAllObjects(ALCTEmployee.class);
        for (Iterator iterator = collection.iterator(); iterator.hasNext();){
            ((ALCTEmployee)iterator.next()).setFirstName(""+System.currentTimeMillis());
        }
        int size = collection.size();
        try{
            Long[] arr = new Long[100000];
            for (int i = 0; i< 100000; ++i){
                arr[i] = new Long(i);
            }
        }catch (Error er){
            //ignore
        }
        if (((UnitOfWorkImpl)uow).getCloneMapping().size() != size){
            throw new TestErrorException("Released Objects with changes on weak references.");
        }
    }
}
;
