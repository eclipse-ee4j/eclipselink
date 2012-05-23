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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.util.Iterator;

import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sequencing.UnaryTableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;


public class SequencingWorkbenchIntegrationTest extends AutoVerifyTestCase {

    public static SequencingWorkbenchIntegrationTest projectXML() {
        return new SequencingWorkbenchIntegrationTest(PROJECT_XML);
    }

    public static SequencingWorkbenchIntegrationTest projectClassGenerated() {
        return new SequencingWorkbenchIntegrationTest(PROJECT_CLASS_GENERATED);
    }

    static int PROJECT_XML = 1;
    static int PROJECT_CLASS_GENERATED = 2;
    static String fileName = "SequencingMWIntegrationEmployeeProject";

    int mode;
    Project originalProject;
    Project project;

    SequencingWorkbenchIntegrationTest(int mode) {
        this.mode = mode;
        String strMode = null;
        if (mode == PROJECT_XML) {
            strMode = " XMLProj";
        } else if (mode == PROJECT_CLASS_GENERATED) {
            strMode = " ProjClassGen";
        }
        setName(getName() + strMode);
    }

    static class EmployeeProjectAmended extends EmployeeProject {
        public void applyLogin() {
            super.applyLogin();
            DatabaseLogin login = getLogin();
            login.setDefaultSequence(new TableSequence("", 25, "MY_SEQUENCE", "MY_SEQ_NAME", "MY_SEQ_COUNT"));
            login.addSequence(new DefaultSequence("EMP_SEQ", 30));
            login.addSequence(new NativeSequence("PROJ_SEQ", 35));
            login.addSequence(new UnaryTableSequence("ADDRESS_SEQ", 40));
        }
    }

    protected void setup() throws Exception {
        originalProject = new EmployeeProjectAmended();
        if (mode == PROJECT_XML) {
            project = WorkbenchIntegrationSystemHelper.buildProjectXML(originalProject, fileName);
        } else if (mode == PROJECT_CLASS_GENERATED) {
            project = WorkbenchIntegrationSystemHelper.buildProjectClass(originalProject, fileName);
        } else {
            throw new TestWarningException("Invalid mode");
        }
    }

    protected void verify() {
        DatabaseLogin originalLogin = originalProject.getLogin();
        DatabaseLogin login = project.getLogin();
        if (!originalLogin.getDefaultSequence().equals(login.getDefaultSequence())) {
            throw new TestErrorException("Default sequence has changed");
        }
        if (login.getDatasourcePlatform().getSequences() == null) {
            throw new TestErrorException("Sequences are missing");
        }
        if (originalLogin.getDatasourcePlatform().getSequences().size() != 
            login.getDatasourcePlatform().getSequences().size()) {
            throw new TestErrorException("Number of sequences has changed");
        }
        Iterator it = originalLogin.getDatasourcePlatform().getSequences().values().iterator();
        while (it.hasNext()) {
            Sequence sequence = (Sequence)it.next();
            if (!sequence.equals(login.getSequence(sequence.getName()))) {
                throw new TestErrorException("Sequence " + sequence.getName() + " has changed");
            }
        }
    }
}

