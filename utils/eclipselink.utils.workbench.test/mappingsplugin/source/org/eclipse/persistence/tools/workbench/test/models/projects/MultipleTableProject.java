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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWDescriptorMultiTableInfoPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWSecondaryTableHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDefaultNullValuePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;


/**
 * The purpose of this project is to provide a test case for multiple table
 * foreign keys. 
 * 
 * @auther Les Davis
 * @version 1.0
 * @date Sep 13, 2007
 */
public class MultipleTableProject extends RelationalTestProject
{

	public MultipleTableProject()
	{
		super();
	}
	
	@Override
	protected MWProject buildEmptyProject() {
		return new MWRelationalProject("MultipleTable", spiManager(), mySqlPlatform());
	}
	
	@Override
	protected void initializeDatabase()
	{
		super.initializeDatabase();
		initializeSequenceTable();
		initializeCowTable();
		initializeCalfTable();
		initializeHorseTable();
		initializeFoalTable();
		initializeHumanTable();
		initializeKidTable();
		initializeSwanTable();
		initializeCygnetTable();
		
		MWTable cowTable = this.tableNamed("MULTI_COW");
		MWTable calfsTable = this.tableNamed("MULTI_CALFS");
		MWTable horseTable = this.tableNamed("MULTI_HORSE");
		MWTable foalsTable = this.tableNamed("MULTI_FOALS");
		MWTable humanTable = this.tableNamed("MULTI_HUMAN");
		MWTable kidsTable = this.tableNamed("MULTI_KIDS");
		MWTable swansTable = this.tableNamed("MULTI_SWAN");
		MWTable cygnetsTable = this.tableNamed("MULTI_CYGNETS");
		
		addReferenceOnDB("COW_CALFS", cowTable, calfsTable, "CALFS_ID", "ID");
		addReferenceOnDB("FOALS_HORSE", foalsTable, horseTable, "HORSE_ID", "ID");
		addReferenceOnDB("KIDS_HUMAN", kidsTable, humanTable, "ID", "ID");
		addReferenceOnDB("SWAN_CYGNET", swansTable, cygnetsTable, "ID", "SWAN_ID");
	}

