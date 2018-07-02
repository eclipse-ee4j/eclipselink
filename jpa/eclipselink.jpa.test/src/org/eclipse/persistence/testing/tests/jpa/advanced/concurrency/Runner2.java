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

package org.eclipse.persistence.testing.tests.jpa.advanced.concurrency;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.testing.models.jpa.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.Equipment;

public class Runner2 implements Runnable {
    protected Object waitOn;
    protected Object equipPK;
    protected Object deptPK;
    protected EntityManagerFactory emf;

    public Runner2(Object waitOn, Object deptPK, Object equipPK, EntityManagerFactory emf) {
        this.waitOn = waitOn;
        this.equipPK = equipPK;
        this.deptPK = deptPK;
        this.emf = emf;
    }

    public void run() {
        try {

            EntityManager em = emf.createEntityManager();
            Equipment equip = em.find(Equipment.class, equipPK);
            equip.setDescription("To changed");
            Department dept = em.find(Department.class, deptPK);
            dept.setName("Name Change As Well");
            UnitOfWorkImpl uow = ((EntityManagerImpl) em).getActivePersistenceContext(null);
            synchronized (this.waitOn) {
                try {
                    this.waitOn.wait();
                } catch (InterruptedException e) {
                }
            }

            uow.issueSQLbeforeCompletion(true);
            uow.mergeClonesAfterCompletion();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
