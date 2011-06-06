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

import java.util.Iterator;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorEventsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWTransactionalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


//This test project should be used for 4.x and 5.0 backward-compatibility testing
//RelationalDirectMapMapping was not supported in those versions
public class LegacyEmployeeProject extends RelationalTestProject {
	
	public LegacyEmployeeProject() {
		super();
	}
		
	public static MWRelationalProject emptyProject() {
		MWRelationalProject project = new MWRelationalProject("Employee", spiManager(), oraclePlatform());
			
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
	
	public MWTableDescriptor getAddressDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.Address");
	}
		
	public MWTableDescriptor getEmployeeDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.Employee");		
	}
	
	public MWAggregateDescriptor getEmploymentPeriodDescriptor()
	{
		return (MWAggregateDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.EmploymentPeriod");		
	}
	
	public MWTableDescriptor getLargeProjectDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.LargeProject");		
	}
		
	public MWTableDescriptor getPhoneNumberDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.PhoneNumber");		
	}
		
	public MWTableDescriptor getProjectDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.Project");		
	}
		
	public MWTableDescriptor getSmallProjectDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.SmallProject");		
	}
			
	public void initializeAddressDescriptor() {
		MWTableDescriptor addressDescriptor = getAddressDescriptor();
		MWTable addressTable = getProject().getDatabase().tableNamed("ADDRESS");
			
		addressDescriptor.setPrimaryTable(addressTable);
			
		addressDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        addressDescriptor.getCachingPolicy().setCacheSize(100);
        
		MWTableDescriptorLockingPolicy lockingPolicy = (MWTableDescriptorLockingPolicy)addressDescriptor.getLockingPolicy();
        lockingPolicy.setLockingType(MWLockingPolicy.OPTIMISTIC_LOCKING);
		lockingPolicy.setOptimisticLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_LOCKING_TYPE);
		lockingPolicy.setOptimisticVersionLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_TIMESTAMP);
		lockingPolicy.setVersionLockField(addressTable.columnNamed("STREET"));
		lockingPolicy.setStoreInCache(false);
		
			
		addDirectMapping(addressDescriptor, "city", addressTable, "CITY");
		addDirectMapping(addressDescriptor, "country", addressTable, "COUNTRY");
		addDirectMapping(addressDescriptor, "id", addressTable, "ADDRESS_ID");
		addDirectMapping(addressDescriptor, "postalCode", addressTable, "P_CODE");
		addDirectMapping(addressDescriptor, "province", addressTable, "PROVINCE");
		addDirectMapping(addressDescriptor, "street", addressTable, "STREET");
	
		
		addressDescriptor.setUsesSequencing(true);
		addressDescriptor.setSequenceNumberTable(addressTable);
		addressDescriptor.setSequenceNumberName("ADDR_SEQ");
		addressDescriptor.setSequenceNumberColumn(addressTable.columnNamed("ADDRESS_ID"));
			
	}
		
	public void initializeAddressTable() {
		MWTable addressTable = database().addTable("ADDRESS");
			
		addPrimaryKeyField(addressTable,"ADDRESS_ID", "integer");
		addField(addressTable,"CITY", "varchar", 20);
		addField(addressTable,"COUNTRY", "varchar", 20);
		addField(addressTable,"P_CODE", "varchar", 20);
		addField(addressTable,"PROVINCE", "varchar", 20);
		addField(addressTable,"STREET", "varchar", 20);
	}
		
	@Override
	protected void initializeDatabase() {
		super.initializeDatabase();

		this.initializeSequenceTable();
		this.initializeAddressTable();
		this.initializeEmployeeTable();
		this.initializeLProjectTable();
		this.initializePhoneTable();
		this.initializeProjEmpTable();
		this.initializeProjectTable();
		this.initializeResponsTable();
		this.initializeSalaryTable();
			
		MWTable employeeTable = this.tableNamed("EMPLOYEE");
		MWTable addressTable = this.tableNamed("ADDRESS");
		MWTable phoneTable = this.tableNamed("PHONE");
		MWTable projEmpTable = this.tableNamed("PROJ_EMP");
		MWTable projectTable = this.tableNamed("PROJECT");
		MWTable responsTable = this.tableNamed("RESPONS");
			
		this.addReferenceOnDB("EMPLOYEE_ADDRESS", employeeTable, addressTable, "ADDR_ID", "ADDRESS_ID");
		this.addReferenceOnDB("EMPLOYEE_EMPLOYEE", employeeTable, employeeTable, "MANAGER_ID", "EMP_ID");
		this.addReferenceOnDB("PHONE_EMPLOYEE", phoneTable, employeeTable, "EMP_ID", "EMP_ID");
		this.addReferenceOnDB("PROJ_EMP_EMPLOYEE", projEmpTable, employeeTable, "EMP_ID", "EMP_ID");
		this.addReferenceOnDB("PROJ_EMP_PROJECT", projEmpTable, projectTable, "PROJ_ID", "PROJ_ID");		
		this.addReferenceOnDB("RESPONS_EMPLOYEE", responsTable, employeeTable, "EMP_ID", "EMP_ID");
		this.addReferenceOnDB("PROJECT_EMPLOYEE", projectTable, employeeTable, "LEADER_ID", "EMP_ID");
	}
		
	@Override
	public void initializeDescriptors() {
		super.initializeDescriptors();

		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.Address");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.Employee");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.Project");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.LargeProject");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.PhoneNumber");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.SmallProject");
		
		this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.EmploymentPeriod");

		this.initializeAddressDescriptor();
		this.initializeEmploymentPeriodDescriptor();
		this.initializePhoneNumberDescriptor();
		this.initializeEmployeeDescriptor();
		this.initializeLargeProjectDescriptor();
		this.initializeSmallProjectDescriptor();
		this.initializeProjectDescriptor();
        
        
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWProjectDefaultsPolicy.AFTER_LOAD_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWProjectDefaultsPolicy.COPY_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWProjectDefaultsPolicy.INSTANTIATION_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWProjectDefaultsPolicy.INHERITANCE_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWRelationalProjectDefaultsPolicy.MULTI_TABLE_INFO_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWRelationalProjectDefaultsPolicy.INTERFACE_ALIAS_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWTransactionalProjectDefaultsPolicy.EVENTS_POLICY);
               
	}

	//remove new attributes that were added to the Employee class 
	//for new features not supporting in legacy projects.
	//this is overridden in EmployeeProject
	protected void legacyRemoveExtraAttributesFromEmployeeClass() {
		MWClass employeeClass = getEmployeeDescriptor().getMWClass();
		employeeClass.removeAttribute(employeeClass.attributeNamed("emailAddressMap"));
	}
	
	public void initializeEmployeeDescriptor() {
		MWTableDescriptor employeeDescriptor = getEmployeeDescriptor();
		MWClass employeeClass = employeeDescriptor.getMWClass();
		legacyRemoveExtraAttributesFromEmployeeClass();
			
		employeeDescriptor.getRefreshCachePolicy().setAlwaysRefreshCache(true);
		employeeDescriptor.getRefreshCachePolicy().setDisableCacheHits(true);
			
		MWTable employeeTable = getProject().getDatabase().tableNamed("EMPLOYEE");
		employeeDescriptor.setPrimaryTable(employeeTable);
	
		//multi-table policy
		employeeDescriptor.addMultiTableInfoPolicy();
	
		MWTable salaryTable = getProject().getDatabase().tableNamed("SALARY");
		employeeDescriptor.addAssociatedTable(salaryTable);
		
		//initialize policies
			
		employeeDescriptor.setUsesSequencing(true);
		employeeDescriptor.setSequenceNumberName("EMP_SEQ");
		employeeDescriptor.setSequenceNumberTable(employeeTable);
		employeeDescriptor.setSequenceNumberColumn(employeeTable.columnNamed("EMP_ID"));
		
        employeeDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        employeeDescriptor.getCachingPolicy().setCacheSize(100);
			
		MWTableDescriptorLockingPolicy lockingPolicy = (MWTableDescriptorLockingPolicy)employeeDescriptor.getLockingPolicy();
        lockingPolicy.setLockingType(MWLockingPolicy.OPTIMISTIC_LOCKING);
		lockingPolicy.setOptimisticLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_LOCKING_TYPE);
		lockingPolicy.setOptimisticVersionLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_VERSION);
		lockingPolicy.setVersionLockField(employeeTable.columnNamed("VERSION"));
		lockingPolicy.setStoreInCache(true);		
			
			
		//direct to field mappings
		addDirectMapping(employeeDescriptor, "firstName", employeeTable, "F_NAME");
		addDirectMapping(employeeDescriptor, "id", employeeTable, "EMP_ID");
		addDirectMapping(employeeDescriptor, "lastName", employeeTable, "L_NAME");
	
		
		addDirectMapping(employeeDescriptor, "salary", salaryTable, "SALARY");
			
		//object type mappings
		MWDirectToFieldMapping genderMapping = addDirectMapping(employeeDescriptor, "gender", employeeTable, "GENDER");  
		MWObjectTypeConverter genderObjectTypeConverter = genderMapping.setObjectTypeConverter();
		try {
			genderObjectTypeConverter.addValuePair("F", "Female");
			genderObjectTypeConverter.addValuePair("M", "Male");
		} catch (MWObjectTypeConverter.ConversionValueException cve) {
			/*** shouldn't happen ***/
		}
					
		//transformation mappings
		MWRelationalTransformationMapping normalHoursMapping = (MWRelationalTransformationMapping)
			employeeDescriptor.addTransformationMapping(employeeClass.attributeNamedFromAll("normalHours"));
		normalHoursMapping.setAttributeTransformer(methodNamed(employeeClass, "buildNormalHours"));	
		normalHoursMapping.addFieldTransformerAssociation(employeeTable.columnNamed("START_TIME"), methodNamed(employeeClass, "getStartTime"));
		normalHoursMapping.addFieldTransformerAssociation(employeeTable.columnNamed("END_TIME"), methodNamed(employeeClass, "getEndTime"));
					
		//1-1 mappings
		MWOneToOneMapping addressMapping = employeeDescriptor.addOneToOneMapping(employeeDescriptor.getMWClass().attributeNamedFromAll("address"));
		addressMapping.setReferenceDescriptor(getAddressDescriptor());
		addressMapping.setReference(employeeDescriptor.getPrimaryTable().referenceNamed("EMPLOYEE_ADDRESS"));
		addressMapping.setPrivateOwned(true);
			
		MWOneToOneMapping managerMapping = employeeDescriptor.addOneToOneMapping(employeeDescriptor.getMWClass().attributeNamedFromAll("manager"));
		managerMapping.setReferenceDescriptor(employeeDescriptor);
		managerMapping.setReference(employeeDescriptor.getPrimaryTable().referenceNamed("EMPLOYEE_EMPLOYEE"));
		
		//1-many mappings
		MWOneToManyMapping managedEmployeesMapping = employeeDescriptor.addOneToManyMapping(employeeDescriptor.getMWClass().attributeNamedFromAll("managedEmployees"));
		managedEmployeesMapping.setReferenceDescriptor(employeeDescriptor);
		managedEmployeesMapping.setReference(employeeDescriptor.getPrimaryTable().referenceNamed("EMPLOYEE_EMPLOYEE"));
		managedEmployeesMapping.setReadOnly(true);
			
		MWOneToManyMapping phoneNumberMapping = employeeDescriptor.addOneToManyMapping(employeeDescriptor.getMWClass().attributeNamedFromAll("phoneNumbers"));
		phoneNumberMapping.setReferenceDescriptor(getPhoneNumberDescriptor());
		phoneNumberMapping.setReference(getPhoneNumberDescriptor().getPrimaryTable().referenceNamed("PHONE_EMPLOYEE"));
		phoneNumberMapping.setPrivateOwned(true);
			
		//many-many mappings
		MWManyToManyMapping projectsMapping = employeeDescriptor.addManyToManyMapping(employeeDescriptor.getMWClass().attributeNamedFromAll("projects"));
		projectsMapping.setReferenceDescriptor(getProjectDescriptor());
		MWTable projEmpTable = database().tableNamed("PROJ_EMP");
		projectsMapping.setRelationTable(projEmpTable);
		projectsMapping.setSourceReference(projEmpTable.referenceNamed("PROJ_EMP_EMPLOYEE"));
		projectsMapping.setTargetReference(projEmpTable.referenceNamed("PROJ_EMP_PROJECT"));
			
		//direct collection mappings
		MWRelationalDirectCollectionMapping responsibilitiesListMapping = 
			(MWRelationalDirectCollectionMapping) addDirectMapping(employeeDescriptor, "responsibilitiesList").asMWDirectCollectionMapping();
		responsibilitiesListMapping.setTargetTable(database().tableNamed("RESPONS"));
		responsibilitiesListMapping.setDirectValueColumn(database().tableNamed("RESPONS").columnNamed("DESCRIP"));
		responsibilitiesListMapping.setReference(database().tableNamed("RESPONS").referenceNamed("RESPONS_EMPLOYEE"));
			
		//aggregate mappings
		MWAggregateMapping periodMapping = employeeDescriptor.addAggregateMapping(employeeDescriptor.getMWClass().attributeNamedFromAll("period"));
		periodMapping.setReferenceDescriptor(getEmploymentPeriodDescriptor());
			
		Iterator fieldAssociations = CollectionTools.sort(periodMapping.pathsToFields()).iterator();
		String[] fieldNames = new String[] {"END_DATE", "START_DATE"};
		for(int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) fieldAssociations.next();
			association.setColumn(employeeTable.columnNamed(fieldNames[i]));
		}
			
	}
		
	public void initializeEmployeeTable() {	
		MWTable employeeTable = database().addTable("EMPLOYEE");
			
		addField(employeeTable,"ADDR_ID", "integer");
		addPrimaryKeyField(employeeTable,"EMP_ID", "integer");
		addField(employeeTable,"END_DATE", "date");
		addField(employeeTable,"END_TIME", "date");
		addField(employeeTable,"F_NAME", "varchar", 20);
		addField(employeeTable,"GENDER", "varchar", 20);
		addField(employeeTable,"L_NAME", "varchar", 20);
		addField(employeeTable,"MANAGER_ID", "integer");
		addField(employeeTable,"START_DATE", "date");
		addField(employeeTable,"START_TIME", "date");
		addField(employeeTable,"VERSION", "integer");		
	}
		
	public void initializeEmploymentPeriodDescriptor() {
		MWAggregateDescriptor periodDescriptor = getEmploymentPeriodDescriptor();
			
		addDirectMapping(periodDescriptor, "endDate");
		addDirectMapping(periodDescriptor, "startDate");
	}
		
	public void initializeLargeProjectDescriptor() {	
		MWTableDescriptor largeProjectDescriptor = getLargeProjectDescriptor();	
		MWTable lProjectTable = getProject().getDatabase().tableNamed("LPROJECT");
		largeProjectDescriptor.setPrimaryTable(lProjectTable);
			
		largeProjectDescriptor.getRefreshCachePolicy().setOnlyRefreshCacheIfNewerVersion(true);
			
		largeProjectDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
			
		largeProjectDescriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)largeProjectDescriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
			
	
		largeProjectDescriptor.addEventsPolicy();
		MWClass largeProjectClass = largeProjectDescriptor.getMWClass();
		MWDescriptorEventsPolicy eventsPolicy = (MWDescriptorEventsPolicy) largeProjectDescriptor.getEventsPolicy();
		eventsPolicy.setPostBuildMethod(methodNamed(largeProjectClass, "postBuild"));
		eventsPolicy.setPostCloneMethod(methodNamed(largeProjectClass, "postClone"));
		eventsPolicy.setPostMergeMethod(methodNamed(largeProjectClass, "postMerge"));
		eventsPolicy.setPostRefreshMethod(methodNamed(largeProjectClass, "postRefresh"));
		eventsPolicy.setPreUpdateMethod(methodNamed(largeProjectClass, "preUpdate"));
		eventsPolicy.setAboutToUpdateMethod(methodNamed(largeProjectClass, "aboutToUpdate"));
		eventsPolicy.setPostUpdateMethod(methodNamed(largeProjectClass, "postUpdate"));
		eventsPolicy.setPreInsertMethod(methodNamed(largeProjectClass, "preInsert"));
		eventsPolicy.setAboutToInsertMethod(methodNamed(largeProjectClass, "aboutToInsert"));
		eventsPolicy.setPostInsertMethod(methodNamed(largeProjectClass, "postInsert"));
		eventsPolicy.setPreWritingMethod(methodNamed(largeProjectClass, "preWrite"));
		eventsPolicy.setPostWritingMethod(methodNamed(largeProjectClass, "postWrite"));
		eventsPolicy.setPreDeletingMethod(methodNamed(largeProjectClass, "preDelete"));
		eventsPolicy.setPostDeletingMethod(methodNamed(largeProjectClass, "postDelete"));
			
			
		addDirectMapping(largeProjectDescriptor, "budget", lProjectTable, "BUDGET");
		addDirectMapping(largeProjectDescriptor, "milestoneVersion", lProjectTable, "MILESTONE");
	}
		
		
	public void initializeLProjectTable() {
		MWTable lProjectTable = database().addTable("LPROJECT");
			
		addField(lProjectTable,"BUDGET", "integer");
		addField(lProjectTable,"MILESTONE", "date");
		addPrimaryKeyField(lProjectTable,"PROJ_ID", "integer");
	}
		
	public void initializePhoneNumberDescriptor() {
		MWTableDescriptor phoneNumberDescriptor = getPhoneNumberDescriptor();
		MWTable phoneTable = getProject().getDatabase().tableNamed("PHONE");
		phoneNumberDescriptor.setPrimaryTable(phoneTable);
			
		phoneNumberDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        phoneNumberDescriptor.getCachingPolicy().setCacheSize(100);
	
		MWTableDescriptorLockingPolicy lockingPolicy = (MWTableDescriptorLockingPolicy)phoneNumberDescriptor.getLockingPolicy();
        lockingPolicy.setLockingType(MWLockingPolicy.OPTIMISTIC_LOCKING);
		lockingPolicy.setOptimisticLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_LOCKING_TYPE);
		lockingPolicy.setOptimisticVersionLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_TIMESTAMP);
		lockingPolicy.setVersionLockField(phoneTable.columnNamed("P_NUMBER"));
		lockingPolicy.setStoreInCache(true);
			
		// dtf's
		addDirectMapping(phoneNumberDescriptor, "areaCode", phoneTable, "AREA_CODE");
			
		MWDirectToFieldMapping numberMapping = addDirectMapping(phoneNumberDescriptor, "number", phoneTable, "P_NUMBER");
		numberMapping.setReadOnly(true);
	
		addDirectMapping(phoneNumberDescriptor, "type", phoneTable, "TYPE");
		
		MWOneToOneMapping ownerMapping = phoneNumberDescriptor.addOneToOneMapping(phoneNumberDescriptor.getMWClass().attributeNamed("owner"));
		ownerMapping.setReferenceDescriptor(getEmployeeDescriptor());
		ownerMapping.setReference(phoneTable.referenceNamed("PHONE_EMPLOYEE"));
	}
		
	public void initializePhoneTable() {
		MWTable phoneTable = database().addTable("PHONE");
			
		addPrimaryKeyField(phoneTable, "EMP_ID", "integer");
		addPrimaryKeyField(phoneTable,"TYPE", "varchar", 20);
		addField(phoneTable,"AREA_CODE", "varchar", 3);
		addField(phoneTable,"P_NUMBER", "varchar", 7);
	}
		
	public void initializeProjEmpTable() {
		MWTable projEmpTable = database().addTable("PROJ_EMP");
			
		addPrimaryKeyField(projEmpTable,"EMP_ID", "integer");
		addPrimaryKeyField(projEmpTable,"PROJ_ID", "integer");
	}
		
	public void initializeProjectDescriptor() {				
		MWTableDescriptor projectDescriptor = getProjectDescriptor();
		MWTable projectTable = getProject().getDatabase().tableNamed("PROJECT");
			
		projectDescriptor.setPrimaryTable(projectTable);
			
		projectDescriptor.setUsesSequencing(true);
		projectDescriptor.setSequenceNumberName("PROJ_SEQ");
		projectDescriptor.setSequenceNumberTable(projectTable);
		projectDescriptor.setSequenceNumberColumn(projectTable.columnNamed("PROJ_ID"));
	
		MWTableDescriptorLockingPolicy lockingPolicy = (MWTableDescriptorLockingPolicy)projectDescriptor.getLockingPolicy();
        lockingPolicy.setLockingType(MWLockingPolicy.OPTIMISTIC_LOCKING);
		lockingPolicy.setOptimisticLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_LOCKING_TYPE);
		lockingPolicy.setOptimisticVersionLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_VERSION);
		lockingPolicy.setVersionLockField(projectTable.columnNamed("VERSION"));
		lockingPolicy.setStoreInCache(false);
	
		projectDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        projectDescriptor.getCachingPolicy().setCacheSize(100);
	
		addDirectMapping(projectDescriptor, "description", projectTable, "DESCRIP");
		addDirectMapping(projectDescriptor, "id", projectTable, "PROJ_ID");
		addDirectMapping(projectDescriptor, "name", projectTable, "PROJ_NAME");
		addDirectMapping(projectDescriptor, "version", projectTable, "VERSION");
	
		
		MWOneToOneMapping teamLeaderMapping = projectDescriptor.addOneToOneMapping(projectDescriptor.getMWClass().attributeNamed("teamLeader"));
		teamLeaderMapping.setReferenceDescriptor(getEmployeeDescriptor());
		teamLeaderMapping.setReference(projectDescriptor.getPrimaryTable().referenceNamed("PROJECT_EMPLOYEE"));
			
		projectDescriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)projectDescriptor.getInheritancePolicy();
		inheritancePolicy.setReadAllSubclassesView(tableNamed("EMPLOYEE"));
	
		MWRelationalClassIndicatorFieldPolicy classIndicatorPolicy = (MWRelationalClassIndicatorFieldPolicy) inheritancePolicy.getClassIndicatorPolicy();
	
		classIndicatorPolicy.setField(projectTable.columnNamed("PROJ_TYPE"));
		classIndicatorPolicy.setIndicatorType(new MWTypeDeclaration(classIndicatorPolicy, typeFor(java.lang.String.class)));
	
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getLargeProjectDescriptor()).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getLargeProjectDescriptor()).setIndicatorValue("L");
	
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getSmallProjectDescriptor()).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getSmallProjectDescriptor()).setIndicatorValue("S");
	}
		
	public void initializeProjectTable() {
		MWTable projectTable = database().addTable("PROJECT");
			
		addField(projectTable,"DESCRIP", "varchar", 200);
		addField(projectTable,"LEADER_ID", "integer");
		addPrimaryKeyField(projectTable,"PROJ_ID", "integer");
		addField(projectTable,"PROJ_NAME", "varchar", 30);
		addField(projectTable,"PROJ_TYPE", "varchar", 1);
		addField(projectTable,"VERSION", "integer");
	}
		
	public void initializeResponsTable() {
		MWTable responsTable = database().addTable("RESPONS");
			
		addPrimaryKeyField(responsTable,"DESCRIP", "varchar", 200);
		addPrimaryKeyField(responsTable,"EMP_ID", "integer");
	}
		
	public void initializeSalaryTable() {
		MWTable salaryTable = database().addTable("SALARY");
			
		addPrimaryKeyField(salaryTable,"EMP_ID", "integer");
		addField(salaryTable,"SALARY", "integer");
	}
		
	public void initializeSmallProjectDescriptor() {
		MWTableDescriptor smallProjectDescriptor = getSmallProjectDescriptor();
		MWTable projectTable = getProject().getDatabase().tableNamed("PROJECT");
		smallProjectDescriptor.setPrimaryTable(projectTable);
			
		smallProjectDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
			
		smallProjectDescriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)smallProjectDescriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
	}
		
}
