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

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;



public class InsuranceRuntimeProject {

	private Project runtimeProject;

	public InsuranceRuntimeProject() {
		this.runtimeProject = new Project();
		this.runtimeProject.setName("Insurance");
		applyLogin();

		this.runtimeProject.addDescriptor(buildAddressDescriptor());
		this.runtimeProject.addDescriptor(buildClaimDescriptor());
		this.runtimeProject.addDescriptor(buildHealthClaimDescriptor());
		this.runtimeProject.addDescriptor(buildHealthPolicyDescriptor());
		this.runtimeProject.addDescriptor(buildHouseClaimDescriptor());
		this.runtimeProject.addDescriptor(buildHousePolicyDescriptor());
		this.runtimeProject.addDescriptor(buildPolicyDescriptor());
		this.runtimeProject.addDescriptor(buildPolicyHolderDescriptor());
		this.runtimeProject.addDescriptor(buildVehicleClaimDescriptor());
		this.runtimeProject.addDescriptor(buildVehiclePolicyDescriptor());
	}

	public void applyLogin() {
		DatabaseLogin login = new DatabaseLogin();
		login.usePlatform(new org.eclipse.persistence.platform.database.MySQLPlatform());
		login.setDriverClassName(TestDatabases.mySQLDriverClassName());
		login.setConnectionString(TestDatabases.mySQLServerURL());
		login.setUserName(TestDatabases.userName());
		login.setPassword(TestDatabases.password());

		// Configuration properties.
		login.useNativeSequencing();
		login.getDefaultSequence().setPreallocationSize(50);
		login.setShouldCacheAllStatements(false);
		login.setUsesByteArrayBinding(true);
		login.setUsesStringBinding(false);
		if (login.shouldUseByteArrayBinding()) { // Can only be used with binding.
			login.setUsesStreamsForBinding(false);
		}
		login.setShouldForceFieldNamesToUpperCase(false);
		login.setShouldOptimizeDataConversion(true);
		login.setShouldTrimStrings(true);
		login.setUsesBatchWriting(false);
		if (login.shouldUseBatchWriting()) { // Can only be used with batch writing.
			login.setUsesJDBCBatchWriting(true);
		}
		login.setUsesExternalConnectionPooling(false);
		login.setUsesExternalTransactionController(false);
		this.runtimeProject.setLogin(login);
	}

