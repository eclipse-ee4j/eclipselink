/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
