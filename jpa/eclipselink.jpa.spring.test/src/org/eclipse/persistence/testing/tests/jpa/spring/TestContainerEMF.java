/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.spring;

import javax.persistence.EntityManagerFactory;

import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * This class applies the basic Spring tests to a configuration that uses:
 * - Spring framework's LocalContainerEntityManagerFactoryBean
 * - direct instantiation of the EntityManagerFactory through bean
 * - a JPATransactionManager
 */
public class TestContainerEMF extends SpringJunitTestCase {

    private ClassPathXmlApplicationContext context;
    private EntityManagerFactory emf;
    private EntityManagerWrapper em;

    public void setUp() {
        context = new ClassPathXmlApplicationContext("appContext_ContainerEMF.xml");
        emf = (EntityManagerFactory)context.getBean("entityManagerFactory");
        em  = new EntityManagerTransactionWrapper(emf);
        super.setEntityManager(em);
    }

    public void tearDown(){
        context.close();
    }

    public void testDataExceptionTranslation(){
        //Not tested in this class; direction instantiation of emf does not support exception translation
    }
}
