/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.db;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.models.projects.TestDatabases;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


abstract class AbstractModelDatabaseTests extends TestCase {
	protected DatabasePlatform databasePlatform;
	protected MWRelationalProject project;
	protected MWDatabase database;
	protected MWLoginSpec loginSpec;
	protected String catalogName;
	protected String schemaName;

	protected MWTable table_EMP;
	protected MWColumn field_EMP_ID;
	protected MWColumn field_ADDR_ID1_FK;
	protected MWColumn field_ADDR_ID2_FK;
	protected MWColumn field_PHONE;
	protected MWColumn field_BOSS_ID_FK;

	protected MWTable table_ADDR;
	protected MWColumn field_ADDR_ID1;
	protected MWColumn field_ADDR_ID2;
	protected MWColumn field_STREET;
	protected MWColumn field_CITY;
	protected MWColumn field_STATE_ID1_FK;
	protected MWColumn field_STATE_ID2_FK;
	protected MWColumn field_ZIP;

	protected MWTable table_STATE;
	protected MWColumn field_STATE_ID1;
	protected MWColumn field_STATE_ID2;
	protected MWColumn field_STATE_CODE;
	protected MWColumn field_STATE_NAME;

	protected MWReference reference_EMP_ADDR;
	protected MWReference reference_EMP_BOSS;
	protected MWReference reference_ADDR_STATE;


	public AbstractModelDatabaseTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		this.databasePlatform = DatabasePlatformRepository.getDefault().platformNamed("Oracle");
		String projectName = ClassTools.shortClassNameForObject(this);
		this.project = new MWRelationalProject(projectName, MappingsModelTestTools.buildSPIManager(), this.databasePlatform);
		this.database = this.project.getDatabase();
		this.loginSpec = TestDatabases.oracleLoginSpec(this.database);
		this.catalogName = null;
		this.schemaName = null;

		// EMP
		this.table_EMP = this.database.addTable(this.catalogName, this.schemaName, "EMP");

		this.field_EMP_ID = this.table_EMP.addColumn("EMP_ID");
		this.field_EMP_ID.setDatabaseType(this.databasePlatform.databaseTypeNamed("NUMBER"));
		this.field_EMP_ID.setSize(0);
		this.field_EMP_ID.setPrimaryKey(true);

		this.field_ADDR_ID1_FK = this.table_EMP.addColumn("ADDR_ID1");
		this.field_ADDR_ID1_FK.setDatabaseType(this.databasePlatform.databaseTypeNamed("NUMBER"));
		this.field_ADDR_ID1_FK.setSize(0);
		
		this.field_ADDR_ID2_FK = this.table_EMP.addColumn("ADDR_ID2");
		this.field_ADDR_ID2_FK.setDatabaseType(this.databasePlatform.databaseTypeNamed("NUMBER"));
		this.field_ADDR_ID2_FK.setSize(0);

		this.field_PHONE = this.table_EMP.addColumn("PHONE");
		this.field_PHONE.setDatabaseType(this.databasePlatform.databaseTypeNamed("VARCHAR2"));
		this.field_PHONE.setSize(20);

		this.field_BOSS_ID_FK = this.table_EMP.addColumn("BOSS_ID");
		this.field_BOSS_ID_FK.setDatabaseType(this.databasePlatform.databaseTypeNamed("NUMBER"));
		this.field_BOSS_ID_FK.setSize(0);

		// ADDR
		this.table_ADDR = this.database.addTable(this.catalogName, this.schemaName, "ADDR");
		
		this.field_ADDR_ID1 = this.table_ADDR.addColumn("ADDR_ID1");
		this.field_ADDR_ID1.setDatabaseType(this.databasePlatform.databaseTypeNamed("NUMBER"));
		this.field_ADDR_ID1.setSize(0);
		this.field_ADDR_ID1.setPrimaryKey(true);
		
		this.field_ADDR_ID2 = this.table_ADDR.addColumn("ADDR_ID2");
		this.field_ADDR_ID2.setDatabaseType(this.databasePlatform.databaseTypeNamed("NUMBER"));
		this.field_ADDR_ID2.setSize(0);
		this.field_ADDR_ID2.setPrimaryKey(true);
		
		this.field_STREET = this.table_ADDR.addColumn("STREET");
		this.field_STREET.setSize(20);
		this.field_CITY = this.table_ADDR.addColumn("CITY");
		this.field_CITY.setSize(20);

		this.field_STATE_ID1_FK = this.table_ADDR.addColumn("STATE_ID1");
		this.field_STATE_ID1_FK.setDatabaseType(this.databasePlatform.databaseTypeNamed("NUMBER"));
		this.field_STATE_ID1_FK.setSize(0);
		
		this.field_STATE_ID2_FK = this.table_ADDR.addColumn("STATE_ID2");
		this.field_STATE_ID2_FK.setDatabaseType(this.databasePlatform.databaseTypeNamed("NUMBER"));
		this.field_STATE_ID2_FK.setSize(0);

		this.field_ZIP = this.table_ADDR.addColumn("ZIP");
		this.field_ZIP.setDatabaseType(this.databasePlatform.databaseTypeNamed("VARCHAR2"));
		this.field_ZIP.setSize(9);

		// STATE
		this.table_STATE = this.database.addTable(this.catalogName, this.schemaName, "STATE");
		this.field_STATE_ID1 = this.table_STATE.addColumn("STATE_ID1");
		this.field_STATE_ID1.setDatabaseType(this.databasePlatform.databaseTypeNamed("NUMBER"));
		this.field_STATE_ID1.setSize(0);
		this.field_STATE_ID1.setPrimaryKey(true);

		this.field_STATE_ID2 = this.table_STATE.addColumn("STATE_ID2");
		this.field_STATE_ID2.setDatabaseType(this.databasePlatform.databaseTypeNamed("NUMBER"));
		this.field_STATE_ID2.setSize(0);
		this.field_STATE_ID2.setPrimaryKey(true);

		this.field_STATE_CODE = this.table_STATE.addColumn("CODE");
		this.field_STATE_CODE.setDatabaseType(this.databasePlatform.databaseTypeNamed("VARCHAR2"));
		this.field_STATE_CODE.setSize(2);

		this.field_STATE_NAME = this.table_STATE.addColumn("NAME");
		this.field_STATE_NAME.setDatabaseType(this.databasePlatform.databaseTypeNamed("VARCHAR2"));
		this.field_STATE_NAME.setSize(20);

		// constraints
		this.reference_EMP_ADDR = this.table_EMP.addReference("EMP_ADDR", this.table_ADDR);
		this.reference_EMP_ADDR.setOnDatabase(true);
		this.reference_EMP_ADDR.addColumnPair(this.field_ADDR_ID1_FK, this.field_ADDR_ID1);
		this.reference_EMP_ADDR.addColumnPair(this.field_ADDR_ID2_FK, this.field_ADDR_ID2);

		this.reference_EMP_BOSS = this.table_EMP.addReference("EMP_BOSS", this.table_EMP);
		this.reference_EMP_BOSS.setOnDatabase(true);
		this.reference_EMP_BOSS.addColumnPair(this.field_BOSS_ID_FK, this.field_EMP_ID);

		this.reference_ADDR_STATE = this.table_ADDR.addReference("ADDR_STATE", this.table_STATE);
		this.reference_ADDR_STATE.setOnDatabase(true);
		this.reference_ADDR_STATE.addColumnPair(this.field_STATE_ID1_FK, this.field_STATE_ID1);
		this.reference_ADDR_STATE.addColumnPair(this.field_STATE_ID2_FK, this.field_STATE_ID2);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}
}
