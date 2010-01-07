/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 package org.eclipse.persistence.testing.tests.jpa.performance.writing;

import java.util.ArrayList;
import java.util.Iterator;

import javax.persistence.*;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.jpa.performance.Employee;

/**
 * This test compares the performance of inserting Employee.
 */
public class JPAMassInsertOrMergeEmployeeWithManagementLevelsPerformanceComparisonTest extends PerformanceRegressionTestCase {
    // true indicates insert; false - merge.
    boolean shouldInsert;
    // true indicates that the pk value will be assigned by sequencing.
    boolean shouldUseSequencing;
    // number of management levels
    int nLevels;
    // number of direct subordinates each manager has
    int nDirects;
    // used to keep ids in case sequencing is not used
    long id = 0;
    
    boolean wasBatchWriting;
     
    // Example: nLevels == 3; nDirects = 4.
    // First all the Employees corresponding to nLevels and nDirects values are created:
    // There is always the single (highest ranking) topEmployee on Level_0;
    // He/she has 4 Level_1 direct subordinates;
    // each of those has 4 Level_2 directs, 
    // each of those has 4 Level_3 directs.
    // For debugging: 
    // Employee's firstName is always his level (in "Level_2" format);
    // Employee's lastName is his number in his level (from 0 to number of employees of this level - 1)
    // in "Number_3" format.

    public JPAMassInsertOrMergeEmployeeWithManagementLevelsPerformanceComparisonTest(boolean shouldInsert, boolean shouldUseSequencing, int nLevels, int nDirects) {
        this.shouldInsert = shouldInsert;
        this.shouldUseSequencing = shouldUseSequencing;
        this.nLevels = nLevels;
        this.nDirects = nDirects;
        setName(getName() + (shouldInsert ? " insert" : " merge") + (shouldUseSequencing ? " useSequencing " : " doNotUseSequencing ") + " nLevel = " + nLevels + "; nDirects = " + nDirects);
        setDescription("This test compares the performance of "+ (shouldInsert ? "insert" : "merge") + (shouldUseSequencing ? " useSequencing " : " doNotUseSequencing ") + " Employee with " + nLevels + " levels of management; each manager has " + nDirects + " direct employees");
    }

    public void setup() throws Exception {
        id = 0;
        // Disable batch writing, as seems to give database issues.
        EntityManager entityManager = createEntityManager();
        if (entityManager instanceof JpaEntityManager) {
            wasBatchWriting = ((JpaEntityManager)entityManager).getServerSession().getLogin().shouldUseBatchWriting();
            ((JpaEntityManager)entityManager).getServerSession().getLogin().setUsesBatchWriting(false);
        }
        entityManager.close();
        if(!shouldUseSequencing) {
            // obtain the last used sequence number
            Employee emp = new Employee();
            entityManager = createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(emp);
            entityManager.getTransaction().commit();
            entityManager.close();
            id = emp.getId();
            entityManager = createEntityManager();
            entityManager.getTransaction().begin();
            emp = entityManager.find(Employee.class, emp.getId());
            entityManager.remove(emp);
            entityManager.getTransaction().commit();
            entityManager.close();
        }
    }

    /**
     * Insert employee.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        try {    
            // topEmployee - the only one on level 0.
            Employee topEmployee = new Employee();
            topEmployee.setFirstName("Level_0");
            topEmployee.setLastName("Number_0");
            if(!shouldUseSequencing) {
                topEmployee.setId(id++);
            }
    
            // During each nLevel loop iterartion 
            // this array contains direct managers for the Employees to be created -
            // all the Employees of nLevel - 1 level.
            ArrayList<Employee> employeesForHigherLevel = new ArrayList<Employee>(1);
            // In the end of each nLevel loop iterartion 
            // this array contains all Employees created during this iteration -
            // all the Employees of nLevel level.
            ArrayList<Employee> employeesForCurrentLevel;
            employeesForHigherLevel.add(topEmployee);
            // total number of employees - for debugging purposes only.
            int nEmployeesTotal = 1;
            for (int nLevel = 1; nLevel <= nLevels; nLevel++) {
                employeesForCurrentLevel = new ArrayList<Employee>(employeesForHigherLevel.size() * nDirects);
                Iterator<Employee> it = employeesForHigherLevel.iterator();
                while(it.hasNext()) {
                    Employee mgr = it.next();
                    for(int nCurrent = 0; nCurrent < nDirects; nCurrent++) {
                        Employee employee = new Employee();
                        employee.setFirstName("Level_" + nLevel);
                        employee.setLastName("Number_" + employeesForCurrentLevel.size());
                        if(!shouldUseSequencing) {
                            employee.setId(id++);
                        }
                        employeesForCurrentLevel.add(employee);
                        mgr.addManagedEmployee(employee);
                    }
                }
                employeesForHigherLevel = employeesForCurrentLevel;
                nEmployeesTotal = nEmployeesTotal + employeesForCurrentLevel.size();
            }
            
            manager.getTransaction().begin();
            if(shouldInsert) {
                manager.persist(topEmployee);
            } else {
                manager.merge(topEmployee);
            }
            manager.getTransaction().commit();
        } finally {
            if(manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }
            manager.close();
        }
    }

    public void reset() throws Exception {
        // Delete all employees
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        manager.createQuery("Delete from Employee where firstName like 'Level_%'").executeUpdate();
        manager.getTransaction().commit();
        manager.close();
        
        // Re-enable batch writing.
        EntityManager entityManager = createEntityManager();
        if (entityManager instanceof JpaEntityManager) {
            ((JpaEntityManager)entityManager).getServerSession().getLogin().setUsesBatchWriting(wasBatchWriting);
        }
        entityManager.close();
    }
}
