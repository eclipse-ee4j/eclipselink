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
package org.eclipse.persistence.tools.workbench.mappingsio;

import java.io.File;
import java.util.prefs.Preferences;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * Use an instance of this class to read and write MW projects.
 */
public class ProjectIOManager {

	/**
	 * The TopLink marshaller and unmarshaller used to read and write projects.
	 * This is created once and re-used with every read/write.
	 */
	private XMLMarshaller marshaller;
  private XMLUnmarshaller unmarshaller;


	/**
	 * The default file name extension for non-project XML files.
	 */
	private static final String DEFAULT_FILE_NAME_EXTENSION = ".xml";



	// ********** constructor/initialization **********

	public ProjectIOManager() {
		super();
		this.initialize();
	}

	private void initialize() {
		//TODO: remove once Blaise fixes problem with SAXPlatform.
		Project tlProject = this.buildProject();
        tlProject.setLogin(new XMLLogin(new DOMPlatform()));
		XMLContext context = new XMLContext(tlProject);
		this.marshaller = context.createMarshaller();
		this.unmarshaller = context.createUnmarshaller();
	}


	// ********** public stuff **********

	public MWProject read(File projectFile, Preferences preferences) {
		return this.read(projectFile, preferences, FileNotFoundListener.NULL_INSTANCE, LegacyProjectReadCallback.NULL_INSTANCE);
	}

	public MWProject read(File projectFile, Preferences preferences, FileNotFoundListener listener, LegacyProjectReadCallback legacyProjectReadCallback) {
		return new ProjectReader(this, projectFile, preferences, listener, legacyProjectReadCallback).read();
	}

	public void write(MWProject project) throws ReadOnlyFilesException {
		this.write(project, FileNotFoundListener.NULL_INSTANCE);
	}

	public void write(MWProject project, FileNotFoundListener listener) throws ReadOnlyFilesException {
		if (project.isDirtyBranch()) {
			new ProjectWriter(this, project, listener).write();
		}
	}
	

	// ********** package stuff **********

	void fireFileNotFound(FileNotFoundListener listener, File missingFile) {
		listener.fileNotFound(new FileNotFoundEvent(this, missingFile));
	}

	XMLMarshaller getMarshaller() {
		return this.marshaller;
	}
  
  XMLUnmarshaller getUnmarshaller() {
    return this.unmarshaller;
  }

	/**
	 * "Root" sub-component containers must have a static field that
	 * specifies the name of the sub-directory that holds the sub-components.
	 */
	String subDirectoryNameFor(Object container) {
		return (String) ClassTools.getStaticFieldValue(container.getClass(), "SUB_DIRECTORY_NAME");
	}

	String defaultFileNameExtension() {
		return DEFAULT_FILE_NAME_EXTENSION;
	}


	// ********** internal stuff **********

	/**
	 * Build and return a TopLink project for reading and
	 * writing MWProjects from and to XML files.
	 */
	private Project buildProject() {
		Project topLinkProject = new Project();
		topLinkProject.setName("MW project");
		
		// handles
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWAttributeHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWTableHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWReferenceHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.QName.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWNamedSchemaComponentHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMappingHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorQueryParameterHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWQueryableHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpecHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnPairHandle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWQueryKeyHandle.buildDescriptor());
		
