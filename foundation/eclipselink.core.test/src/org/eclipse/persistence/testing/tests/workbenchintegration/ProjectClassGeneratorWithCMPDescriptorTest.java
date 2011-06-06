/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.io.File;
import java.lang.reflect.Method;

import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.PessimisticLockingPolicy;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.ProjectClassGenerator;


public class ProjectClassGeneratorWithCMPDescriptorTest extends AutoVerifyTestCase {
    Project project, generatedProject;
    boolean foundException;
    String projectShortClassName = "ProjectClassGeneratorWithCMPDescriptorTestProject";

    public ProjectClassGeneratorWithCMPDescriptorTest() {
        super();
        setDescription("Test if ProjectTestGenerator generates a correct project when a CMP Descriptor is used.");
    }

    public static void main(String[] args) {
        ProjectClassGeneratorWithCMPDescriptorTest test = new ProjectClassGeneratorWithCMPDescriptorTest();
        test.setup();
        test.test();
        test.verify();
        test.reset();

    }

    public void reset() {
        File file = new File(projectShortClassName + ".java");
        file.delete();
        file = new File(projectShortClassName + ".class");
        file.delete();
    }

    protected void setup() {
        project = new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();

        // initialize new CMP descriptor with non-default settings

        CMPPolicy cmpPolicy = new CMPPolicy();
        cmpPolicy.setPessimisticLockingPolicy(new PessimisticLockingPolicy());

        cmpPolicy.setDeferModificationsUntilCommit(5);
        cmpPolicy.setForceUpdate(true);
        cmpPolicy.setNonDeferredCreateTime(1000);
        cmpPolicy.setUpdateAllFields(true);

        cmpPolicy.setPessimisticLockingPolicy(new PessimisticLockingPolicy());
        cmpPolicy.getPessimisticLockingPolicy().setLockingMode(ObjectLevelReadQuery.LOCK_NOWAIT);

        // set CMP descriptor to Address descriptor
        project.getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Address.class).setCMPPolicy(cmpPolicy);
    }

    public void test() {
        String fileName = projectShortClassName + ".java";
        try {
            ProjectClassGenerator.write(project, projectShortClassName, fileName);
        } catch (Exception e) {
            throw new TestErrorException("Falied to generate project file " + fileName, e);
        }

        try {
        	Object[] params = new Object[1];
            String[] source = { fileName };
            params[0] = source;
            Class mainClass = Class.forName("com.sun.tools.javac.Main");
            Class[] parameterTypes = new Class[1];
            parameterTypes[0] = String[].class;
            Method method = mainClass.getMethod("compile", parameterTypes);
            int result = ((Integer)method.invoke(null, params)).intValue();
            if (result != 0) {
                throw new TestErrorException("Failed to compiled the generated project file " + fileName + ". This could either be a legitimate compile " +
                 		"failure, or could result if you do not have the tools.jar from your JDK on the classpath.");
            }
            //Class projectClass = (Class) getSession().getPlatform().getConversionManager().convertObject(projectShortClassName, ClassConstants.CLASS);
            Class projectClass = Class.forName(projectShortClassName);
            generatedProject = (org.eclipse.persistence.sessions.Project)projectClass.newInstance();
        } catch (Exception exception) {
            throw new TestErrorException("Failed obtain new project instance from the generated and compiled project. " +
            		" It may be possible to solve this issue by adding the tools.jar from your JDK to the classpath.", 
                                         exception);
        }
    }

    protected void verify() {

        CMPPolicy cmpPolicy = 
            project.getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Address.class).getCMPPolicy();
        CMPPolicy generatedCMPPolicy = 
            generatedProject.getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Address.class).getCMPPolicy();
        String errors = new String();

        if (generatedCMPPolicy == null) {
            errors += "CMPPolicy is null.\n";
        } else {
            if (generatedCMPPolicy.getDeferModificationsUntilCommit() != 
                cmpPolicy.getDeferModificationsUntilCommit()) {
                errors += "CMPPolicy: deferModificationsUntilCommit setting is not the same.\n";
            }
            if (generatedCMPPolicy.getForceUpdate() != cmpPolicy.getForceUpdate()) {
                errors += "CMPPolicy: forceUpdate setting is not the same.\n";
            }
            if (generatedCMPPolicy.getNonDeferredCreateTime() != cmpPolicy.getNonDeferredCreateTime()) {
                errors += "CMPPolicy: nonDeferredCreateTime setting is not the same.\n";
            }
            if (generatedCMPPolicy.getUpdateAllFields() != cmpPolicy.getUpdateAllFields()) {
                errors += "CMPPolicy: updateAllFields setting is not the same.\n";
            }
            if (generatedCMPPolicy.getPessimisticLockingPolicy() == null) {
                errors += "CMPPolicy: pessimistic locking policy is null\n";
            } else if (generatedCMPPolicy.getPessimisticLockingPolicy().getLockingMode() != 
                       cmpPolicy.getPessimisticLockingPolicy().getLockingMode()) {
                errors += "PessimisticLockingPolicy: locking mode is not the same\n";
            }
        }

        if (errors.length() > 0) {
            throw new TestErrorException("The following settings of the generated project instance does not have the expected value:\n" + 
                                         errors);
        }
    }
}
