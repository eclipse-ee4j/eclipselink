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
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMappingHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
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
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWModifier;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWTransactionalProjectCachingPolicy;
//import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWTransactionalProjectEjbPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWSequencingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWTableGenerationPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAutoGeneratedQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadObjectQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWStringQueryFormat;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;

class PersistenceTools45 {

	public final static String PROJECT_FILE_EXTENSION = ".mwp";
	private static PersistenceTools45 INSTANCE;


	private PersistenceTools45() {
		super();
	}
	
	private DatasourceLogin buildLogin(File directory) {	
		MWXMLFileLogin45 login = new MWXMLFileLogin45(directory.getAbsolutePath());
		login.setAccessorClass(MWAccessor45.class);
		login.createDirectoriesAsNeeded();
	
		login.setDefaultNullValue(int.class, new Integer(0));
		login.setDefaultNullValue(long.class, new Long(0));
		
		return login;
	}
	
	private Project buildProject() {
		Project p = new Project();
		p.setName("Builder");
	 
		// MWProject and associated classes
		p.addDescriptor(MWRelationalProject.legacy45BuildDescriptor());
		p.addDescriptor(MWSequencingPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWTableGenerationPolicy.legacy45BuildDescriptor());
//		p.addDescriptor(MWTransactionalProjectEjbPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWRelationalProjectDefaultsPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWTransactionalProjectCachingPolicy.legacy45BuildDescriptor());
		
		// MWQuery and associated classes
		p.addDescriptor(MWAutoGeneratedQueryFormat.legacy45BuildDescriptor());
		p.addDescriptor(MWAbstractRelationalReadQuery.legacy45BuildDescriptor());
		p.addDescriptor(MWRelationalReadAllQuery.legacy45BuildDescriptor());
		p.addDescriptor(MWRelationalReadObjectQuery.legacy45BuildDescriptor());
		p.addDescriptor(MWQueryParameter.legacy45BuildDescriptor());
		p.addDescriptor(MWRelationalQueryManager.legacy45BuildDescriptor());
		p.addDescriptor(MWQueryFormat.legacy45BuildDescriptor());
		p.addDescriptor(MWStringQueryFormat.legacy45BuildDescriptor());
		
		// MWClass and associated classes
		p.addDescriptor(MWTypeDeclaration.legacy45BuildDescriptor());
		p.addDescriptor(MWClassAttribute.legacy45BuildDescriptor());
		p.addDescriptor(MWClass.legacy45BuildDescriptor());
		p.addDescriptor(MWClassRepository.legacy45BuildDescriptor());
		p.addDescriptor(MWMethod.legacy45BuildDescriptor());
		p.addDescriptor(MWModifier.legacy45BuildDescriptor());
		
		// MWDatabase and associated classes
		p.addDescriptor(MWDatabase.legacy45BuildDescriptor());
		p.addDescriptor(MWColumn.legacy45BuildDescriptor());
		p.addDescriptor(MWColumnPair.legacy45BuildDescriptor());
		p.addDescriptor(MWLoginSpec.legacy45BuildDescriptor());
		p.addDescriptor(MWReference.legacy45BuildDescriptor());
		p.addDescriptor(MWTable.legacy45BuildDescriptor());
		
		// MWDescriptor and associated classes
		p.addDescriptor(MWRelationalClassDescriptor.legacy45BuildDescriptor());
		p.addDescriptor(MWTableDescriptor.legacy45BuildDescriptor());
		p.addDescriptor(MWDescriptor.legacy45BuildDescriptor());
		p.addDescriptor(MWMappingDescriptor.legacy45BuildDescriptor());
		p.addDescriptor(MWAggregateDescriptor.legacy45BuildDescriptor());
		p.addDescriptor(MWInterfaceDescriptor.legacy45BuildDescriptor());
		p.addDescriptor(MWUserDefinedQueryKey.legacy45BuildDescriptor());
		p.addDescriptor(MWRelationalDescriptorInheritancePolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWClassIndicatorValue.legacy45BuildDescriptor());
		p.addDescriptor(MWRelationalClassIndicatorFieldPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWAbstractClassIndicatorPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWClassIndicatorExtractionMethodPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWDescriptorCachingPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWDescriptorCopyPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWDescriptorInstantiationPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWDescriptorInterfaceAliasPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWDescriptorAfterLoadingPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWDescriptorEventsPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWTableDescriptorLockingPolicy.legacy45BuildDescriptor());
		p.addDescriptor(MWDescriptorMultiTableInfoPolicy.legacy45BuildDescriptor());
		
		// MWMapping and associated classes
		p.addDescriptor(MWDirectToFieldMapping.legacy45BuildDescriptor());
		p.addDescriptor(MWLegacyUnmappedMapping.legacy45BuildDescriptor());
		p.addDescriptor(MWMapping.legacy45BuildDescriptor());
		p.addDescriptor(MWVariableOneToOneMapping.legacy45BuildDescriptor());
		p.addDescriptor(MWAbstractTableReferenceMapping.legacy45BuildDescriptor());
		p.addDescriptor(MWOneToOneMapping.legacy45BuildDescriptor());
		p.addDescriptor(MWOneToManyMapping.legacy45BuildDescriptor());
		p.addDescriptor(MWManyToManyMapping.legacy45BuildDescriptor());
		p.addDescriptor(MWAggregateMapping.legacy45BuildDescriptor());
		p.addDescriptor(MWAggregatePathToColumn.legacy45BuildDescriptor());
		p.addDescriptor(MWCollectionMapping.legacy45BuildDescriptor());
		p.addDescriptor(MWRelationalDirectCollectionMapping.legacy45BuildDescriptor());
		p.addDescriptor(MWRelationalTransformationMapping.legacy45BuildDescriptor());
		p.addDescriptor(MWRelationalFieldTransformerAssociation.legacy45BuildDescriptor());
		
		// Handles
		p.addDescriptor(MWColumnHandle.legacy45BuildDescriptor());
		p.addDescriptor(MWMethodHandle.legacy45BuildDescriptor());
		p.addDescriptor(MWReferenceHandle.legacy45BuildDescriptor());
		p.addDescriptor(MWMappingHandle.legacy45BuildDescriptor());
		
		return p;
	}
	
