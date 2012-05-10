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
 *     James Sutherland - initial impl
 ******************************************************************************/  
 package org.eclipse.persistence.testing.models.jpa.performance;

import java.util.List;

/**
 * EmployeeService session bean interface.
 */
public interface EmployeeService {
    List findAll();
    
    Employee findById(long id);
    
    Employee fetchById(long id);
    
    void update(Employee employee);
    
    long insert(Employee employee);
    
    void createTables();

    void populate();
}
