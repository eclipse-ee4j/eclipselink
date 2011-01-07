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
package org.eclipse.persistence.testing.tests.stress;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;

/**
 * Test stressing things.
 */
 
public class StressTestModel extends TestModel {
public StressTestModel () 
{
	setDescription("Tests stressing things.");
}
public void addRequiredSystems() 
{
	addRequiredSystem(new EmployeeSystem());
}
public void addTests() 
{
	TestSuite suite = new TestSuite();
	suite.setName("StressSuite(50)");
	suite.addTest(new StressLoginTest(50));
	suite.addTest(new StressInsertTest(50));
	suite.addTest(new StressUpdateTest(50));
	suite.addTest(new StressReadTest(10));
	suite.addTest(new StressUnitOfWorkTest(5));
	suite.addTest(new StressThreeTierTest(10));
	//suite.addTest(new StressObjectCreationTest(50));
	addTest(suite);
	
	TestSuite suite2 = new TestSuite();
	suite2.setName("StressSuite(200)");
	suite2.addTest(new StressLoginTest(200));
	suite2.addTest(new StressInsertTest(200));
	suite2.addTest(new StressUpdateTest(200));
	suite2.addTest(new StressReadTest(20));
	suite2.addTest(new StressUnitOfWorkTest(20));
	suite2.addTest(new StressThreeTierTest(100));
	//suite2.addTest(new StressObjectCreationTest(400));
	addTest(suite2);
}
public void setup() 
{
	((DatabaseSession) getSession()).addDescriptor(Address.descriptor());
	SchemaManager schemaManager = new SchemaManager((DatabaseSession) getSession());
	schemaManager.replaceObject(Address.tableDefinition());
	schemaManager.createSequences();
	getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update \"SEQUENCE\" set SEQ_COUNT = 900000000000 where SEQ_NAME = 'STRESS_SEQ'"));
	for (int i = 0; i < 50; i++) {
		Address address = new Address();
		getDatabaseSession().insertObject(address);
	}
}
}
