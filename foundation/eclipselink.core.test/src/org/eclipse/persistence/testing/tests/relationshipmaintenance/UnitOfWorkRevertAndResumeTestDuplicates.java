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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.relationshipmaintenance;

import java.util.Iterator;
import java.util.Vector;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.relationshipmaintenance.Dept;
import org.eclipse.persistence.testing.models.relationshipmaintenance.Emp;

public class UnitOfWorkRevertAndResumeTestDuplicates extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    protected int initialSize = 0;

    public UnitOfWorkRevertAndResumeTestDuplicates() {
        super();
        this.setDescription("This test checks for duplicates in a collection when a unitofwork is reverted");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        beginTransaction();
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Vector allDepts = uow.readAllObjects(Dept.class);
        Dept cloneDept = (Dept)allDepts.get(0);
        this.initialSize = cloneDept.getEmpCollection().size();

        Dept deptToEdit = (Dept)allDepts.get(0);
        Iterator empIter = deptToEdit.getEmpCollection().iterator();
        empIter.next();
        Emp empToEdit = (Emp)empIter.next();
        empToEdit.setEname("whatever");

        uow.revertAndResume();
        Vector allDeptsAgain = uow.readAllObjects(Dept.class);
        cloneDept = (Dept)allDepts.get(0);
        if (cloneDept.getEmpCollection().size() != this.initialSize) {
            throw new TestErrorException("Revert cause duplicates to be entered in relationship maintenanced collection");
        }
    }
}
