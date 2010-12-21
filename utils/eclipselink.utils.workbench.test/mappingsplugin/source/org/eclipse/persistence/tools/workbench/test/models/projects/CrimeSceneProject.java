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

import java.sql.Blob;
import java.util.Iterator;

import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorAfterLoadingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorEventsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWJoinFetchableMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWJoinFetchableMapping.JoinFetchOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDefaultNullValuePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToXmlTypeMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWStringQueryFormat;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;

public class CrimeSceneProject extends RelationalTestProject {
	private static MWRelationalProject REUSABLE_PROJECT;
	
	public CrimeSceneProject() {
		super();
	}
	
	public MWClass crimeSceneType() {
		return refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.CrimeScene");
	}
	
	public MWClass detectiveType() {
		return refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Detective");
	}
	
	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
	
	public static MWRelationalProject emptyProject() 
	{
		MWRelationalProject project = new MWRelationalProject("Crime Scene Project", spiManager(), mySqlPlatform());
		
		// Defaults policy  Had to do this because in the tests when you get an empty project it was never setting the defaults
		project.getDefaultsPolicy().getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_CHECK_DATABASE);
		project.getDefaultsPolicy().getCachingPolicy().setCacheSize(405);
		project.getDefaultsPolicy().getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		project.getDefaultsPolicy().setMethodAccessing(true);
		return project;
	} 

	public MWClass evidenceType() 
	{
		return refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.PieceOfEvidence");
	}
	public MWClass fingerprintType() 
	{
		return refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Fingerprint");
	}
	
	public MWClass firearmType() 
	{
		return refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Firearm");
	}
	
	public MWAggregateDescriptor getAddressDescriptor() {
		return aggregateDescriptorWithShortName("Address");
	}
	
	public MWTableDescriptor getCrimeSceneDescriptor() {
		return tableDescriptorWithShortName("CrimeScene");
	}
	
	public MWTableDescriptor getDetectiveDescriptor() {
		return tableDescriptorWithShortName("Detective");
	}

	public MWOneToOneMapping getCrimeSceneMappingInPieceOfEvidence() {
		return (MWOneToOneMapping) getPieceOfEvidenceDescriptor().mappingNamed("crimeScene");
	}
	
	public MWDirectToFieldMapping getGenderMappingInPerson() {
		return (MWDirectToFieldMapping) getPersonDescriptor().mappingNamed("gender");
	}
	
	public MWOneToManyMapping getEvidenceMappingInCrimeScene() {
		return(MWOneToManyMapping) getCrimeSceneDescriptor().mappingNamed("evidence");
	}
	
	public MWAggregateMapping getAddressMappingInCrimeScene() {
		return (MWAggregateMapping) getPersonDescriptor().mappingNamed("address");
	}
	
	public MWTableDescriptor getFingerprintDescriptor() {
		return tableDescriptorWithShortName("Fingerprint");
	}
	
	public MWTableDescriptor getFirearmDescriptor() {
		return tableDescriptorWithShortName("Firearm");
	}
	
	public MWDirectToFieldMapping getFirstNameMappingInPerson() {
		return(MWDirectToFieldMapping) getPersonDescriptor().mappingNamed("firstName");
	}
	
	public MWRelationalTransformationMapping getHeightMappingInSuspect() {
		return(MWRelationalTransformationMapping) getSuspectDescriptor().mappingNamed("height");
	}

	public MWRelationalDirectCollectionMapping getKeywordsMappingInCrimeScene() {
		return (MWRelationalDirectCollectionMapping) getCrimeSceneDescriptor().mappingNamed("keywords");
	}
	
	public MWTableDescriptor getPersonDescriptor() {
		return tableDescriptorWithShortName("Person");
	}
	public MWTableDescriptor getPieceOfEvidenceDescriptor() {
		return tableDescriptorWithShortName("PieceOfEvidence");
	}
	public MWTableDescriptor getSuspectDescriptor() {
		return tableDescriptorWithShortName("Suspect");
	}
	public MWTableDescriptor getVictimDescriptor() {
		return tableDescriptorWithShortName("Victim");
	}
	public MWManyToManyMapping getSuspectsMappingInCrimeScene() {
		return(MWManyToManyMapping) getCrimeSceneDescriptor().mappingNamed("suspects");
	}
	public MWDirectToFieldMapping getTimeMappingInCrimeScene() {

		return(MWDirectToFieldMapping) getCrimeSceneDescriptor().mappingNamed("time");
	}
	public MWTableDescriptor getWeaponDescriptor() {
		return tableDescriptorWithShortName("Weapon");
	}
	protected void initializeAddressDescriptor() {
		MWAggregateDescriptor descriptor = getAddressDescriptor();
	
		// Direct to field mappings
		addDirectMapping(descriptor, "street");
		addDirectMapping(descriptor, "city");
		addDirectMapping(descriptor, "state");
		addDirectMapping(descriptor, "zip");
		
		descriptor.addEventsPolicy();
		MWDescriptorEventsPolicy manager = (MWDescriptorEventsPolicy) descriptor.getEventsPolicy();
		manager.setPostBuildMethod(methodNamed(descriptor.getMWClass(), "handleEvents"));
		manager.setPostMergeMethod(methodNamed(descriptor.getMWClass(), "handleEvents"));
		manager.setPostRefreshMethod(methodNamed(descriptor.getMWClass(), "handleEvents"));
		manager.setPostCloneMethod(methodNamed(descriptor.getMWClass(), "handleEvents"));
	}
	
	protected void initializeCrimeSceneDescriptor() {
		MWTableDescriptor descriptor = getCrimeSceneDescriptor();

		MWTable table = tableNamed("CRIME_SCENE");   
		descriptor.setPrimaryTable(table);

		// Sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName(descriptor.getName());
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));
		
