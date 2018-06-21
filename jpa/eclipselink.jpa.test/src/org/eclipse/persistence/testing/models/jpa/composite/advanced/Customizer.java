/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.composite.advanced;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatasourceAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.querykeys.DirectCollectionQueryKey;
import org.eclipse.persistence.mappings.querykeys.ManyToManyQueryKey;
import org.eclipse.persistence.mappings.querykeys.OneToManyQueryKey;
import org.eclipse.persistence.mappings.querykeys.OneToOneQueryKey;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.testing.models.jpa.composite.advanced.member_1.Address;
import org.eclipse.persistence.testing.models.jpa.composite.advanced.member_2.Employee;
import org.eclipse.persistence.testing.models.jpa.composite.advanced.member_3.LargeProject;
import org.eclipse.persistence.testing.models.jpa.composite.advanced.member_3.Project;

/**
 * Session and descriptor customizer.
 */
public class Customizer implements SessionCustomizer, DescriptorCustomizer {
    static HashMap sessionCalls = new HashMap();
    static HashMap descriptorCalls = new HashMap();

    public void customize(Session session) {
        String sessionName = session.getName();
        Integer numberOfCalls = (Integer)sessionCalls.get(sessionName);
        int num = 0;
        if(numberOfCalls != null) {
            num = numberOfCalls.intValue();
        }
        sessionCalls.put(sessionName, new Integer(num + 1));

        //**temp
        session.getEventManager().addListener(new AcquireReleaseListener());

        session.getEventManager().addListener(new SessionEventAdapter() {
            public void postLogin(SessionEvent event) {
                if (event.getSession().getPlatform().isPostgreSQL()) {
                    event.getSession().setQueryTimeoutDefault(0);
                }
            }
        });
    }

    public void customize(ClassDescriptor descriptor) {
        String javaClassName = descriptor.getJavaClass().getName();
        Integer numberOfCalls = (Integer)descriptorCalls.get(javaClassName);
        int num = 0;
        if(numberOfCalls != null) {
            num = numberOfCalls.intValue();
        }
        descriptorCalls.put(javaClassName, new Integer(num + 1));

        addCustomQueryKeys(descriptor);
    }

    public static Map getSessionCalls() {
        return sessionCalls;
    }

    public static Map getDescriptorCalls() {
        return descriptorCalls;
    }

    public static int getNumberOfCallsForSession(String sessionName) {
        Integer numberOfCalls = (Integer)sessionCalls.get(sessionName);
        if(numberOfCalls == null) {
            return 0;
        } else {
            return numberOfCalls.intValue();
        }
    }

    public static int getNumberOfCallsForClass(String javaClassName) {
        Integer numberOfCalls = (Integer)descriptorCalls.get(javaClassName);
        if(numberOfCalls == null) {
            return 0;
        } else {
            return numberOfCalls.intValue();
        }
    }

