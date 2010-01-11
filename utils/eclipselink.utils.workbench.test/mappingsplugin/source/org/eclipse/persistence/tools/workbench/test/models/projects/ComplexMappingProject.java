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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import java.util.Iterator;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorAfterLoadingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorEventsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDefaultNullValuePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWSequencingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWTableGenerationPolicy;


public class ComplexMappingProject extends RelationalTestProject {

	public ComplexMappingProject() {
		super();
	}

	public static MWRelationalProject emptyProject() {
		MWRelationalProject project = new MWRelationalProject("ComplexMapping", spiManager(), oraclePlatform());

		// Defaults policy  
		project.getDefaultsPolicy().getCachingPolicy().setCacheSize(50);
		project.getDefaultsPolicy().getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		project.getDefaultsPolicy().setMethodAccessing(false);
		project.getDefaultsPolicy().getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_CHECK_DATABASE);

		return project;
	}

	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
	
	public MWTableDescriptor getAddressDescriptor() {
		return tableDescriptorWithShortName("Address");
	}

	public MWTableDescriptor getComputerDescriptor() {
		return tableDescriptorWithShortName("Computer");
	}

	public MWTableDescriptor getEmployeeDescriptor() {
		return tableDescriptorWithShortName("Employee");
	}

	public MWTableDescriptor getHardwareDescriptor() {
		return tableDescriptorWithShortName("Hardware");
	}

	public MWTableDescriptor getMonitorDescriptor() {
		return tableDescriptorWithShortName("Monitor");
	}

	public MWTableDescriptor getPhoneDescriptor() {
		return tableDescriptorWithShortName("Phone");
	}

	public MWTableDescriptor getShipmentDescriptor() {
		return tableDescriptorWithShortName("Shipment");
	}

	public void initializeAddressDescriptor() {
		MWTableDescriptor descriptor = getAddressDescriptor();
		MWClass addressClass = descriptor.getMWClass();
		MWTable table = tableNamed("MAP_ADD");
		
		descriptor.setPrimaryTable(table);

		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ_ID");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("A_ID"));

		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_CHECK_DATABASE);
		descriptor.getCachingPolicy().setCacheSize(50);
				
		//events
		descriptor.addEventsPolicy();		
		MWDescriptorEventsPolicy eventsPolicy = (MWDescriptorEventsPolicy) descriptor.getEventsPolicy();
		eventsPolicy.setPostBuildMethod(methodNamed(descriptor.getMWClass(), "postBuild"));
		eventsPolicy.setPostCloneMethod(methodNamed(descriptor.getMWClass(), "postClone"));
		eventsPolicy.setPostMergeMethod(methodNamed(descriptor.getMWClass(), "postMerge"));
		eventsPolicy.setPostRefreshMethod(methodNamed(descriptor.getMWClass(), "postBuild"));
		eventsPolicy.setPreUpdateMethod(methodNamed(descriptor.getMWClass(), "postClone"));
		eventsPolicy.setAboutToUpdateMethod(methodNamed(descriptor.getMWClass(), "postBuild"));
		eventsPolicy.setPostUpdateMethod(methodNamed(descriptor.getMWClass(), "postClone"));
		eventsPolicy.setPreInsertMethod(methodNamed(descriptor.getMWClass(), "postBuild"));
		eventsPolicy.setAboutToInsertMethod(methodNamed(descriptor.getMWClass(), "postClone"));
		eventsPolicy.setPostInsertMethod(methodNamed(descriptor.getMWClass(), "postMerge"));
		eventsPolicy.setPreWritingMethod(methodNamed(descriptor.getMWClass(), "postMerge"));
		eventsPolicy.setPostWritingMethod(methodNamed(descriptor.getMWClass(), "postBuild"));
		eventsPolicy.setPreDeletingMethod(methodNamed(descriptor.getMWClass(), "postMerge"));
		eventsPolicy.setPostDeletingMethod(methodNamed(descriptor.getMWClass(), "postBuild"));

		//direct to field mapping
		addDirectMapping(descriptor, "id", table, "A_ID");
		addDirectMapping(descriptor, "location", table, "LOCATION");

		//transformation mapping
		MWRelationalTransformationMapping provinceMapping = (MWRelationalTransformationMapping)
			descriptor.addTransformationMapping(addressClass.attributeNamed("province"));
		provinceMapping.setAttributeTransformer(methodNamed(addressClass, "getProvinceFromRow"));	
		provinceMapping.addFieldTransformerAssociation(table.columnNamed("PROVINCE"), methodNamed(addressClass, "getProvinceFromObject"));
		
		//1-1 mapping
		MWOneToOneMapping employeeMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("employee"));
		employeeMapping.setReferenceDescriptor(getEmployeeDescriptor());
		employeeMapping.setReference(descriptor.getPrimaryTable().referenceNamed("MAP_ADD_MAP_EMP"));
		employeeMapping.addTargetForeignKey((MWColumnPair)employeeMapping.getReference().columnPairs().next());
	}

	public void initializeComputerDescriptor() {
		MWTableDescriptor descriptor = getComputerDescriptor();
		MWTable table = tableNamed("MAP_COM");
		descriptor.setPrimaryTable(table);

		try {
			descriptor.mapInheritedAttributesToRootMinusOne();
		} catch(ClassNotFoundException ex) {throw new RuntimeException(ex);}
		
		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ_ID");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));
		//caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_PROJECT_DEFAULT);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);
		descriptor.getCachingPolicy().setCacheSize(100);
		//			
		//inheritance policy
		descriptor.addInheritancePolicy();		
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getHardwareDescriptor());

		//direct to field mappings
		addDirectMapping(descriptor, "id", table, "ID");
		addDirectMapping(descriptor, "description", table, "DESCRIP");
		MWDirectToFieldMapping serialNumberMapping = addDirectMapping(descriptor, "serialNumber", table, "SERL_NO");
		serialNumberMapping.setUsesMethodAccessing(true);

		//object type mapping
		MWObjectTypeConverter genderMapping = addDirectMapping(descriptor, "isMacintosh", table, "IS_MAC").setObjectTypeConverter();
		genderMapping.setDataType(new MWTypeDeclaration(genderMapping, typeNamed("java.lang.String")));
		genderMapping.setAttributeType(new MWTypeDeclaration(genderMapping, typeNamed("java.lang.Boolean")));

		try {
			genderMapping.addValuePair("No", "false");
			genderMapping.addValuePair("Yes", "true");
		}
		catch (MWObjectTypeConverter.ConversionValueException cve) {
			/*** shouldn't happen ***/
		}

		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("distibuted"));
		descriptor.removeMappingForAttribute(descriptor.getMWClass().attributeNamedFromAll("employee"));

		//1-1 mappings
		MWOneToOneMapping monitorMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("monitor"));
		monitorMapping.setReferenceDescriptor(getMonitorDescriptor());
		monitorMapping.setUsesMethodAccessing(true);
		monitorMapping.setReference(descriptor.getPrimaryTable().referenceNamed("MAP_COM_MAP_MON"));
		monitorMapping.setPrivateOwned(true);
	}

	@Override
	public void initializeDatabase() {
		super.initializeDatabase();

		this.initializeMapAddTable();
		this.initializeMapComTable();
		this.initializeMapEmpTable();
		this.initializeMapEmphTable();
		this.initializeMapEmspTable();
		this.initializeMapHrwTable();
		this.initializeMapMonTable();
		this.initializeMapPhoTable();
		this.initializeMapPolTable();
		this.initializeMapShipTable();

		this.initializeTableReferences();
	}

	protected void initializeTableReferences() {
		MWTable mapAddTable = this.tableNamed("MAP_ADD");
		MWTable mapEmpTable = this.tableNamed("MAP_EMP");
		MWTable mapComTable = this.tableNamed("MAP_COM");
		MWTable mapMonTable = this.tableNamed("MAP_MON");
		MWTable mapHrwTable = this.tableNamed("MAP_HRW");
		MWTable mapEmphTable = this.tableNamed("MAP_EMPH");
		MWTable mapPhoTable = this.tableNamed("MAP_PHO");
		MWTable mapEmspTable = this.tableNamed("MAP_EMSP");
		MWTable mapShipTable = this.tableNamed("MAP_SHIP");
		MWTable mapPolTable = this.tableNamed("MAP_POL");

		addReferenceOnDB("MAP_ADD_MAP_EMP", mapAddTable, mapEmpTable, "A_ID", "A_ID");
		addReferenceOnDB("MAP_COM_MAP_MON", mapComTable, mapMonTable, "MON_SER", "SERL_NO");
		addReferenceOnDB("MAP_EMP_MAP_EMP", mapEmpTable, mapEmpTable, "M_FNAME", "FNAME", "M_LNAME", "LNAME");
		addReferenceOnDB("MAP_EMP_MAP_EMP2", mapEmpTable, mapEmpTable, "M_FNAME", "FNAME", "M_LNAME", "LNAME");
		addReferenceOnDB("MAP_EMP_MAP_HRW", mapEmpTable, mapHrwTable, "FNAME", "EMP_FNAME", "LNAME", "EMP_LNAME");
		addReferenceOnDB("MAP_EMPH_MAP_EMP", mapEmphTable, mapEmpTable, "FNAME", "FNAME", "LNAME", "LNAME");
		addReferenceOnDB("MAP_EMPH_MAP_PHO", mapEmphTable, mapPhoTable, "P_ID", "P_ID");
		addReferenceOnDB("MAP_EMSP_MAP_EMP", mapEmspTable, mapEmpTable, "EMP_FNAME", "FNAME", "EMP_LNAME", "LNAME");
		addReferenceOnDB("MAP_EMSP_MAP_EMP2", mapEmspTable, mapEmpTable, "EMP_FNAME", "FNAME", "EMP_LNAME", "LNAME");
		addReferenceOnDB("MAP_EMSP_MAP_SHIP", mapEmspTable, mapShipTable, "SP_TSMIL", "SP_TSMIL", "SP_TS", "SP_TS");
		addReferenceOnDB("MAP_EMSP_MAP_SHIP2", mapEmspTable, mapShipTable, "SP_TSMIL", "SP_TSMIL", "SP_TS", "SP_TS");
		addReferenceOnDB("MAP_HRW_MAP_EMP", mapHrwTable, mapEmpTable, "EMP_FNAME", "FNAME", "EMP_LNAME", "LNAME");
		addReferenceOnDB("MAP_MON_MAP_COM", mapMonTable, mapComTable, "COM_SER", "SERL_NO");
		addReferenceOnDB("MAP_POL_MAP_EMP", mapPolTable, mapEmpTable, "FNAME", "FNAME", "LNAME", "LNAME");
	}

	@Override
	public void initializeDescriptors() {
		super.initializeDescriptors();
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexmapping.Address");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexmapping.Computer");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexmapping.Employee");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexmapping.Hardware");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexmapping.Monitor");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexmapping.Phone");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexmapping.Shipment");

		this.initializeAddressDescriptor();
		this.initializeComputerDescriptor();
		this.initializeEmployeeDescriptor();
		this.initializeMonitorDescriptor();
		this.initializeHardwareDescriptor();
		this.initializePhoneDescriptor();
		this.initializeShipmentDescriptor();
	}
	public void initializeEmployeeDescriptor() {
		MWTableDescriptor descriptor = getEmployeeDescriptor();
		MWClass employeeClass = descriptor.getMWClass();
		MWTable table = tableNamed("MAP_EMP");
		
		descriptor.setPrimaryTable(table);

		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		descriptor.getCachingPolicy().setCacheSize(100);
		
		//afterload policy
		descriptor.addAfterLoadingPolicy();
		MWClass postLoadClass = typeNamed("org.eclipse.persistence.tools.workbench.test.models.complexmapping.Employee");
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethodClass(postLoadClass);
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethod(methodNamed(postLoadClass, "addToDescriptor"));

		//direct to field mappings
		addDirectMapping(descriptor, "firstName", table, "FNAME");
		addDirectMapping(descriptor, "lastName", table, "LNAME");

		MWDirectToFieldMapping jobDescriptionMapping = addDirectMapping(descriptor, "jobDescription", table, "JDESC");
		jobDescriptionMapping.setUsesMethodAccessing(true);
		jobDescriptionMapping.setSerializedObjectConverter();

		MWTypeConversionConverter joiningDateMappingConverter = addDirectMapping(descriptor, "joiningDate", table, "JDAY").setTypeConversionConverter();
		joiningDateMappingConverter.setDataType(new MWTypeDeclaration(joiningDateMappingConverter, typeFor(java.sql.Date.class)));

		//object type mappings
		MWDirectToFieldMapping sexMapping = addDirectMapping(descriptor, "sex", table, "SEX");
		sexMapping.setUseNullValue(true);
		sexMapping.getNullValuePolicy().setNullValueType(new MWTypeDeclaration((MWDefaultNullValuePolicy) sexMapping.getNullValuePolicy(), typeNamed("java.lang.String")));
		sexMapping.getNullValuePolicy().setNullValue("female");
		MWObjectTypeConverter sexMappingConverter = sexMapping.setObjectTypeConverter();
		sexMappingConverter.setDataType(new MWTypeDeclaration(sexMappingConverter, typeFor(java.lang.String.class)));
		sexMappingConverter.setAttributeType(new MWTypeDeclaration(sexMappingConverter, typeFor(java.lang.String.class)));

		try {
			sexMappingConverter.addValuePair("F", "female");
			sexMappingConverter.addValuePair("M", "male");
		}
		catch (MWObjectTypeConverter.ConversionValueException cve) {
			throw new RuntimeException(cve);
		}

		//transformation mappings
		MWRelationalTransformationMapping dateAndTimeOfBirthMapping = (MWRelationalTransformationMapping)
			descriptor.addTransformationMapping(employeeClass.attributeNamed("dateAndTimeOfBirth"));
		dateAndTimeOfBirthMapping.setAttributeTransformer(methodNamed(employeeClass, "setDateAndTime"));	
		dateAndTimeOfBirthMapping.addFieldTransformerAssociation(table.columnNamed("BTIME"), methodNamed(employeeClass, "getTime"));
		dateAndTimeOfBirthMapping.addFieldTransformerAssociation(table.columnNamed("BDAY"), methodNamed(employeeClass, "getDate"));
		
		MWRelationalTransformationMapping designationMapping = (MWRelationalTransformationMapping)
			descriptor.addTransformationMapping(employeeClass.attributeNamed("designation"));
		designationMapping.setUsesMethodAccessing(true);
		designationMapping.setUseValueHolderIndirection();
		designationMapping.setAttributeTransformer(methodNamed(employeeClass, "getRankFromRow"));	
		designationMapping.addFieldTransformerAssociation(table.columnNamed("RANK"), methodNamed(employeeClass, "getRankFromObject"));
		
		//1-1 mappings
		MWOneToOneMapping computerMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("computer"));
		computerMapping.setReferenceDescriptor(getHardwareDescriptor());
		computerMapping.setUsesMethodAccessing(true);
		computerMapping.setReference(descriptor.getPrimaryTable().referenceNamed("MAP_EMP_MAP_HRW"));
		computerMapping.setPrivateOwned(true);
		Iterator fieldAssociations = computerMapping.getReference().columnPairs();
		computerMapping.addTargetForeignKey((MWColumnPair) fieldAssociations.next());
		computerMapping.addTargetForeignKey((MWColumnPair) fieldAssociations.next());
		
		MWOneToOneMapping managerMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("manager"));
		managerMapping.setUsesMethodAccessing(true);
		managerMapping.setReferenceDescriptor(descriptor);
		managerMapping.setReference(descriptor.getPrimaryTable().referenceNamed("MAP_EMP_MAP_EMP2"));
			
		//1-many mappings
		MWOneToManyMapping managedEmployeesMapping = descriptor.addOneToManyMapping(descriptor.getMWClass().attributeNamed("managedEmployees"));
		managedEmployeesMapping.setReferenceDescriptor(descriptor);
		managedEmployeesMapping.setUsesMethodAccessing(true);
		managedEmployeesMapping.setGetMethod(methodNamed(descriptor.getMWClass(), "getManagedEmployeesForTOPLink"));
		managedEmployeesMapping.setSetMethod(methodNamed(descriptor.getMWClass(), "setManagedEmployeesFromTOPLink"));
		managedEmployeesMapping.setReference(descriptor.getPrimaryTable().referenceNamed("MAP_EMP_MAP_EMP"));
		managedEmployeesMapping.setUseNoIndirection();
		
		//many-many mappings
		MWManyToManyMapping phoneNumberMapping = descriptor.addManyToManyMapping(descriptor.getMWClass().attributeNamed("phoneNumbers"));
		phoneNumberMapping.setReferenceDescriptor(getPhoneDescriptor());
		phoneNumberMapping.setPrivateOwned(true);
		MWTable mapEmphTable = database().tableNamed("MAP_EMPH");
		phoneNumberMapping.setRelationTable(mapEmphTable);
		phoneNumberMapping.setSourceReference(mapEmphTable.referenceNamed("MAP_EMPH_MAP_EMP"));
		phoneNumberMapping.setTargetReference(mapEmphTable.referenceNamed("MAP_EMPH_MAP_PHO"));
		
		MWManyToManyMapping shipmentsMapping = descriptor.addManyToManyMapping(descriptor.getMWClass().attributeNamed("shipments"));
		shipmentsMapping.setReferenceDescriptor(getShipmentDescriptor());
		MWTable mapEmspTable = database().tableNamed("MAP_EMSP");
		shipmentsMapping.setRelationTable(mapEmspTable);
		shipmentsMapping.setSourceReference(mapEmspTable.referenceNamed("MAP_EMSP_MAP_EMP"));
		shipmentsMapping.setTargetReference(mapEmspTable.referenceNamed("MAP_EMSP_MAP_SHIP"));
		shipmentsMapping.setUseNoIndirection();
				
		//direct collection mappings
		MWRelationalDirectCollectionMapping policiesMapping = 
			(MWRelationalDirectCollectionMapping) addDirectMapping(descriptor, "policies").asMWDirectCollectionMapping();
		policiesMapping.setTargetTable(database().tableNamed("MAP_POL"));
		policiesMapping.setDirectValueColumn(database().tableNamed("MAP_POL").columnNamed("POLICY"));
		policiesMapping.setReference(database().tableNamed("MAP_POL").referenceNamed("MAP_POL_MAP_EMP"));

	}
	public void initializeHardwareDescriptor() {
		MWTableDescriptor descriptor = getHardwareDescriptor();
		MWTable table = tableNamed("MAP_HRW");
		descriptor.setPrimaryTable(table);

		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_WEAK);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE);
		descriptor.getCachingPolicy().setCacheSize(100);

		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));

		//afterload policy
		descriptor.addAfterLoadingPolicy();
		MWClass postLoadClass = descriptor.getMWClass();
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethodClass(postLoadClass);
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethod(methodNamed(postLoadClass, "addToDescriptor"));
							
		//inheritance policy
		descriptor.addInheritancePolicy();		
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
	
		MWRelationalClassIndicatorFieldPolicy classIndicatorPolicy = (MWRelationalClassIndicatorFieldPolicy) inheritancePolicy.getClassIndicatorPolicy();

		classIndicatorPolicy.setField(table.columnNamed("TYPE"));
		classIndicatorPolicy.setIndicatorType(new MWTypeDeclaration(classIndicatorPolicy, typeFor(java.lang.String.class)));

		classIndicatorPolicy.getClassIndicatorValueForDescriptor(descriptor).setInclude(false);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getComputerDescriptor()).setIndicatorValue("C");
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getMonitorDescriptor()).setIndicatorValue("M");


		//direct to field mappings
		MWDirectToFieldMapping idMapping = addDirectMapping(descriptor, "id", table, "ID");
		idMapping.setUsesMethodAccessing(false);

		MWDirectToFieldMapping distributedMapping = addDirectMapping(descriptor, "distibuted", table, "DIST");
		distributedMapping.setUsesMethodAccessing(false);

		//1-1 mappings
		MWOneToOneMapping employeeMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("employee"));
		employeeMapping.setReferenceDescriptor(getEmployeeDescriptor());
		employeeMapping.setReference(descriptor.getPrimaryTable().referenceNamed("MAP_HRW_MAP_EMP"));
		employeeMapping.setUsesMethodAccessing(false);
	}

	public void initializeMapAddTable() {
		MWTable table = database().addTable("MAP_ADD");

		addPrimaryKeyField(table, "A_ID", "decimal", 15);
		addField(table, "LOCATION", "varchar", 20);
		addField(table, "PROVINCE", "varchar", 3);
	}

	public void initializeMapComTable() {
		MWTable table = database().addTable("MAP_COM");

		addPrimaryKeyField(table, "ID", "decimal", 15);
		addField(table, "DESCRIP", "varchar", 30);
		addField(table, "IS_MAC", "varchar", 3);
		addField(table, "MON_SER", "varchar", 30);
		addField(table, "SERL_NO", "varchar", 30);

	}

	public void initializeMapEmpTable() {
		MWTable table = database().addTable("MAP_EMP");

		addPrimaryKeyField(table, "FNAME", "varchar", 20);
		addPrimaryKeyField(table, "LNAME", "varchar", 20);
		addPrimaryKeyField(table, "SEX", "varchar", 10);

		addField(table, "BDAY", "date");
		addField(table, "BTIME", "date");
		addField(table, "JDAY", "date");
		addField(table, "A_ID", "decimal", 20);
		addField(table, "GENDER", "varchar", 10);
		addField(table, "M_FNAME", "varchar", 20);
		addField(table, "M_LNAME", "varchar", 20);
		addField(table, "RANK", "decimal", 10);
		addField(table, "JDESC", "longtext");
	}

	public void initializeMapEmphTable() {
		MWTable table = database().addTable("MAP_EMPH");

		addPrimaryKeyField(table, "FNAME", "varchar", 20);
		addPrimaryKeyField(table, "LNAME", "varchar", 20);
		addPrimaryKeyField(table, "P_ID", "decimal", 15);
	}

	public void initializeMapEmspTable() {
		MWTable table = database().addTable("MAP_EMSP");

		addPrimaryKeyField(table, "EMP_FNAME", "varchar", 20);
		addPrimaryKeyField(table, "EMP_LNAME", "varchar", 20);
		addPrimaryKeyField(table, "SP_TS", "date");
		addPrimaryKeyField(table, "SP_TSMIL", "decimal", 10);
		addField(table, "STATUS", "varchar", 1);
	}

	public void initializeMapHrwTable() {
		MWTable table = database().addTable("MAP_HRW");

		addPrimaryKeyField(table, "ID", "decimal", 15);
		addField(table, "EMP_FNAME", "varchar", 25);
		addField(table, "EMP_LNAME", "varchar", 25);
		addField(table, "DIST", "varchar", 5);
		addField(table, "TYPE", "varchar", 5);
	}

	public void initializeMapMonTable() {
		MWTable table = database().addTable("MAP_MON");

		addPrimaryKeyField(table, "ID", "decimal", 15);
		addField(table, "BRAND", "varchar", 30);
		addField(table, "COM_SER", "varchar", 30);
		addField(table, "MSIZE", "decimal", 10);
		addField(table, "SERL_NO", "varchar", 30);
	}

	public void initializeMapPhoTable() {
		MWTable table = database().addTable("MAP_PHO");

		addPrimaryKeyField(table, "P_ID", "decimal", 15);
		addField(table, "AREACODE", "varchar", 15);
		addField(table, "PNUMBER", "varchar", 30);
	}

	public void initializeMapPolTable() {
		MWTable table = database().addTable("MAP_POL");

		addPrimaryKeyField(table, "FNAME", "varchar", 20);
		addPrimaryKeyField(table, "LNAME", "varchar", 20);
		addPrimaryKeyField(table, "POLICY", "varchar", 20);
	}

	public void initializeMapShipTable() {
		MWTable table = database().addTable("MAP_SHIP");

		addPrimaryKeyField(table, "SP_TS", "date");
		addPrimaryKeyField(table, "SP_TSMIL", "decimal", 10);
		addField(table, "QUANTITY", "varchar", 20);
		addField(table, "SHP_MODE", "varchar", 50);
	}

	public void initializeMonitorDescriptor() {
		MWTableDescriptor descriptor = getMonitorDescriptor();
		MWTable table = tableNamed("MAP_MON");
		descriptor.setPrimaryTable(table);

		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_WEAK_WITH_SOFT_SUBCACHE);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_CHECK_DATABASE);
		descriptor.getCachingPolicy().setCacheSize(100);
		
		//afterload policy
		descriptor.addAfterLoadingPolicy();
		MWClass postLoadClass = descriptor.getMWClass();
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethodClass(postLoadClass);
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethod(methodNamed(postLoadClass, "addToDescriptor"));
			
		//inheritance policy	
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getHardwareDescriptor());

		//direct to field mapping
		MWDirectToFieldMapping brandMapping = addDirectMapping(descriptor, "brand", table, "BRAND");
		brandMapping.setUsesMethodAccessing(true);

		MWDirectToFieldMapping serialNumberMapping = addDirectMapping(descriptor, "serialNumber", table, "SERL_NO");
		serialNumberMapping.setUsesMethodAccessing(true);

		MWDirectToFieldMapping sizeMapping = addDirectMapping(descriptor, "size", table, "MSIZE");
		sizeMapping.setUsesMethodAccessing(true);

		//1-1 mappings
		MWOneToOneMapping computerMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("computer"));
		computerMapping.setReferenceDescriptor(getComputerDescriptor());
		computerMapping.setUsesMethodAccessing(true);
		computerMapping.setReference(descriptor.getPrimaryTable().referenceNamed("MAP_MON_MAP_COM"));

	}

	public void initializePhoneDescriptor() {
		MWTableDescriptor descriptor = getPhoneDescriptor();
		MWTable table = tableNamed("MAP_PHO");
		descriptor.setPrimaryTable(table);

		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_WEAK_WITH_HARD_SUBCACHE);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE);
		descriptor.getCachingPolicy().setCacheSize(100);

		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ_ID");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("P_ID"));

		//direct to field mapping
		addDirectMapping(descriptor, "id", table, "P_ID");
		addDirectMapping(descriptor, "areaCode", table, "AREACODE");
		addDirectMapping(descriptor, "number", table, "PNUMBER");
	}

	@Override
	protected void initializeSequencingPolicy() {
		MWSequencingPolicy policy = getProject().getSequencingPolicy();
		policy.setSequencingType(MWSequencingPolicy.DEFAULT_SEQUENCING);
		policy.setPreallocationSize(25);
	}

	@Override
	protected void initializeTableGenerationPolicy() {
		MWTableGenerationPolicy tgPolicy = getProject().getTableGenerationPolicy();
		tgPolicy.setDefaultPrimaryKeyName("foo");
		tgPolicy.setPrimaryKeySearchPattern("*foo");
	}

	public void initializeShipmentDescriptor() {
		MWTableDescriptor descriptor = getShipmentDescriptor();
		MWTable table = tableNamed("MAP_SHIP");
		descriptor.setPrimaryTable(table);

		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_NONE);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);
		descriptor.getCachingPolicy().setCacheSize(100);
		//direct to field mappings
		addDirectMapping(descriptor, "creationTimestamp", table, "SP_TS");
		addDirectMapping(descriptor, "creationTimestampMillis", table, "SP_TSMIL");
		addDirectMapping(descriptor, "quantityShipped", table, "QUANTITY");
		addDirectMapping(descriptor, "shipMode", table, "SHP_MODE");

		//many-many mappings
		MWManyToManyMapping employeesMapping = descriptor.addManyToManyMapping(descriptor.getMWClass().attributeNamed("employees"));
		employeesMapping.setReferenceDescriptor(getEmployeeDescriptor());
		employeesMapping.setReadOnly(true);
		MWTable mapEmphTable = database().tableNamed("MAP_EMSP");
		employeesMapping.setRelationTable(mapEmphTable);
		employeesMapping.setSourceReference(mapEmphTable.referenceNamed("MAP_EMSP_MAP_SHIP2"));
		employeesMapping.setTargetReference(mapEmphTable.referenceNamed("MAP_EMSP_MAP_EMP2"));
		employeesMapping.setUseNoIndirection();
	}

}
