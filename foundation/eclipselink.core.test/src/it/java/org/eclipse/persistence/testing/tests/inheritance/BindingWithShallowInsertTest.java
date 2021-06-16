/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.inheritance;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.inheritance.BudgettedProject;
import org.eclipse.persistence.testing.models.inheritance.ProjectWorker;

/**
 * Test for Bug 2996585
 * Ensure that an IndexOutOfBounds exception is not thrown when binding a null
 * in the update part of a shallow insert.
 */
public class BindingWithShallowInsertTest extends TestCase {
    protected boolean caughtException = false;
    protected boolean shouldBindParameters = false;
    protected ProjectWorker worker = null;

    public BindingWithShallowInsertTest() {
        setDescription("This test uses a set of mappings with a cycle to test shallow inserts. " + " It ensures that when a shallow insert occurs, null can properly be handled when binding parameters.");
    }

    public void setup() {
        caughtException = false;
        shouldBindParameters = getSession().getLogin().shouldBindAllParameters();
        getSession().getLogin().bindAllParameters();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getAbstractSession().beginTransaction();
    }

    public void test() {
        try {
            worker = new ProjectWorker();
            UnitOfWork uow = getSession().acquireUnitOfWork();
            ProjectWorker person = (ProjectWorker)uow.registerObject(worker);
            person.setName("Gigere");

            BudgettedProject headProject = (BudgettedProject)uow.newInstance(BudgettedProject.class);

            // "NA" is the null value, so this will cause a null to be written
            headProject.setName("NA");
            headProject.setTitle("");
            headProject.setBudget(new Integer(-1));

            // adding the project to both mappings will cause a shallow insert because
            // foreign keys will have to be updated on both tables
            person.setHeadProject(headProject);
            person.addProject(headProject);

            uow.commit();
        } catch (ArrayIndexOutOfBoundsException exception) {
            caughtException = true;
        }
    }

    public void verify() {
        if (caughtException) {
            throw new TestErrorException("An ArrayIndexOutOfBoundsException is throw when running a shallow " + " insert with a null parameter when using binding. This likely means that the parameter fields on " + " the SQLCall are not being properly maintained in the translate method.");
        }
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ProjectWorker resultWorker = (ProjectWorker)getSession().readObject(worker);
        if ((resultWorker == null) || (!resultWorker.getName().equals("Gigere")) || (resultWorker.getHeadProject() == null) || (resultWorker.getProjects().size() != 1)) {
            throw new TestErrorException("The shallow insert of an object using binding with a null parameter was unsuccessful.");
        }
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getLogin().setShouldBindAllParameters(shouldBindParameters);
    }
}
