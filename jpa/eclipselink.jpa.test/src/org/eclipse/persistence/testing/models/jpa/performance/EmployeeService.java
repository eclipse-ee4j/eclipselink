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
//     James Sutherland - initial impl
 package org.eclipse.persistence.testing.models.jpa.performance;

import java.util.List;

/**
 * EmployeeService session bean interface.
 */
public interface EmployeeService {
    List findAll();

    void batchFind(long ids[]);

    int batchUpdate(long ids[], int retry);

    Employee findById(long id);

    Employee fetchById(long id);

    void update(Employee employee);

    long insert(Employee employee);

    void createTables();

    void populate();
}
