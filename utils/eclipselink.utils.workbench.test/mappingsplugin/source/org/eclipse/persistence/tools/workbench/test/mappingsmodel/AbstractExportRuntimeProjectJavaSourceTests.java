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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.test.models.employee.Employee;
import org.eclipse.persistence.tools.workbench.test.utility.JavaTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;



public abstract class AbstractExportRuntimeProjectJavaSourceTests extends TestCase {
	protected File tempDir;

	protected AbstractExportRuntimeProjectJavaSourceTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.tempDir = FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName());
	}

	protected void compileAndCheckJavaExport(MWRelationalProject project) throws Exception {
		File javaFile = project.projectSourceFile();
		List classpathEntries = new ArrayList();
		classpathEntries.add(Classpath.locationFor(Project.class));
		classpathEntries.add(Classpath.locationFor(Employee.class));
		JavaTools.compile(javaFile, new Classpath(classpathEntries).path());

		ClassLoader classLoader = new URLClassLoader(new URL[] {javaFile.getParentFile().getAbsoluteFile().toURL()}, this.getClass().getClassLoader());
		Class projectClass = Class.forName(this.className(project), true, classLoader);

		Project runtimeProject = (Project) projectClass.newInstance();
		((DatasourceLogin) runtimeProject.getDatasourceLogin()).setConnector(project.getDatabase().getDeploymentLoginSpec().buildConnector());

// TODO compare projects
//		Project orginalProject = project.buildRuntimeProject();
		//lazy initialize
//		runtimeProject.getDatasourceLogin().getDatasourcePlatform().getDefaultSequence();
//		assertEquivalent(runtimeProject, orginalProject);


		DatabaseSession session = runtimeProject.createDatabaseSession();
		session.dontLogMessages();
//		session.logMessages();
		session.login();	// this will verify the mappings
		session.logout();
	}

	/**
	 * force the project to use the Oracle database
	 */
	protected void configureDeploymentLogin(MWRelationalProject project) {
		MWDatabase database = project.getDatabase();
		database.setDeploymentLoginSpec(database.loginSpecNamed("MySQL"));
	}

	protected File logFile(MWRelationalProject project) {
		return new File(this.tempDir, this.logFileName(project));
	}

	protected File sourceFile(MWRelationalProject project) {
		return new File(this.tempDir, this.sourceFileName(project));
	}

	private String logFileName(MWRelationalProject project) {
		return this.className(project) + ".log";
	}

	private String sourceFileName(MWRelationalProject project) {
		return this.className(project) + ".java";
	}

	protected String className(MWRelationalProject project) {
		return StringTools.removeAllSpaces(project.getName());
	}

}
