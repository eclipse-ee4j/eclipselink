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
 package org.eclipse.persistence.testing.tests.jpa.performance.reading;

import java.util.*;
import javax.persistence.*;

/**
 * This test compares the performance of read all Employee.
 */
public class JPAReadAllEmployeePerformanceComparisonTest extends JPAReadPerformanceComparisonTest {

    public JPAReadAllEmployeePerformanceComparisonTest(boolean isReadOnly) {
        super(isReadOnly);
        setName("JPAReadAllEmployeePerformanceComparisonTest-readonly:" + isReadOnly);
        setDescription("This test compares the performance of read all Employee.");
    }

    /**
     * Read all employee.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        Query query = manager.createQuery("Select e from Employee e");
        List result = list(query, manager);
        manager.close();
    }
}
