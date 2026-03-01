/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.aggregate.Computer;
import org.eclipse.persistence.testing.models.aggregate.Employee;
import org.eclipse.persistence.testing.models.aggregate.Language;
import org.eclipse.persistence.testing.models.aggregate.ProjectDescription;
import org.eclipse.persistence.testing.models.aggregate.Responsibility;

import java.util.Enumeration;
import java.util.Vector;

public class DescriptorRefreshCacheTest extends ReadObjectTest {
    public ClassDescriptor resDescriptor;
    public ClassDescriptor langDescriptor;
    public ClassDescriptor comDescriptor;
    public ClassDescriptor empDescriptor;

    /**
     * DescriptorRefreshCacheTest constructor comment.
     */
    public DescriptorRefreshCacheTest(Object originalObject) {
        setOriginalObject(originalObject);
        setName("DescriptorRefreshCacheTest(" + originalObject.getClass() + ")");
        setDescription("This test case tests refresh cache hit property on descriptor.");
    }

    protected void changeObject(Employee employee) {
        employee.setFirstName("Zack");
        ProjectDescription projectDescription = employee.getProjectDescription();
        projectDescription.setDescription("FBI Project");
        ((Vector)projectDescription.getResponsibilities().getValue()).remove(((Vector)projectDescription.getResponsibilities().getValue()).get(0));
        ((Vector)projectDescription.getResponsibilities().getValue()).add(Responsibility.example1(employee));
        ((Vector)projectDescription.getResponsibilities().getValue()).add(Responsibility.example2(employee));

        ((Vector)projectDescription.getLanguages().getValue()).remove(((Vector)projectDescription.getLanguages().getValue()).get(0));
        ((Vector)projectDescription.getLanguages().getValue()).add(Language.example1());
        ((Vector)projectDescription.getLanguages().getValue()).add(Language.example2());
        projectDescription.getComputer().getValue().setDescription("Newton");
    }

    @Override
    public void reset() {
        this.empDescriptor.dontAlwaysRefreshCache();
        this.empDescriptor.dontDisableCacheHits();
        this.resDescriptor.dontAlwaysRefreshCache();
        this.langDescriptor.dontAlwaysRefreshCache();
        this.comDescriptor.dontAlwaysRefreshCache();
        this.comDescriptor.dontDisableCacheHits();

        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    protected void setup() {
        beginTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        this.empDescriptor = getSession().getClassDescriptor(Employee.class);
        this.resDescriptor = getSession().getClassDescriptor(Responsibility.class);
        this.langDescriptor = getSession().getClassDescriptor(Language.class);
        this.comDescriptor = getSession().getClassDescriptor(Computer.class);

        this.empDescriptor.alwaysRefreshCache();
        this.empDescriptor.disableCacheHits();

        this.resDescriptor.alwaysRefreshCache();
        this.langDescriptor.alwaysRefreshCache();
        this.comDescriptor.alwaysRefreshCache();
        this.comDescriptor.disableCacheHits();

        this.objectFromDatabase = getSession().readObject(this.originalObject);
    }

    @Override
    public void test() {
        Employee originalState = (Employee)this.objectFromDatabase;
        String originalFirstName = originalState.getFirstName();
        String originalProjectDescription = originalState.getProjectDescription().getDescription();
        Vector originalResponsibilities = (Vector)((Vector)originalState.getProjectDescription().getResponsibilities().getValue()).clone();
        Vector originalLanguages = (Vector)((Vector)originalState.getProjectDescription().getLanguages().getValue()).clone();
        Computer originalComputer = originalState.getProjectDescription().getComputer().getValue();
        String originalComputerDescription = originalComputer.getDescription();

        changeObject(originalState);

        Employee employee = (Employee)getSession().readObject(originalState);

        if (employee != originalState) {
            throw new TestErrorException("Always refresh cache does not work.");
        }

        if (originalComputer != employee.getProjectDescription().getComputer().getValue()) {
            throw new TestErrorException("Always refresh cache does not work.");
        }

        for (Enumeration enumtr = originalResponsibilities.elements(); enumtr.hasMoreElements();) {
            Responsibility responsibility = (Responsibility)enumtr.nextElement();
            if (!employee.getProjectDescription().getResponsibilities().getValue().contains(responsibility)) {
                throw new TestErrorException("Always refresh cache does not work.");
            }
        }

        for (Enumeration enumtr = originalLanguages.elements(); enumtr.hasMoreElements();) {
            Language language = (Language)enumtr.nextElement();
            if (!employee.getProjectDescription().getLanguages().getValue().contains(language)) {
                throw new TestErrorException("Always refresh cache does not work.");
            }
        }

        ProjectDescription projectDescription = employee.getProjectDescription();
        Vector responsibilities = (Vector)projectDescription.getResponsibilities().getValue();
        Vector languages = (Vector)projectDescription.getLanguages().getValue();
        Computer computer = projectDescription.getComputer().getValue();

        if (!employee.getFirstName().equals(originalFirstName)) {
            throw new TestErrorException("Always refresh cache does not work.");
        }
        if (!projectDescription.getDescription().equals(originalProjectDescription)) {
            throw new TestErrorException("Always refresh cache does not work.");
        }
        if (responsibilities.size() != originalResponsibilities.size()) {
            throw new TestErrorException("Always refresh cache does not work.");
        }
        if (languages.size() != originalLanguages.size()) {
            throw new TestErrorException("Always refresh cache does not work.");
        }
        if (!computer.getDescription().equals(originalComputerDescription)) {
            throw new TestErrorException("Always refresh cache does not work.");
        }
    }
}
