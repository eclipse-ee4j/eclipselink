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
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.models.aggregate.Computer;
import org.eclipse.persistence.testing.models.aggregate.Employee;
import org.eclipse.persistence.testing.models.aggregate.Language;
import org.eclipse.persistence.testing.models.aggregate.ProjectDescription;
import org.eclipse.persistence.testing.models.aggregate.Responsibility;

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
        ((Vector)projectDescription.getResponsibilities().getValue()).removeElement(((Vector)projectDescription.getResponsibilities().getValue()).firstElement());
        ((Vector)projectDescription.getResponsibilities().getValue()).addElement(Responsibility.example1(employee));
        ((Vector)projectDescription.getResponsibilities().getValue()).addElement(Responsibility.example2(employee));

        ((Vector)projectDescription.getLanguages().getValue()).removeElement(((Vector)projectDescription.getLanguages().getValue()).firstElement());
        ((Vector)projectDescription.getLanguages().getValue()).addElement(Language.example1());
        ((Vector)projectDescription.getLanguages().getValue()).addElement(Language.example2());
        ((Computer)projectDescription.getComputer().getValue()).setDescription("Newton");
    }

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

    public void test() {
        Employee originalState = (Employee)this.objectFromDatabase;
        String originalFirstName = originalState.getFirstName();
        String originalProjectDescription = originalState.getProjectDescription().getDescription();
        Vector originalResponsibilities = (Vector)((Vector)originalState.getProjectDescription().getResponsibilities().getValue()).clone();
        Vector originalLanguages = (Vector)((Vector)originalState.getProjectDescription().getLanguages().getValue()).clone();
        Computer originalComputer = (Computer)originalState.getProjectDescription().getComputer().getValue();
        String originalComputerDescription = originalComputer.getDescription();

        changeObject(originalState);

        Employee employee = (Employee)getSession().readObject(originalState);

        if (employee != originalState) {
            throw new TestErrorException("Always refresh cache does not work.");
        }

        if (originalComputer != (Computer)employee.getProjectDescription().getComputer().getValue()) {
            throw new TestErrorException("Always refresh cache does not work.");
        }

        for (Enumeration enumtr = originalResponsibilities.elements(); enumtr.hasMoreElements();) {
            Responsibility responsibility = (Responsibility)enumtr.nextElement();
            if (!((Vector)employee.getProjectDescription().getResponsibilities().getValue()).contains(responsibility)) {
                throw new TestErrorException("Always refresh cache does not work.");
            }
        }

        for (Enumeration enumtr = originalLanguages.elements(); enumtr.hasMoreElements();) {
            Language language = (Language)enumtr.nextElement();
            if (!((Vector)employee.getProjectDescription().getLanguages().getValue()).contains(language)) {
                throw new TestErrorException("Always refresh cache does not work.");
            }
        }

        ProjectDescription projectDescription = employee.getProjectDescription();
        Vector responsibilities = (Vector)projectDescription.getResponsibilities().getValue();
        Vector languages = (Vector)projectDescription.getLanguages().getValue();
        Computer computer = (Computer)projectDescription.getComputer().getValue();

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
