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
package org.eclipse.persistence.testing.tests.history;

import org.eclipse.persistence.testing.models.employee.relational.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.tools.history.*;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.testing.tests.workbenchintegration.*;

/**
 * <b>Purpose:</b>A specialized Employee System for testing generic historical schema support.
 * <p>
 * This EmployeeSystem creates descriptors with history policies configured, and
 * generates a historical schema on the database when tables are created.
 * <p>
 * The specialized descriptors are effected using the HistoryFacade, and two
 * options are to write those descriptors to a Project xml file and read them back,
 * or to write them to a Project Class generated file and compile and load the
 * class on the fly.  The latter two are for testing the workbench integration.
 * @author Stephen McRitchie
 * @since 10.0.3
 */
public class HistoricalEmployeeSystem extends EmployeeSystem {
	// Allow history tests to be run with a hard coded project, a project 
    // loaded from an xml file, or one loaded from a project class file 
    // which is generated on the fly.
	int mode;

	public HistoricalEmployeeSystem(int mode) {
		this("MWIntegrationTestHistoricalEmployeeProject", mode);
	}
    
	public HistoricalEmployeeSystem(String fileName, int mode) {
		this.mode = mode;
		initializeProject(getInitialProject(), fileName);
	}

	public void initializeProject(org.eclipse.persistence.sessions.Project initialProject, String fileName) {
		if (mode == HistoryTestModel.BASIC) {
			project = initialProject;
		} else if (mode == HistoryTestModel.PROJECT_XML) {
			project = WorkbenchIntegrationSystemHelper.buildProjectXML(initialProject, fileName);
		} else if (mode == HistoryTestModel.PROJECT_CLASS_GENERATED) {
			project = WorkbenchIntegrationSystemHelper.buildProjectClass(initialProject, fileName);
		}
	}

	public org.eclipse.persistence.sessions.Project getInitialProject() {
		org.eclipse.persistence.sessions.Project initialProject = new EmployeeProject();
		HistoryFacade.generateHistoryPolicies(initialProject);
		return initialProject;
	}

	public void createTables(DatabaseSession session) {
		if (!SchemaManager.FAST_TABLE_CREATOR) {
    	    if (session.getPlatform().isOracle()) {
                
    			executeCall(session, "drop table PHONE CASCADE CONSTRAINTS");
    			executeCall(session, "drop table RESPONS CASCADE CONSTRAINTS");
    			executeCall(session, "drop table SALARY CASCADE CONSTRAINTS");
    			executeCall(session, "drop table PROJ_EMP CASCADE CONSTRAINTS");
    			executeCall(session, "drop table LPROJECT CASCADE CONSTRAINTS");
    			executeCall(session, "drop table PROJECT CASCADE CONSTRAINTS");
    			executeCall(session, "drop table EMPLOYEE CASCADE CONSTRAINTS");
    			executeCall(session, "drop table ADDRESS CASCADE CONSTRAINTS");
    		} else {
    			executeCall(session, "drop table PHONE");
    			executeCall(session, "drop table RESPONS");
    			executeCall(session, "drop table SALARY");
    			executeCall(session, "drop table PROJ_EMP");
    			executeCall(session, "drop table LPROJECT");
    			executeCall(session, "drop table PROJECT");
    			executeCall(session, "drop table EMPLOYEE");
    			executeCall(session, "drop table ADDRESS");
    		}
		}

		EmployeeTableCreator creator = new EmployeeTableCreator();
		HistoryFacade.generateHistoricalTableDefinitions(creator, session);
		// reset creator as it might have been set by tests that used
		// the non-historical table definitions
		creator.resetFastTableCreator();
		creator.replaceTables(session);
	}
    
    private void executeCall(DatabaseSession session, String call) {
        try {
            session.executeNonSelectingCall(new SQLCall(call));
		} catch (Exception e) {
            // ignore exception
        }
    }
}
