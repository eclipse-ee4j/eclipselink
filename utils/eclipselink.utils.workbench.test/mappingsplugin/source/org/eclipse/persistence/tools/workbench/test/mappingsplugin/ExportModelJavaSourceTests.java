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
package org.eclipse.persistence.tools.workbench.test.mappingsplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.workbench.framework.Application;
import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.IconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.generation.MWDescriptorGenerator;
import org.eclipse.persistence.tools.workbench.mappingsmodel.generation.MWRelationshipHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPluginResourceBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.sourcegen.SourceCodeGenerator;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.models.projects.TestDatabases;
import org.eclipse.persistence.tools.workbench.test.utility.JavaTools;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.CancelException;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public class ExportModelJavaSourceTests 
	extends TestCase
{	
	private MWRelationalProject project;
	private File tempDirectory;
	private String clientClassShortName;

	public static Test suite() {
		TestTools.setUpJUnitThreadContextClassLoader();
		return new TestSuite(ExportModelJavaSourceTests.class);
	}

	public ExportModelJavaSourceTests(String name) {
		super(name);
	}


	// **************** test set up, tear down stuff **************************
	
	protected void setUp() throws Exception {
		super.setUp();
		this.tempDirectory = FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName());
		this.project = this.buildProject();
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	/**
	 * builds a project with tables only
	 */
	private MWRelationalProject buildProject() {
		MWRelationalProject result = new MWRelationalProject(ClassTools.shortClassNameForObject(this), MappingsModelTestTools.buildSPIManager(), null);
		
		// database
		MWDatabase database = result.getDatabase();
		TestDatabases.configureMySQLDatabase(database);
		DatabasePlatform platform = database.getDatabasePlatform();
		
		// employee table
		MWTable employeeTable = database.addTable("employee");
		MWColumn empIdField = employeeTable.addColumn("id");
		empIdField.setDatabaseType(platform.databaseTypeNamed("integer"));
		empIdField.setPrimaryKey(true);
		MWColumn nameField = employeeTable.addColumn("name");
		nameField.setDatabaseType(platform.databaseTypeNamed("varchar"));
		
		// phone number table
		MWTable phoneNumberTable = database.addTable("phone_number");
		MWColumn phoneIdField = phoneNumberTable.addColumn("id");
		phoneIdField.setDatabaseType(platform.databaseTypeNamed("integer"));
		phoneIdField.setPrimaryKey(true);
		MWColumn phoneEmpIdField = phoneNumberTable.addColumn("emp_id");
		phoneEmpIdField.setDatabaseType(platform.databaseTypeNamed("integer"));
		MWColumn numField = phoneNumberTable.addColumn("num");
		numField.setDatabaseType(platform.databaseTypeNamed("varchar"));
		
		// phone number -> employee reference
		MWReference phoneNumberEmployeeReference = phoneNumberTable.addReference("phone_number_employee", employeeTable);
		phoneNumberEmployeeReference.addColumnPair(phoneEmpIdField, empIdField);

		return result;
	}
	
	private String getPackageName()
	{
		return "foo";
	}
	
	private String getClientClassShortName()
	{
		return this.clientClassShortName;
	}
	
	private String getClientClassName()
	{
		return getPackageName() + "." + getClientClassShortName();
	}
	
	// **************** actual test methods ***********************************
	
	public void testGenerateEndToEndWithDirectFieldAccessing()
		throws Exception
	{
		this.clientClassShortName = "DirectAccessingEndToEndClient";
		internalTestGenerateEndToEnd(false);
	}
	
	public void testGenerateEndToEndWithMethodAccessing()
		throws Exception
	{
		this.clientClassShortName = "MethodAccessingEndToEndClient";
		internalTestGenerateEndToEnd(true);
	}
	
	
	// **************** guts of the tests *************************************
	
	private void internalTestGenerateEndToEnd(boolean generateMethods)
		throws Exception
	{
		generateDescriptors(generateMethods);
		generateSourceCode();
		compileSourceCode();
		generateAndCompileProjectCode();
		setUpDatabase();
		exerciseClient();
	}
	
	
	// **************** generating the descriptors ****************************
	
	private void generateDescriptors(boolean generateMethods)
	{
		MWDescriptorGenerator generator = new MWDescriptorGenerator();
		generator.setGenerateBidirectionalRelationships(false);
		generator.setGenerateEjbs(false);
		generator.setGenerateMethodAccessors(generateMethods);
		generator.setGenerateLocalInterfaces(false);
		generator.setGenerateRemoteInterfaces(false);
		generator.setPackageName(getPackageName());
		generator.setProject(this.project);
		generator.setRelationshipsToCreate(relationshipsToCreate());
		generator.setTables(CollectionTools.collection(this.project.getDatabase().tables()));
		generator.generateClassesAndDescriptors();
	}
	
	private Collection relationshipsToCreate()
	{
		Collection relationships = new Vector();
		
		MWReference phoneNumberToEmployeeRef = this.project.getDatabase().tableNamed("phone_number").referenceNamed("phone_number_employee");
		
		MWRelationshipHolder phoneNumberToEmployee = new MWRelationshipHolder(phoneNumberToEmployeeRef, false);
		phoneNumberToEmployee.setOneToOne();
		relationships.add(phoneNumberToEmployee);
		
		MWRelationshipHolder employeeToPhoneNumbers = new MWRelationshipHolder(phoneNumberToEmployeeRef, true);
		employeeToPhoneNumbers.setOneToMany();
		relationships.add(employeeToPhoneNumbers);
		
		return relationships;
	}
	
	
	// **************** generating the source code ****************************
	
	private void generateSourceCode()
	{	
		SourceCodeGenerator generator = new SourceCodeGenerator(buildApplicationContext());
		generator.setOverwriteFiles(true);
		try {
			generator.generateSourceCode(this.project, CollectionTools.collection(this.project.descriptors()), this.tempDirectory);
		}
		catch (CancelException e) {
			throw new RuntimeException(e);
		}
	}
	
	private ApplicationContext buildApplicationContext() {
		return new ApplicationContext() {
			public Application getApplication() {
				return new Application() {
					public String getFullProductName() {
						return null;
					}
					public String getProductName() {
						return null;
					}
					public String getShortProductName() {
						return null;
					}
					public String getVersionNumber() {
						return null;
					}
					public String getReleaseDesignation() {
						return null;
					}
					public String getFullProductNameAndVersionNumber() {
						return null;
					}
					public String getBuildNumber() {
						return null;
					}
					public boolean isDevelopmentMode() {
						return false;
					}
					public boolean isFirstExecution() {
						return false;
					}
				};
			}

			public Preferences getPreferences() {
				return null;
			}

			public ResourceRepository getResourceRepository() {
				return new DefaultResourceRepository(MappingsPluginResourceBundle.class);
			}

			public NodeManager getNodeManager() {
				return null;
			}

			public HelpManager getHelpManager() {
				return null;
			}

			public ApplicationContext buildRedirectedPreferencesContext(String path) {
				return null;
			}

			public ApplicationContext buildExpandedResourceRepositoryContext(Class resourceBundleClass, IconResourceFileNameMap iconResourceFileNameMap) {
				return null;
			}

			public ApplicationContext buildExpandedResourceRepositoryContext(IconResourceFileNameMap iconResourceFileNameMap) {
				return null;
			}

			public ApplicationContext buildExpandedResourceRepositoryContext(Class resourceBundleClass) {
				return null;
			}
		};
	}
	
	// **************** compiling the source code *****************************
	private void compileSourceCode()
		throws Exception
	{
		// the code includes references to ValueHolderInterface
		JavaTools.compile(FileTools.filesInTree(this.tempDirectory), Classpath.locationFor(ValueHolderInterface.class));
	}
	
	
	// **************** generating and compiling the project source code ******
	
	private void generateAndCompileProjectCode()
		throws Exception
	{
		String projectClassShortName = "EndToEndProject";
		
		this.project.setProjectSourceClassName(getPackageName() + "." + projectClassShortName);
		this.project.setProjectSourceDirectoryName(this.tempDirectory.getAbsolutePath());
		this.project.getRepository().addClasspathEntry(this.tempDirectory.getAbsolutePath());
		this.project.exportProjectSource();
		
		copyClientFileToTempDir();
		
		
		// easier to recompile model code than work them into the classpath
		JavaTools.compile(FileTools.filesInTree(this.tempDirectory), Classpath.locationFor(Project.class));
	}
	
	private void copyClientFileToTempDir()
		throws Exception
	{
		File clientFile = FileTools.resourceFile("/export/" + getClientClassShortName() + ".java");
		FileTools.copyToDirectory(clientFile, new File(this.tempDirectory, getPackageName()));
	}
	
	
	// **************** exercising the client *********************************
	
	private void setUpDatabase() throws Exception {
		this.project.getDatabase().login();
		this.project.getDatabase().generateTables();
		this.project.getDatabase().logout();
	}

	private void exerciseClient() throws Exception {
		// all we should need on the classpath is the domain classes, toplink, and the db driver
		List classpathEntries = new ArrayList();
		classpathEntries.add(this.tempDirectory.getAbsolutePath());
		classpathEntries.add(Classpath.locationFor(Project.class));
		MWLoginSpec loginSpec = this.project.getDatabase().getDeploymentLoginSpec();
		for (Iterator stream = loginSpec.fullyQualifiedDriverClasspathFiles(); stream.hasNext(); ) {
			File file = (File) stream.next();
			classpathEntries.add(file.getAbsolutePath());
		}
		Classpath classpath = new Classpath(classpathEntries);
		// this *should* throw an exception if there are any problems
		JavaTools.java(this.getClientClassName(), classpath.path());
	}

}
