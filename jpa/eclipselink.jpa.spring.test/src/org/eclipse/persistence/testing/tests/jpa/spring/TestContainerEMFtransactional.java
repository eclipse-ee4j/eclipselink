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
    
    public void setUp() {
        context = new ClassPathXmlApplicationContext("appContext_ContainerEMFtransactional.xml");
        em = (EntityManagerWrapper) context.getBean("transactionalEM");
        super.setEntityManager(em);
    }
    
    public void tearDown(){
        context.close();
    }

}
