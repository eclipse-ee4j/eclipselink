/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.inheritance;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.inheritance.BaseProject;
import org.eclipse.persistence.testing.models.inheritance.BudgettedProject;

/**
 * <p>
 * <b>Purpose</b>: This test checks to see if the Unit of Work Identity Map functions with the Inheritance mappings.
 * <p>
 * <p>
 * <b>Motivation </b>: This test was written to test inheritance using UnitOfWorkIdentityMapAccessor.
 * <p>
 * <b>Responsibilities</b>: Check if the unit of work identity map works correctly with inheritance.
 * <p>
 * <p>
 * */
public class UnitOfWorkIdentityMapAccessorTest extends AutoVerifyTestCase {

    private UnitOfWork unitOfWork;
    private BaseProject baseProject;
    private Object result;
    
    public UnitOfWorkIdentityMapAccessorTest() {
        super();
    }

    protected void setup() {
        DatabaseSession session = (DatabaseSession)getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        baseProject = new BaseProject();
        baseProject.setName("Bob");
        uow.registerNewObject(baseProject);
        uow.commit();
    }

    protected void test() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        unitOfWork = getSession().acquireUnitOfWork();
        
        ReadObjectQuery query = new ReadObjectQuery(BaseProject.class);
        query.setShouldRefreshIdentityMapResult(true);
        unitOfWork.getParent().executeQuery(query);
        
        // Fetch the object of another subclass using same PK as fetched above for different object.
        result = unitOfWork.getIdentityMapAccessor().getFromIdentityMap(baseProject.getId(), BudgettedProject.class);
    }

    /**
     * Verify if the objects fetched does not belong to other subclass.
     */
    protected void verify() {
        try {
            if (result != null && !(result instanceof BudgettedProject)) {
                throw new TestErrorException("The object fetched from identity map accessor belong to different subclass " + result.getClass().getName());
            }
        } catch (DatabaseException exception) {
            if (getSession().getPlatform().isDBase()) {
                throw new TestWarningException("This fails because of some strange bug in the DBase driver. " + exception.getMessage());
            } else {
                throw exception;
            }
        } finally {
            unitOfWork.release();
        }
    }
    
    public void reset() {
        DatabaseSession session = (DatabaseSession)getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        uow.deleteObject(baseProject);
        uow.commit();
        
        this.unitOfWork = null;
        this.result = null;
        this.baseProject = null;
    }
}
