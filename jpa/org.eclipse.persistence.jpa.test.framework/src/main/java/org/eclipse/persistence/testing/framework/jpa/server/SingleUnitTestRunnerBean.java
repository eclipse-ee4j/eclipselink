/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
 package org.eclipse.persistence.testing.framework.jpa.server;

import jakarta.annotation.Resource;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;

/**
 * Server side JUnit test invocation implemented as a stateless session bean.
 *
 * @author mschinca
 */
@Stateless(name="SingleUnitTestRunner")
@Remote(TestRunner.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class SingleUnitTestRunnerBean extends GenericTestRunner {

    public SingleUnitTestRunnerBean() {
    }

    /** The entity manager for the test is injected and passed to the test server platform. */
    @PersistenceContext
    private EntityManager entityManager;

    /** The entity manager factory for the test is injected and passed to the test server platform. */
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Resource(name="ejbLookup")
    private Boolean ejbLookup = false;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    protected EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    @Override
    protected boolean getEjbLookup() {
        return ejbLookup;
    }
}
