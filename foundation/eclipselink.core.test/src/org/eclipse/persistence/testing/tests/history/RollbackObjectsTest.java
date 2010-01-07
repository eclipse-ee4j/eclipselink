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
package org.eclipse.persistence.testing.tests.history;

import java.util.*;

import org.eclipse.persistence.history.*;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.Project;

/**
 * <b>Purpose:</b>Tests rolling back objects to past state using history.
 * <p>
 * This test also acts as a kind of reset method, as tables can continually
 * be rolled back to a starting state.
 */

public class RollbackObjectsTest extends AutoVerifyTestCase {

	Class domainClass;
	AsOfClause pastTime;
	Vector pastObjects;
	
	public RollbackObjectsTest(Class domainClass, AsOfClause pastTime) {
		super();
		this.domainClass = domainClass;
		this.pastTime = pastTime;
	}

	public void setup() {
		getSession().getIdentityMapAccessor().initializeIdentityMaps();
	}

	public void test() {
		org.eclipse.persistence.sessions.Session hs = getSession().acquireHistoricalSession(pastTime);
		pastObjects = hs.readAllObjects(domainClass);
		//getSession().getProject().checkDatabaseForDoesExist();

		Vector currentProjects = getSession().readAllObjects(Project.class);
		for (Enumeration enumtr = currentProjects.elements(); enumtr.hasMoreElements();) {
			((Project)enumtr.nextElement()).getTeamLeader();
		}

			UnitOfWork uow = getSession().acquireUnitOfWork();
		
		for (Enumeration enumtr = pastObjects.elements(); enumtr.hasMoreElements();) {
			Employee emp = (Employee)enumtr.nextElement();
			emp.getProjects();
			for (Enumeration proj = emp.getProjects().elements(); proj.hasMoreElements();) {
				((Project)proj.nextElement()).getTeamLeader();
			}
			emp.getAddress();
		}
		for (Enumeration enumtr = pastObjects.elements(); enumtr.hasMoreElements();) {
			Employee emp = (Employee)enumtr.nextElement();
			uow.registerObject(emp);
		}

		Vector pastProjects = hs.readAllObjects(Project.class);
		for (Enumeration enumtr = pastProjects.elements(); enumtr.hasMoreElements();) {
			Project project = (Project)enumtr.nextElement();
			uow.registerObject(project);
			uow.deepMergeClone(project);
		}
			//uow.registerAllObjects(pastProjects);
			//clone = uow.deepClone(obj);
			//} else {
			//	clone = uow.deepMergeClone(obj);
			//}
			uow.commit();
	}

	public void verify() {
		try {
			Vector restoredObjects = getSession().readAllObjects(domainClass);
			if (restoredObjects.size() != pastObjects.size()) {
				throw new TestErrorException("Not all objects were restored.  Restored: " + restoredObjects.size() + " Total: " + pastObjects.size());
			}
		} finally {
			pastObjects = null;
		}
	}

	public void reset() {
		getSession().getIdentityMapAccessor().initializeIdentityMaps();
	}
}
