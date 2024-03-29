/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * This class applies the basic Spring tests to a configuration that uses:
 * - Spring framework's LocalContainerEntityManagerFactoryBean
 * - injection of EntityManager; use of @Transactional annotation
 * - a JPATransactionManager
 */
public class TestContainerEMFtransactional extends SpringJunitTestCase {

    private ClassPathXmlApplicationContext context;
    private EntityManagerWrapper em;

    @Override
    public void setUp() {
        context = new ClassPathXmlApplicationContext("appContext_ContainerEMFtransactional.xml");
        em = (EntityManagerWrapper) context.getBean("transactionalEM");
        super.setEntityManager(em);
    }

    @Override
    public void tearDown(){
        context.close();
    }

}