//		// Post load method
		descriptor.addAfterLoadingPolicy();
		MWClass postLoadClass = descriptor.getMWClass();
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethodClass(postLoadClass);
		((MWDescriptorAfterLoadingPolicy) descriptor.getAfterLoadingPolicy()).setPostLoadMethod((MWMethod)postLoadClass.candidateDescriptorAfterLoadMethods().next());

		//	Caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_WEAK);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);
		descriptor.getCachingPolicy().setCacheSize(98);

		// Mappings
		// primary key
		addDirectMapping(descriptor,"id", table, "ID");	
		MWDirectToXmlTypeMapping descriptionMapping = addDirectMapping(descriptor,"description", table, "DESCRIPTION").asMWDirectToXmlTypeMapping();
		descriptionMapping.setReadWholeDocument(true);	
	
		// Time - String type conversion mapping
		MWDirectToFieldMapping timeMapping = addDirectMapping(descriptor, "time", table, "CS_TIME");
		MWTypeConversionConverter converter = timeMapping.setTypeConversionConverter();
		converter.setDataType(new MWTypeDeclaration(converter, typeFor(java.sql.Date.class)));
		converter.setAttributeType(new MWTypeDeclaration(converter, typeFor(java.sql.Date.class)));

	
		// Keywords - Direct Collection mapping
		MWTable keywordTable = tableNamed("KEYWORD");
		MWRelationalDirectCollectionMapping keywords = 
			(MWRelationalDirectCollectionMapping) addDirectMapping(descriptor,"keywords").asMWDirectCollectionMapping();
		keywords.setTargetTable(keywordTable);
		keywords.setReference(keywordTable.referenceNamed("KEYWORD_CRIME_SCENE"));
		keywords.setDirectValueColumn(keywordTable.columnNamed("WORD"));
		keywords.setUsesBatchReading(true);
		keywords.setUseNoIndirection();
        keywords.setListContainerPolicy();
        keywords.getContainerPolicy().getDefaultingContainerClass().setContainerClass(this.typeFor(java.util.ArrayList.class));

		// Suspects - Many to Many mapping
		MWManyToManyMapping suspects = descriptor.addManyToManyMapping(descriptor.getMWClass().attributeNamed("suspects"));
		MWClass suspectType = typeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Suspect");
		MWTable relationTable = tableNamed("CS_SUSPECT");
		suspects.setRelationTable(relationTable);
		suspects.setPrivateOwned(false);
		suspects.setSourceReference(relationTable.referenceNamed("CS_SUSPECT_CRIME_SCENE"));
		suspects.setTargetReference(relationTable.referenceNamed("CS_SUSPECT_PERSON"));
		suspects.setReferenceDescriptor(getProject().descriptorForType(suspectType));
		suspects.setUsesBatchReading(true);
		suspects.setUseNoIndirection();
        suspects.setListContainerPolicy();
		suspects.getContainerPolicy().getDefaultingContainerClass().setContainerClass(this.typeFor(java.util.ArrayList.class));

		// Evidence - One to Many mapping
		MWOneToManyMapping evidence = descriptor.addOneToManyMapping(descriptor.getMWClass().attributeNamed("evidence"));
		MWClass evidenceType = typeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.PieceOfEvidence");    
		MWTable evidenceTable = tableNamed("EVIDENCE");
		evidence.setPrivateOwned(true);
		evidence.setReference(evidenceTable.referenceNamed("EVIDENCE_CRIME_SCENE"));
		evidence.setReferenceDescriptor(getProject().descriptorForType(evidenceType));
		evidence.addOrdering(((MWTableDescriptor) evidence.getReferenceDescriptor()).queryKeyNamed("name"));
		evidence.setUsesBatchReading(true);
		evidence.setUseNoIndirection();
        evidence.setListContainerPolicy();
		evidence.getContainerPolicy().getDefaultingContainerClass().setContainerClass(this.typeFor(java.util.ArrayList.class));
	}
	public void initializeCrimeSceneSuspectTable() {
		MWTable table = database().addTable("CS_SUSPECT");
		addPrimaryKeyField(table,"CS_ID", "integer");
		addPrimaryKeyField(table,"SUSPECT_ID", "integer");
	}
	protected void initializeCrimeSceneTable() {
		MWTable table = database().addTable("CRIME_SCENE");
		addPrimaryKeyField(table,"ID", "integer");
		addField(table,"DESCRIPTION", "varchar", 50);
		addField(table,"CS_TIME", "date");
	}

	@Override
	protected void initializeDatabase() {
		super.initializeDatabase();
		this.initializeSequenceTable();
		this.initializeEvidenceTable();
		this.initializeWeaponTable();
		this.initializeFirearmTable();
		this.initializeFingerprintTable();
		this.initializeKeywordTable();
		this.initializePersonTable();
		this.initializeCrimeSceneTable();
		this.initializeCrimeSceneSuspectTable();

		// create the references
		MWTable crimeSceneSuspectTable = this.tableNamed("CS_SUSPECT");
		MWTable crimeSceneTable = this.tableNamed("CRIME_SCENE");
		MWTable evidenceTable = this.tableNamed("EVIDENCE");
		MWTable keywordTable = this.tableNamed("KEYWORD");
		MWTable personTable = this.tableNamed("PERSON");

		// EVIDENCE to CRIME_SCENE
		this.addReferenceOnDB("EVIDENCE_CRIME_SCENE", evidenceTable, crimeSceneTable, "CS_ID", "ID");

		// KEYWORD to CRIME_SCENE
		addReferenceOnDB("KEYWORD_CRIME_SCENE", keywordTable, crimeSceneTable, "CS_ID", "ID");
	
		// CS_SUSPECT to CRIME_SCENE
		addReferenceOnDB("CS_SUSPECT_CRIME_SCENE", crimeSceneSuspectTable, crimeSceneTable, "CS_ID", "ID");
	
		// CS_SUSPECT to PERSON
		addReferenceOnDB("CS_SUSPECT_PERSON", crimeSceneSuspectTable, personTable, "SUSPECT_ID", "ID");
	}
	
	@Override
	protected void initializeDescriptors() {
		super.initializeDescriptors();
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Detective");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Suspect");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Victim");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Person");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Fingerprint");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Firearm");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Weapon");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.PieceOfEvidence");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.CrimeScene");

		this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Address");
	
		
		initializeAddressDescriptor();

		// The leaf and branch nodes for an inheritance hierarchy
		// must be initialized first
		initializeDetectiveDescriptor();
		initializeSuspectDescriptor();
		initializeVictimDescriptor();
		initializePersonDescriptor();

		// same here
		initializeFingerprintDescriptor();
		initializeFirearmDescriptor();
		initializeWeaponDescriptor();
		initializePieceOfEvidenceDescriptor();

		// This should be initialized last, since it depends on all the others
		initializeCrimeSceneDescriptor();

	}
	protected void initializeDetectiveDescriptor() 
	{	
		MWTableDescriptor descriptor = getDetectiveDescriptor();
		MWTable personTable = tableNamed("PERSON");
		descriptor.setPrimaryTable(personTable);

		// Inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getPersonDescriptor());
	
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);

		//Direct to Field Mapping
		addDirectMapping(descriptor, "precinct", personTable, "PRECINCT");	
	}
	public void initializeEvidenceTable() {
		MWTable table = database().addTable("EVIDENCE");
		addPrimaryKeyField(table,"ID", "integer");
		addField(table,"CS_ID", "integer");
		addField(table,"EVIDENCE_TYPE", "varchar", 1);
		addField(table,"NAME", "varchar", 50);
		addField(table,"DESCRIPTION", "varchar", 128);
	}
	protected void initializeFingerprintDescriptor() {
		MWTableDescriptor descriptor = getFingerprintDescriptor();
		MWTable fingerPrintTable = tableNamed("FINGERPRINT");
		descriptor.setPrimaryTable(fingerPrintTable);

		descriptor.getRefreshCachePolicy().setAlwaysRefreshCache(true);
		descriptor.getRefreshCachePolicy().setDisableCacheHits(true);

		// Inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getPieceOfEvidenceDescriptor());

		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);

		// Mappings
		// Image mapping
		addDirectMapping(descriptor, "image", fingerPrintTable, "PRINT_IMAGE");	
	}
	public void initializeFingerprintTable() {
		MWTable table = database().addTable("FINGERPRINT");
		addPrimaryKeyField(table,"ID", "integer");
		addField(table,"PRINT_IMAGE", "varchar", 50);
	}
	protected void initializeFirearmDescriptor() {
		MWTableDescriptor descriptor = getFirearmDescriptor();
		MWTable fireArmTable = tableNamed("FIREARM");
		descriptor.setPrimaryTable(fireArmTable);

		descriptor.getRefreshCachePolicy().setAlwaysRefreshCache(true);
		descriptor.getRefreshCachePolicy().setDisableCacheHits(false);

		// Inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		MWClass parentType = typeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Weapon");
		inheritancePolicy.setParentDescriptor((MWMappingDescriptor)getProject().descriptorForType(parentType));

		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);

		// Mappings
		// Direct to field mapping
		MWDirectToFieldMapping mapping = addDirectMapping(descriptor, "type", fireArmTable, "FIREARM_TYPE");	

		mapping.setUseNullValue(true);
		mapping.getNullValuePolicy().setNullValueType(new MWTypeDeclaration((MWDefaultNullValuePolicy) mapping.getNullValuePolicy(), typeFor(java.lang.String.class)));
		mapping.getNullValuePolicy().setNullValue("Gun");

		addDirectMapping(descriptor, "caliber", fireArmTable, "CALIBER");	

		MWDirectToFieldMapping byteArrayMapping = addDirectMapping(descriptor, "byteArray", fireArmTable, "BYTE_ARRAY");
		MWTypeConversionConverter converter = byteArrayMapping.setTypeConversionConverter();
		for (Iterator i = converter.getBasicTypes().iterator(); i.hasNext(); ) {
			MWTypeDeclaration typeDeclaration = (MWTypeDeclaration) i.next();
			if (typeDeclaration.typeName().equals(Blob.class.getName())) {
				converter.setAttributeType(typeDeclaration);				
			}
			if (typeDeclaration.shortTypeName().equals("byte") && typeDeclaration.getDimensionality() == 1) {
				converter.setDataType(typeDeclaration);				
			}
		}
	}
	
	public void initializeFirearmTable() {
		MWTable table = database().addTable("FIREARM");

		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "FIREARM_TYPE", "varchar" ,50);
		addField(table, "CALIBER", "varchar",50);
		addField(table, "BYTE_ARRAY", "varchar",50);
	}
	public void initializeKeywordTable() {
		MWTable table = database().addTable("KEYWORD");
		addField(table, "CS_ID", "integer");
		addField(table, "WORD", "varchar", 50);
	}
	protected void initializePersonDescriptor() 
	{
		MWClass addressClass = refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Address");
		MWTableDescriptor descriptor = getPersonDescriptor();
		MWTable personTable = tableNamed("PERSON");
		
		descriptor.setPrimaryTable(personTable);
		
		// Sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName(descriptor.getName());
		descriptor.setSequenceNumberTable(personTable);
		descriptor.setSequenceNumberColumn(personTable.columnNamed("ID"));

        descriptor.getTransactionalPolicy().setDescriptorAlias("Person");

        // Refreshing cache policy
		descriptor.getRefreshCachePolicy().setOnlyRefreshCacheIfNewerVersion(true);
	
		// Query Manager
		MWRelationalQueryManager qm = descriptor.getRelationalQueryManager();
		qm.setDeleteSQLString("KILL 'EM ALL!!!!!");
		// Queries
		MWAbstractRelationalReadQuery query = (MWAbstractRelationalReadQuery) qm.addReadAllQuery("findByIQ");
		query.setQueryFormatType(MWRelationalQuery.SQL_FORMAT);
		((MWStringQueryFormat) query.getQueryFormat()).setQueryString("Get all the smart'uns!");
		query.setCacheUsage(MWRelationalReadQuery.CONFORM_RESULTS_IN_UNIT_OF_WORK);
		query.setLocking(MWQuery.LOCK_NOWAIT);
		query.setBindAllParameters(new TriStateBoolean(true));
		query.setCacheQueryResults(true);
		query.setCacheStatement(new TriStateBoolean(true));
		query.setMaintainCache(false);
		query.setRefreshIdentityMapResult(true);
		query.setRefreshRemoteIdentityMapResult(true);
				
		// Caching policy
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		descriptor.getCachingPolicy().setCacheSize(400);

		// Inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setIsRoot(true);
		
		MWRelationalClassIndicatorFieldPolicy classIndicatorPolicy = (MWRelationalClassIndicatorFieldPolicy) inheritancePolicy.getClassIndicatorPolicy();
		classIndicatorPolicy.setField(personTable.columnNamed("PERSON_TYPE"));
		
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(descriptor).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(descriptor).setIndicatorValue("P");

		classIndicatorPolicy.getClassIndicatorValueForDescriptor((MWMappingDescriptor) getProject().descriptorForType(suspectType())).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor((MWMappingDescriptor) getProject().descriptorForType(suspectType())).setIndicatorValue("S");

		classIndicatorPolicy.getClassIndicatorValueForDescriptor((MWMappingDescriptor) getProject().descriptorForType(detectiveType())).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor((MWMappingDescriptor) getProject().descriptorForType(detectiveType())).setIndicatorValue("D");

		classIndicatorPolicy.getClassIndicatorValueForDescriptor((MWMappingDescriptor) getProject().descriptorForType(victimType())).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor((MWMappingDescriptor) getProject().descriptorForType(victimType())).setIndicatorValue("V");


		// Mappings

		// Object Type Mappings
		// M-F male-female object type mapping
		MWObjectTypeConverter otm = addDirectMapping(descriptor, "gender", personTable, "GENDER").setObjectTypeConverter();
		otm.setDataType(new MWTypeDeclaration(otm, typeFor(java.lang.String.class)));
		otm.setAttributeType(new MWTypeDeclaration(otm, typeFor(java.lang.String.class)));
		try
		{
			otm.addValuePair("M", "Male");
			otm.addValuePair("F", "Female");
		}
		catch (MWObjectTypeConverter.ConversionValueException cve) { /*** shouldn't happen ***/}
		
		//Aggregate Mappings
		// Address aggregate mapping
		MWAggregateMapping addressMapping = descriptor.addAggregateMapping(descriptor.getMWClass().attributeNamed("address"));
		MWAggregateDescriptor addressDescriptor = (MWAggregateDescriptor) getProject().descriptorForType(addressClass);
		addressMapping.setReferenceDescriptor(addressDescriptor);
		Iterator fieldAssociations = CollectionTools.sort(addressMapping.pathsToFields()).iterator();
		String[] fieldNames = new String[] {"ADD_CITY", "ADD_STATE", "ADD_STREET", "ADD_ZIP"};
		for(int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) fieldAssociations.next();
			association.setColumn(personTable.columnNamed(fieldNames[i]));
		}
		
		//Direct To Field Mappings
		addDirectMapping(descriptor, "id", personTable, "ID");
		addDirectMapping(descriptor, "firstName", personTable, "F_NAME");
		addDirectMapping(descriptor, "lastName", personTable, "L_NAME");
		addDirectMapping(descriptor, "age", personTable, "AGE");
	}
	public void initializePersonTable() {
		MWTable table = database().addTable("PERSON");
		addPrimaryKeyField(table,"ID", "integer");
		addField(table,"F_NAME", "varchar", 50);
		addField(table,"L_NAME", "varchar", 50);
		addField(table,"GENDER", "varchar", 1);
		addField(table,"AGE", "integer");
		addField(table,"PERSON_TYPE", "varchar", 1);

		//  Add fields specific to Suspect
		addField(table,"HEIGHT_FEET", "integer");
		addField(table,"HEIGHT_INCHES", "integer"); 
		addField(table,"ALIAS", "varchar",50);
		//  Add field specific to Victim
		addField(table,"STATEMENT","varchar", 50);
		//  Add fields specific to Detective
		addField(table,"PRECINCT", "varchar",50);
		//  Add fields specific to Address
		addField(table,"ADD_STREET", "varchar", 80);
		addField(table,"ADD_CITY", "varchar", 40);
		addField(table,"ADD_STATE", "varchar", 2);
		addField(table,"ADD_ZIP", "varchar", 10);
	}
	protected void initializePieceOfEvidenceDescriptor() {
		MWTableDescriptor descriptor = (MWTableDescriptor)  getProject().descriptorForType(evidenceType());
		MWTable evidenceTable = tableNamed("EVIDENCE");
		
		descriptor.setPrimaryTable(evidenceTable);
		
		// Sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberTable(evidenceTable);
		descriptor.setSequenceNumberColumn(evidenceTable.columnNamed("ID"));
		descriptor.setSequenceNumberName(descriptor.getName());

		// Inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setIsRoot(true);

		MWRelationalClassIndicatorFieldPolicy classIndicatorPolicy = (MWRelationalClassIndicatorFieldPolicy) inheritancePolicy.getClassIndicatorPolicy();

		classIndicatorPolicy.setField(evidenceTable.columnNamed("EVIDENCE_TYPE"));

		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getWeaponDescriptor()).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getWeaponDescriptor()).setIndicatorValue("W");
				
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getFingerprintDescriptor()).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getFingerprintDescriptor()).setIndicatorValue("F");
		
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getFirearmDescriptor()).setInclude(true);
		classIndicatorPolicy.getClassIndicatorValueForDescriptor(getFirearmDescriptor()).setIndicatorValue("G");
		

		//MAPPINGS

		//One To One Mapping

		MWOneToOneMapping crimeSceneMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("crimeScene"));
		crimeSceneMapping.setReferenceDescriptor(getCrimeSceneDescriptor());
		crimeSceneMapping.setReference(evidenceTable.referenceNamed("EVIDENCE_CRIME_SCENE"));
		crimeSceneMapping.setUsesBatchReading(true);
		crimeSceneMapping.setJoinFetchOption(MWJoinFetchableMapping.JOIN_FETCH_INNER);

		//Direct To Field Mappings
		addDirectMapping(descriptor, "name", evidenceTable, "NAME");	
		addDirectMapping(descriptor, "description", evidenceTable, "DESCRIPTION");	
		addDirectMapping(descriptor, "id", evidenceTable, "ID");	
	}
	
	protected void initializeSuspectDescriptor() {
		MWTableDescriptor descriptor = getSuspectDescriptor();
		MWClass suspectClass = descriptor.getMWClass();
		MWTable personTable = tableNamed("PERSON");    
	
		descriptor.setPrimaryTable(personTable);

		// Inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getPersonDescriptor());

		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);

		//Direct to field Mapping
		addDirectMapping(descriptor, "alias", personTable, "ALIAS");	

		//Transformation Mapping
		MWRelationalTransformationMapping heightMapping = 
			(MWRelationalTransformationMapping) descriptor.addTransformationMapping(suspectClass.attributeNamed("height"));
		heightMapping.setAttributeTransformer(methodNamed(suspectType(), "calculateHeight"));
		heightMapping.addFieldTransformerAssociation(personTable.columnNamed("HEIGHT_FEET"), methodNamed(suspectType(), "heightInFeet"));
		heightMapping.addFieldTransformerAssociation(personTable.columnNamed("HEIGHT_INCHES"), methodNamed(suspectType(), "inchesRemainder"));
	}
	
	protected void initializeVictimDescriptor() {
		MWClass type = refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Victim");

		MWTableDescriptor descriptor = (MWTableDescriptor)  getProject().descriptorForType(type);
		MWTable personTable =tableNamed("PERSON");
		descriptor.setPrimaryTable(personTable);

		// Inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		MWClass parentType = typeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Person");
		inheritancePolicy.setParentDescriptor((MWMappingDescriptor) getProject().descriptorForType(parentType));

		addDirectMapping(descriptor, "statement", personTable, "STATEMENT");	

		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);

		// Queries
		MWAbstractRelationalReadQuery query = (MWAbstractRelationalReadQuery) descriptor.getQueryManager().addReadObjectQuery("findByHeadWoundDescription");
        query.setQueryFormatType(MWRelationalQuery.SQL_FORMAT);
		((MWStringQueryFormat) query.getQueryFormat()).setQueryString("Get the guy whose head was shaped like a (blank)!");
		query.setCacheUsage(MWRelationalReadQuery.CONFORM_RESULTS_IN_UNIT_OF_WORK);
		query.setLocking(MWQuery.LOCK_NOWAIT);
		query.setBindAllParameters(new TriStateBoolean(true));

		query.setCacheStatement(new TriStateBoolean(true));
		query.setMaintainCache(false);
		query.setRefreshIdentityMapResult(true);
		query.setRefreshRemoteIdentityMapResult(true);
		query.addParameter(typeFor(java.lang.String.class)).setName("shape");
	}
	protected void initializeWeaponDescriptor() {
		MWClass type = refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Weapon");

		MWTableDescriptor descriptor = (MWTableDescriptor)   getProject().descriptorForType(type);
		MWTable weaponTable = getProject().getDatabase().tableNamed("WEAPON");
		descriptor.setPrimaryTable(weaponTable);


		// Inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		MWClass parentType = typeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.PieceOfEvidence");
		inheritancePolicy.setParentDescriptor((MWMappingDescriptor) getProject().descriptorForType(parentType));

		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);

		//Direct to Field Mapping
		addDirectMapping(descriptor, "usedInCrime", weaponTable, "USEDINCRIME");	
	}
	public void initializeWeaponTable() {
		MWTable table = database().addTable("WEAPON");
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "USEDINCRIME", "integer");
	}
	public static MWRelationalProject reusableProject() {
		if(REUSABLE_PROJECT == null) {
			REUSABLE_PROJECT = new CrimeSceneProject().getProject();
		}
		return REUSABLE_PROJECT;
	}
	public MWClass suspectType() {
		return refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Suspect");
	}
	public MWClass victimType() {
		return refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Victim");
	}
	public MWClass weaponType() {
		return refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Weapon");
	}
}
