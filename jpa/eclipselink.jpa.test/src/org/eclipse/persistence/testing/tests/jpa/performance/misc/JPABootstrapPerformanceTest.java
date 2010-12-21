/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland (Oracle) - initial API and implementation
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.performance.misc;

import javax.persistence.*;

import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of read object Address.
 */
public class JPABootstrapPerformanceTest extends PerformanceRegressionTestCase {

    public JPABootstrapPerformanceTest() {
        setDescription("This tests the JPA deployment and bootstraping performance.");
    }

    /**
     * Rebuild EntityManagerFactory.
     */
    public void test() throws Exception {
        getExecutor().getEntityManagerFactory().close();
        getExecutor().setEntityManagerFactory(null);
        EntityManager manager = createEntityManager();
        manager.close();
    }
}
