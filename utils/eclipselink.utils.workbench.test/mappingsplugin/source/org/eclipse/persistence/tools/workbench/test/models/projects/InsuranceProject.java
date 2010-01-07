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

import java.util.TreeMap;

import org.eclipse.persistence.tools.workbench.test.models.insurance.Policy;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCopyPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInstantiationPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWSequencingPolicy;


public class InsuranceProject extends RelationalTestProject {

	public InsuranceProject() {
		super();
	}
	
	public static MWRelationalProject emptyProject() {
		MWRelationalProject project = new MWRelationalProject("Insurance", spiManager(), mySqlPlatform());

		// Defaults policy  
		project.getDefaultsPolicy().getCachingPolicy().setCacheSize(0);
		project.getDefaultsPolicy().setMethodAccessing(true);
		project.getDefaultsPolicy().getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);
		return project;
	} 

	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
	
	public MWTableDescriptor getAddressDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.Address");
	}
	
	public MWTableDescriptor getClaimDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.Claim");		
	}
	
	public MWTableDescriptor getHealthClaimDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.HealthClaim");		
	}
	
	public MWTableDescriptor getHealthPolicyDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.HealthPolicy");		
	}
	
	public MWTableDescriptor getHouseClaimDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.HouseClaim");		
	}
	
	public MWTableDescriptor getHousePolicyDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.HousePolicy");		
	}
	
	public MWTableDescriptor getPhoneDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.Phone");		
	}	

	public MWTableDescriptor getPolicyDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.Policy");		
	}	
	
	public MWTableDescriptor getPolicyHolderDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.PolicyHolder");		
	}
	
	public MWTableDescriptor getVehicleClaimDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.VehicleClaim");		
	}
	
	public MWTableDescriptor getVehiclePolicyDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.VehiclePolicy");		
	}
	
	
	public void initializeAddressDescriptor() {
		
		MWTableDescriptor addressDescriptor = getAddressDescriptor();
		MWTable addressTable = getProject().getDatabase().tableNamed("INS_ADDR");
		addressDescriptor.setPrimaryTable(addressTable);
		addressDescriptor.getRelationalTransactionalPolicy().setConformResultsInUnitOfWork(true);
		addressDescriptor.getRefreshCachePolicy().setAlwaysRefreshCache(true);
		
		addressDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		addressDescriptor.getCachingPolicy().setCacheSize(0);
		addressDescriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);
		
		//direct to field mappings
		MWDirectToFieldMapping cityMapping = addDirectMapping(addressDescriptor, "city", addressTable, "CITY");
		cityMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping countryMapping = addDirectMapping(addressDescriptor, "country", addressTable, "COUNTRY");
		countryMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping stateMapping = addDirectMapping(addressDescriptor, "state", addressTable, "STATE");
		stateMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping streetMapping = addDirectMapping(addressDescriptor, "street", addressTable, "STREET");
		streetMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping zipCodeMapping = addDirectMapping(addressDescriptor, "zipCode", addressTable, "ZIPCODE");
		zipCodeMapping.setUsesMethodAccessing(true);

		//one-to-one mappings
		MWOneToOneMapping policyHolderMapping = addressDescriptor.addOneToOneMapping(addressDescriptor.getMWClass().attributeNamed("policyHolder"));
		policyHolderMapping.setUsesMethodAccessing(true);
		policyHolderMapping.setReferenceDescriptor(getPolicyHolderDescriptor());
		policyHolderMapping.setReference(addressTable.referenceNamed("INS_ADDR_HOLDER"));

	}
	
	public void initializeHealthClaimDescriptor() {
		
		MWTableDescriptor healthClaimDescriptor = getHealthClaimDescriptor();
		MWTable claimTable = getProject().getDatabase().tableNamed("CLAIM");
		healthClaimDescriptor.setPrimaryTable(claimTable);
		healthClaimDescriptor.getRefreshCachePolicy().setAlwaysRefreshCache(true);
		healthClaimDescriptor.getRefreshCachePolicy().setDisableCacheHits(true);
		healthClaimDescriptor.getTransactionalPolicy().setDescriptorAlias("HC");

		healthClaimDescriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);
		//inheritance policy
		healthClaimDescriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)healthClaimDescriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getClaimDescriptor());
		
		//instantiation policy
		healthClaimDescriptor.addInstantiationPolicy();
		((MWDescriptorInstantiationPolicy) healthClaimDescriptor.getInstantiationPolicy()).setPolicyType(MWDescriptorInstantiationPolicy.METHOD);
		((MWDescriptorInstantiationPolicy) healthClaimDescriptor.getInstantiationPolicy()).setUseMethod(methodNamed(healthClaimDescriptor.getMWClass(), "example1"));
	
		//copy policy
		healthClaimDescriptor.addCopyPolicy();
		((MWDescriptorCopyPolicy) healthClaimDescriptor.getCopyPolicy()).setPolicyType(MWDescriptorCopyPolicy.CLONE);
		((MWDescriptorCopyPolicy) healthClaimDescriptor.getCopyPolicy()).setMethod(methodNamed(healthClaimDescriptor.getMWClass(), "healthClaimExample"));

		//direct to field mappings
		MWDirectToFieldMapping diseaseMapping = addDirectMapping(healthClaimDescriptor, "disease", claimTable, "DISEASE");
		diseaseMapping.setUsesMethodAccessing(true);
	
	}
	
	public void initializeHealthPolicyDescriptor() {
		
		MWTableDescriptor healthPolicyDescriptor = getHealthPolicyDescriptor();
		MWTable policyTable = getProject().getDatabase().tableNamed("POLICY");
		healthPolicyDescriptor.setPrimaryTable(policyTable);
	
		healthPolicyDescriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);

		//instantiation policy
		healthPolicyDescriptor.addInstantiationPolicy();
		((MWDescriptorInstantiationPolicy) healthPolicyDescriptor.getInstantiationPolicy()).setPolicyType(MWDescriptorInstantiationPolicy.FACTORY);
		((MWDescriptorInstantiationPolicy) healthPolicyDescriptor.getInstantiationPolicy()).setFactoryType(healthPolicyDescriptor.getMWClass());
		((MWDescriptorInstantiationPolicy) healthPolicyDescriptor.getInstantiationPolicy()).setFactoryMethod(methodNamed(healthPolicyDescriptor.getMWClass(), "example1"));
		((MWDescriptorInstantiationPolicy) healthPolicyDescriptor.getInstantiationPolicy()).setInstantiationMethod(methodNamed(healthPolicyDescriptor.getMWClass(), "clone"));

		//inheritance policy
		healthPolicyDescriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)healthPolicyDescriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getPolicyDescriptor());


		//direct to field mappings
		MWDirectToFieldMapping coverageRateMapping = addDirectMapping(healthPolicyDescriptor, "coverageRate", policyTable, "COV_RATE");
		coverageRateMapping.setUsesMethodAccessing(true);

	}
	
	public void initializeHolderTable() {	
		MWTable holderTable = database().addTable("HOLDER");
		
		addField(holderTable,"BDATE", "date");
		addField(holderTable,"F_NAME", "varchar", 20);
		addField(holderTable,"L_NAME", "varchar", 20);		
		addField(holderTable,"OCC", "varchar", 20);		
		addField(holderTable,"SEX", "char", 1);		
		addPrimaryKeyField(holderTable,"SSN", "decimal", 19);		
	}
	
	public void initializeHouseClaimDescriptor() {
		MWTableDescriptor houseClaimDescriptor = getHouseClaimDescriptor();
		MWTable claimTable = getProject().getDatabase().tableNamed("CLAIM");
		houseClaimDescriptor.setPrimaryTable(claimTable);
		
		//inheritance policy
		houseClaimDescriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)houseClaimDescriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getClaimDescriptor());

		//copy policy
		houseClaimDescriptor.addCopyPolicy();
		((MWDescriptorCopyPolicy) houseClaimDescriptor.getCopyPolicy()).setPolicyType(MWDescriptorCopyPolicy.CLONE);
		((MWDescriptorCopyPolicy) houseClaimDescriptor.getCopyPolicy()).setMethod(methodNamed(houseClaimDescriptor.getMWClass(), "example3"));
		
		//direct to field mappings
		MWDirectToFieldMapping areaMapping = addDirectMapping(houseClaimDescriptor, "area", claimTable, "AREA");
		areaMapping.setUsesMethodAccessing(true);
	}
	
	public void initializeHousePolicyDescriptor() {
		
		MWTableDescriptor housePolicyDescriptor = getHousePolicyDescriptor();
		MWTable policyTable = getProject().getDatabase().tableNamed("POLICY");
		housePolicyDescriptor.setPrimaryTable(policyTable);
		
		housePolicyDescriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);

		//inheritance policy
		housePolicyDescriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)housePolicyDescriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getPolicyDescriptor());


		//direct to field mappings
		MWDirectToFieldMapping dateOfConstructionMapping = addDirectMapping(housePolicyDescriptor, "dateOfConstruction", policyTable, "CNST_DTE");
		dateOfConstructionMapping.setUsesMethodAccessing(true);
	}
	
	public void initializeInsAddrTable() {
		MWTable addressTable = database().addTable("INS_ADDR");
		
		addField(addressTable,"CITY", "varchar", 25);
		addField(addressTable,"COUNTRY", "varchar", 20);
		addPrimaryKeyField(addressTable,"SSN", "decimal", 19);
		addField(addressTable,"STATE", "varchar", 2);
		addField(addressTable,"STREET", "varchar", 30);
		addField(addressTable,"ZIPCODE", "varchar", 10);
	}
	
	@Override
	protected void initializeDatabase() {
		super.initializeDatabase();

		this.initializeChildrenNamesTable();
		this.initializeClaimTable();
		this.initializeHolderTable();
		this.initializeInsAddrTable();
		this.initializePolicyTable();
		this.initializeVehicleClaimTable();
		this.initializeVehiclePolicyTable();
	
		MWTable childrenNamesTable = this.tableNamed("CHILDRENNAMES");
		MWTable holderTable = this.tableNamed("HOLDER");
		MWTable claimTable = this.tableNamed("CLAIM");
		MWTable policyTable = this.tableNamed("POLICY");
		MWTable insAddrTable = this.tableNamed("INS_ADDR");
	
		this.addReferenceOnDB("CHILDRENNAMES_HOLDER", childrenNamesTable, holderTable, "HOLDER_ID", "SSN");
		this.addReferenceOnDB("CLAIM_POLICY", claimTable, policyTable, "POL_ID", "POL_ID");
		this.addReferenceOnDB("HOLDER_INS_ADDR", holderTable, insAddrTable, "SSN", "SSN");
		this.addReferenceOnDB("INS_ADDR_HOLDER", insAddrTable, holderTable, "SSN", "SSN");
		this.addReferenceOnDB("POLICY_HOLDER", policyTable, holderTable, "SSN", "SSN");				
	}
	
	@Override
	protected void initializeDescriptors() {
		super.initializeDescriptors();

		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.Address");
	 	this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.Claim");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.HealthClaim");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.HealthPolicy");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.HouseClaim");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.HousePolicy");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.Phone");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.Policy");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.PolicyHolder");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.VehicleClaim");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.insurance.VehiclePolicy");
		
		this.initializeAddressDescriptor();
		this.initializeHealthClaimDescriptor();
		this.initializeHouseClaimDescriptor();
		this.initializeVehicleClaimDescriptor();
		this.initializeClaimDescriptor();
		this.initializeHousePolicyDescriptor();
		this.initializeHealthPolicyDescriptor();
		this.initializeVehiclePolicyDescriptor();
		this.initializePolicyDescriptor();
		this.initializePolicyHolderDescriptor();
		this.initializePhoneDescriptor();
	}
	
	
	public void initializeChildrenNamesTable() {	
		MWTable childrenNamesTable =  database().addTable("CHILDRENNAMES");
		
		addField(childrenNamesTable,"CHILD_NAME", "varchar", 20);
		addField(childrenNamesTable,"HOLDER_ID", "decimal", 19).setAllowsNull(false);		
	}
	
	public void initializeClaimDescriptor() {
		MWTableDescriptor claimDescriptor = getClaimDescriptor();
		MWTable claimTable = getProject().getDatabase().tableNamed("CLAIM");
		claimDescriptor.setPrimaryTable(claimTable);
		claimDescriptor.getTransactionalPolicy().setReadOnly(true);
		
		//caching policy
		claimDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		claimDescriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);
		claimDescriptor.getCachingPolicy().setCacheSize(0);
		
		//inheritance policy
		claimDescriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)claimDescriptor.getInheritancePolicy();
	
		MWRelationalClassIndicatorFieldPolicy classIndicatorPolicy = (MWRelationalClassIndicatorFieldPolicy) inheritancePolicy.getClassIndicatorPolicy();

		classIndicatorPolicy.setField(claimTable.columnNamed("CLM_TYPE"));
		classIndicatorPolicy.setIndicatorType(new MWTypeDeclaration(classIndicatorPolicy, typeFor(java.lang.String.class)));

		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getHouseClaimDescriptor()).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getHouseClaimDescriptor()).setIndicatorValue("O");
		
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getVehicleClaimDescriptor()).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getVehicleClaimDescriptor()).setIndicatorValue("V");
		
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getHealthClaimDescriptor()).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getHealthClaimDescriptor()).setIndicatorValue("H");
				
		
		//direct to field mappings
		MWDirectToFieldMapping amountMapping = addDirectMapping(claimDescriptor, "amount", claimTable, "AMOUNT");
		amountMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping idMapping = addDirectMapping(claimDescriptor, "id", claimTable, "CLM_ID");
		idMapping.setUsesMethodAccessing(true);
		
		
		//one-to-one mappings
		MWOneToOneMapping policyMapping = claimDescriptor.addOneToOneMapping(claimDescriptor.getMWClass().attributeNamed("policy"));
		policyMapping.setUsesMethodAccessing(true);
		policyMapping.setReferenceDescriptor(getPolicyDescriptor());
		policyMapping.setReference(claimTable.referenceNamed("CLAIM_POLICY"));
	}
	
	public void initializeClaimTable() {	
		MWTable claimTable = database().addTable("CLAIM");
		
		addField(claimTable,"AMOUNT", "decimal", 19).setSubSize(4);
		addField(claimTable,"AREA", "decimal", 19).setSubSize(4);		
		addPrimaryKeyField(claimTable,"CLM_ID", "decimal", 19);
		addField(claimTable,"CLM_TYPE", "varchar", 20);		
		addField(claimTable,"DISEASE", "varchar", 50);		
		addField(claimTable,"POL_ID", "decimal", 19);		
	}
	
	public void initializePhoneDescriptor() {	
		MWTableDescriptor descriptor = getPhoneDescriptor();
				
		//direct to field mappings
		addDirectMapping(descriptor, "areaCode", descriptor.getMWClass().methodWithSignature("getAreaCode()"), descriptor.getMWClass().methodWithSignature("setAreaCode(int)"));
		addDirectMapping(descriptor, "number", descriptor.getMWClass().methodWithSignature("getNumber()"), descriptor.getMWClass().methodWithSignature("setNumber(int)"));
		addDirectMapping(descriptor, "type", descriptor.getMWClass().methodWithSignature("getType()"), descriptor.getMWClass().methodWithSignature("setType(java.lang.String)"));
		
		descriptor.setActive(false);
	}
	public void initializePolicyDescriptor() {	
		MWTableDescriptor policyDescriptor = getPolicyDescriptor();
		MWTable policyTable = getProject().getDatabase().tableNamed("POLICY");
		policyDescriptor.setPrimaryTable(policyTable);
		policyDescriptor.getRelationalTransactionalPolicy().setConformResultsInUnitOfWork(true);	
				
		//caching policy
		policyDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		policyDescriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);
		policyDescriptor.getCachingPolicy().setCacheSize(0);

		//inheritance policy
		policyDescriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)policyDescriptor.getInheritancePolicy();

		MWRelationalClassIndicatorFieldPolicy classIndicatorPolicy = (MWRelationalClassIndicatorFieldPolicy) inheritancePolicy.getClassIndicatorPolicy();

		classIndicatorPolicy.setField(policyTable.columnNamed("POL_TYPE"));
		classIndicatorPolicy.setIndicatorType(new MWTypeDeclaration(classIndicatorPolicy, typeFor(java.lang.String.class)));
	
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getHousePolicyDescriptor()).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getHousePolicyDescriptor()).setIndicatorValue("H");
				
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getVehiclePolicyDescriptor()).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getVehiclePolicyDescriptor()).setIndicatorValue("V");
				
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getHealthPolicyDescriptor()).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getHealthPolicyDescriptor()).setIndicatorValue("E");
		
		//copy policy
		policyDescriptor.addCopyPolicy();
		((MWDescriptorCopyPolicy) policyDescriptor.getCopyPolicy()).setPolicyType(MWDescriptorCopyPolicy.INSTANTIATION_POLICY);
		
		//direct to field mappings
		MWDirectToFieldMapping descriptionMapping = addDirectMapping(policyDescriptor, "description", policyTable, "DESCRIPT");
		descriptionMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping maxCoverageMapping = addDirectMapping(policyDescriptor, "maxCoverage", policyTable, "MAX_COV");
		maxCoverageMapping.setUsesMethodAccessing(true);

		MWDirectToFieldMapping policyNumberMapping = addDirectMapping(policyDescriptor, "policyNumber", policyTable, "POL_ID");
		policyNumberMapping.setUsesMethodAccessing(true);

		//one-to-one mappings
		MWOneToOneMapping policyHolderMapping = policyDescriptor.addOneToOneMapping(policyDescriptor.getMWClass().attributeNamed("policyHolder"));
		policyHolderMapping.setUsesMethodAccessing(true);
		policyHolderMapping.setReferenceDescriptor(getPolicyHolderDescriptor());
		policyHolderMapping.setReference(policyTable.referenceNamed("POLICY_HOLDER"));

		//one-to-many mappings
		MWOneToManyMapping claimsMapping = policyDescriptor.addOneToManyMapping(policyDescriptor.getMWClass().attributeNamed("claims"));
		claimsMapping.setUsesMethodAccessing(true);
		claimsMapping.setReferenceDescriptor(getClaimDescriptor());
		claimsMapping.setPrivateOwned(true);
		claimsMapping.setReference(getClaimDescriptor().getPrimaryTable().referenceNamed("CLAIM_POLICY"));
		claimsMapping.setUseNoIndirection();	
	}
	
	public void initializePolicyHolderDescriptor() {
		MWTableDescriptor policyHolderDescriptor = getPolicyHolderDescriptor();
		MWTable holderTable = getProject().getDatabase().tableNamed("HOLDER");
		policyHolderDescriptor.setPrimaryTable(holderTable);
								
		//caching policy
		policyHolderDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		policyHolderDescriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);
		policyHolderDescriptor.getCachingPolicy().setCacheSize(0);

		//direct to field mappings
		MWDirectToFieldMapping birthDateMapping = addDirectMapping(policyHolderDescriptor, "birthDate", holderTable, "BDATE");
		birthDateMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping firstNameMapping = addDirectMapping(policyHolderDescriptor, "firstName", holderTable, "F_NAME");
		firstNameMapping.setUsesMethodAccessing(true);

		MWDirectToFieldMapping lastNameMapping = addDirectMapping(policyHolderDescriptor, "lastName", holderTable, "L_NAME");
		lastNameMapping.setUsesMethodAccessing(true);

		MWDirectToFieldMapping occupationMapping = addDirectMapping(policyHolderDescriptor, "occupation", holderTable, "OCC");
		occupationMapping.setUsesMethodAccessing(true);

		MWDirectToFieldMapping ssnMapping = addDirectMapping(policyHolderDescriptor, "ssn", holderTable, "SSN");
		ssnMapping.setUsesMethodAccessing(true);
		
		//object type mappings
		MWDirectToFieldMapping sexMapping = addDirectMapping(policyHolderDescriptor, "sex", holderTable, "SEX");
		sexMapping.setUsesMethodAccessing(true);
		MWObjectTypeConverter objectTypeConverter = sexMapping.setObjectTypeConverter();
		objectTypeConverter.setDataType(new MWTypeDeclaration(objectTypeConverter, typeNamed("java.lang.Character")));
		objectTypeConverter.setAttributeType(new MWTypeDeclaration(objectTypeConverter, typeNamed("java.lang.String")));
		
		try {
			objectTypeConverter.addValuePair("F", "Female");
			objectTypeConverter.addValuePair("M", "Male");	
		}
		catch (MWObjectTypeConverter.ConversionValueException cve) { 
			/*** shouldn't happen ***/
		}
		
		//direct collection mappings
		MWRelationalDirectCollectionMapping childrenNamesMapping = 
			(MWRelationalDirectCollectionMapping) addDirectMapping(policyHolderDescriptor, "childrenNames").asMWDirectCollectionMapping();
		childrenNamesMapping.setTargetTable(database().tableNamed("CHILDRENNAMES"));
		childrenNamesMapping.setDirectValueColumn(database().tableNamed("CHILDRENNAMES").columnNamed("CHILD_NAME"));
		//childrenNamesMapping.setUsesMethodAccessing(true);
		childrenNamesMapping.setReference(database().tableNamed("CHILDRENNAMES").referenceNamed("CHILDRENNAMES_HOLDER"));
		childrenNamesMapping.setUseNoIndirection();
		
		//one-to-one mappings
		MWOneToOneMapping addressMapping = policyHolderDescriptor.addOneToOneMapping(policyHolderDescriptor.getMWClass().attributeNamed("address"));
		addressMapping.setUsesMethodAccessing(true);
		addressMapping.setReferenceDescriptor(getAddressDescriptor());
		addressMapping.setReference(holderTable.referenceNamed("HOLDER_INS_ADDR"));
		addressMapping.setPrivateOwned(true);
		addressMapping.addTargetForeignKey((MWColumnPair) addressMapping.getReference().columnPairs().next());

		//one-to-many mappings
		MWOneToManyMapping policiesMapping = policyHolderDescriptor.addOneToManyMapping(policyHolderDescriptor.getMWClass().attributeNamed("policies"));
		policiesMapping.setUsesMethodAccessing(true);
		policiesMapping.setReferenceDescriptor(getPolicyDescriptor());
		policiesMapping.setPrivateOwned(true);
		policiesMapping.setReference(getPolicyDescriptor().getPrimaryTable().referenceNamed("POLICY_HOLDER"));
		policiesMapping.setUseNoIndirection();

		MWMapContainerPolicy containerPolicy = (MWMapContainerPolicy) policiesMapping.getContainerPolicy();
		containerPolicy.getDefaultingContainerClass().setContainerClass(typeFor(TreeMap.class));
		containerPolicy.setKeyMethod(typeFor(Policy.class).methodWithSignature("getPolicyHolder()"));
	}
	
	public void initializePolicyTable() {
		MWTable policyTable = database().addTable("POLICY");
		
		addField(policyTable,"CNST_DTE", "date");
		addField(policyTable,"COV_RATE", "decimal", 19).setSubSize(4);
		addField(policyTable,"DESCRIPT", "varchar", 100);
		addField(policyTable,"MAX_COV", "decimal", 19).setSubSize(4);
		addPrimaryKeyField(policyTable,"POL_ID", "decimal", 19);
		addField(policyTable,"POL_TYPE", "decimal", 1);
		addField(policyTable,"SSN", "decimal", 19);
	}	

	@Override
	protected void initializeSequencingPolicy() {
		MWSequencingPolicy policy = getProject().getSequencingPolicy();
		policy.setSequencingType(MWSequencingPolicy.NATIVE_SEQUENCING);
		policy.setPreallocationSize(50);
	}
	
	public void initializeVehicleClaimDescriptor() {
		MWTableDescriptor vehicleClaimDescriptor = getVehicleClaimDescriptor();
		MWTable vhclClmTable = getProject().getDatabase().tableNamed("VHCL_CLM");
		vehicleClaimDescriptor.setPrimaryTable(vhclClmTable);
		
		vehicleClaimDescriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);

		//inheritance policy
		vehicleClaimDescriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)vehicleClaimDescriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getClaimDescriptor());
		
		
		//direct to field mappings
		MWDirectToFieldMapping partMapping = addDirectMapping(vehicleClaimDescriptor, "part", vhclClmTable, "PART");
		partMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping partDescriptionMapping = addDirectMapping(vehicleClaimDescriptor, "partDescription", vhclClmTable, "PART_DESC");
		partDescriptionMapping.setUsesMethodAccessing(true);
	}
	
	public void initializeVehiclePolicyDescriptor() {	
		MWTableDescriptor vehiclePolicyDescriptor = getVehiclePolicyDescriptor();
		MWTable vhclPolTable = getProject().getDatabase().tableNamed("VHCL_POL");
		vehiclePolicyDescriptor.setPrimaryTable(vhclPolTable);
								
		vehiclePolicyDescriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);

		//inheritance policy
		vehiclePolicyDescriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)vehiclePolicyDescriptor.getInheritancePolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		inheritancePolicy.setParentDescriptor(getPolicyDescriptor());

		
		//direct to field mappings
		MWDirectToFieldMapping modelMapping = addDirectMapping(vehiclePolicyDescriptor, "model", vhclPolTable, "MODEL");
		modelMapping.setUsesMethodAccessing(true);
	}
	
	public void initializeVehicleClaimTable() {	
		MWTable vehicleClaimTable = database().addTable("VHCL_CLM");
		
		addPrimaryKeyField(vehicleClaimTable,"CLM_ID", "decimal", 19);
		addField(vehicleClaimTable,"PART", "varchar", 30);
		addField(vehicleClaimTable,"PART_DESC", "varchar", 30);		
	}
	
	public void initializeVehiclePolicyTable() {	
		MWTable vehiclePolicyTable = database().addTable("VHCL_POL");
		
		addField(vehiclePolicyTable,"MODEL", "varchar", 30);
		addPrimaryKeyField(vehiclePolicyTable,"POL_ID", "decimal", 19);
	}
}
