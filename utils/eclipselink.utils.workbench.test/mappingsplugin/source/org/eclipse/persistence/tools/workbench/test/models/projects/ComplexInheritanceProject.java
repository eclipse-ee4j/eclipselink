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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorExtractionMethodPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorAfterLoadingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWDescriptorMultiTableInfoPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;



public class ComplexInheritanceProject extends RelationalTestProject {

	public ComplexInheritanceProject () {
	 	super();
	}
	
	public static MWRelationalProject emptyProject() {
		MWRelationalProject project = new MWRelationalProject("ComplexInheritance", spiManager(), oraclePlatform());
		
		// Defaults policy  
		project.getDefaultsPolicy().getCachingPolicy().setCacheSize(100);
		project.getDefaultsPolicy().getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_WEAK_WITH_SOFT_SUBCACHE);
		project.getDefaultsPolicy().setMethodAccessing(false);
		
		return project;
	} 

	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
	
	public MWTableDescriptor getBicycleDescriptor() {
		return tableDescriptorWithShortName("Bicycle");
	}
	
	public MWTableDescriptor getBoatDescriptor() {
		return tableDescriptorWithShortName("Boat");
	}
	
	public MWTableDescriptor getBusDescriptor() {
		return tableDescriptorWithShortName("Bus");
	}
	
	public MWTableDescriptor getCarDescriptor() {
		return tableDescriptorWithShortName("Car");
	}
	
	public MWTableDescriptor getCompanyDescriptor() {
		return tableDescriptorWithShortName("Company");
	}
	
	public MWTableDescriptor getComputerDescriptor() {
		return tableDescriptorWithShortName("Computer");
	}
	
	public MWTableDescriptor getEngineerDescriptor() {
		return tableDescriptorWithShortName("Engineer");
	}
	
	public MWTableDescriptor getFueledVehicleDescriptor() {
		return tableDescriptorWithShortName("FueledVehicle");
	}
	
	public MWTableDescriptor getIBMPCDescriptor() {
		return tableDescriptorWithShortName("IBMPC");
	}
	
	public MWTableDescriptor getMacDescriptor() {
		return tableDescriptorWithShortName("Mac");
	}
	
	public MWTableDescriptor getMainframeDescriptor() {
		return tableDescriptorWithShortName("Mainframe");
	}
	
	public MWTableDescriptor getNonFueledVehicleDescriptor() {
		return tableDescriptorWithShortName("NonFueledVehicle");
	}
	
	public MWTableDescriptor getPCDescriptor() {
		return tableDescriptorWithShortName("PC");
	}
	
	public MWTableDescriptor getPersonDescriptor() {
		return tableDescriptorWithShortName("Person");
	}
	
	public MWTableDescriptor getSalesRepDescriptor() {
		return tableDescriptorWithShortName("SalesRep");
	}
	
	public MWTableDescriptor getSoftwareEngineerDescriptor() {
		return tableDescriptorWithShortName("SoftwareEngineer");
	}
	
	public MWTableDescriptor getSportsCarDescriptor() {
		return tableDescriptorWithShortName("SportsCar");
	}
	
	public MWTableDescriptor getVehicleDescriptor() {
		return tableDescriptorWithShortName("Vehicle");
	}
	
	public void initializeBicycleDescriptor() {		
		MWTableDescriptor descriptor = getBicycleDescriptor();
		MWTable table = tableNamed("VEHICLE");
		descriptor.setPrimaryTable(table);

		try {
			descriptor.mapInheritedAttributesToRootMinusOne();
		} catch(ClassNotFoundException ex){throw new RuntimeException(ex);}
				
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);

		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getNonFueledVehicleDescriptor());

		//direct to field mapping
		addDirectMapping(descriptor, "description", table, "BICY_DES");
		
		//direct collection mapping
		MWRelationalDirectCollectionMapping partNumbersMapping = 
			(MWRelationalDirectCollectionMapping) addDirectMapping(descriptor, "partNumbers").asMWDirectCollectionMapping();	
		partNumbersMapping.setTargetTable(tableNamed("PARTNUMS"));
		partNumbersMapping.setDirectValueColumn(tableNamed("PARTNUMS").columnNamed("PART_NUM"));
		partNumbersMapping.setReference(tableNamed("PARTNUMS").referenceNamed("PARTNUMS_VEHICLE"));
		partNumbersMapping.setUseNoIndirection();
		
		
		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("id"));
		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("owner"));
		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("passengerCapacity"));

	}
	
	public void initializeBoatDescriptor() {		
		MWTableDescriptor descriptor = getBoatDescriptor();
		MWTable table = tableNamed("VEHICLE");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);

		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getNonFueledVehicleDescriptor());
	}
	
	public void initializeBusDescriptor() {		
		MWTableDescriptor descriptor = getBusDescriptor();
		MWTable table = tableNamed("BUS");
		descriptor.setPrimaryTable(table);

		try {
			descriptor.mapInheritedAttributesToRootMinusOne();
		} catch(ClassNotFoundException ex){throw new RuntimeException(ex);}
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
	
		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getFueledVehicleDescriptor());

		//multi-table info policy
		descriptor.addMultiTableInfoPolicy();
		
		MWTable fuelVehTable = tableNamed("FUEL_VEH");
		descriptor.addAssociatedTable(fuelVehTable);

		
		//direct to field mapping
		addDirectMapping(descriptor, "description", table, "DESCRIP");
		addDirectMapping(descriptor, "fuelType", fuelVehTable, "FUEL_TYP");
		
		//direct collection mapping
		MWRelationalDirectCollectionMapping partNumbersMapping = 
			(MWRelationalDirectCollectionMapping) addDirectMapping(descriptor, "partNumbers").asMWDirectCollectionMapping();	
		partNumbersMapping.setTargetTable(tableNamed("PARTNUMS"));
		partNumbersMapping.setDirectValueColumn(tableNamed("PARTNUMS").columnNamed("PART_NUM"));
		partNumbersMapping.setReference(tableNamed("PARTNUMS").referenceNamed("PARTNUMS_BUS"));
		partNumbersMapping.setUseNoIndirection();
				

		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("id"));
		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("owner"));
		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("passengerCapacity"));
		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("fuelCapacity"));

	}
	
	public void initializeBusTable() {
		MWTable table = database().addTable("BUS");
		
		addField(table,"DESCRIP", "varchar", 30);
		addPrimaryKeyField(table,"ID", "decimal", 15);
	}
	
	public void initializeCarDescriptor() {		
		MWTableDescriptor descriptor = getCarDescriptor();
		MWTable table = tableNamed("CAR");
		descriptor.setPrimaryTable(table);
	
		try {
			descriptor.mapInheritedAttributesToRootMinusOne();
		} catch(ClassNotFoundException ex){throw new RuntimeException(ex);}
	
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);

		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getFueledVehicleDescriptor());

		//multi-table info policy
		descriptor.addMultiTableInfoPolicy();
		
		MWTable fuelVehTable = tableNamed("FUEL_VEH");
		descriptor.addAssociatedTable(fuelVehTable);

		
		//direct to field mapping
		addDirectMapping(descriptor, "description", table, "CDESCRIP");
		addDirectMapping(descriptor, "fuelType", fuelVehTable, "FUEL_TYP");
			
		//direct collection mapping
		MWRelationalDirectCollectionMapping partNumbersMapping = 
			(MWRelationalDirectCollectionMapping) addDirectMapping(descriptor, "partNumbers").asMWDirectCollectionMapping();	
		partNumbersMapping.setTargetTable(tableNamed("PARTNUMS"));
		partNumbersMapping.setDirectValueColumn(tableNamed("PARTNUMS").columnNamed("PART_NUM"));
		partNumbersMapping.setReference(tableNamed("PARTNUMS").referenceNamed("PARTNUMS_CAR"));
		partNumbersMapping.setUseNoIndirection();

		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("id"));
		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("owner"));
		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("passengerCapacity"));
		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("fuelCapacity"));
	
	}

	public void initializeCarTable() {
		MWTable table = database().addTable("CAR");
		
		addField(table,"CDESCRIP", "varchar", 30);
		addField(table,"FUEL_CAP", "decimal", 10);
		addPrimaryKeyField(table,"ID", "decimal", 15);
	}
	
	public void initializeCompanyDescriptor() {
		
		MWTableDescriptor descriptor = getCompanyDescriptor();
		MWTable table = tableNamed("COMPANY");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        descriptor.getCachingPolicy().setCacheSize(100);

		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));
		
		//direct to field mapping
		addDirectMapping(descriptor, "id", table, "ID");
		addDirectMapping(descriptor, "name", table, "NAME");

		//1-many mappings
		MWOneToManyMapping vehiclesMapping = descriptor.addOneToManyMapping(descriptor.getMWClass().attributeNamed("vehicles"));
		vehiclesMapping.setReferenceDescriptor(getVehicleDescriptor());
		vehiclesMapping.setReference(getVehicleDescriptor().getPrimaryTable().referenceNamed("VEHICLE_COMPANY"));
		vehiclesMapping.setPrivateOwned(true);
	}

	public void initializeCompanyTable() {
		MWTable table = database().addTable("COMPANY");
		
		addPrimaryKeyField(table,"ID", "decimal", 15);
		addField(table,"NAME", "varchar", 30);
	}
	
	public void initializeComputerDescriptor() {		
		MWTableDescriptor descriptor = getComputerDescriptor();
		MWTable table = tableNamed("INH_COMP");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        descriptor.getCachingPolicy().setCacheSize(100);
		
		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));
		
		//afterload policy
		descriptor.addAfterLoadingPolicy();
		MWClass postLoadClass = typeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Computer");
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethodClass(postLoadClass);
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethod(methodNamed(postLoadClass, "addToDescriptor"));
		
		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.useClassExtractionMethodIndicatorPolicy();
		((MWClassIndicatorExtractionMethodPolicy) inheritancePolicy.getClassIndicatorPolicy()).setClassExtractionMethod(methodNamed(descriptor.getMWClass(), "getClassFromRow"));

		//direct to field mapping
		addDirectMapping(descriptor, "id", table, "ID");
		addDirectMapping(descriptor, "manufacturer", table, "MANUFAC");
		addDirectMapping(descriptor, "memory", table, "MEMORY");
		addDirectMapping(descriptor, "processorMake", table, "MAKE");
		addDirectMapping(descriptor, "processorSpeed", table, "SPEED");
	}
	
	@Override
	protected void initializeDatabase()  {
		super.initializeDatabase();
		this.initializeSequenceTable();
		this.initializeBusTable();
		this.initializeCarTable();
		this.initializeCompanyTable();
		this.initializeFuelVehTable();
		this.initializeInhCompTable();
		this.initializeInhMfTable();
		this.initializePartNumsTable();
		this.initializePerson2Table();
		this.initializeVehicleTable();
		
		this.initializeReferences();
	}
	
	protected void initializeReferences() {
		MWTable vehicleTable = tableNamed("VEHICLE");
		MWTable companyTable =  tableNamed("COMPANY");
		MWTable partNumsTable = tableNamed("PARTNUMS");
		MWTable busTable = tableNamed("BUS");
		MWTable carTable = database().tableNamed("CAR");
		MWTable inhCompTable =  tableNamed("INH_COMP");
		MWTable inhMfTable =  tableNamed("INH_MF");
		MWTable person2Table = tableNamed("PERSON2");
		
		addReferenceOnDB("VEHICLE_COMPANY", vehicleTable, companyTable, "OWNER_ID", "ID");
		addReferenceOnDB("PARTNUMS_BUS", partNumsTable, busTable, "VEHIC_ID", "ID");
		addReferenceOnDB("PARTNUMS_CAR", partNumsTable, carTable, "VEHIC_ID", "ID");
		addReferenceOnDB("PARTNUMS_VEHICLE", partNumsTable, vehicleTable, "VEHIC_ID", "ID");
		addReferenceOnDB("PERSON2_CAR", person2Table, carTable, "CAR_ID", "ID");				
		addReferenceOnDB("PERSON2_PERSON2", person2Table, person2Table, "BOSS_ID", "ID");				
		addReferenceOnDB("PERSON2_PERSON22", person2Table, person2Table, "FRIEND_ID", "ID");				
		addReferenceOnDB("PERSON2_PERSON23", person2Table, person2Table, "REP_ID", "ID");				
		addReferenceOnDB("INH_MF_INH_COMP", inhMfTable, inhCompTable, "MF_ID", "ID");
	}
	
	@Override
	protected void initializeDescriptors()  {
		super.initializeDescriptors();
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Bicycle");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Boat");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Bus");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Car");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Company");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Computer");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Engineer");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.FueledVehicle");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.IBMPC");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Mac");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Mainframe");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.NonFueledVehicle");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.PC");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Person");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.SalesRep");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.SoftwareEngineer");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.SportsCar");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Vehicle");
	
		
		initializeBicycleDescriptor();
		initializeBoatDescriptor();
		initializeBusDescriptor();
		initializeSportsCarDescriptor();
		initializeCarDescriptor();
		initializeEngineerDescriptor();
		initializeFueledVehicleDescriptor();
		initializeIBMPCDescriptor();
		initializeMacDescriptor();
		initializeMainframeDescriptor();
		initializeNonFueledVehicleDescriptor();
		initializePCDescriptor();
		initializeSalesRepDescriptor();
		initializeSoftwareEngineerDescriptor();
		initializePersonDescriptor();
		initializeVehicleDescriptor();
		initializeCompanyDescriptor();
		initializeComputerDescriptor();
	
	}	
	
	public void initializeEngineerDescriptor() {		
		MWTableDescriptor descriptor = getEngineerDescriptor();
		MWTable table = tableNamed("PERSON2");
		descriptor.setPrimaryTable(table);

		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);

		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getPersonDescriptor());
	}
	
	public void initializeFueledVehicleDescriptor() {		
		MWTableDescriptor descriptor = getFueledVehicleDescriptor();
		MWTable table = tableNamed("FUEL_VEH");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		
		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getVehicleDescriptor());

		//direct to field mapping
		addDirectMapping(descriptor, "fuelCapacity", table, "FUEL_CAP");
	}
	
	public void initializeFuelVehTable() {
		MWTable table = database().addTable("FUEL_VEH");

		addField(table,"FUEL_CAP", "decimal", 10);
		addField(table,"FUEL_TYP", "varchar", 30);
		addPrimaryKeyField(table,"ID", "decimal", 15);
	}	

	public void initializeIBMPCDescriptor() {
		MWTableDescriptor descriptor = getIBMPCDescriptor();
		MWTable table = tableNamed("INH_COMP");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);	

		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getPCDescriptor());