	/**
	 * Creates a new XML session given a directory name.
	 * You should use getSession(...) instead, as that will reuse a
	 * session if one already exists for a directory.
	 **/
	private DatabaseSession createSession(File directory) {
		Project project = buildProject();
		return createSession(project, directory);
	}
	/**
	 * Creates a new XML session given a project and a directory name.
	 * You should use getSession(...) instead, as that will reuse a
	 * session if one already exists for a directory.
	 **/
	private DatabaseSession createSession(Project project, File directory) {
		
		project.setLogin(buildLogin(directory));
		
		DatabaseSession s = project.createDatabaseSession();
	
		s.dontLogMessages();
	
		// named queries
		MWAccessor45 accessor = (MWAccessor45)((AbstractSession)s).getAccessor();
		accessor.addRootElementNameOverride("BldrProject");
		accessor.setOverrideFileExtension(PROJECT_FILE_EXTENSION);
		s.login();
	
		return s;
	}
	
	
	/**
	 * Returns a MWRProject stored in the given XML database directory that
	 * matches the appropriate project name.  If there is more
	 * than one project with that name, or no projects with that
	 * name, it throws a Runtime exception.
	 **/
	
	MWRelationalProject readMWProjectNamed(File directory, String projectName) throws DescriptorException 
	{
		MWRelationalProject project = null;
		Session session;
		try 
		{
			session = getSessionForDirectory(directory);
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
	 * Returns an XML session for a given directory.  If none exists,
	 * it creates a new one.
	 **/
	
	private DatabaseSession getSessionForDirectory(File directory) {
		return createSession(directory);
	}
	
	static synchronized PersistenceTools45 instance() {
		if (INSTANCE == null) {
			INSTANCE = new PersistenceTools45();
		}
		return INSTANCE;
	}
}
