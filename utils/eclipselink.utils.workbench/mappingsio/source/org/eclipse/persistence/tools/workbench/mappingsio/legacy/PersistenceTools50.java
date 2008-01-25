/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsio.legacy;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAbstractClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorExtractionMethodPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorValue;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorAfterLoadingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCopyPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorEventsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInstantiationPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInterfaceAliasPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWDescriptorMultiTableInfoPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWUserDefinedQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWAttributeHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorQueryParameterHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMappingHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWQueryableHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWReferenceHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWLegacyUnmappedMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWTransactionalProjectCachingPolicy;
//import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWTransactionalProjectEjbPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWSequencingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWTableGenerationPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAutoGeneratedQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBasicExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCompoundExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWEJBQLQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpressionQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWLiteralArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryParameterArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgumentElement;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadObjectQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWSQLQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWStringQueryFormat;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import deprecated.xml.XMLDescriptor;
import deprecated.xml.XMLFileAccessor;
import deprecated.xml.XMLFileAccessorFilePolicy;
import deprecated.xml.XMLFileLogin;
/**
 * 
 */
class PersistenceTools50 {

	// singleton
	private static PersistenceTools50 INSTANCE;

	// standard MW Project file extension; all other files have .xml extension
	public final static String PROJECT_FILE_EXTENSION = ".mwp";
	

	/**
	 * Constructor is private. Use #instance() to get the singleton.
	 */
	private PersistenceTools50() {
		super();
	}
	
	private DatasourceLogin buildLogin(File directory) {
	
		XMLFileLogin login = new MWXMLFileLogin50(directory.getAbsolutePath());
		login.setAccessorClass(MWAccessor50.class);
		login.createDirectoriesAsNeeded();
	
		login.setDefaultNullValue(int.class, new Integer(0));
		login.setDefaultNullValue(long.class, new Long(0));
	
		return login;
	}
	
	/**
	 * Build and return a TopLink project for reading and
	 * writing MWClasses from and to XML files.
	 */
	private Project buildMiniProject() {
		
		Project	miniTopLinkProject = new Project();
		miniTopLinkProject.setName("MW mini project");
		 
		miniTopLinkProject.addDescriptor(MWClass.legacy50BuildDescriptor());
		miniTopLinkProject.addDescriptor(MWClassAttribute.legacy50BuildDescriptor());
		miniTopLinkProject.addDescriptor(MWAttributeHandle.legacy50BuildDescriptor());
		miniTopLinkProject.addDescriptor(MWMethod.legacy50BuildDescriptor());
		miniTopLinkProject.addDescriptor(MWMethodHandle.legacy50BuildDescriptor());
		miniTopLinkProject.addDescriptor(MWTypeDeclaration.legacy50BuildDescriptor());
			
		return miniTopLinkProject;
	}
	
	/**
	 * Build and return a TopLink project for reading and
	 * writing MWProjects from and to XML files.
	 */
	private Project buildProject() {
		Project topLinkProject = buildMiniProject();
		topLinkProject.setName("MW project");
		 
			
		topLinkProject.addDescriptor(MWCompoundExpression.legacy50BuildDescriptor());
		
		
		topLinkProject.addDescriptor(MWRelationalProject.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWSequencingPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWTableGenerationPolicy.legacy50BuildDescriptor());
//		topLinkProject.addDescriptor(MWTransactionalProjectEjbPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWRelationalProjectDefaultsPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWTransactionalProjectCachingPolicy.legacy50BuildDescriptor());
		
		topLinkProject.addDescriptor(MWClassRepository.legacy50BuildDescriptor());
		
		topLinkProject.addDescriptor(MWDatabase.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWColumn.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWColumnPair.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWLoginSpec.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWReference.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWTable.legacy50BuildDescriptor());

		topLinkProject.addDescriptor(MWDescriptor.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWMappingDescriptor.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWRelationalClassDescriptor.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWTableDescriptor.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWAggregateDescriptor.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWInterfaceDescriptor.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWRelationalDescriptorInheritancePolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWRelationalClassIndicatorFieldPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWClassIndicatorExtractionMethodPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWAbstractClassIndicatorPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWClassIndicatorValue.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWDescriptorCachingPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWDescriptorCopyPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWDescriptorInstantiationPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWDescriptorInterfaceAliasPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWDescriptorAfterLoadingPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWDescriptorEventsPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWTableDescriptorLockingPolicy.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWDescriptorMultiTableInfoPolicy.legacy50BuildDescriptor());
		
		topLinkProject.addDescriptor(MWMapping.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWDirectToFieldMapping.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWVariableOneToOneMapping.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWLegacyUnmappedMapping.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWAbstractTableReferenceMapping.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWOneToOneMapping.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWOneToManyMapping.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWManyToManyMapping.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWCollectionMapping.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWAggregateMapping.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWAggregatePathToColumn.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWRelationalDirectCollectionMapping.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWRelationalTransformationMapping.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWRelationalFieldTransformerAssociation.legacy50BuildDescriptor());
	
		topLinkProject.addDescriptor(MWDescriptorQueryParameterHandle.legacy50BuildDescriptor());		
		topLinkProject.addDescriptor(MWColumnHandle.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWReferenceHandle.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWMappingHandle.legacy50BuildDescriptor());
		
		topLinkProject.addDescriptor(MWEJBQLQueryFormat.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWArgument.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWAutoGeneratedQueryFormat.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWBasicExpression.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWExpression.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWExpressionQueryFormat.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWAbstractRelationalReadQuery.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWRelationalReadAllQuery.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWRelationalReadObjectQuery.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWQueryParameter.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWLiteralArgument.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWQueryableArgument.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWQueryableArgumentElement.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWQueryableHandle.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWQueryFormat.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWRelationalQueryManager.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWQueryParameterArgument.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWQueryParameter.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWSQLQueryFormat.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWStringQueryFormat.legacy50BuildDescriptor());
		topLinkProject.addDescriptor(MWUserDefinedQueryKey.legacy50BuildDescriptor());

		return topLinkProject;
	}
	