	@Override
	protected void initializeDescriptors()
	{
		super.initializeDescriptors();

		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.multipletable.Cow");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.multipletable.Horse");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.multipletable.Human");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.multipletable.Swan");

		initializeCowDescriptor();
		initializeHorseDescriptor();
		initializeHumanDescriptor();
		initializeSwanDescriptor();
	}
	
	public MWTableDescriptor getCowDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.multipletable.Cow");
	}
	
	public MWTableDescriptor getHorseDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.multipletable.Horse");
	}

	public MWTableDescriptor getHumanDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.multipletable.Human");
	}

	public MWTableDescriptor getSwanDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.multipletable.Swan");
	}

	public void initializeCowDescriptor() {
		MWTableDescriptor cowDescriptor = getCowDescriptor();
		MWTable cowTable = this.tableNamed("MULTI_COW");
		MWTable calfTable = this.tableNamed("MULTI_CALFS");
		cowDescriptor.setPrimaryTable(cowTable);
		
		// Multi-table policy
		cowDescriptor.addMultiTableInfoPolicy();
		MWSecondaryTableHolder tableHolder = ((MWDescriptorMultiTableInfoPolicy)cowDescriptor.getMultiTableInfoPolicy()).addSecondaryTable(calfTable); 
		tableHolder.setPrimaryKeysHaveSameName(false);
		tableHolder.setReference(cowTable.referenceNamed("COW_CALFS"));
		
		cowDescriptor.setUsesSequencing(true);
		cowDescriptor.setSequenceNumberName("CALF_COUNT_SEQ");
		cowDescriptor.setSequenceNumberTable(calfTable);
		cowDescriptor.setSequenceNumberColumn(calfTable.columnNamed("ID"));
		
		addDirectMapping(cowDescriptor, "cowId", cowTable, "ID");
		addDirectMapping(cowDescriptor, "calfCountId", calfTable, "ID");
		addDirectMapping(cowDescriptor, "name", cowTable, "NAME");
		addDirectMapping(cowDescriptor, "calfCount", calfTable, "CALFS");
		
	}
	
	public void initializeHorseDescriptor() {
		MWTableDescriptor horseDescriptor = getHorseDescriptor();

		MWTable horseTable = this.tableNamed("MULTI_HORSE");
		MWTable foalTable = this.tableNamed("MULTI_FOALS");
		horseDescriptor.setPrimaryTable(horseTable);
		
		// Multi-table policy
		horseDescriptor.addMultiTableInfoPolicy();
		MWSecondaryTableHolder tableHolder = ((MWDescriptorMultiTableInfoPolicy)horseDescriptor.getMultiTableInfoPolicy()).addSecondaryTable(foalTable); 
		tableHolder.setPrimaryKeysHaveSameName(false);
		tableHolder.setReference(foalTable.referenceNamed("FOALS_HORSE"));
		
		horseDescriptor.setUsesSequencing(true);
		horseDescriptor.setSequenceNumberName("MULTI_HORSE_SEQ");
		horseDescriptor.setSequenceNumberTable(horseTable);
		horseDescriptor.setSequenceNumberColumn(horseTable.columnNamed("ID"));
		
		addDirectMapping(horseDescriptor, "id", horseTable, "ID");
		addDirectMapping(horseDescriptor, "name", horseTable, "NAME");
		addDirectMapping(horseDescriptor, "foalCount", foalTable, "FOALS");

	}
	
	public void initializeHumanDescriptor() {
		MWTableDescriptor humanDescriptor = getHumanDescriptor();

		MWTable humanTable = this.tableNamed("MULTI_HUMAN");
		MWTable kidTable = this.tableNamed("MULTI_KIDS");
		humanDescriptor.setPrimaryTable(humanTable);
		
		// Multi-table policy
		humanDescriptor.addMultiTableInfoPolicy();
		MWSecondaryTableHolder tableHolder =((MWDescriptorMultiTableInfoPolicy)humanDescriptor.getMultiTableInfoPolicy()).addSecondaryTable(kidTable); 
		tableHolder.setPrimaryKeysHaveSameName(false);
		tableHolder.setReference(kidTable.referenceNamed("KIDS_HUMAN"));
		
		humanDescriptor.setUsesSequencing(true);
		humanDescriptor.setSequenceNumberName("MULTI_HUMAN_SEQ");
		humanDescriptor.setSequenceNumberTable(humanTable);
		humanDescriptor.setSequenceNumberColumn(humanTable.columnNamed("ID"));
		
		addDirectMapping(humanDescriptor, "id", humanTable, "ID");
		addDirectMapping(humanDescriptor, "name", humanTable, "NAME");
		addDirectMapping(humanDescriptor, "kidCount", kidTable, "KIDS");
	}
	
	public void initializeSwanDescriptor() {
		MWTableDescriptor swanDescriptor = getSwanDescriptor();

		MWTable swanTable = this.tableNamed("MULTI_SWAN");
		MWTable cygetTable = this.tableNamed("MULTI_CYGNETS");
		swanDescriptor.setPrimaryTable(swanTable);
		
		// Multi-table policy
		swanDescriptor.addMultiTableInfoPolicy();
		MWSecondaryTableHolder tableHolder = ((MWDescriptorMultiTableInfoPolicy)swanDescriptor.getMultiTableInfoPolicy()).addSecondaryTable(cygetTable); 
		tableHolder.setPrimaryKeysHaveSameName(false);
		tableHolder.setReference(swanTable.referenceNamed("SWAN_CYGNET"));
		
		swanDescriptor.setUsesSequencing(true);
		swanDescriptor.setSequenceNumberName("MULTI_SWAN_SEQ");
		swanDescriptor.setSequenceNumberTable(swanTable);
		swanDescriptor.setSequenceNumberColumn(swanTable.columnNamed("ID"));
		
		addDirectMapping(swanDescriptor, "id", swanTable, "ID");
		addDirectMapping(swanDescriptor, "name", swanTable, "NAME");
		addDirectMapping(swanDescriptor, "cygnetCount", cygetTable, "CYGNETS");
	}
	
	public void initializeCowTable() {	
		MWTable cowTable = database().addTable("MULTI_COW");
		
		addPrimaryKeyField(cowTable,"ID", "integer");
		addField(cowTable,"CALFS_ID", "integer");
		addField(cowTable, "NAME", "varchar", 40);
	}
	
	public void initializeCalfTable() {
		MWTable table = database().addTable("MULTI_CALFS");
		
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "CALFS", "integer");
	}
	
	public void initializeHorseTable() {
		MWTable table = database().addTable("MULTI_HORSE");
		
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "NAME", "varchar");
	}
	
	public void initializeFoalTable() {
		MWTable table = database().addTable("MULTI_FOALS");
		
		addPrimaryKeyField(table, "HORSE_ID", "integer");
		addField(table, "FOALS", "integer");
	}
	
	public void initializeHumanTable() {
		MWTable table = database().addTable("MULTI_HUMAN");
		
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "NAME", "varchar");
	}
	
	public void initializeKidTable() {
		MWTable table = database().addTable("MULTI_KIDS");
		
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "KIDS", "integer");
	}
	
	public void initializeSwanTable() {
		MWTable table = database().addTable("MULTI_SWAN");
		
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "NAME", "varchar");
	}
	
	public void initializeCygnetTable() {
		MWTable table = database().addTable("MULTI_CYGNETS");
		
		addPrimaryKeyField(table, "SWAN_ID", "integer");
		addField(table, "CYGNETS", "integer");
	}
}
