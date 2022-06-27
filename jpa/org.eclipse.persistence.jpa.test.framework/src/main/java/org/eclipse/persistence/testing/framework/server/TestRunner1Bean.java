/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.framework.server;

import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import org.eclipse.persistence.testing.framework.server.GenericTestRunner;
import org.eclipse.persistence.testing.framework.server.TestRunner;

/**
 * Server side JUnit test invocation implemented as a stateless session bean.
 *
 * @author mschinca
 */
@Stateless(name="TestRunner1")
@Remote(TestRunner.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class TestRunner1Bean extends GenericTestRunner {

    /** The entity manager for the test is injected and passed to the test server platform. */
    @PersistenceContext(unitName="MulitPU-1")
    private EntityManager entityManager;

    /** The entity manager factory for the test is injected and passed to the test server platform. */
    @PersistenceUnit(unitName="MulitPU-1")
    private EntityManagerFactory entityManagerFactory;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    protected EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

}
