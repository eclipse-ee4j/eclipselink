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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork.transactionisolation;

import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.Project;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * <p>
 * Tests the interaction of the ComplexQueryResult / shouldIncludeData feature
 * and executing queries on the UnitOfWork.
 * <p>
 * For many to many must return the EMP_ID/PROJ_ID rows from the relation table
 * in addition to the batch objects so we can figure out which projects belong
 * to which employees.  This is done with query.setShouldIncludeData(true).
 * <p>
 * However, the implementation of shouldIncludeData is scary, for if anyone
 * else touches the query result except those expecting it (i.e. a UnitOfWork
 * trying to register it) TopLink will be die.
 * @author  smcritch
 */
public class

TransactionIsolationM2MBatchReadTest extends AutoVerifyTestCase {
    UnitOfWork unitOfWork;

    protected void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        unitOfWork = getSession().acquireUnitOfWork();
    }

    public void reset() throws Exception {
        if (unitOfWork != null) {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            unitOfWork.release();
            unitOfWork = null;
        }
    }

    public void test() {

        unitOfWork.beginEarlyTransaction();

        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.addBatchReadAttribute("projects");

        Expression workersExp = (new ExpressionBuilder()).notEmpty("projects");

        query.setSelectionCriteria(workersExp);
        Vector workers = (Vector)unitOfWork.executeQuery(query);
        Employee worker = (Employee)workers.elementAt(0);
        Employee otherWorker = (Employee)workers.elementAt(1);


        Vector workerProjects = worker.getProjects();

        Vector otherWorkerProjects = otherWorker.getProjects();

        // By now the test should have failed if it was going to fail.
        // Try one more thing to make sure that they are batch read correctly.
        strongAssert(workerProjects != otherWorkerProjects, "Everyone is getting back the same batched result.");
        strongAssert(workerProjects != null, "The batched attribute [projects] was null.");
        strongAssert(workerProjects.size() > 0, "The batched attribute [projects] was empty.");

        Employee originalWorker = (Employee)getSession().readObject(worker);

        verifyCloneHasSameProjectsAsOriginal(workerProjects, originalWorker.getProjects());
    }

    private void verifyCloneHasSameProjectsAsOriginal(Vector clones, Vector originals) {
        strongAssert(clones.size() == originals.size(), 
                     "The clones collections was of a different size than the original. " + clones.size() + 
                     " instead of: " + originals.size());
        for (int i = 0; i < clones.size(); i++) {
            Project clone = (Project)clones.elementAt(i);
            boolean match = false;
            for (int j = 0; j < originals.size(); j++) {
                Project original = (Project)originals.elementAt(j);
                if (clone.getId().equals(original.getId())) {
                    match = true;
                    break;
                }
            }
            strongAssert(match, "The project's contents are different");
        }
    }

}
