/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.tests.simultaneous;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;

public class Runner1 implements Runnable {
    protected Object waitOn;
    protected Object projPK;
    protected Object empPK;
    protected Session session;

    public Runner1(Object waitOn, Object empPK, Object projPK, Session session) {
        this.waitOn = waitOn;
        this.projPK = projPK;
        this.empPK = empPK;
        this.session = session;
    }

    public void run() {

            UnitOfWorkImpl uow = (UnitOfWorkImpl)this.session.acquireUnitOfWork();
            ReadObjectQuery roq = new ReadObjectQuery(Employee.class);
            roq.setSelectionCriteria(roq.getExpressionBuilder().get("id").equal(empPK));
            Employee emp = (Employee)uow.executeQuery(roq);
            roq = new ReadObjectQuery(SmallProject.class);
            roq.setSelectionCriteria(roq.getExpressionBuilder().get("id").equal(projPK));
            SmallProject project = (SmallProject)uow.executeQuery(roq);
            emp.getProjects().add(project);

            try {
                Thread.currentThread().sleep(4000);
            } catch (InterruptedException e) {
           }
            uow.issueSQLbeforeCompletion(true);
            synchronized (this.waitOn) {
                try {
                    this.waitOn.notify();
                    this.waitOn.wait();
                } catch (InterruptedException e) {
                }
            }
            CacheKey cacheKey = uow.getParent().getIdentityMapAccessorInstance().getCacheKeyForObject(emp);
            synchronized (cacheKey) {
                cacheKey.notify();
            }
            try {
                Thread.currentThread().sleep(4000);
            } catch (InterruptedException e) {
            }
            uow.mergeClonesAfterCompletion();
    }


}
