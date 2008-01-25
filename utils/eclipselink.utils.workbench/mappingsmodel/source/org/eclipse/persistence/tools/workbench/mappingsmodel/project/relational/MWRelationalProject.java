/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.TableStringHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.DescriptorStringHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SPIManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SimpleSPIManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile.CFExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparatorEngine.StringHolderPair;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import deprecated.sdk.SDKAggregateObjectMapping;
import deprecated.sdk.SDKObjectCollectionMapping;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.sessions.factories.TableCreatorClassGenerator;


public final class MWRelationalProject 
	extends MWProject
{
	/** A non-changing privately-owned repository for tables and login information */ 
	private MWDatabase tableRepository;

	/** non-changing policy */
	private MWSequencingPolicy sequencingPolicy;

	/**
	 * table creator source code settings
	 */
	private volatile String tableCreatorSourceClassName;
		public final static String TABLE_CREATOR_SOURCE_CLASS_NAME_PROPERTY = "tableCreatorSourceClassName";
		
	private volatile String tableCreatorSourceDirectoryName;
		public final static String TABLE_CREATOR_SOURCE_DIRECTORY_NAME_PROPERTY = "tableCreatorSourceDirectoryName";


	/** non-changing; generate tables from classes */
	private MWTableGenerationPolicy tableGenerationPolicy;

	//This is used for backward compatibility when generating a runtime project
	//If true we will generate ObjectTypeMappings, TypeConversionMappings, and SerializedObjectMappings with converters
	//If false we will generate only DirectToFieldMappings with converters
	private volatile boolean generateDeprecatedDirectMappings;
		public final static String GENERATE_DEPRECATED_DIRECT_MAPPINGS_PROPERTY = "generateDeprecatedDirectMappings";


	// ********** static methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWRelationalProject.class);
		descriptor.getInheritancePolicy().setParentClass(MWProject.class);

		XMLCompositeObjectMapping databaseMapping = new XMLCompositeObjectMapping();
		databaseMapping.setAttributeName("tableRepository");
		databaseMapping.setReferenceClass(MWDatabase.class);
		databaseMapping.setXPath("table-repository");
		descriptor.addMapping(databaseMapping);
		
		XMLCompositeObjectMapping sequencingPolicyMapping = new XMLCompositeObjectMapping();
		sequencingPolicyMapping.setAttributeName("sequencingPolicy");
		sequencingPolicyMapping.setReferenceClass(MWSequencingPolicy.class);
		sequencingPolicyMapping.setXPath("sequencing");
		descriptor.addMapping(sequencingPolicyMapping);
		
		XMLDirectMapping tableCreatorSourceClassNameMapping = new XMLDirectMapping();
		tableCreatorSourceClassNameMapping.setAttributeName("tableCreatorSourceClassName");
		tableCreatorSourceClassNameMapping.setXPath("table-creator/class/text()");
		tableCreatorSourceClassNameMapping.setNullValue("");
		descriptor.addMapping(tableCreatorSourceClassNameMapping);
		
		XMLDirectMapping tableCreatorSourceDirectoryNameMapping = new XMLDirectMapping();
		tableCreatorSourceDirectoryNameMapping.setAttributeName("tableCreatorSourceDirectoryName");
		tableCreatorSourceDirectoryNameMapping.setSetMethodName("setTableCreatorSourceDirectoryNameForTopLink");
		tableCreatorSourceDirectoryNameMapping.setGetMethodName("getTableCreatorSourceDirectoryNameForTopLink");
		tableCreatorSourceDirectoryNameMapping.setXPath("table-creator/directory/text()");
		tableCreatorSourceDirectoryNameMapping.setNullValue("");
		descriptor.addMapping(tableCreatorSourceDirectoryNameMapping);
				
		XMLCompositeObjectMapping tableGenerationPolicyMapping = new XMLCompositeObjectMapping();
		tableGenerationPolicyMapping.setAttributeName("tableGenerationPolicy");
		tableGenerationPolicyMapping.setReferenceClass(MWTableGenerationPolicy.class);
		tableGenerationPolicyMapping.setXPath("table-generation");
		descriptor.addMapping(tableGenerationPolicyMapping);

        XMLDirectMapping generateDeprecatedDirectMappingsMapping = new XMLDirectMapping();
        generateDeprecatedDirectMappingsMapping.setAttributeName("generateDeprecatedDirectMappings");
        generateDeprecatedDirectMappingsMapping.setXPath("generate-deprecated-direct-mappings/text()");
        generateDeprecatedDirectMappingsMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(generateDeprecatedDirectMappingsMapping);
    
		return descriptor;
	}
	
	public static ClassDescriptor legacy50BuildDescriptor() {
		ClassDescriptor descriptor = MWModel.legacy50BuildStandardDescriptor();
		descriptor.setJavaClass(MWRelationalProject.class);
		descriptor.setTableName("project");
		descriptor.addPrimaryKeyFieldName("name");
		
		descriptor.addDirectMapping("name", "name");
		
		SDKAggregateObjectMapping databaseMapping = new SDKAggregateObjectMapping();
		databaseMapping.setAttributeName("tableRepository");
		databaseMapping.setReferenceClass(MWDatabase.class);
		databaseMapping.setFieldName("project-database");
		descriptor.addMapping(databaseMapping);
		
		SDKObjectCollectionMapping descriptorsMapping = MWModel.legacyBuildOneToManyMapping();
		descriptorsMapping.setAttributeName("descriptors");
		descriptorsMapping.setSetMethodName("legacySetDescriptorsForTopLink");
		descriptorsMapping.setGetMethodName("legacyGetDescriptorsForTopLink");
		descriptorsMapping.setReferenceClass(MWDescriptor.class);
		descriptorsMapping.setFieldName("descriptors");
		descriptorsMapping.setReferenceDataTypeName("package-descriptor");
		descriptorsMapping.setSourceForeignKeyFieldName("name");
		descriptorsMapping.dontUseIndirection();
		descriptor.addMapping(descriptorsMapping);
		
		SDKAggregateObjectMapping defaultsPolicyMapping = new SDKAggregateObjectMapping();
		defaultsPolicyMapping.setAttributeName("defaultsPolicy");
		defaultsPolicyMapping.setReferenceClass(MWRelationalProjectDefaultsPolicy.class);
		defaultsPolicyMapping.setFieldName("defaults-policy");
		descriptor.addMapping(defaultsPolicyMapping);
		
		SDKAggregateObjectMapping repositoryMapping = new SDKAggregateObjectMapping();
		repositoryMapping.setAttributeName("classRepository");
		repositoryMapping.setReferenceClass(MWClassRepository.class);
		repositoryMapping.setFieldName("repository");
		descriptor.addMapping(repositoryMapping);
		
		SDKAggregateObjectMapping sequencingPolicyMapping = new SDKAggregateObjectMapping();
		sequencingPolicyMapping.setAttributeName("sequencingPolicy");
		sequencingPolicyMapping.setReferenceClass(MWSequencingPolicy.class);
		sequencingPolicyMapping.setFieldName("project-sequencing-policy");
		descriptor.addMapping(sequencingPolicyMapping);
		
		DirectToFieldMapping projectPackageNameMapping = new DirectToFieldMapping();
		projectPackageNameMapping.setAttributeName("projectSourceClassName");
		projectPackageNameMapping.setFieldName("project-class-name");
		projectPackageNameMapping.setNullValue("");
		descriptor.addMapping(projectPackageNameMapping);
		
		DirectToFieldMapping projectSourceRootDirMapping = new DirectToFieldMapping();
		projectSourceRootDirMapping.setAttributeName("projectSourceDirectoryName");
		projectSourceRootDirMapping.setSetMethodName("legacy50SetProjectSourceDirectoryNameForTopLink");
		projectSourceRootDirMapping.setGetMethodName("legacy50GetProjectSourceDirectoryNameForTopLink");
		projectSourceRootDirMapping.setFieldName("project-source-root-directory");
		projectSourceRootDirMapping.setNullValue("");
		descriptor.addMapping(projectSourceRootDirMapping);
		
		DirectToFieldMapping projectXmlFileNameMapping = new DirectToFieldMapping();
		projectXmlFileNameMapping.setAttributeName("deploymentXMLFileName");
		projectXmlFileNameMapping.setSetMethodName("legacy50SetDeploymentXMLFileNameForTopLink");
		projectXmlFileNameMapping.setGetMethodName("legacy50GetDeploymentXMLFileNameForTopLink");
		projectXmlFileNameMapping.setFieldName("project-deployment-xml-file-name");
		projectXmlFileNameMapping.setNullValue("");
		descriptor.addMapping(projectXmlFileNameMapping);
		
		DirectToFieldMapping projectXmlDirectoryMapping = new DirectToFieldMapping();
		projectXmlDirectoryMapping.setAttributeName("deploymentXMLDirectoryName");
		projectXmlDirectoryMapping.setSetMethodName("legacy50SetDeploymentXMLDirectoryNameForTopLink");
		projectXmlDirectoryMapping.setGetMethodName("legacy50GetDeploymentXMLDirectoryNameForTopLink");
		projectXmlDirectoryMapping.setFieldName("project-deployment-xml-directory");
		projectXmlDirectoryMapping.setNullValue("");
		descriptor.addMapping(projectXmlDirectoryMapping);
		
		DirectToFieldMapping tableCreatorSourceClassNameMapping = new DirectToFieldMapping();
		tableCreatorSourceClassNameMapping.setAttributeName("tableCreatorSourceClassName");
		tableCreatorSourceClassNameMapping.setFieldName("table-creator-class-name");
		tableCreatorSourceClassNameMapping.setNullValue("");
		descriptor.addMapping(tableCreatorSourceClassNameMapping);
		
		DirectToFieldMapping tableCreatorSourceDirectoryNameMapping = new DirectToFieldMapping();
		tableCreatorSourceDirectoryNameMapping.setAttributeName("tableCreatorSourceDirectoryName");
		tableCreatorSourceDirectoryNameMapping.setSetMethodName("legacy50SetTableCreatorSourceDirectoryNameForTopLink");
		tableCreatorSourceDirectoryNameMapping.setGetMethodName("legacy50GetTableCreatorSourceDirectoryNameForTopLink");
		tableCreatorSourceDirectoryNameMapping.setFieldName("table-creator-source-root-directory");
		tableCreatorSourceDirectoryNameMapping.setNullValue("");
		descriptor.addMapping(tableCreatorSourceDirectoryNameMapping);
		
		DirectToFieldMapping modelSourceRootDirMapping = new DirectToFieldMapping();
		modelSourceRootDirMapping.setAttributeName("modelSourceDirectoryName");
		modelSourceRootDirMapping.setSetMethodName("legacy50SetModelSourceDirectoryNameForTopLink");
		modelSourceRootDirMapping.setGetMethodName("legacy50GetModelSourceDirectoryNameForTopLink");
		modelSourceRootDirMapping.setFieldName("model-source-root-directory");
		modelSourceRootDirMapping.setNullValue("");
		descriptor.addMapping(modelSourceRootDirMapping);
		
		SDKAggregateObjectMapping tableGenerationPolicyMapping = new SDKAggregateObjectMapping();
		tableGenerationPolicyMapping.setAttributeName("tableGenerationPolicy");
		tableGenerationPolicyMapping.setReferenceClass(MWTableGenerationPolicy.class);
		tableGenerationPolicyMapping.setFieldName("project-table-generation-policy");
		descriptor.addMapping(tableGenerationPolicyMapping);
		
		return descriptor;
	}
	
	public static ClassDescriptor legacy45BuildDescriptor() {
		ClassDescriptor descriptor = MWModel.legacy45BuildStandardDescriptor();
		descriptor.setJavaClass(MWRelationalProject.class);
		descriptor.setTableName("BldrProject");

		descriptor.addPrimaryKeyFieldName("name");
		descriptor.addDirectMapping("name", "name");

		SDKAggregateObjectMapping defaultsPolicyMapping = new SDKAggregateObjectMapping();
		defaultsPolicyMapping.setAttributeName("defaultsPolicy");
		defaultsPolicyMapping.setReferenceClass(MWRelationalProjectDefaultsPolicy.class);
		defaultsPolicyMapping.setFieldName("defaultsPolicy");
		descriptor.addMapping(defaultsPolicyMapping);
	
		SDKAggregateObjectMapping sequencingPolicyMapping = new SDKAggregateObjectMapping();
		sequencingPolicyMapping.setAttributeName("sequencingPolicy");
		sequencingPolicyMapping.setReferenceClass(MWSequencingPolicy.class);
		sequencingPolicyMapping.setFieldName("sequencingPolicy");
		descriptor.addMapping(sequencingPolicyMapping);
	
		SDKAggregateObjectMapping tableGenerationPolicyMapping = new SDKAggregateObjectMapping();
		tableGenerationPolicyMapping.setAttributeName("tableGenerationPolicy");
		tableGenerationPolicyMapping.setReferenceClass(MWTableGenerationPolicy.class);
		tableGenerationPolicyMapping.setFieldName("tableGenerationPolicy");
		descriptor.addMapping(tableGenerationPolicyMapping);
	
		OneToOneMapping repositoryMapping = new OneToOneMapping();
		repositoryMapping.setAttributeName("classRepository");
		repositoryMapping.setReferenceClass(MWClassRepository.class);
		repositoryMapping.setForeignKeyFieldName("repository");
		repositoryMapping.privateOwnedRelationship();
		repositoryMapping.dontUseIndirection();
		descriptor.addMapping(repositoryMapping);
	
		OneToOneMapping databaseMapping = new OneToOneMapping();
		databaseMapping.setAttributeName("tableRepository");
		databaseMapping.setReferenceClass(MWDatabase.class);
		databaseMapping.setForeignKeyFieldName("database");
		databaseMapping.privateOwnedRelationship();
		databaseMapping.dontUseIndirection();
		descriptor.addMapping(databaseMapping);
	
		XMLTransformationMapping descriptorsMapping = new XMLTransformationMapping();
		descriptorsMapping.setAttributeName("descriptors");
		descriptorsMapping.setSetMethodName("legacySetDescriptorsForTopLink");
		descriptorsMapping.setGetMethodName("legacyGetDescriptorsForTopLink");
		descriptorsMapping.setAttributeMethodName("legacyGetDescriptorsFromRowForTopLink");
		descriptor.addMapping(descriptorsMapping);
	
		return descriptor;
	}
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWRelationalProject() {
		super();
	}

	/**
	 * this should ONLY be used by DynamicClassOverridePolicy50, via hackery
	 * ~bjv
	 */
	private MWRelationalProject(String name) {
		this(name, legacy50BuildSPIManager(), null);
	}

	private static SPIManager legacy50BuildSPIManager() {
		SimpleSPIManager mgr = new SimpleSPIManager();
		mgr.setExternalClassRepositoryFactory(CFExternalClassRepositoryFactory.instance());
		return mgr;
	}

	public MWRelationalProject(String name, SPIManager spiManager, DatabasePlatform databasePlatform) {
		super(name, spiManager);
		this.tableRepository = new MWDatabase(this, databasePlatform);
	}


	// ********** initialization **********

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.sequencingPolicy = new MWSequencingPolicy(this);
		this.tableCreatorSourceClassName = "";
		this.tableCreatorSourceDirectoryName = "";
		this.tableGenerationPolicy = new MWTableGenerationPolicy(this);
		this.generateDeprecatedDirectMappings = false;
	}

	protected MWProjectDefaultsPolicy buildDefaultsPolicy() {
		return new MWRelationalProjectDefaultsPolicy(this);
	}
	
	//*********** misc *************

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.tableRepository);
		children.add(this.sequencingPolicy);
		children.add(this.tableGenerationPolicy);
	}

	public void notifyExpressionsToRecalculateQueryables() {
		for (Iterator descriptors = this.descriptors(); descriptors.hasNext(); ) {
			((MWRelationalDescriptor) descriptors.next()).notifyExpressionsToRecalculateQueryables();
		}
	}	


	//*********** accessors *************

	/** This is used by the I/O manager. */
	public MWModel getMetaDataRepository() {
		return this.getTableRepository();
	}

	/** This is used by MWModel - allows us to make getDatabase() final. */
	public MWDatabase getTableRepository() {
		return this.tableRepository;
	}

	public MWSequencingPolicy getSequencingPolicy() {
		return this.sequencingPolicy;
	}
	
	public MWTableGenerationPolicy getTableGenerationPolicy() {
		return this.tableGenerationPolicy;
	}

	public String getTableCreatorSourceClassName() {
		return this.tableCreatorSourceClassName;
	}

	public void setTableCreatorSourceClassName(String tableCreatorSourceClassName) {
		Object old = this.tableCreatorSourceClassName;
		this.tableCreatorSourceClassName = tableCreatorSourceClassName;
		this.firePropertyChanged(TABLE_CREATOR_SOURCE_CLASS_NAME_PROPERTY, old, tableCreatorSourceClassName);
	}

	public String getTableCreatorSourceDirectoryName() {
		return this.tableCreatorSourceDirectoryName;
	}

	public void setTableCreatorSourceDirectoryName(String tableCreatorSourceDirectoryName) {
		if (tableCreatorSourceDirectoryName == null) {
			throw new NullPointerException();
		}
		Object old = this.tableCreatorSourceDirectoryName;
		this.tableCreatorSourceDirectoryName = tableCreatorSourceDirectoryName;
		this.firePropertyChanged(TABLE_CREATOR_SOURCE_DIRECTORY_NAME_PROPERTY, old, tableCreatorSourceDirectoryName);
	}


	// ********** descriptors and mappings **********

	protected MWDescriptor createDescriptorForType(MWClass type) throws InterfaceDescriptorCreationException {
		if (type.isInterface()) {
			return new MWInterfaceDescriptor(this, type, type.fullName());
		} else {
			return new MWTableDescriptor(this, type, type.fullName());
		}
	}

	public MWAggregateDescriptor addAggregateDescriptorForType(MWClass type) {
		MWAggregateDescriptor descriptor = new MWAggregateDescriptor(this, type, type.fullName());
		this.addDescriptor(descriptor);
		return descriptor;
	}

	public Iterator descriptorsThatImplement(final MWRelationalDescriptor descriptor) {
		return new FilteringIterator(this.tableDescriptors()) {
			protected boolean accept(Object next) {
				return ((MWDescriptor) next).getMWClass().allInterfacesContains(descriptor.getMWClass());
			}
		};
	}

	public Iterator tableDescriptors() {
		return new FilteringIterator(this.descriptors()) {
			protected boolean accept(Object next) {
				return ((MWRelationalDescriptor) next).isTableDescriptor();
			}
		};
	}

	public Iterator aggregateDescriptors() {
		return new FilteringIterator(this.descriptors()) {
			protected boolean accept(Object next) {
				return ((MWRelationalDescriptor) next).isAggregateDescriptor();
			}
		};
	}

	public Iterator interfaceDescriptors() {
		return new FilteringIterator(this.descriptors()) {
			protected boolean accept(Object next) {
				return ((MWRelationalDescriptor) next).isInterfaceDescriptor();
			}
		};	
	}	

	public Iterator allWriteableManyToManyMappings() {
		return new FilteringIterator(this.allWriteableMappings()) {
			protected boolean accept(Object next) {
				return ((MWMapping) next) instanceof MWManyToManyMapping;
			}
		};
	}
	
	public Iterator interfaceDescriptorsThatImplement(final MWMappingDescriptor descriptor) {
		return new FilteringIterator(this.interfaceDescriptors()) {
			protected boolean accept(Object next) {
				return ((MWInterfaceDescriptor) next).hasImplementor(descriptor);
			}
		};	
	}


	// ********** automap **********

	/**
	 * the project must have some tables before anything can be automapped
	 */
	public boolean canAutomapDescriptors() {
		return this.getDatabase().tablesSize() > 0;
	}

	/**
	 * match classes with tables using partial string matching on their short
	 * names, with any prevailing prefixes and/or suffixes stripped off
	 */
	protected void matchClassesAndMetaData(Collection automapDescriptors) {
		super.matchClassesAndMetaData(automapDescriptors);
		DescriptorStringHolder[] descriptorHolders = this.buildMetaDataDescriptorStringHolders(automapDescriptors);
		TableStringHolder[] tableHolders = this.buildTableStringHolders();
		StringHolderPair[] pairs = CLASS_META_DATA_NAME_COMPARATOR_ENGINE.match(descriptorHolders, tableHolders);
		for (int i = pairs.length; i-- > 0;) {
			StringHolderPair pair = pairs[i];
			DescriptorStringHolder descriptorHolder = (DescriptorStringHolder) pair.getStringHolder1();
			TableStringHolder tableHolder = (TableStringHolder) pair.getStringHolder2();
			if ((descriptorHolder == null) || (tableHolder == null)) {
				continue;
			}
			if (pair.getScore() > 0.50) {		// ???
				((MWTableDescriptor) descriptorHolder.getDescriptor()).setPrimaryTable(tableHolder.getTable());
			}
		}
	}

	private TableStringHolder[] buildTableStringHolders() {
		Collection unassignedTables = new ArrayList(this.getDatabase().tablesSize());
		CollectionTools.addAll(unassignedTables, this.getDatabase().tables());
		unassignedTables.removeAll(this.assignedTables());
		return TableStringHolder.buildHolders(unassignedTables);
	}

	/**
	 * "primary" tables only?
	 */
	private Set assignedTables() {
		Set tables = new HashSet(this.descriptorsSize());
		for (Iterator stream = this.tableDescriptors(); stream.hasNext(); ) {
			tables.add(((MWTableDescriptor) stream.next()).getPrimaryTable());
		}
		return tables;
	}


	// ********** export table creator source **********

	public void exportTableCreatorSource() {
		File tableCreatorSourceFile = this.tableCreatorSourceFile();
		tableCreatorSourceFile.getParentFile().mkdirs();

		TableCreator tableCreator = new TableCreator(CollectionTools.vector(this.getDatabase().runtimeTableDefinitions()));
		tableCreator.setName(this.getName());

		TableCreatorClassGenerator generator = new TableCreatorClassGenerator(tableCreator);
		generator.setClassName(this.tableCreatorSourceClassName);
		generator.setPackageName(this.tableCreatorSourcePackageName());
		generator.setOutputFileName(tableCreatorSourceFile.getAbsolutePath());
		generator.generate();
	}

	/**
	 * combine the current project source directory and Java file name
	 * return a File object representing the absolute location of the intended table creator source file
	 */
	public File tableCreatorSourceFile() {
		return new File(this.absoluteTableCreatorSourceDirectory(), Classpath.convertToJavaFileName(this.tableCreatorSourceClassName));
	}
	
	/**
	 * if the table creator source directory is relative, relate it to the
	 * project's save directory
	 */
	public File absoluteTableCreatorSourceDirectory() {
		File dir = new File(this.tableCreatorSourceDirectoryName);
		if ( ! dir.isAbsolute()) {
			dir = new File(this.getSaveDirectory(), this.tableCreatorSourceDirectoryName);
		}
		return dir;
	}

	public String tableCreatorSourcePackageName() {
		return ClassTools.packageNameForClassNamed(this.tableCreatorSourceClassName);
	}


	// ********** aggregate path to field **********

	public void recalculateAggregatePathsToColumn(MWMappingDescriptor descriptor) {
		this.recalculateAggregatePathsToColumn(descriptor, null);
	}

	//current mapping is used so that we can stay out of an infinite loop if the aggregates are set up with circular references
	public void recalculateAggregatePathsToColumn(MWMappingDescriptor descriptor, MWAggregateMapping currentMapping) {
		for (Iterator stream = this.aggregateMappingsWithReferenceDescriptor(descriptor); stream.hasNext(); ) {
			MWAggregateMapping mapping = (MWAggregateMapping) stream.next();
			if (mapping != currentMapping){
				mapping.updatePathsToFields();
				this.recalculateAggregatePathsToColumn(mapping.getParentDescriptor(), mapping);
			}
		}
	}

	private Iterator aggregateMappingsWithReferenceDescriptor(final MWMappingDescriptor descriptor) {
		return new FilteringIterator(this.allAggregateMappings()) {
			protected boolean accept(Object next) {
				return (((MWAggregateMapping) next).getReferenceDescriptor() == descriptor);
			}
		};
	}
	
	private Iterator allAggregateMappings() {
		return new FilteringIterator(this.allMappings()) {
			protected boolean accept(Object next) {
				return (next instanceof MWAggregateMapping);
			}
		};	
	}
	
	
	// ********** conversion to runtime **********
	 
	public boolean isGenerateDeprecatedDirectMappings() {
		return this.generateDeprecatedDirectMappings;
	}
	
	public void setGenerateDeprecatedDirectMappings(boolean newValue) {
		boolean oldValue = this.generateDeprecatedDirectMappings;
		this.generateDeprecatedDirectMappings = newValue;
		firePropertyChanged(GENERATE_DEPRECATED_DIRECT_MAPPINGS_PROPERTY, oldValue, newValue);
	} 
	
	protected DatasourceLogin buildRuntimeLogin() {
		DatabaseLogin login = this.getDatabase().buildDeploymentRuntimeDatabaseLogin();
		this.getSequencingPolicy().adjustRuntimeLogin(login);
		return login;
	}


	// ********** TopLink methods **********
	
	protected void legacy50PostBuild(DescriptorEvent event) {
		super.legacy50PostBuild(event);
		this.generateDeprecatedDirectMappings = true;
	}
	
	protected void legacy45PostBuild(DescriptorEvent event) {
		super.legacy45PostBuild(event);
		this.tableCreatorSourceClassName = "";
		this.tableCreatorSourceDirectoryName = "";
		this.generateDeprecatedDirectMappings = true;
	}

	/**
	 * convert to platform-independent representation
	 */
	private String getTableCreatorSourceDirectoryNameForTopLink() {
		return this.tableCreatorSourceDirectoryName.replace('\\', '/');
	}
	
	/**
	 * convert to platform-specific representation
	 */
	private void setTableCreatorSourceDirectoryNameForTopLink(String dirName) {
		this.tableCreatorSourceDirectoryName = new File(dirName).getPath();
	}

	/**
	 * strip out back slashes stored in old projects
	 */
	private void legacy50SetTableCreatorSourceDirectoryNameForTopLink(String dirName) {
		this.setTableCreatorSourceDirectoryNameForTopLink(dirName.replace('\\', '/'));
	}
	private String legacy50GetTableCreatorSourceDirectoryNameForTopLink() {
		throw new UnsupportedOperationException();
	}

}
