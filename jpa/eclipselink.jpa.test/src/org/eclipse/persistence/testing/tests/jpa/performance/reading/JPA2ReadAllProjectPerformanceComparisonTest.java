/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.performance.reading;

import java.util.*;
import javax.persistence.*;

/**
 * This test compares the performance of read all Project.
 */
public class JPA2ReadAllProjectPerformanceComparisonTest extends JPAReadPerformanceComparisonTest {

    public JPA2ReadAllProjectPerformanceComparisonTest() {
        super(false);
        setDescription("This test compares the performance of read all Project.");
    }

    /**
     * Read all.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        Query query = manager.createQuery("Select p from Project p");
        List result = list(query, manager);
        result.size();
        manager.close();
    }
}