		// MWClass and associated classes
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethodParameter.buildDescriptor());
		
		// MWDatabase and associated classes
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisLoginSpec.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWProperty.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable.buildDescriptor());
		
		// MWXmlSchemaRepository and associated classes
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema.BuiltInNamespace.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.AbstractSchemaComponent.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.AbstractNamedSchemaComponent.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ExplicitAttributeDeclaration.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ExplicitElementDeclaration.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ExplicitSchemaTypeDefinition.buildDescriptor());				
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ExplicitComplexTypeDefinition.buildDescriptor());				
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ExplicitSimpleTypeDefinition.buildDescriptor());			
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ModelGroupDefinition.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.SchemaComponentReference.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ReferencedModelGroup.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ReferencedSchemaTypeDefinition.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ReferencedComplexTypeDefinition.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ReferencedSimpleTypeDefinition.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ReferencedAttributeDeclaration.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ReferencedElementDeclaration.buildDescriptor());				
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.AbstractParticle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ExplicitModelGroup.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.NullParticle.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.Wildcard.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.Content.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ComplexContent.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.EmptyContent.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.SimpleContent.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.IdentityConstraintDefinition.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.IdentityConstraint.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.KeyIdentityConstraint.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.KeyRefIdentityConstraint.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.schema.UniqueIdentityConstraint.buildDescriptor());
		
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceSpecification.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ClasspathResourceSpecification.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.resource.FileResourceSpecification.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.resource.UrlResourceSpecification.buildDescriptor());
		
		// MWProject and associated classes
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWTransactionalProjectCachingPolicy.buildDescriptor());

		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWSequencingPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProjectDefaultsPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWTableGenerationPolicy.buildDescriptor());

		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWXmlProject.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProjectDefaultsPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProjectDefaultsPolicy.buildDescriptor());
		
		// Basic XML classes
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField.buildDescriptor());
		
		// MWDescriptor and associated classes
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWRefreshCachePolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAbstractTransactionalPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCopyPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInstantiationPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorLockingPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorAfterLoadingPolicy.buildDescriptor());
        topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCachingPolicy.buildDescriptor());
        topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCacheExpiry.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorEventsPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInterfaceAliasPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAbstractClassIndicatorPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorExtractionMethodPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorValue.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy.buildDescriptor());
		
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalTransactionalPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalPrimaryKeyPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWUserDefinedQueryKey.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptorInheritancePolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWDescriptorMultiTableInfoPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWSecondaryTableHolder.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalReturningPolicyInsertFieldReturnOnlyFlag.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalReturningPolicy.buildDescriptor());
		
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWCompositeEisDescriptor.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWRootEisDescriptor.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisTransactionalPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXTransactionalPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptorLockingPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlPrimaryKeyPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptorInheritancePolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptorInheritancePolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlClassIndicatorFieldPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptorLockingPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisReturningPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisReturningPolicyInsertFieldReturnOnlyFlag.buildDescriptor());
		
		// MWMapping and associated classes
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDefaultNullValuePolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverter.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConverter.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter.ValuePair.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWSerializedObjectConverter.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWAbstractReferenceMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWFieldTransformerAssociation.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformer.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMethodBasedTransformer.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWClassBasedTransformer.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerPolicy.MWContainerPolicyRoot.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.DefaultingContainerClass.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWCollectionContainerPolicy.buildDescriptor());
        topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWListContainerPolicy.buildDescriptor());
        topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWSetContainerPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapContainerPolicy.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectContainerMapping.buildDescriptor());

		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToXmlTypeMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWColumnQueryKeyPair.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWCollectionMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWCollectionOrdering.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectContainerMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalFieldTransformerAssociation.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTypeConversionConverter.buildDescriptor());

		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectCollectionMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAbstractAnyMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyAttributeMapping.buildDescriptor());		
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyObjectMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyCollectionMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAbstractCompositeMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeObjectMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeCollectionMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAbstractXmlDirectCollectionMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAbstractXmlDirectMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAbstractXmlReferenceMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlCollectionReferenceMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentCollectionMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlObjectReferenceMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlTransformationMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFieldTransformerAssociation.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFieldPair.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToOneMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisReferenceMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToManyMapping.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlTypeConversionConverter.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWContainerAccessor.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAttributeContainerAccessor.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWMethodContainerAccessor.buildDescriptor());

		// MWQuery and associated classes
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractReadQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter.buildDescriptor());

		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadAllQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalSpecificQueryOptions.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadObjectQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractCustomQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWInsertQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWDeleteQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWUpdateQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCustomReadAllQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCustomReadObjectQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportAttributeItem.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWGroupingItem.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWOrderingItem.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBatchReadItem.buildDescriptor());
        topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWJoinedItem.buildDescriptor());
        topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportOrderingItem.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWArgument.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWNullArgument.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAutoGeneratedQueryFormat.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBasicExpression.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCompoundExpression.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWEJBQLQueryFormat.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpression.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpressionQueryFormat.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWLiteralArgument.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgument.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgumentElement.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQueryManager.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryParameterArgument.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWSQLQueryFormat.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWStringQueryFormat.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWStoredProcedureQueryFormat.buildDescriptor());
		
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisQueryManager.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWOXQueryManager.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWAbstractEisReadQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisReadAllQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisReadObjectQuery.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisInteraction.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisInteraction.ArgumentPair.buildDescriptor());						
		
		//stored procedures
		
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedure.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractProcedureArgument.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractProcedureInOutputArgument.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedureNamedInArgument.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedureUnamedInArgument.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedureNamedOutputArgument.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedureUnamedOutputArgument.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedureNamedInOutputArgument.buildDescriptor());
		topLinkProject.addDescriptor(org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedureUnamedInOutputArgument.buildDescriptor());
		
		
		return topLinkProject;
	}

}