//		//afterload policy
		descriptor.addAfterLoadingPolicy();
		MWClass postLoadClass = descriptor.getMWClass();
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethodClass(postLoadClass);
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethod(methodNamed(postLoadClass, "addToDescriptor"));

		//direct to field mapping
		addDirectMapping(descriptor, "isClone", table, "CLONE");	
	}
	
	public void initializeInhCompTable() {
		MWTable table = database().addTable("INH_COMP");

		addField(table,"CLONE", "decimal", 1).setAllowsNull(false);
		addField(table,"CTYPE", "varchar", 20);
		addPrimaryKeyField(table,"ID", "decimal", 10);
		addField(table,"MAKE", "varchar", 100);
		addField(table,"MANUFAC", "varchar", 100);
		addField(table,"MEMORY", "decimal", 10);
		addField(table,"PCTYPE", "varchar", 20);
		addField(table,"SPEED", "decimal", 10);
	}	

	public void initializeInhMfTable() {
		MWTable table = database().addTable("INH_MF");

		addPrimaryKeyField(table,"MF_ID", "decimal", 10);
		addField(table,"PROCS", "decimal", 10);
	}	

	public void initializeMacDescriptor() {		
		MWTableDescriptor descriptor = getMacDescriptor();
		MWTable table = tableNamed("INH_COMP");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		
		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getPCDescriptor());

		//afterload policy
		descriptor.addAfterLoadingPolicy();
		MWClass postLoadClass = descriptor.getMWClass();
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethodClass(postLoadClass);
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethod(methodNamed(postLoadClass, "addToDescriptor"));
	}
	
	public void initializeMainframeDescriptor() {		
		MWTableDescriptor descriptor = getMainframeDescriptor();
		MWTable table = tableNamed("INH_COMP");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);

		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getComputerDescriptor());

		//afterload policy
		descriptor.addAfterLoadingPolicy();
		MWClass postLoadClass = descriptor.getMWClass();
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethodClass(postLoadClass);
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethod(methodNamed(postLoadClass, "addToDescriptor"));

		//multi-table info policy
		descriptor.addMultiTableInfoPolicy();
		MWDescriptorMultiTableInfoPolicy multiTablePolicy = (MWDescriptorMultiTableInfoPolicy) descriptor.getMultiTableInfoPolicy();

		MWTable inhMfTable = tableNamed("INH_MF");
		descriptor.addAssociatedTable(inhMfTable);
		multiTablePolicy.secondaryTableHolderFor(inhMfTable).setPrimaryKeysHaveSameName(false);
		multiTablePolicy.secondaryTableHolderFor(inhMfTable).setReference(inhMfTable.referenceNamed("INH_MF_INH_COMP"));
		//more needed here for the table associations		
		

		//direct to field mapping
		addDirectMapping(descriptor, "numberOfProcessors", inhMfTable, "PROCS");	
	}
	
	public void initializeNonFueledVehicleDescriptor() {
		
		MWTableDescriptor descriptor = getNonFueledVehicleDescriptor();
		MWTable table = tableNamed("VEHICLE");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);

		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getVehicleDescriptor());
	}
	
	public void initializePartNumsTable() {
		MWTable table = database().addTable("PARTNUMS");

		addPrimaryKeyField(table,"PART_NUM", "varchar", 30);
		addPrimaryKeyField(table,"VEHIC_ID", "decimal", 15);
	}	

	public void initializePerson2Table() {
		MWTable table = database().addTable("PERSON2");

		addField(table,"BOSS_ID", "decimal", 15);
		addField(table,"C_TYPE", "varchar", 100);
		addField(table,"CAR_ID", "decimal", 15);
		addField(table,"FRIEND_ID", "decimal", 15);
		addPrimaryKeyField(table,"ID", "decimal", 15);
		addField(table,"KNOWS_JAVA", "decimal", 1).setAllowsNull(false);
		addField(table,"NAME", "varchar", 20);
		addField(table,"REP_ID", "decimal", 15);
		
	}	

	public void initializePCDescriptor() {		
		MWTableDescriptor descriptor = getPCDescriptor();
		MWTable table = tableNamed("INH_COMP");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		
		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getComputerDescriptor());
		
		//afterload policy
		descriptor.addAfterLoadingPolicy();
		MWClass postLoadClass = descriptor.getMWClass();
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethodClass(postLoadClass);
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethod(methodNamed(postLoadClass, "addToDescriptor"));
	}
	
	public void initializePersonDescriptor() {		
		MWTableDescriptor descriptor = getPersonDescriptor();
		MWTable table = tableNamed("PERSON2");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        descriptor.getCachingPolicy().setCacheSize(100);

		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));
		
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		((MWRelationalClassIndicatorFieldPolicy) inheritancePolicy.getClassIndicatorPolicy()).setField(table.columnNamed("C_TYPE"));
		((MWRelationalClassIndicatorFieldPolicy) inheritancePolicy.getClassIndicatorPolicy()).setClassNameIsIndicator(true);
		
		//direct to field mappings
		addDirectMapping(descriptor, "id", table, "ID");
		addDirectMapping(descriptor, "name", table, "NAME");
	
		//1-1 mappings
		MWOneToOneMapping bestFriendMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("bestFriend"));
		bestFriendMapping.setReferenceDescriptor(getEngineerDescriptor());
		bestFriendMapping.setReference(getEngineerDescriptor().getPrimaryTable().referenceNamed("PERSON2_PERSON22"));

		MWOneToOneMapping carMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("car"));
		carMapping.setReferenceDescriptor(getCarDescriptor());
		carMapping.setReference(table.referenceNamed("PERSON2_CAR"));
		carMapping.setPrivateOwned(true);

		MWOneToOneMapping representitiveMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("representitive"));
		representitiveMapping.setReferenceDescriptor(getSalesRepDescriptor());
		representitiveMapping.setReference(table.referenceNamed("PERSON2_PERSON23"));
	}
	
	public void initializeSalesRepDescriptor() {		
		MWTableDescriptor descriptor = getSalesRepDescriptor();
		MWTable table = tableNamed("PERSON2");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		
		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getPersonDescriptor());
	}
	
	public void initializeSoftwareEngineerDescriptor() {		
		MWTableDescriptor descriptor = getSoftwareEngineerDescriptor();
		MWTable table = tableNamed("PERSON2");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		
		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getEngineerDescriptor());
		
		//direct to field mappings
		addDirectMapping(descriptor, "isExperiencedInJava", table, "KNOWS_JAVA");

		//1-1 mappings
		MWOneToOneMapping bossMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("boss"));
		bossMapping.setReferenceDescriptor(getEngineerDescriptor());
		bossMapping.setReference(getEngineerDescriptor().getPrimaryTable().referenceNamed("PERSON2_PERSON2"));
	}
	
	public void initializeSportsCarDescriptor() {		
		MWTableDescriptor sportsCarDescriptor = getSportsCarDescriptor();
		
		MWTable table = tableNamed("CAR");
		sportsCarDescriptor.setPrimaryTable(table);
		
		sportsCarDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		
		//inheritance policy
		sportsCarDescriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)sportsCarDescriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getCarDescriptor());
	}	
	
	public void initializeVehicleDescriptor() {		
		MWTableDescriptor descriptor = getVehicleDescriptor();
		MWTable table = tableNamed("VEHICLE");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        descriptor.getCachingPolicy().setCacheSize(100);
	
		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));
			
		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		
		MWRelationalClassIndicatorFieldPolicy classIndicatorPolicy = (MWRelationalClassIndicatorFieldPolicy) inheritancePolicy.getClassIndicatorPolicy();
		classIndicatorPolicy.setIndicatorType(new MWTypeDeclaration(classIndicatorPolicy, typeFor(java.lang.String.class)));
		
		classIndicatorPolicy.setField(table.columnNamed("TYPE"));
		
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getVehicleDescriptor()).setInclude(false);
		
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getNonFueledVehicleDescriptor()).setIndicatorValue("2");

		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getFueledVehicleDescriptor()).setIndicatorValue("1");

		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getBusDescriptor()).setIndicatorValue("3");

		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getCarDescriptor()).setIndicatorValue("4");

		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getBicycleDescriptor()).setIndicatorValue("6");

		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getSportsCarDescriptor()).setIndicatorValue("5");

		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getBoatDescriptor()).setIndicatorValue("7");


		//direct to field mappings
		addDirectMapping(descriptor, "id", table, "ID");
		addDirectMapping(descriptor, "passengerCapacity", table, "CAPACITY");

		//1-1 mappings
		MWOneToOneMapping ownerMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("owner"));
		ownerMapping.setReferenceDescriptor(getCompanyDescriptor());
		ownerMapping.setReference(table.referenceNamed("VEHICLE_COMPANY"));
	}
	
	public void initializeVehicleTable() {
		MWTable table = database().addTable("VEHICLE");

		addField(table,"BICY_DES", "varchar", 30);
		addField(table,"CAPACITY", "decimal", 10);
		addPrimaryKeyField(table,"ID", "decimal", 15);
		addField(table,"OWNER_ID", "decimal", 15);
		addField(table,"TYPE", "decimal", 15);
	}	
}
