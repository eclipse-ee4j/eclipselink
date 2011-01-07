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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.ProjectClassGenerator;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProjectSubFileComponentContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.DescriptorCreationFailureEvent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.DescriptorCreationFailureListener;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.DescriptorStringHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.ExternalClassLoadFailureContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.ExternalClassLoadFailureEvent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SPIManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.ManifestInterrogator;
import org.eclipse.persistence.tools.workbench.utility.XMLTools;
import org.eclipse.persistence.tools.workbench.utility.ManifestInterrogator.Defaults;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.events.DefaultChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.AffixStrippingPartialStringComparatorEngine;
import org.eclipse.persistence.tools.workbench.utility.string.ExhaustivePartialStringComparatorEngine;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparatorEngine;
import org.w3c.dom.Document;

public abstract class MWProject 
	extends MWModel
	implements ProjectSubFileComponentContainer
{
	private volatile String name;
		public static final String NAME_PROPERTY = "name";

	/**
	 * transient attribute configured upon construction;
	 * does not change
	 */
	private SPIManager spiManager;

	private Collection descriptors;
		public static final String DESCRIPTORS_COLLECTION = "descriptors";

	private MWClassRepository classRepository;

	private MWProjectDefaultsPolicy defaultsPolicy;

	/**
	 * deployment xml
	 */
	private volatile String deploymentXMLFileName;
		public static final String DEPLOYMENT_XML_FILE_NAME_PROPERTY = "deploymentXMLFileName";

	/**
	 * project source code settings
	 */
	private volatile String projectSourceClassName;
		public static final String PROJECT_SOURCE_CLASS_NAME_PROPERTY = "projectSourceClassName";

	private volatile String projectSourceDirectoryName;
		public static final String PROJECT_SOURCE_DIRECTORY_NAME_PROPERTY = "projectSourceDirectoryName";

	/**
	 * model source code settings
	 */
	private volatile String modelSourceDirectoryName;
		public static final String MODEL_SOURCE_DIRECTORY_NAME_PROPERTY = "modelSourceDirectoryName";

	/**
	 * transient attribute whose value is determined by where the project
	 * was read from or where it was last written
	 */
	private volatile File saveDirectory;
		public static final String SAVE_DIRECTORY_PROPERTY = "saveDirectory";

	/**
	 * these descriptor names are read in by TopLink and are then
	 * used and managed by the IOManager;
	 * DO NOT use them for anything else  ~bjv
	 */
	private Collection descriptorNames;
		private static final String DESCRIPTOR_NAMES_COLLECTION = "descriptorNames";

	/**
	 * transient attribute used to determine if the version has
	 * changed; see #postProjectBuild()
	 */
	private String version; 
	
	/**
	 * transient attribute used only to inform the user
	 * that legacy projects must be saved in the new file format
	 */
	private volatile boolean legacyProject;
	
	/**
	 * transient attribute used to forward change notifications to listeners;
	 * by default it simply forwards the notifications,
	 * but it is configured by the UI to forward the notifications on the
	 * AWT event dispatch thread
	 */
	private ChangeNotifier changeNotifier;
	
	/**
	 * transient attribute used to generate problems - does nothing by default;
	 * but is configured by the UI to generate problems in a separate thread
	 */
	private Validator validator;
	
	/**
	 * This flag is set to true when the project is being validated.
	 */
	private volatile boolean validating;
		public static final String VALIDATING_PROPERTY = "validating";


	//This is used for project weaving option which affects indirection validation for mappings
	private volatile boolean usesWeaving;
		public final static String USES_WEAVING_PROPERTY = "usesWeaving";

	// ********** static fields **********

	/**
	 * The name of the root node of the XML document in the
	 * project file (this is used to detect old projects).
	 * Unfortunately, this has changed over time....
	 * If this changes, update the appropriate code in ProjectReader
	 * and MWProject.
	 */
	public static final String CURRENT_PROJECT_ROOT_ELEMENT_NAME = "project";

	/**
	 * The name of the XML element in the project file that
	 * contains the schema version (which is used to detect old projects).
	 * Unfortunately, this has changed over time....
	 * If this changes, update the appropriate code in ProjectReader
	 * and MWProject.
	 */
	public static final String CURRENT_SCHEMA_VERSION_ELEMENT_NAME = "schema-version";

	/**
	 * The current schema version number (major.minor).
	 * Change the minor number when the schema changes but is still
	 * backward-compatible. Change the major number when the schema
	 * changes and is no longer backward-compatible.
	 * If the major number changes, update the appropriate code in ProjectReader
	 * and MWProject.
	 */
	public static final String CURRENT_SCHEMA_VERSION = "7.0";
	
	/*
	 * The above static values are used to discover the schema version
	 * in an XML document that looks like this (old projects will look slightly different):
	 * 	<project>
	 * 		<name>Foo Project</name>
	 * 		<product-version>11.1.0.0</product-version>
	 * 		<schema-version>7.0</schema-version>
	 * 		...
	 * 	</project>
	 */
	 
	// used by I/O Manager
	private static final String SUB_DIRECTORY_NAME = "descriptors";
	
	/** The project-specific file name extension - used by the I/O manager. */
	public static final String FILE_NAME_EXTENSION = ".mwp";

	private static final ManifestInterrogator MANIFEST_INTERROGATOR = new ManifestInterrogator(MWProject.class, new LocalManifestDefaults());


	// ********** static methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWProject.class);
		descriptor.setDefaultRootElement(CURRENT_PROJECT_ROOT_ELEMENT_NAME);

		InheritancePolicy ip = (InheritancePolicy) descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWRelationalProject.class, "relational");
		ip.addClassIndicator(MWOXProject.class, "o-x");
		ip.addClassIndicator(MWEisProject.class, "eis");

		descriptor.addDirectMapping("name", "name/text()");
		((XMLDirectMapping) descriptor.addDirectMapping("comment", "comment/text()")).setNullValue("");
		descriptor.addDirectMapping("version", "getProductVersionForTopLink", "setProductVersionForTopLink", "product-version/text()");
		//TODO - billy : what's the best way to handle this?
		descriptor.addDirectMapping("schemaVersion", "getSchemaVersionForTopLink", "setSchemaVersionForTopLink", CURRENT_SCHEMA_VERSION_ELEMENT_NAME + "/text()");

		XMLCompositeObjectMapping repositoryMapping = new XMLCompositeObjectMapping();
		repositoryMapping.setAttributeName("classRepository");
		repositoryMapping.setReferenceClass(MWClassRepository.class);
		repositoryMapping.setXPath("class-repository");
		descriptor.addMapping(repositoryMapping);
		
		XMLCompositeDirectCollectionMapping descriptorNamesMapping = new XMLCompositeDirectCollectionMapping();
		descriptorNamesMapping.setAttributeName("descriptorNames");
		descriptorNamesMapping.setSetMethodName("setDescriptorNamesForTopLink");
		descriptorNamesMapping.setGetMethodName("getDescriptorNamesForTopLink");
		descriptorNamesMapping.useCollectionClass(HashSet.class);
		descriptorNamesMapping.setXPath("descriptor-names/descriptor-name/text()");
		descriptor.addMapping(descriptorNamesMapping);
		
		XMLCompositeObjectMapping defaultsPolicyMapping = new XMLCompositeObjectMapping();
		defaultsPolicyMapping.setAttributeName("defaultsPolicy");
		defaultsPolicyMapping.setReferenceClass(MWProjectDefaultsPolicy.class);
		defaultsPolicyMapping.setXPath("defaults-policy");
		descriptor.addMapping(defaultsPolicyMapping);

		XMLDirectMapping deploymentXMLFileNameMapping = new XMLDirectMapping();
		deploymentXMLFileNameMapping.setAttributeName("deploymentXMLFileName");
		deploymentXMLFileNameMapping.setSetMethodName("setDeploymentXMLFileNameForTopLink");
		deploymentXMLFileNameMapping.setGetMethodName("getDeploymentXMLFileNameForTopLink");
		deploymentXMLFileNameMapping.setXPath("deployment-xml-file/text()");
		deploymentXMLFileNameMapping.setNullValue("");
		descriptor.addMapping(deploymentXMLFileNameMapping);
		
		XMLDirectMapping projectSourceClassNameMapping = new XMLDirectMapping();
		projectSourceClassNameMapping.setAttributeName("projectSourceClassName");
		projectSourceClassNameMapping.setXPath("project-source/class/text()");
		projectSourceClassNameMapping.setNullValue("");
		descriptor.addMapping(projectSourceClassNameMapping);
		
		XMLDirectMapping projectSourceDirectoryNameMapping = new XMLDirectMapping();
		projectSourceDirectoryNameMapping.setAttributeName("projectSourceDirectoryName");
		projectSourceDirectoryNameMapping.setSetMethodName("setProjectSourceDirectoryNameForTopLink");
		projectSourceDirectoryNameMapping.setGetMethodName("getProjectSourceDirectoryNameForTopLink");
		projectSourceDirectoryNameMapping.setXPath("project-source/directory/text()");
		projectSourceDirectoryNameMapping.setNullValue("");
		descriptor.addMapping(projectSourceDirectoryNameMapping);
		
		XMLDirectMapping modelSourceDirectoryNameMapping = new XMLDirectMapping();
		modelSourceDirectoryNameMapping.setAttributeName("modelSourceDirectoryName");
		modelSourceDirectoryNameMapping.setSetMethodName("setModelSourceDirectoryNameForTopLink");
		modelSourceDirectoryNameMapping.setGetMethodName("getModelSourceDirectoryNameForTopLink");
		modelSourceDirectoryNameMapping.setXPath("model-source/directory/text()");
		modelSourceDirectoryNameMapping.setNullValue("");
		descriptor.addMapping(modelSourceDirectoryNameMapping);
		
        XMLDirectMapping useWeavingMapping = new XMLDirectMapping();
        useWeavingMapping.setAttributeName("usesWeaving");
        useWeavingMapping.setXPath("use-weaving/text()");
        useWeavingMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(useWeavingMapping);

        return descriptor;
	}

	public static XMLDescriptor buildLegacy60Descriptor() {
		XMLDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();
		descriptor.setJavaClass(MWProject.class);
		descriptor.setDefaultRootElement(CURRENT_PROJECT_ROOT_ELEMENT_NAME);

		InheritancePolicy ip = (InheritancePolicy) descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWRelationalProject.class, "relational");
		ip.addClassIndicator(MWOXProject.class, "o-x");
		ip.addClassIndicator(MWEisProject.class, "eis");

		descriptor.addDirectMapping("name", "name/text()");
		((XMLDirectMapping) descriptor.addDirectMapping("comment", "comment/text()")).setNullValue("");
		descriptor.addDirectMapping("version", "getProductVersionForTopLink", "setProductVersionForTopLink", "product-version/text()");
		//TODO - billy : what's the best way to handle this?
		descriptor.addDirectMapping("schemaVersion", "getSchemaVersionForTopLink", "setSchemaVersionForTopLink", CURRENT_SCHEMA_VERSION_ELEMENT_NAME + "/text()");

		XMLCompositeObjectMapping repositoryMapping = new XMLCompositeObjectMapping();
		repositoryMapping.setAttributeName("classRepository");
		repositoryMapping.setReferenceClass(MWClassRepository.class);
		repositoryMapping.setXPath("class-repository");
		descriptor.addMapping(repositoryMapping);
		
		XMLCompositeDirectCollectionMapping descriptorNamesMapping = new XMLCompositeDirectCollectionMapping();
		descriptorNamesMapping.setAttributeName("descriptorNames");
		descriptorNamesMapping.setSetMethodName("setDescriptorNamesForTopLink");
		descriptorNamesMapping.setGetMethodName("getDescriptorNamesForTopLink");
		descriptorNamesMapping.useCollectionClass(HashSet.class);
		descriptorNamesMapping.setXPath("descriptor-names/descriptor-name/text()");
		descriptor.addMapping(descriptorNamesMapping);
		
		XMLCompositeObjectMapping defaultsPolicyMapping = new XMLCompositeObjectMapping();
		defaultsPolicyMapping.setAttributeName("defaultsPolicy");
		defaultsPolicyMapping.setReferenceClass(MWProjectDefaultsPolicy.class);
		defaultsPolicyMapping.setXPath("defaults-policy");
		descriptor.addMapping(defaultsPolicyMapping);
		
		XMLDirectMapping deploymentXMLFileNameMapping = new XMLDirectMapping();
		deploymentXMLFileNameMapping.setAttributeName("deploymentXMLFileName");
		deploymentXMLFileNameMapping.setSetMethodName("setDeploymentXMLFileNameForTopLink");
		deploymentXMLFileNameMapping.setGetMethodName("getDeploymentXMLFileNameForTopLink");
		deploymentXMLFileNameMapping.setXPath("deployment-xml-file/text()");
		deploymentXMLFileNameMapping.setNullValue("");
		descriptor.addMapping(deploymentXMLFileNameMapping);
		
		XMLDirectMapping projectSourceClassNameMapping = new XMLDirectMapping();
		projectSourceClassNameMapping.setAttributeName("projectSourceClassName");
		projectSourceClassNameMapping.setXPath("project-source/class/text()");
		projectSourceClassNameMapping.setNullValue("");
		descriptor.addMapping(projectSourceClassNameMapping);
		
		XMLDirectMapping projectSourceDirectoryNameMapping = new XMLDirectMapping();
		projectSourceDirectoryNameMapping.setAttributeName("projectSourceDirectoryName");
		projectSourceDirectoryNameMapping.setSetMethodName("setProjectSourceDirectoryNameForTopLink");
		projectSourceDirectoryNameMapping.setGetMethodName("getProjectSourceDirectoryNameForTopLink");
		projectSourceDirectoryNameMapping.setXPath("project-source/directory/text()");
		projectSourceDirectoryNameMapping.setNullValue("");
		descriptor.addMapping(projectSourceDirectoryNameMapping);
		
		XMLDirectMapping modelSourceDirectoryNameMapping = new XMLDirectMapping();
		modelSourceDirectoryNameMapping.setAttributeName("modelSourceDirectoryName");
		modelSourceDirectoryNameMapping.setSetMethodName("setModelSourceDirectoryNameForTopLink");
		modelSourceDirectoryNameMapping.setGetMethodName("getModelSourceDirectoryNameForTopLink");
		modelSourceDirectoryNameMapping.setXPath("model-source/directory/text()");
		modelSourceDirectoryNameMapping.setNullValue("");
		descriptor.addMapping(modelSourceDirectoryNameMapping);
		
		return descriptor;
	}

	// ********** constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	protected MWProject() {
		super();
	}

	protected MWProject(String name, SPIManager spiManager) {
		// projects are the only things that can go without a parent
		super(null);
		this.name = name;
		this.spiManager = spiManager;
	}


	// ********** initialization **********
	
	/**
	 * initialize transient state
	 */
	protected void initialize() {
		super.initialize();
		this.descriptors = new Vector();	// descriptors are not mapped directly
		this.changeNotifier = DefaultChangeNotifier.instance();
		this.validator = NULL_VALIDATOR;	// clients will replace this if appropriate
		this.validating = false;
	}

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.descriptorNames = new HashSet();
		this.classRepository = new MWClassRepository(this);
		this.defaultsPolicy = this.buildDefaultsPolicy();
		this.deploymentXMLFileName = "";
		this.projectSourceClassName = "";
		this.projectSourceDirectoryName = "";
		this.modelSourceDirectoryName = "";
		this.usesWeaving = false;
	}

	protected abstract MWProjectDefaultsPolicy buildDefaultsPolicy();

	protected void checkParent(Node parent) {
		// only Projects can lack a parent
		if (parent != null) {
			throw new IllegalArgumentException("An MWProject should not have a parent");
		} 
	}

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.descriptors) { children.addAll(this.descriptors); }
		children.add(this.classRepository);
		children.add(this.defaultsPolicy);
	}
		
	
	// ********** accessors **********

	// ***** name
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
	}


	// ***** SPI manager
	public SPIManager getSPIManager() {
		return this.spiManager;
	}


	// ***** descriptors
	public Iterator descriptors() {
		return new CloneIterator(this.descriptors) {
			protected void remove(Object current) {
				MWProject.this.removeDescriptor((MWDescriptor) current);
			}
		};
	}

	/** private - called by I/O Manager */
	private void setDescriptors(Collection descriptors) {
		this.descriptors = descriptors;
	}

	public int descriptorsSize() {
		return this.descriptors.size();
	}

	/** private - descriptors cannot be added directly */
	protected MWDescriptor addDescriptor(MWDescriptor descriptor) {
		descriptor.applyAdvancedPolicyDefaults(this.getDefaultsPolicy());
		this.addItemToCollection(descriptor, this.descriptors, DESCRIPTORS_COLLECTION);
		return descriptor;
	}
	
	public MWDescriptor addDescriptorForType(MWClass type) throws InterfaceDescriptorCreationException {
		return this.addDescriptor(this.createDescriptorForType(type));
	}
	
	protected abstract MWDescriptor createDescriptorForType(MWClass type) throws InterfaceDescriptorCreationException;

	public void removeDescriptor(MWDescriptor descriptor) {
		this.removeNodeFromCollection(descriptor, this.descriptors, DESCRIPTORS_COLLECTION);
	}

	public void removeDescriptorForType(MWClass type) {
		MWDescriptor descriptor = this.descriptorForType(type);
		if (descriptor != null) {
			this.removeDescriptor(descriptor);
		}
	}

	public void removeDescriptors(Collection descs) {
		this.removeNodesFromCollection(descs, this.descriptors, DESCRIPTORS_COLLECTION);
	}

	public void removeDescriptors(Iterator descs) {
		this.removeNodesFromCollection(descs, this.descriptors, DESCRIPTORS_COLLECTION);
	}

	public void replaceDescriptor(MWDescriptor oldDescriptor, MWDescriptor newDescriptor) {
		// don't want to trigger cascading #nodeRemoved(Node) - only need to trigger #descriptorReplaced()
		this.removeItemFromCollection(oldDescriptor, this.descriptors, DESCRIPTORS_COLLECTION);
		this.descriptorReplaced(oldDescriptor, newDescriptor);
	}
	

	// ***** class repository
	/**
	 * this is used by MWModel - allows us
	 * to make classRepository() final
	 */
	public MWClassRepository getClassRepository() {
		return this.classRepository;
	}


	// ***** defaults policy
	public MWProjectDefaultsPolicy getDefaultsPolicy() {
		return this.defaultsPolicy;
	}

	// ***** deployment xml
	public String getDeploymentXMLFileName() {
		return this.deploymentXMLFileName;
	}

	public void setDeploymentXMLFileName(String deploymentXMLFileName) {
		if (deploymentXMLFileName == null) {
			throw new NullPointerException();
		}
		Object old = this.deploymentXMLFileName;
		this.deploymentXMLFileName = deploymentXMLFileName;
		this.firePropertyChanged(DEPLOYMENT_XML_FILE_NAME_PROPERTY, old, deploymentXMLFileName);
	}
	

	// ***** project source
	public String getProjectSourceClassName() {
		return this.projectSourceClassName;
	}

	public void setProjectSourceClassName(String projectSourceClassName) {
		Object old = this.projectSourceClassName;
		this.projectSourceClassName = projectSourceClassName;
		this.firePropertyChanged(PROJECT_SOURCE_CLASS_NAME_PROPERTY, old, projectSourceClassName);
	}

	public String getProjectSourceDirectoryName() {
		return this.projectSourceDirectoryName;
	}

	public void setProjectSourceDirectoryName(String projectSourceDirectoryName) {
		if (projectSourceDirectoryName == null) {
			throw new NullPointerException();
		}
		Object old = this.projectSourceDirectoryName;
		this.projectSourceDirectoryName = projectSourceDirectoryName;
		this.firePropertyChanged(PROJECT_SOURCE_DIRECTORY_NAME_PROPERTY, old, projectSourceDirectoryName);
	}


	// ***** model source
	public String getModelSourceDirectoryName() {
		return this.modelSourceDirectoryName;
	}
	
	public void setModelSourceDirectoryName(String modelSourceDirectoryName) {
		if (modelSourceDirectoryName == null) {
			throw new NullPointerException();
		}
		Object old = this.modelSourceDirectoryName;
		this.modelSourceDirectoryName = modelSourceDirectoryName;
		this.firePropertyChanged(MODEL_SOURCE_DIRECTORY_NAME_PROPERTY, old, modelSourceDirectoryName);
	}
		

	// ***** save directory
	public File getSaveDirectory() {
		return this.saveDirectory;
	}

	/**
	 * if the save directory changes, we mark the entire
	 * project dirty so it is written out in the new directory
	 */
	public void setSaveDirectory(File saveDirectory) {
		Object old = this.saveDirectory;
		this.saveDirectory = saveDirectory;
		this.firePropertyChanged(SAVE_DIRECTORY_PROPERTY, old, saveDirectory);
		if (this.attributeValueHasChanged(old, saveDirectory)) {
			this.markEntireBranchDirty();
		}
	}


	// ***** legacy project
	public boolean isLegacyProject() {
		return this.legacyProject;
	}

	public void setIsLegacyProject(boolean legacyProject) {
		this.legacyProject = legacyProject;
	}
	

	// ***** changeNotifier
	/**
	 * as the root node, we must implement this method
	 */
	public ChangeNotifier getChangeNotifier() {
		return this.changeNotifier;
	}

	/**
	 * allow clients to install another change notifier
	 */
	public void setChangeNotifier(ChangeNotifier changeNotifier) {
		this.changeNotifier = changeNotifier;
	}


	// ***** validator
	/**
	 * as the root node, we must implement this method
	 */
	public Validator getValidator() {
		return this.validator;
	}

	/**
	 * allow clients to install an active validator
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}


	// ***** validating
	public final boolean isValidating() {
		return this.validating;
	}

	private void setIsValidating(boolean validating) {
		boolean old = this.validating;
		this.validating = validating;
		this.firePropertyChanged(VALIDATING_PROPERTY, old, validating);
	}


	// ********** descriptors and mappings **********

	/**
	 * return only the "mapping" descriptors, as opposed to "interface" descriptors
	 */
	public Iterator mappingDescriptors() {
		return new FilteringIterator(this.descriptors()) {
			protected boolean accept(Object next) {
				return next instanceof MWMappingDescriptor;
			}
		};
	}
	
	public Iterator interfaceDescriptorsThatImplement(MWMappingDescriptor descriptor) {
		return NullIterator.instance();
	}

	public Iterator descriptorsInPackage(final String packageName) {
		return new FilteringIterator(this.descriptors()) {
			protected boolean accept(Object next) {
				return ((MWDescriptor) next).packageName().equals(packageName);
			}
		};	
	}

	/**
	 * Attempt to either refresh the type and add a descriptor or, if a descriptor exists already,
	 * refresh the descriptor's type. Return the refresh failures.
	 */
	public ExternalClassLoadFailureContainer addDescriptorsForExternalClassDescriptions(Iterator externalClassDescriptions, DescriptorCreationFailureListener listener) {
		ExternalClassLoadFailureContainer failures = new ExternalClassLoadFailureContainer();
		while (externalClassDescriptions.hasNext()) {
			ExternalClassDescription exClassDescription = (ExternalClassDescription) externalClassDescriptions.next();
			try {
				this.classRepository.refreshTypeFor(exClassDescription);
			} catch (ExternalClassNotFoundException ex) {
				failures.externalClassLoadFailure(new ExternalClassLoadFailureEvent(this, exClassDescription.getName(), ex));
				continue;	// skip to the next external class description
			}
			MWClass type = this.typeNamed(exClassDescription.getName());
			MWDescriptor descriptor = this.descriptorForType(type);
			if (descriptor == null) {
				try {
					descriptor = this.addDescriptorForType(type);
				} catch (InterfaceDescriptorCreationException ex) {
					listener.descriptorCreationFailure(new DescriptorCreationFailureEvent(this, type.getName(), "DESCRIPTOR_CREATION_ERROR_MESSAGE"));
				}
			}
		}
		return failures;
	}

	public Iterator activeDescriptors() {
		return new FilteringIterator(this.descriptors()) {
			public boolean accept(Object next) {
				return ((MWDescriptor) next).isActive();
			}
		};
	}
	
	/**
	 * Returns a descriptor for a given type. This will NOT create the descriptor.
	 */
	public MWDescriptor descriptorForType(MWClass type) {
		synchronized (this.descriptors) {
			for (Iterator stream = this.descriptors.iterator(); stream.hasNext(); ) {
				MWDescriptor descriptor = (MWDescriptor) stream.next();
				if (descriptor.getMWClass() == type) {
					return descriptor;
				}
			}
		}
		return null;
	}

	public MWDescriptor descriptorForTypeNamed(String typeName) {
		return this.descriptorForType(this.typeNamed(typeName));
	}

	public MWDescriptor descriptorNamed(String descriptorName) {
		synchronized (this.descriptors) {
			for (Iterator stream = this.descriptors.iterator(); stream.hasNext(); ) {
				MWDescriptor descriptor = (MWDescriptor) stream.next();
				if (descriptor.getName().equals(descriptorName)) {
					return descriptor;
				}
			}
		}
		return null;
	}

	public void implementorsChangedFor(MWInterfaceDescriptor descriptor) {
		for (Iterator stream = this.mappingDescriptors(); stream.hasNext(); ) {
			((MWMappingDescriptor) stream.next()).implementorsChangedFor(descriptor);
		}
	}

	/**
	 * this is used by MWModel - allows us
	 * to make descriptorRepository() final
	 */
	public MWProject getDescriptorRepository() {
		return this;
	}

	/**
	 * return all the mappings in the project's active descriptors
	 * that have not been marked "read-only"
	 */
	public Iterator allWriteableMappings() {
		return new FilteringIterator(this.allMappings()) {
			protected boolean accept(Object next) {
				return ! ((MWMapping) next).isReadOnly();
			}

		};
	}

	/**
	 * return all the mappings in the project's active descriptors
	 */
	public Iterator allMappings() {
		return new CompositeIterator(
			new TransformationIterator(this.activeDescriptors()) {
				protected Object transform(Object next) {
					return ((MWDescriptor) next).mappings();
				}
			}
		);
	}

	public DescriptorStringHolder[] descriptorStringHolders() {
		return DescriptorStringHolder.buildHolders(this.descriptors);
	}

	// ********** deployment XML **********

	public void exportDeploymentXML() {
		File file = this.deploymentXMLFile();
		file.getParentFile().mkdirs();
		XMLProjectWriter.write(file.getAbsolutePath(), this.buildRuntimeProject());
	}

	/**
	 * if the deployment XML file is relative, relate it to the
	 * project's save directory
	 */
	public File deploymentXMLFile() {
		File file = new File(this.deploymentXMLFileName);
		return FileTools.convertToAbsoluteFile(file, this.getSaveDirectory());
	}


	// ********** export project source **********

	public void exportProjectSource() {
		File projectSourceFile = this.projectSourceFile();
		projectSourceFile.getParentFile().mkdirs();

		ProjectClassGenerator generator = new ProjectClassGenerator();
		generator.setProject(this.buildRuntimeProject());
		generator.setClassName(this.projectSourceClassName);
		generator.setPackageName(this.projectSourcePackageName());
		generator.setOutputFileName(projectSourceFile.getAbsolutePath());
		generator.generate();
	}

	/**
	 * combine the current project source directory and Java file name
	 */
	public File projectSourceFile() {
		return new File(this.absoluteProjectSourceDirectory(), Classpath.convertToJavaFileName(this.projectSourceClassName));
	}

	/**
	 * if the project source directory is relative, relate it to the
	 * project's save directory
	 */
	public File absoluteProjectSourceDirectory() {
		File dir = new File(this.projectSourceDirectoryName);
		return FileTools.convertToAbsoluteFile(dir, this.getSaveDirectory());
		}

	public String projectSourcePackageName() {
		return ClassTools.packageNameForClassNamed(this.projectSourceClassName);
	}


	// ********** export model source **********

	public File absoluteModelSourceDirectory() {
		File dir = new File(this.modelSourceDirectoryName);
		return FileTools.convertToAbsoluteFile(dir, this.getSaveDirectory());
	}


	// ********** automap **********

	/**
	 * Return whether the project is in a state to automap a
	 * collection of descriptors; in particular, whether the project
	 * has any tables or XML schemas.
	 */
	public abstract boolean canAutomapDescriptors();

	/**
	 * Automap the specified descriptors.
	 */
	public void automap(Collection automapDescriptors) {
		// automap inheritance first so the parent descriptors will be set, if possible
		for (Iterator stream = automapDescriptors.iterator(); stream.hasNext(); ) {
			((MWDescriptor) stream.next()).automapInheritanceHierarchy(automapDescriptors);
		}

		this.matchClassesAndMetaData(automapDescriptors);

		for (Iterator stream = automapDescriptors.iterator(); stream.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) stream.next();
			descriptor.automap();
		}
	}

	protected void matchClassesAndMetaData(Collection automapDescriptors) {
		// do nothing by default
	}

	/**
	 * convenience method for subclasses that implement #matchClassesAndMetaData(Collection)
	 */
	protected DescriptorStringHolder[] buildMetaDataDescriptorStringHolders(Collection automapDescriptors) {
		Collection descs = new ArrayList(automapDescriptors.size());
		for (Iterator stream = automapDescriptors.iterator(); stream.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) stream.next();
			if (descriptor.autoMapRequiresMetaData()) {
				descs.add(descriptor);
			}
		}
		return DescriptorStringHolder.buildHolders(descs);
	}

	/**
	 * convenience constant for subclasses that implement #matchClassesAndMetaData(Collection);
	 * this is the percentage of classes (or tables/schemas) that must have the same affix (prefix
	 * or suffix) for that affix to be stripped off before comparison
	 */
	protected static final float CLASS_META_DATA_PARTIAL_STRING_AFFIX_THRESHOLD = 0.80f;		// ???

	/**
	 * convenience constant for subclasses that implement #matchClassesAndMetaData(Collection)
	 */
	protected static final PartialStringComparatorEngine CLASS_META_DATA_NAME_COMPARATOR_ENGINE =
				AffixStrippingPartialStringComparatorEngine.forPrefixStripping(
					AffixStrippingPartialStringComparatorEngine.forSuffixStripping(
						new ExhaustivePartialStringComparatorEngine(PartialStringComparator.DEFAULT_COMPARATOR),
						CLASS_META_DATA_PARTIAL_STRING_AFFIX_THRESHOLD
					),
					CLASS_META_DATA_PARTIAL_STRING_AFFIX_THRESHOLD
				);


	// **************** runtime conversion *******************

	public Project buildRuntimeProject() {
		Project project = new Project();
		project.setName(this.getName());
		project.setLogin(this.buildRuntimeLogin());

		for (Iterator stream = CollectionTools.sortedSet(this.activeDescriptors()).iterator(); stream.hasNext(); ) {
			project.addDescriptor(((MWDescriptor) stream.next()).buildRuntimeDescriptor());
		}

		this.defaultsPolicy.adjustRuntimeProject(project);
		return project;
	}

	protected abstract DatasourceLogin buildRuntimeLogin();


	// ********** misc stuff **********

	public void nodeRenamed(Node node) {
		super.nodeRenamed(node);
		if (this.descriptors.contains(node)) {
			// if a descriptor has been renamed, we need to fire an "internal"
			// change event so the project is marked dirty
			this.fireCollectionChanged(DESCRIPTOR_NAMES_COLLECTION);
		}
	}

	/**
	 * this is used by MWModel - allows us
	 * to make metaDataRepository() final
	 */
	public abstract MWModel getMetaDataRepository();

	public ProjectSubFileComponentContainer getMetaDataSubComponentContainer() {
		return (ProjectSubFileComponentContainer) this.getMetaDataRepository();
	}


	public Iterator packageNames() {
		Set packageNames = new HashSet();
		synchronized (this.descriptors) {
			for (Iterator stream = this.descriptors.iterator(); stream.hasNext(); ) {
				packageNames.add(((MWDescriptor) stream.next()).packageName());
			}
		}
		return packageNames.iterator();
	}

	public void recalculateAggregatePathsToColumn(MWMappingDescriptor descriptor) {
		// by default, do nothing - only used by relational projects for now
	}
		
	public void recalculateAggregatePathsToColumn(MWMappingDescriptor descriptor, MWAggregateMapping currentMapping) {
		// by default, do nothing - only used by relational projects for now
	}

	public File saveFile() {
		File dir = this.getSaveDirectory();
		return (dir == null) ? null : new File(dir, FileTools.FILE_NAME_ENCODER.encode(this.getName()) + FILE_NAME_EXTENSION);
	}

	public void notifyExpressionsToRecalculateQueryables() {
		// by default, do nothing - only used by relational projects for now
	}

    public void hierarchyChanged(MWClass type) {
        for (Iterator stream = this.mappingDescriptors(); stream.hasNext(); ) {
            ((MWMappingDescriptor) stream.next()).hierarchyChanged(type);
        }
    }

    protected void addTransientAspectNamesTo(Set transientAspectNames) {
    	super.addTransientAspectNamesTo(transientAspectNames);
		transientAspectNames.add(VALIDATING_PROPERTY);
    }
    
    protected void addInsignificantAspectNamesTo(Set insignificantAspectNames) {
    	super.addInsignificantAspectNamesTo(insignificantAspectNames);
		insignificantAspectNames.add(VALIDATING_PROPERTY);
    }
    
	public void validateBranch() {
		this.setIsValidating(true);
		super.validateBranch();
		this.setIsValidating(false);
	}

    
	// ********** SubComponentContainer implementation **********

	public Iterator projectSubFileComponents() {
		return this.descriptors();
	}

	public void setProjectSubFileComponents(Collection subComponents) {
		this.setDescriptors(subComponents);
	}

	public Iterator originalProjectSubFileComponentNames() {
		return this.descriptorNames.iterator();
	}

	public void setOriginalProjectSubFileComponentNames(Collection originalSubComponentNames) {
		this.descriptorNames = originalSubComponentNames;
	}

	/**
	 * return whether the .mwp file has changed and must be written out;
	 * the .mwp file holds the project itself and all the objects
	 * beneath it except the "sub-components" written to separate files
	 * (classes, tables/schemas, and descriptors);
	 * this is a bit hacky; but it's a nice usability feature
	 */
	public boolean hasChangedMainProjectSaveFile() {
		if (this.isDirty()) {
			// the project itself is dirty
			return true;
		}
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			if (this.childHasChangedTheSaveFile(stream.next())) {
				return true;
			}
		}
		// only the "sub-components" must be dirty
		return false;
	}

	/**
	 * return whether the specified child of the project is dirty AND
	 * is written to the .mwp file
	 */
	private boolean childHasChangedTheSaveFile(Object child) {
		if (this.descriptors.contains(child)) {
			// descriptors are written to separate files
			return false;
		}
		if (child instanceof ProjectSubFileComponentContainer) {
			return ((ProjectSubFileComponentContainer) child).hasChangedMainProjectSaveFile();
		}
		// the child is NOT a sub-component container,
		// so all of its state is written to the .mwp file
		return ((Node) child).isDirtyBranch();
	}


	// ********** TopLink/IOManager methods **********

	/**
	 * the versions are write-only
	 */
	private String getProductVersionForTopLink() {
		return MANIFEST_INTERROGATOR.getVersionNumber();
	}
	public void setProductVersionForTopLink(String productVersion) {
		this.version = productVersion;
	}

	private String getSchemaVersionForTopLink() {
		return CURRENT_SCHEMA_VERSION;
	}
	private void setSchemaVersionForTopLink(String schemaVersion) {
		// ignore - this is for a write-only mapping - the version is hard-coded
	}

	/**
	 * convert to platform-independent representation
	 */
	private String getDeploymentXMLFileNameForTopLink() {
		return this.deploymentXMLFileName.replace('\\', '/');
	}
	/**
	 * convert to platform-specific representation
	 */
	private void setDeploymentXMLFileNameForTopLink(String deploymentXMLFileName) {
		this.deploymentXMLFileName = new File(deploymentXMLFileName).getPath();
	}

	/**
	 * convert to platform-independent representation
	 */
	private String getProjectSourceDirectoryNameForTopLink() {
		return this.projectSourceDirectoryName.replace('\\', '/');
	}
	/**
	 * convert to platform-specific representation
	 */
	private void setProjectSourceDirectoryNameForTopLink(String projectSourceDirectoryName) {
		this.projectSourceDirectoryName = new File(projectSourceDirectoryName).getPath();
	}

	/**
	 * convert to platform-independent representation
	 */
	private String getModelSourceDirectoryNameForTopLink() {
		return this.modelSourceDirectoryName.replace('\\', '/');
	}
	/**
	 * convert to platform-specific representation
	 */
	private void setModelSourceDirectoryNameForTopLink(String modelSourceDirectoryName) {
		this.modelSourceDirectoryName = new File(modelSourceDirectoryName).getPath();
	}

	/**
	 * sort the descriptor names for TopLink
	 */
	private Collection getDescriptorNamesForTopLink() {
		List names = new ArrayList(this.descriptors.size());
		synchronized (this.descriptors) {
			for (Iterator stream = this.descriptors.iterator(); stream.hasNext(); ) {
				names.add(((MWDescriptor) stream.next()).getName());
			}
		}
		return CollectionTools.sort(names, Collator.getInstance());
	}
	/**
	 * TopLink sets this value, which is then used by the
	 * ProjectIOManager to read in the actual descriptors
	 */
	private void setDescriptorNamesForTopLink(Collection descriptorNames) {
		this.descriptorNames = descriptorNames;
	}

	/**
	 * this is where the whole #postProjectBuild() cascade begins
	 * @see ProjectReader#readProject()
	 */
	public void postProjectBuild() {
		// set the child backpointers first
		this.setChildBackpointers();
		// resolve all handles and such
		this.resolveInternalReferences();
		// then, cascade to the entire project
		super.postProjectBuild();
		
		this.buildBasicTypes();
		// now, mark the entire project as "clean"
		if (this.version != null) {
			// do NOT mark project clean if it is a legacy project
			this.markEntireBranchClean();
		}
	}

	protected void resolveInternalReferences() {
		// resolve handles
		this.resolveHandles();
	}

	/** this is where all references to root objects are resolved */
	private void resolveHandles() {
		this.resolveClassHandles();
		this.resolveMetadataHandles();
		this.resolveColumnHandles();
		this.resolveReferenceHandles();
		this.resolveDescriptorHandles();
		this.resolveMethodHandles();
	}

	/**
	 * this is called just after the project is read in,
	 * so we don't need to mark the project dirty
	 */
	private void setSaveDirectoryForIOManager(File saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	/**
	 * this is called just after the project is read in,
	 * so we don't need to mark the project dirty
	 */
	private void setSPIManagerForIOManager(SPIManager spiManager) {
		this.spiManager = spiManager;
	}

	// ********** displaying and printing **********
	
	public String displayString() {
		return this.getName();
	}

	public void toString(StringBuffer sb) {
		sb.append(getName());
	}


	// ********** member classes **********

	private static class LocalManifestDefaults implements Defaults {
		public String defaultSpecificationTitle() {
			return "EclipseLink";
		}
		public String defaultSpecificationVendor() {
			return "Eclipse";
		}
		public String defaultReleaseDesignation() {
			return "Version 1.0.0";
		}
		public String defaultLibraryDesignation() {
			return "Workbench";
		}
		public String defaultSpecificationVersion() {
			return "1.0.0";
		}
		public String defaultImplementationVersion() {
			return this.defaultSpecificationVersion();
		}
	}

	// ***************** weaving *****************
	
	public boolean usesWeaving() {
		return this.usesWeaving;
	}
	
	public void setUsesWeaving(boolean newValue) {
		boolean oldValue = this.usesWeaving;
		this.usesWeaving = newValue;
		firePropertyChanged(USES_WEAVING_PROPERTY, oldValue, newValue);
	} 

}