	public RelationalDescriptor buildAddressDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Address.class.getName());
		descriptor.addTableName("INS_ADDR");
		descriptor.addPrimaryKeyFieldName("INS_ADDR.SSN");

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(0);
		descriptor.alwaysConformResultsInUnitOfWork();
		descriptor.alwaysRefreshCache();
		descriptor.setAlias("Address");

		// Query manager.
		descriptor.getDescriptorQueryManager().assumeExistenceForDoesExist();

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping cityMapping = new DirectToFieldMapping();
		cityMapping.setAttributeName("city");
		cityMapping.setGetMethodName("getCity");
		cityMapping.setSetMethodName("setCity");
		cityMapping.setFieldName("INS_ADDR.CITY");
		descriptor.addMapping(cityMapping);

		DirectToFieldMapping countryMapping = new DirectToFieldMapping();
		countryMapping.setAttributeName("country");
		countryMapping.setGetMethodName("getCountry");
		countryMapping.setSetMethodName("setCountry");
		countryMapping.setFieldName("INS_ADDR.COUNTRY");
		descriptor.addMapping(countryMapping);

		DirectToFieldMapping stateMapping = new DirectToFieldMapping();
		stateMapping.setAttributeName("state");
		stateMapping.setGetMethodName("getState");
		stateMapping.setSetMethodName("setState");
		stateMapping.setFieldName("INS_ADDR.STATE");
		descriptor.addMapping(stateMapping);

		DirectToFieldMapping streetMapping = new DirectToFieldMapping();
		streetMapping.setAttributeName("street");
		streetMapping.setGetMethodName("getStreet");
		streetMapping.setSetMethodName("setStreet");
		streetMapping.setFieldName("INS_ADDR.STREET");
		descriptor.addMapping(streetMapping);

		DirectToFieldMapping zipCodeMapping = new DirectToFieldMapping();
		zipCodeMapping.setAttributeName("zipCode");
		zipCodeMapping.setGetMethodName("getZipCode");
		zipCodeMapping.setSetMethodName("setZipCode");
		zipCodeMapping.setFieldName("INS_ADDR.ZIPCODE");
		descriptor.addMapping(zipCodeMapping);

		OneToOneMapping policyHolderMapping = new OneToOneMapping();
		policyHolderMapping.setAttributeName("policyHolder");
		policyHolderMapping.setGetMethodName("getPolicyHolder");
		policyHolderMapping.setSetMethodName("setPolicyHolder");
		policyHolderMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.PolicyHolder.class.getName());
		policyHolderMapping.dontUseIndirection();
		policyHolderMapping.addForeignKeyFieldName("INS_ADDR.SSN", "HOLDER.SSN");
		descriptor.addMapping(policyHolderMapping);

		return descriptor;
	}

	public RelationalDescriptor buildClaimDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Claim.class.getName());
		descriptor.addTableName("CLAIM");
		descriptor.addPrimaryKeyFieldName("CLAIM.CLM_ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setClassIndicatorFieldName("CLAIM.CLM_TYPE");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.insurance.HouseClaim.class.getName(), "O");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.insurance.VehicleClaim.class.getName(), "V");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.insurance.HealthClaim.class.getName(), "H");
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(0);
		descriptor.setReadOnly();
		descriptor.setAlias("Claim");

		// Query manager.
		descriptor.getDescriptorQueryManager().assumeExistenceForDoesExist();

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping amountMapping = new DirectToFieldMapping();
		amountMapping.setAttributeName("amount");
		amountMapping.setGetMethodName("getAmount");
		amountMapping.setSetMethodName("setAmount");
		amountMapping.setFieldName("CLAIM.AMOUNT");
		descriptor.addMapping(amountMapping);

		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setGetMethodName("getId");
		idMapping.setSetMethodName("setId");
		idMapping.setFieldName("CLAIM.CLM_ID");
		descriptor.addMapping(idMapping);

		OneToOneMapping policyMapping = new OneToOneMapping();
		policyMapping.setAttributeName("policy");
		policyMapping.setGetMethodName("getPolicy");
		policyMapping.setSetMethodName("setPolicy");
		policyMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Policy.class.getName());
		policyMapping.dontUseIndirection();
		policyMapping.addForeignKeyFieldName("CLAIM.POL_ID", "POLICY.POL_ID");
		descriptor.addMapping(policyMapping);

		return descriptor;
	}

	public RelationalDescriptor buildHealthClaimDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.HealthClaim.class.getName());
		descriptor.addTableName("CLAIM");
		descriptor.addPrimaryKeyFieldName("CLAIM.CLM_ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Claim.class.getName());
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.alwaysRefreshCache();
		descriptor.disableCacheHits();
		descriptor.setAlias("HC");
		descriptor.useCloneCopyPolicy("healthClaimExample");
		descriptor.useMethodInstantiationPolicy("example1");

		// Query manager.
		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeExistence);

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping diseaseMapping = new DirectToFieldMapping();
		diseaseMapping.setAttributeName("disease");
		diseaseMapping.setGetMethodName("getDisease");
		diseaseMapping.setSetMethodName("setDisease");
		diseaseMapping.setFieldName("CLAIM.DISEASE");
		descriptor.addMapping(diseaseMapping);

		return descriptor;
	}

	public RelationalDescriptor buildHealthPolicyDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.HealthPolicy.class.getName());
		descriptor.addTableName("POLICY");
		descriptor.addPrimaryKeyFieldName("POLICY.POL_ID");
		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Policy.class.getName());
		descriptor.getDescriptorInheritancePolicy().dontReadSubclassesOnQueries();

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.setAlias("HealthPolicy");
		descriptor.getInstantiationPolicy().useFactoryInstantiationPolicy(org.eclipse.persistence.tools.workbench.test.models.insurance.HealthPolicy.class.getName(), "clone", "example1");

		// Query manager.
		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeExistence);

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping coverageRateMapping = new DirectToFieldMapping();
		coverageRateMapping.setAttributeName("coverageRate");
		coverageRateMapping.setGetMethodName("getCoverageRate");
		coverageRateMapping.setSetMethodName("setCoverageRate");
		coverageRateMapping.setFieldName("POLICY.COV_RATE");
		descriptor.addMapping(coverageRateMapping);

		return descriptor;
	}

	public RelationalDescriptor buildHouseClaimDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.HouseClaim.class.getName());
		descriptor.addTableName("CLAIM");
		descriptor.addPrimaryKeyFieldName("CLAIM.CLM_ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Claim.class.getName());
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.setAlias("HouseClaim");
		descriptor.useCloneCopyPolicy("example3");

		// Query manager.
		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeExistence);

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping areaMapping = new DirectToFieldMapping();
		areaMapping.setAttributeName("area");
		areaMapping.setGetMethodName("getArea");
		areaMapping.setSetMethodName("setArea");
		areaMapping.setFieldName("CLAIM.AREA");
		descriptor.addMapping(areaMapping);

		return descriptor;
	}

	public RelationalDescriptor buildHousePolicyDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.HousePolicy.class.getName());
		descriptor.addTableName("POLICY");
		descriptor.addPrimaryKeyFieldName("POLICY.POL_ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Policy.class.getName());
		descriptor.getDescriptorInheritancePolicy().dontReadSubclassesOnQueries();

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.setAlias("HousePolicy");

		// Query manager.
		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeExistence);

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping dateOfConstructionMapping = new DirectToFieldMapping();
		dateOfConstructionMapping.setAttributeName("dateOfConstruction");
		dateOfConstructionMapping.setGetMethodName("getDateOfConstruction");
		dateOfConstructionMapping.setSetMethodName("setDateOfConstruction");
		dateOfConstructionMapping.setFieldName("POLICY.CNST_DTE");
		descriptor.addMapping(dateOfConstructionMapping);

		return descriptor;
	}

	public RelationalDescriptor buildPolicyDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Policy.class.getName());
		descriptor.addTableName("POLICY");
		descriptor.addPrimaryKeyFieldName("POLICY.POL_ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setClassIndicatorFieldName("POLICY.POL_TYPE");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.insurance.HealthPolicy.class.getName(), "E");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.insurance.VehiclePolicy.class.getName(), "V");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.insurance.HousePolicy.class.getName(), "H");
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(0);
		descriptor.alwaysConformResultsInUnitOfWork();
		descriptor.setAlias("Policy");
		descriptor.useInstantiationCopyPolicy();


		// Query manager.
		descriptor.getDescriptorQueryManager().assumeExistenceForDoesExist();

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping descriptionMapping = new DirectToFieldMapping();
		descriptionMapping.setAttributeName("description");
		descriptionMapping.setGetMethodName("getDescription");
		descriptionMapping.setSetMethodName("setDescription");
		descriptionMapping.setFieldName("POLICY.DESCRIPT");
		descriptor.addMapping(descriptionMapping);

		DirectToFieldMapping maxCoverageMapping = new DirectToFieldMapping();
		maxCoverageMapping.setAttributeName("maxCoverage");
		maxCoverageMapping.setGetMethodName("getMaxCoverage");
		maxCoverageMapping.setSetMethodName("setMaxCoverage");
		maxCoverageMapping.setFieldName("POLICY.MAX_COV");
		descriptor.addMapping(maxCoverageMapping);

		DirectToFieldMapping policyNumberMapping = new DirectToFieldMapping();
		policyNumberMapping.setAttributeName("policyNumber");
		policyNumberMapping.setGetMethodName("getPolicyNumber");
		policyNumberMapping.setSetMethodName("setPolicyNumber");
		policyNumberMapping.setFieldName("POLICY.POL_ID");
		descriptor.addMapping(policyNumberMapping);

		OneToOneMapping policyHolderMapping = new OneToOneMapping();
		policyHolderMapping.setAttributeName("policyHolder");
		policyHolderMapping.setGetMethodName("getPolicyHolder");
		policyHolderMapping.setSetMethodName("setPolicyHolder");
		policyHolderMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.PolicyHolder.class.getName());
		policyHolderMapping.dontUseIndirection();
		policyHolderMapping.addForeignKeyFieldName("POLICY.SSN", "HOLDER.SSN");
		descriptor.addMapping(policyHolderMapping);

		OneToManyMapping claimsMapping = new OneToManyMapping();
		claimsMapping.setAttributeName("claims");
		claimsMapping.setGetMethodName("getClaims");
		claimsMapping.setSetMethodName("setClaims");
		claimsMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Claim.class.getName());
		claimsMapping.dontUseIndirection();
        claimsMapping.useListClassName("java.util.Vector");
		claimsMapping.privateOwnedRelationship();
		claimsMapping.addTargetForeignKeyFieldName("CLAIM.POL_ID", "POLICY.POL_ID");
		descriptor.addMapping(claimsMapping);

		return descriptor;
	}

	public RelationalDescriptor buildPolicyHolderDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.PolicyHolder.class.getName());
		descriptor.addTableName("HOLDER");
		descriptor.addPrimaryKeyFieldName("HOLDER.SSN");

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(0);
		descriptor.setAlias("PolicyHolder");

		// Query manager.
		descriptor.getDescriptorQueryManager().assumeExistenceForDoesExist();

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping birthDateMapping = new DirectToFieldMapping();
		birthDateMapping.setAttributeName("birthDate");
		birthDateMapping.setGetMethodName("getBirthDate");
		birthDateMapping.setSetMethodName("setBirthDate");
		birthDateMapping.setFieldName("HOLDER.BDATE");
		descriptor.addMapping(birthDateMapping);

		DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
		firstNameMapping.setAttributeName("firstName");
		firstNameMapping.setGetMethodName("getFirstName");
		firstNameMapping.setSetMethodName("setFirstName");
		firstNameMapping.setFieldName("HOLDER.F_NAME");
		descriptor.addMapping(firstNameMapping);

		DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
		lastNameMapping.setAttributeName("lastName");
		lastNameMapping.setGetMethodName("getLastName");
		lastNameMapping.setSetMethodName("setLastName");
		lastNameMapping.setFieldName("HOLDER.L_NAME");
		descriptor.addMapping(lastNameMapping);

		DirectToFieldMapping occupationMapping = new DirectToFieldMapping();
		occupationMapping.setAttributeName("occupation");
		occupationMapping.setGetMethodName("getOccupation");
		occupationMapping.setSetMethodName("setOccupation");
		occupationMapping.setFieldName("HOLDER.OCC");
		descriptor.addMapping(occupationMapping);

		DirectToFieldMapping ssnMapping = new DirectToFieldMapping();
		ssnMapping.setAttributeName("ssn");
		ssnMapping.setGetMethodName("getSsn");
		ssnMapping.setSetMethodName("setSsn");
		ssnMapping.setFieldName("HOLDER.SSN");
		descriptor.addMapping(ssnMapping);

		DirectToFieldMapping sexMapping = new DirectToFieldMapping();
		ObjectTypeConverter converter = new ObjectTypeConverter(sexMapping);
		sexMapping.setConverter(converter);
		sexMapping.setAttributeName("sex");
		sexMapping.setGetMethodName("getSex");
		sexMapping.setSetMethodName("setSex");
		sexMapping.setFieldName("HOLDER.SEX");
		converter.addConversionValue(new Character('F'), "Female");
		converter.addConversionValue(new Character('M'), "Male");
		descriptor.addMapping(sexMapping);

		DirectCollectionMapping childrenNamesMapping = new DirectCollectionMapping();
		childrenNamesMapping.setAttributeName("childrenNames");
		childrenNamesMapping.setGetMethodName("getChildrenNames");
		childrenNamesMapping.setSetMethodName("setChildrenNames");
		childrenNamesMapping.dontUseIndirection();
        childrenNamesMapping.useListClassName("java.util.Vector");
		childrenNamesMapping.setReferenceTableName("CHILDRENNAMES");
		childrenNamesMapping.setDirectFieldName("CHILDRENNAMES.CHILD_NAME");
		childrenNamesMapping.addReferenceKeyFieldName("CHILDRENNAMES.HOLDER_ID", "HOLDER.SSN");
		descriptor.addMapping(childrenNamesMapping);

		OneToOneMapping addressMapping = new OneToOneMapping();
		addressMapping.setAttributeName("address");
		addressMapping.setGetMethodName("getAddress");
		addressMapping.setSetMethodName("setAddress");
		addressMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Address.class.getName());
		addressMapping.dontUseIndirection();
		addressMapping.privateOwnedRelationship();
		addressMapping.addTargetForeignKeyFieldName("INS_ADDR.SSN", "HOLDER.SSN");
		descriptor.addMapping(addressMapping);

		OneToManyMapping policiesMapping = new OneToManyMapping();
		policiesMapping.setAttributeName("policies");
		policiesMapping.setGetMethodName("getPolicies");
		policiesMapping.setSetMethodName("setPolicies");
		policiesMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Policy.class.getName());
		policiesMapping.dontUseIndirection();
		policiesMapping.privateOwnedRelationship();
		policiesMapping.addTargetForeignKeyFieldName("POLICY.SSN", "HOLDER.SSN");

		ContainerPolicy containerPolicy = new MapContainerPolicy(java.util.TreeMap.class.getName());
		((MapContainerPolicy) containerPolicy).setKeyName("getPolicyHolder");
		policiesMapping.setContainerPolicy(containerPolicy);

		descriptor.addMapping(policiesMapping);

		return descriptor;
	}

	public RelationalDescriptor buildVehicleClaimDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.VehicleClaim.class.getName());
		descriptor.addTableName("VHCL_CLM");
		descriptor.addPrimaryKeyFieldName("VHCL_CLM.CLM_ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Claim.class.getName());
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.setAlias("VehicleClaim");

		// Query manager.
		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeExistence);

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping partMapping = new DirectToFieldMapping();
		partMapping.setAttributeName("part");
		partMapping.setGetMethodName("getPart");
		partMapping.setSetMethodName("setPart");
		partMapping.setFieldName("VHCL_CLM.PART");
		descriptor.addMapping(partMapping);

		DirectToFieldMapping partDescriptionMapping = new DirectToFieldMapping();
		partDescriptionMapping.setAttributeName("partDescription");
		partDescriptionMapping.setGetMethodName("getPartDescription");
		partDescriptionMapping.setSetMethodName("setPartDescription");
		partDescriptionMapping.setFieldName("VHCL_CLM.PART_DESC");
		descriptor.addMapping(partDescriptionMapping);

		return descriptor;
	}

	public RelationalDescriptor buildVehiclePolicyDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.VehiclePolicy.class.getName());
		descriptor.addTableName("VHCL_POL");
		descriptor.addPrimaryKeyFieldName("VHCL_POL.POL_ID");
		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.insurance.Policy.class.getName());
		descriptor.getDescriptorInheritancePolicy().dontReadSubclassesOnQueries();

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.setAlias("VehiclePolicy");

		// Query manager.
		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeExistence);

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping modelMapping = new DirectToFieldMapping();
		modelMapping.setAttributeName("model");
		modelMapping.setGetMethodName("getModel");
		modelMapping.setSetMethodName("setModel");
		modelMapping.setFieldName("VHCL_POL.MODEL");
		descriptor.addMapping(modelMapping);

		return descriptor;
	}

	public Project getRuntimeProject() {
		return this.runtimeProject;
	}
}