    protected void addCustomQueryKeys(ClassDescriptor descriptor) {
        ExpressionBuilder builder;

        // add QueryKeys to Employee descriptor
        if(descriptor.getJavaClass().equals(Employee.class)) {
            // Employee's Address (same as address attribute).
            OneToOneQueryKey queryKey = new OneToOneQueryKey();
            queryKey.setName("addressQK");
            queryKey.setReferenceClass(Address.class);
            builder = new ExpressionBuilder();
            queryKey.setJoinCriteria(
                    builder.getField("MBR1_ADDRESS.ADDRESS_ID").equal(
                    builder.getParameter("MBR2_EMPLOYEE.ADDR_ID")));
            descriptor.addQueryKey(queryKey);

            // Projects led by Employee.
            OneToManyQueryKey managedProjectsQueryKey = new OneToManyQueryKey();
            managedProjectsQueryKey.setName("managedProjects");
            managedProjectsQueryKey.setReferenceClass(Project.class);
            builder = new ExpressionBuilder();
            managedProjectsQueryKey.setJoinCriteria(
                    builder.getField("MBR3_PROJECT.LEADER_ID").equal(
                    builder.getParameter("MBR2_EMPLOYEE.EMP_ID")));
            descriptor.addQueryKey(managedProjectsQueryKey);

            // LargeProjects led by Employee.
            OneToManyQueryKey managedLargeProjectsQueryKey = new OneToManyQueryKey();
            managedLargeProjectsQueryKey.setName("managedLargeProjects");
            managedLargeProjectsQueryKey.setReferenceClass(LargeProject.class);
            builder = new ExpressionBuilder();
            managedLargeProjectsQueryKey.setJoinCriteria(
                    builder.getField("MBR3_PROJECT.LEADER_ID").equal(
                    builder.getParameter("MBR2_EMPLOYEE.EMP_ID")));
            descriptor.addQueryKey(managedLargeProjectsQueryKey);

            // Employee's Projects (same as projects attribute).
            ManyToManyQueryKey projectsQueryKey = new ManyToManyQueryKey();
            projectsQueryKey.setName("projectsQK");
            projectsQueryKey.setReferenceClass(Project.class);
            builder = new ExpressionBuilder();
            projectsQueryKey.setJoinCriteria(
                    (builder.getParameter("MBR2_EMPLOYEE.EMP_ID").equal(
                    builder.getTable("MBR3_EMP_PROJ").getField("EMPLOYEES_EMP_ID")).and(
                    builder.getTable("MBR3_EMP_PROJ").getField("PROJECTS_PROJ_ID").equal(
                    builder.getField("MBR3_PROJECT.PROJ_ID")))));
            descriptor.addQueryKey(projectsQueryKey);

            // Employee's LargeProjects.
            ManyToManyQueryKey largeProjectsQueryKey = new ManyToManyQueryKey();
            largeProjectsQueryKey.setName("largeProjects");
            largeProjectsQueryKey.setReferenceClass(LargeProject.class);
            builder = new ExpressionBuilder();
            largeProjectsQueryKey.setJoinCriteria(
                    (builder.getParameter("MBR2_EMPLOYEE.EMP_ID").equal(
                    builder.getTable("MBR3_EMP_PROJ").getField("EMPLOYEES_EMP_ID")).and(
                    builder.getTable("MBR3_EMP_PROJ").getField("PROJECTS_PROJ_ID").equal(
                    builder.getField("MBR3_PROJECT.PROJ_ID")))));
            descriptor.addQueryKey(largeProjectsQueryKey);

            // Employee's responsibilities (same as responsibilities) - can't make this work so far.
            DirectCollectionQueryKey responsibilitiesQueryKey = new DirectCollectionQueryKey();
            responsibilitiesQueryKey.setName("responsibilitiesQK");
            builder = new ExpressionBuilder();
            responsibilitiesQueryKey.setJoinCriteria(
                    builder.getField("MBR1_RESPONS.DESCRIPTION").equal(
                    builder.getField("MBR1_RESPONS.EMP_ID").equal(
                    builder.getParameter("MBR2_EMPLOYEE.EMP_ID"))));
            descriptor.addQueryKey(responsibilitiesQueryKey);

        } else if(descriptor.getJavaClass().equals(Address.class)) {
            // Employee that references Address
            OneToOneQueryKey ownerQueryKey = new OneToOneQueryKey();
            ownerQueryKey.setName("owner");
            ownerQueryKey.setReferenceClass(Employee.class);
            builder = new ExpressionBuilder();
            ownerQueryKey.setJoinCriteria(
                    builder.getField("MBR2_EMPLOYEE.ADDR_ID").equal(
                    builder.getParameter("MBR1_ADDRESS.ADDRESS_ID")));
            descriptor.addQueryKey(ownerQueryKey);

        } else  if(descriptor.getJavaClass().equals(Project.class)) {
            // Project's employees.
            ManyToManyQueryKey employesQueryKey = new ManyToManyQueryKey();
            employesQueryKey.setName("employees");
            employesQueryKey.setReferenceClass(Employee.class);
            builder = new ExpressionBuilder();
            employesQueryKey.setJoinCriteria(
                    (builder.getParameter("MBR3_PROJECT.PROJ_ID").equal(
                    builder.getTable("MBR3_EMP_PROJ").getField("PROJECTS_PROJ_ID")).and(
                    builder.getTable("MBR3_EMP_PROJ").getField("EMPLOYEES_EMP_ID").equal(
                    builder.getField("MBR2_EMPLOYEE.EMP_ID")))));
            descriptor.addQueryKey(employesQueryKey);
        }
    }
    //**temp
    static class AcquireReleaseListener extends SessionEventAdapter {
        public void postAcquireConnection(SessionEvent event) {
            DatasourceAccessor accessor = (DatasourceAccessor)event.getResult();
            try {
                if(accessor.getLogin() == null) {
                    throw new RuntimeException("acquired accessor.getLogin() == null");
                }
                if(accessor.getDatasourceConnection() == null) {
                    throw new RuntimeException("AcquireReleaseListener: acquired accessor.getDatasourceConnection() == null");
                }
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
        public void preReleaseConnection(SessionEvent event) {
            DatasourceAccessor accessor = (DatasourceAccessor)event.getResult();
            try {
                if(accessor.getLogin() == null) {
                    throw new RuntimeException("released accessor.getLogin() == null");
                }
                if(accessor.getDatasourceConnection() == null) {
                    throw new RuntimeException("AcquireReleaseListener: released accessor.getDatasourceConnection() == null");
                }
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
    }
}