	/**
	 * Build and return a new XML session for the specified
	 * run-time project and directory.
	 */
	private DatabaseSession buildSession(Project runtimeProject, File directory) {
		runtimeProject.setLogin(this.buildLogin(directory));
		DatabaseSession session = runtimeProject.createDatabaseSession();
	
		// add the MWProject file policy: this will read/write MWProjects
		// from the base directory instead of a subdirectory
		MWAccessor50 accessor = (MWAccessor50) ((AbstractSession) session).getAccessor();
		String rootElementName = ((XMLDescriptor) runtimeProject.getDescriptors().get(MWRelationalProject.class)).getRootElementName();
		XMLFileAccessorFilePolicy policy = new MWProjectXMLFilePolicy50(directory, PROJECT_FILE_EXTENSION);
		accessor.addFilePolicy(rootElementName, policy);
	
		// now add the MWClass override policies:
		XMLDescriptor mwClassDescriptor = (XMLDescriptor) runtimeProject.getDescriptors().get(MWClass.class);
		String overrideRootElementName = mwClassDescriptor.getRootElementName();
	
		Session miniSession = this.buildMiniSession();
		
		//   the first keeps "core" classes (primitive, Java, TopLink) from being written out to files
		accessor.addOverridePolicy(this.buildCoreClassOverridePolicy(accessor, miniSession, overrideRootElementName));
	
		//   the second keeps "stub" classes from being written out to files
		String stubElementName = ((DirectToFieldMapping) mwClassDescriptor.getMappingForAttributeName(MWClass.LEGACY_50_STUB_ATTRIBUTE_NAME)).getFieldName();
		accessor.addOverridePolicy(this.buildStubClassOverridePolicy(accessor, miniSession, overrideRootElementName, stubElementName, MWClass.LEGACY_50_STUB_NULL_VALUE));

		session.dontLogMessages();
		session.login();
		return session;
	}
	
	
	/**
	 * Build and return an XML session that supports only reading
	 * and writing MWClasses and the classes contained therein.
	 */
	private Session buildMiniSession() {
		Project runtimeProject = this.buildMiniProject();
		runtimeProject.setLogin(this.buildLogin(new File(FileTools.USER_TEMPORARY_DIRECTORY_NAME)));
		XMLFileLogin login = (XMLFileLogin) runtimeProject.getDatasourceLogin();
		login.dontCreateDirectoriesAsNeeded();	// this will trigger an exception if we have any leaks
		DatabaseSession session = runtimeProject.createDatabaseSession();
		session.dontLogMessages();
		session.login();
		return session;
	}
	
	/**
	 * Build and return an override policy that will prevent the generation
	 * of XML files for the following classes:
	 *   Java primitives and void
	 *   Java runtime classes
	 *   TopLink runtime classes
	 */
	private MWAccessorOverridePolicy50 buildCoreClassOverridePolicy(XMLFileAccessor parentAccessor, Session miniSession, String rootElementName) {
		CoreClassOverridePolicy50 overridePolicy = new CoreClassOverridePolicy50(parentAccessor, miniSession, rootElementName);
		overridePolicy.addOverrideClassNames(MWClassRepository.coreClassNames());
		return overridePolicy;
	}
	
	/**
	 * Build and return an override policy that will prevent the generation
	 * of XML files for "stub" classes.
	 */
	private MWAccessorOverridePolicy50 buildStubClassOverridePolicy(MWAccessor50 parentAccessor, Session miniSession, String rootElementName, String stubElementName, Object stubNullValue) {
		return new StubClassOverridePolicy50(parentAccessor, miniSession, rootElementName, stubElementName, stubNullValue);
	}
	
	/**
	 * Return an XML session for the specified directory.
	 */
	private DatabaseSession buildSessionForDirectory(File directory) {
		return this.buildSession(this.buildProject(), directory);
	}
	
	MWRelationalProject readMWProjectNamed(File directory, String projectName) throws DescriptorException 
	{
		MWRelationalProject project = null;
		Session session;
		try 
		{
			session = buildSessionForDirectory(directory);
			project = (MWRelationalProject) session.readObject(MWRelationalProject.class, new ExpressionBuilder().get("name").equal(projectName));
		}
		catch (DescriptorException de) 
		{
			Throwable exception = de.getInternalException();
	
			if ((de.getErrorCode() == 106) &&
				 (exception instanceof InvocationTargetException))
			{
				InvocationTargetException invocationException = (InvocationTargetException) exception;
				Throwable targetException = invocationException.getTargetException();
				String message = targetException.getMessage();
	
				if ((targetException instanceof ClassNotFoundException) &&
					 (message.startsWith("TOPLink") ||
					  message.startsWith("com.webgain")))
				{
					// If this is a renaming issue, we'll throw a PNF exception and do
					// something about it in mainView
					//TODO HIGH PRIORITY - need to fix exception, BuilderException no longer exists
					throw new RuntimeException("Unable to load project");
					//throw BuilderException.projectNotFound(directory, projectName);
				}
			}
	
			throw de;
		}
			
		// Toplink we'll throw an exception before we get here if the project is bad, but just in case, we'll throw a NPE	
		if (project == null) 
		{
			throw new NullPointerException();
		}
		
		return project;
	}
	
	/**
	 * Return the singleton.
	 */
	static synchronized PersistenceTools50 instance() {
		if (INSTANCE == null) {
			INSTANCE = new PersistenceTools50();
		}
		return INSTANCE;
	}
	
}
