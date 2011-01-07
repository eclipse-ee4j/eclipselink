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

import java.sql.Blob;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy;
import org.eclipse.persistence.queries.QueryResultsCachePolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.mappings.xdb.DirectToXMLTypeMapping;


/**
 * @author kamoore
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CrimeSceneRuntimeProject {

	private Project runtimeProject;


	public CrimeSceneRuntimeProject() {
		this.runtimeProject = new Project();
		this.runtimeProject.setName("Crime Scene Project");
		applyLogin();

		this.runtimeProject.addDescriptor(buildAddressDescriptor());
		this.runtimeProject.addDescriptor(buildCrimeSceneDescriptor());
		this.runtimeProject.addDescriptor(buildDetectiveDescriptor());
		this.runtimeProject.addDescriptor(buildFingerprintDescriptor());
		this.runtimeProject.addDescriptor(buildFirearmDescriptor());
		this.runtimeProject.addDescriptor(buildPersonDescriptor());
		this.runtimeProject.addDescriptor(buildPieceOfEvidenceDescriptor());
		this.runtimeProject.addDescriptor(buildSuspectDescriptor());
		this.runtimeProject.addDescriptor(buildVictimDescriptor());
		this.runtimeProject.addDescriptor(buildWeaponDescriptor());
	}

	public void applyLogin() {
		DatabaseLogin login = new DatabaseLogin();
		login.usePlatform(new MySQLPlatform());
		login.setDriverClassName(TestDatabases.mySQLDriverClassName());
		login.setConnectionString(TestDatabases.mySQLServerURL());
		login.setUserName(TestDatabases.userName());
		login.setPassword(TestDatabases.password());

		// Configuration properties.
		((TableSequence)login.getDefaultSequence()).setTableName("SEQUENCE");
		((TableSequence)login.getDefaultSequence()).setNameFieldName("SEQ_NAME");
		((TableSequence)login.getDefaultSequence()).setCounterFieldName("SEQ_COUNT");
		((TableSequence)login.getDefaultSequence()).setPreallocationSize(50);
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
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Address.class.getName());

		// Descriptor properties.
		descriptor.setAlias("Address");

		// Query manager.

		//Named Queries

		// Event manager.
		descriptor.getDescriptorEventManager().setPostBuildSelector("handleEvents");
		descriptor.getDescriptorEventManager().setPostCloneSelector("handleEvents");
		descriptor.getDescriptorEventManager().setPostMergeSelector("handleEvents");
		descriptor.getDescriptorEventManager().setPostRefreshSelector("handleEvents");

		// Mappings.
		DirectToFieldMapping cityMapping = new DirectToFieldMapping();
		cityMapping.setAttributeName("city");
		cityMapping.setGetMethodName("getCity");
		cityMapping.setSetMethodName("setCity");
		cityMapping.setFieldName("city->DIRECT");
		descriptor.addMapping(cityMapping);

		DirectToFieldMapping stateMapping = new DirectToFieldMapping();
		stateMapping.setAttributeName("state");
		stateMapping.setGetMethodName("getState");
		stateMapping.setSetMethodName("setState");
		stateMapping.setFieldName("state->DIRECT");
		descriptor.addMapping(stateMapping);

		DirectToFieldMapping streetMapping = new DirectToFieldMapping();
		streetMapping.setAttributeName("street");
		streetMapping.setGetMethodName("getStreet");
		streetMapping.setSetMethodName("setStreet");
		streetMapping.setFieldName("street->DIRECT");
		descriptor.addMapping(streetMapping);

		DirectToFieldMapping zipMapping = new DirectToFieldMapping();
		zipMapping.setAttributeName("zip");
		zipMapping.setGetMethodName("getZip");
		zipMapping.setSetMethodName("setZip");
		zipMapping.setFieldName("zip->DIRECT");
		descriptor.addMapping(zipMapping);

		return descriptor;
	}

	public RelationalDescriptor buildCrimeSceneDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.CrimeScene.class.getName());
		descriptor.addTableName("CRIME_SCENE");
		descriptor.addPrimaryKeyFieldName("CRIME_SCENE.ID");

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.useWeakIdentityMap();
		descriptor.setIdentityMapSize(98);
		descriptor.setSequenceNumberFieldName("CRIME_SCENE.ID");
		descriptor.setSequenceNumberName("org.eclipse.persistence.tools.workbench.test.models.crimescene.CrimeScene");
		descriptor.setAlias("CrimeScene");
		descriptor.setAmendmentClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.CrimeScene.class.getName());
		descriptor.setAmendmentMethodName("addToDescriptor");

		// Query manager.
		descriptor.getDescriptorQueryManager().assumeExistenceForDoesExist();

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToXMLTypeMapping descriptionMapping = new DirectToXMLTypeMapping();
		descriptionMapping.setAttributeName("description");
		descriptionMapping.setGetMethodName("getDescription");
		descriptionMapping.setSetMethodName("setDescription");
		descriptionMapping.setFieldName("CRIME_SCENE.DESCRIPTION");
		descriptionMapping.setShouldReadWholeDocument(true);
		descriptor.addMapping(descriptionMapping);

		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setGetMethodName("getId");
		idMapping.setSetMethodName("setId");
		idMapping.setFieldName("CRIME_SCENE.ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping timeMapping = new DirectToFieldMapping();
		TypeConversionConverter converter = new TypeConversionConverter(timeMapping);
		timeMapping.setConverter(converter);
		timeMapping.setAttributeName("time");
		timeMapping.setGetMethodName("getTime");
		timeMapping.setSetMethodName("setTime");
		timeMapping.setFieldName("CRIME_SCENE.CS_TIME");
		converter.setDataClassName(java.sql.Date.class.getName());
		converter.setObjectClassName(java.sql.Date.class.getName());
		descriptor.addMapping(timeMapping);

		DirectCollectionMapping keywordsMapping = new DirectCollectionMapping();
		keywordsMapping.setAttributeName("keywords");
		keywordsMapping.setGetMethodName("getKeywords");
		keywordsMapping.setSetMethodName("setKeywords");
		keywordsMapping.dontUseIndirection();
		keywordsMapping.useBatchReading();
		keywordsMapping.useListClassName("java.util.ArrayList");
		keywordsMapping.setReferenceTableName("KEYWORD");
		keywordsMapping.setDirectFieldName("KEYWORD.WORD");
		keywordsMapping.addReferenceKeyFieldName("KEYWORD.CS_ID", "CRIME_SCENE.ID");
		descriptor.addMapping(keywordsMapping);

		OneToManyMapping evidenceMapping = new OneToManyMapping();
		evidenceMapping.setAttributeName("evidence");
		evidenceMapping.setGetMethodName("getEvidence");
		evidenceMapping.setSetMethodName("setEvidence");
		evidenceMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.PieceOfEvidence.class.getName());
		evidenceMapping.dontUseIndirection();
		evidenceMapping.useBatchReading();
		evidenceMapping.privateOwnedRelationship();
		evidenceMapping.useListClassName("java.util.ArrayList");
		evidenceMapping.addAscendingOrdering("name");
		evidenceMapping.addTargetForeignKeyFieldName("EVIDENCE.CS_ID", "CRIME_SCENE.ID");
		descriptor.addMapping(evidenceMapping);

		ManyToManyMapping suspectsMapping = new ManyToManyMapping();
		suspectsMapping.setAttributeName("suspects");
		suspectsMapping.setGetMethodName("getSuspects");
		suspectsMapping.setSetMethodName("setSuspects");
		suspectsMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Suspect.class.getName());
		suspectsMapping.dontUseIndirection();
		suspectsMapping.useBatchReading();
		suspectsMapping.useListClassName("java.util.ArrayList");
		suspectsMapping.setRelationTableName("CS_SUSPECT");
		suspectsMapping.addSourceRelationKeyFieldName("CS_SUSPECT.CS_ID", "CRIME_SCENE.ID");
		suspectsMapping.addTargetRelationKeyFieldName("CS_SUSPECT.SUSPECT_ID", "PERSON.ID");
		descriptor.addMapping(suspectsMapping);

		descriptor.applyAmendmentMethod();
		return descriptor;
	}

	public RelationalDescriptor buildDetectiveDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Detective.class.getName());
		descriptor.addTableName("PERSON");
		descriptor.addPrimaryKeyFieldName("PERSON.ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Person.class.getName());
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.setAlias("Detective");

		// Query manager.
		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeExistence);

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping precinctMapping = new DirectToFieldMapping();
		precinctMapping.setAttributeName("precinct");
		precinctMapping.setGetMethodName("getPrecinct");
		precinctMapping.setSetMethodName("setPrecinct");
		precinctMapping.setFieldName("PERSON.PRECINCT");
		descriptor.addMapping(precinctMapping);

		return descriptor;
	}

	public RelationalDescriptor buildFingerprintDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Fingerprint.class.getName());
		descriptor.addTableName("FINGERPRINT");
		descriptor.addPrimaryKeyFieldName("FINGERPRINT.ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.PieceOfEvidence.class.getName());
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setAlias("Fingerprint");
		descriptor.setIsIsolated(false);
		
		// Query manager.
		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeExistence);

		descriptor.alwaysRefreshCache();
		descriptor.disableCacheHits();
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping imageMapping = new DirectToFieldMapping();
		imageMapping.setAttributeName("image");
		imageMapping.setGetMethodName("getImage");
		imageMapping.setSetMethodName("setImage");
		imageMapping.setFieldName("FINGERPRINT.PRINT_IMAGE");
		descriptor.addMapping(imageMapping);

		return descriptor;
	}

	public RelationalDescriptor buildFirearmDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Firearm.class.getName());
		descriptor.addTableName("FIREARM");
		descriptor.addPrimaryKeyFieldName("FIREARM.ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Weapon.class.getName());
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.setAlias("Firearm");
		descriptor.setShouldAlwaysRefreshCache(true);
		descriptor.setShouldDisableCacheHits(false);

		// Query manager.
		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeExistence);

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping caliberMapping = new DirectToFieldMapping();
		caliberMapping.setAttributeName("caliber");
		caliberMapping.setGetMethodName("getCaliber");
		caliberMapping.setSetMethodName("setCaliber");
		caliberMapping.setFieldName("FIREARM.CALIBER");
		descriptor.addMapping(caliberMapping);

		DirectToFieldMapping typeMapping = new DirectToFieldMapping();
		typeMapping.setAttributeName("type");
		typeMapping.setGetMethodName("getType");
		typeMapping.setSetMethodName("setType");
		typeMapping.setFieldName("FIREARM.FIREARM_TYPE");
		typeMapping.setNullValue("Gun");
		descriptor.addMapping(typeMapping);

		DirectToFieldMapping byteArrayMapping = new DirectToFieldMapping();
		TypeConversionConverter converter = new TypeConversionConverter(byteArrayMapping);
		converter.setObjectClassName(Blob.class.getName());
		converter.setDataClassName("[B");
		byteArrayMapping.setConverter(converter);

		byteArrayMapping.setAttributeName("byteArray");
		byteArrayMapping.setGetMethodName("getByteArray");
		byteArrayMapping.setSetMethodName("setByteArray");
		byteArrayMapping.setFieldName("FIREARM.BYTE_ARRAY");
		descriptor.addMapping(byteArrayMapping);

		return descriptor;
	}

	public RelationalDescriptor buildPersonDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Person.class.getName());
		descriptor.addTableName("PERSON");
		descriptor.addPrimaryKeyFieldName("PERSON.ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setClassIndicatorFieldName("PERSON.PERSON_TYPE");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.crimescene.Suspect.class.getName(), "S");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.crimescene.Person.class.getName(), "P");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.crimescene.Detective.class.getName(), "D");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.crimescene.Victim.class.getName(), "V");
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(400);
		descriptor.setSequenceNumberFieldName("PERSON.ID");
		descriptor.setSequenceNumberName("org.eclipse.persistence.tools.workbench.test.models.crimescene.Person");
		descriptor.onlyRefreshCacheIfNewerVersion();
		descriptor.setAlias("Person");

		// Query manager.
		descriptor.getDescriptorQueryManager().assumeExistenceForDoesExist();
		descriptor.getDescriptorQueryManager().setDeleteSQLString("KILL 'EM ALL!!!!!");
		descriptor.getDescriptorQueryManager().getDeleteQuery().setShouldBindAllParameters(true);

		//Named Queries
		//Named Query -- findByIQ
		ReadAllQuery namedQuery0 = new ReadAllQuery();
		namedQuery0.setSQLString("Get all the smart'uns!");
		namedQuery0.setName("findByIQ");
		namedQuery0.setCascadePolicy(1);
		namedQuery0.setQueryTimeout(-1);
		namedQuery0.setShouldUseWrapperPolicy(true);
		namedQuery0.setShouldBindAllParameters(true);
		namedQuery0.setShouldCacheStatement(true);
		namedQuery0.setShouldMaintainCache(false);
		namedQuery0.setShouldPrepare(true);
		namedQuery0.setQueryResultsCachePolicy(new QueryResultsCachePolicy());
		namedQuery0.setMaxRows(0);
		namedQuery0.setShouldRefreshIdentityMapResult(true);
		namedQuery0.setCacheUsage(5);
		namedQuery0.setLockMode((short)2);
		namedQuery0.setShouldRefreshRemoteIdentityMapResult(true);
		namedQuery0.setDistinctState((short)0);
		namedQuery0.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		descriptor.getDescriptorQueryManager().addQuery("findByIQ", namedQuery0);


		// Event manager.

		// Mappings.
		DirectToFieldMapping ageMapping = new DirectToFieldMapping();
		ageMapping.setAttributeName("age");
		ageMapping.setGetMethodName("getAge");
		ageMapping.setSetMethodName("setAge");
		ageMapping.setFieldName("PERSON.AGE");
		descriptor.addMapping(ageMapping);

		DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
		firstNameMapping.setAttributeName("firstName");
		firstNameMapping.setGetMethodName("getFirstName");
		firstNameMapping.setSetMethodName("setFirstName");
		firstNameMapping.setFieldName("PERSON.F_NAME");
		descriptor.addMapping(firstNameMapping);

		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setGetMethodName("getId");
		idMapping.setSetMethodName("setId");
		idMapping.setFieldName("PERSON.ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
		lastNameMapping.setAttributeName("lastName");
		lastNameMapping.setGetMethodName("getLastName");
		lastNameMapping.setSetMethodName("setLastName");
		lastNameMapping.setFieldName("PERSON.L_NAME");
		descriptor.addMapping(lastNameMapping);

		DirectToFieldMapping genderMapping = new DirectToFieldMapping();
		ObjectTypeConverter converter = new ObjectTypeConverter(genderMapping);
		genderMapping.setConverter(converter);
		genderMapping.setAttributeName("gender");
		genderMapping.setGetMethodName("getGender");
		genderMapping.setSetMethodName("setGender");
		genderMapping.setFieldName("PERSON.GENDER");
		converter.addConversionValue("F", "Female");
		converter.addConversionValue("M", "Male");
		descriptor.addMapping(genderMapping);

		AggregateObjectMapping addressMapping = new AggregateObjectMapping();
		addressMapping.setAttributeName("address");
		addressMapping.setGetMethodName("getAddress");
		addressMapping.setSetMethodName("setAddress");
		addressMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Address.class.getName());
		addressMapping.setIsNullAllowed(false);
		addressMapping.addFieldNameTranslation("PERSON.ADD_CITY", "city->DIRECT");
		addressMapping.addFieldNameTranslation("PERSON.ADD_STATE", "state->DIRECT");
		addressMapping.addFieldNameTranslation("PERSON.ADD_STREET", "street->DIRECT");
		addressMapping.addFieldNameTranslation("PERSON.ADD_ZIP", "zip->DIRECT");
		descriptor.addMapping(addressMapping);

		return descriptor;
	}

	public RelationalDescriptor buildPieceOfEvidenceDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.PieceOfEvidence.class.getName());
		descriptor.addTableName("EVIDENCE");
		descriptor.addPrimaryKeyFieldName("EVIDENCE.ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setClassIndicatorFieldName("EVIDENCE.EVIDENCE_TYPE");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.crimescene.Weapon.class.getName(), "W");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.crimescene.Firearm.class.getName(), "G");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.crimescene.Fingerprint.class.getName(), "F");
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(405);
		descriptor.setSequenceNumberFieldName("EVIDENCE.ID");
		descriptor.setSequenceNumberName("org.eclipse.persistence.tools.workbench.test.models.crimescene.PieceOfEvidence");
		descriptor.setAlias("PieceOfEvidence");


		// Query manager.
		descriptor.getDescriptorQueryManager().checkDatabaseForDoesExist();

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping descriptionMapping = new DirectToFieldMapping();
		descriptionMapping.setAttributeName("description");
		descriptionMapping.setGetMethodName("getDescription");
		descriptionMapping.setSetMethodName("setDescription");
		descriptionMapping.setFieldName("EVIDENCE.DESCRIPTION");
		descriptor.addMapping(descriptionMapping);

		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setGetMethodName("getId");
		idMapping.setSetMethodName("setId");
		idMapping.setFieldName("EVIDENCE.ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping nameMapping = new DirectToFieldMapping();
		nameMapping.setAttributeName("name");
		nameMapping.setGetMethodName("getName");
		nameMapping.setSetMethodName("setName");
		nameMapping.setFieldName("EVIDENCE.NAME");
		descriptor.addMapping(nameMapping);

		OneToOneMapping crimeSceneMapping = new OneToOneMapping();
		crimeSceneMapping.setAttributeName("crimeScene");
		crimeSceneMapping.setGetMethodName("getCrimeScene");
		crimeSceneMapping.setSetMethodName("setCrimeScene");
		crimeSceneMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.CrimeScene.class.getName());
		crimeSceneMapping.dontUseIndirection();
		crimeSceneMapping.useBatchReading();
		crimeSceneMapping.addForeignKeyFieldName("EVIDENCE.CS_ID", "CRIME_SCENE.ID");
		crimeSceneMapping.useInnerJoinFetch();
		descriptor.addMapping(crimeSceneMapping);

		return descriptor;
	}

	public RelationalDescriptor buildSuspectDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Suspect.class.getName());
		descriptor.addTableName("PERSON");
		descriptor.addPrimaryKeyFieldName("PERSON.ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Person.class.getName());
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setAlias("Suspect");
		descriptor.setIsIsolated(false);
		
		// Query manager.
		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeExistence);

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping aliasMapping = new DirectToFieldMapping();
		aliasMapping.setAttributeName("alias");
		aliasMapping.setGetMethodName("getAlias");
		aliasMapping.setSetMethodName("setAlias");
		aliasMapping.setFieldName("PERSON.ALIAS");
		descriptor.addMapping(aliasMapping);

		TransformationMapping heightMapping = new TransformationMapping();
		heightMapping.setAttributeName("height");
		heightMapping.setGetMethodName("getHeight");
		heightMapping.setSetMethodName("setHeight");
		heightMapping.setAttributeTransformation("calculateHeight");
		heightMapping.addFieldTransformation("PERSON.HEIGHT_INCHES", "inchesRemainder");
		heightMapping.addFieldTransformation("PERSON.HEIGHT_FEET", "heightInFeet");
		descriptor.addMapping(heightMapping);

		return descriptor;
	}

	public RelationalDescriptor buildVictimDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Victim.class.getName());
		descriptor.addTableName("PERSON");
		descriptor.addPrimaryKeyFieldName("PERSON.ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Person.class.getName());
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.setAlias("Victim");

		// Query manager.
		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeExistence);
		//Named Queries
		//Named Query -- findByHeadWoundDescription
		ReadObjectQuery namedQuery0 = new ReadObjectQuery();
		namedQuery0.setSQLString("Get the guy whose head was shaped like a (blank)!");
		namedQuery0.setName("findByHeadWoundDescription");
		namedQuery0.setCascadePolicy(1);
		namedQuery0.setQueryTimeout(-1);
		namedQuery0.setShouldUseWrapperPolicy(true);
		namedQuery0.setShouldBindAllParameters(true);
		namedQuery0.setShouldCacheStatement(true);
		namedQuery0.setShouldMaintainCache(false);
		namedQuery0.setShouldPrepare(true);
		namedQuery0.setMaxRows(0);
		namedQuery0.setShouldRefreshIdentityMapResult(true);
		namedQuery0.setCacheUsage(5);
		namedQuery0.setLockMode((short)2);
		namedQuery0.setShouldRefreshRemoteIdentityMapResult(true);
		namedQuery0.setDistinctState((short)0);
		namedQuery0.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		namedQuery0.addArgumentByTypeName("shape", java.lang.String.class.getName());
		descriptor.getDescriptorQueryManager().addQuery("findByHeadWoundDescription", namedQuery0);


		// Event manager.

		// Mappings.
		DirectToFieldMapping statementMapping = new DirectToFieldMapping();
		statementMapping.setAttributeName("statement");
		statementMapping.setGetMethodName("getStatement");
		statementMapping.setSetMethodName("setStatement");
		statementMapping.setFieldName("PERSON.STATEMENT");
		descriptor.addMapping(statementMapping);

		return descriptor;
	}

	public RelationalDescriptor buildWeaponDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.Weapon.class.getName());
		descriptor.addTableName("WEAPON");
		descriptor.addPrimaryKeyFieldName("WEAPON.ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.crimescene.PieceOfEvidence.class.getName());
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.setAlias("Weapon");

		// Query manager.
		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeExistence);

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping usedInCrimeMapping = new DirectToFieldMapping();
		usedInCrimeMapping.setAttributeName("usedInCrime");
		usedInCrimeMapping.setGetMethodName("isUsedInCrime");
		usedInCrimeMapping.setSetMethodName("setUsedInCrime");
		usedInCrimeMapping.setFieldName("WEAPON.USEDINCRIME");
		descriptor.addMapping(usedInCrimeMapping);

		return descriptor;
	}


	public Project getRuntimeProject() {
		return this.runtimeProject;
	}
}
