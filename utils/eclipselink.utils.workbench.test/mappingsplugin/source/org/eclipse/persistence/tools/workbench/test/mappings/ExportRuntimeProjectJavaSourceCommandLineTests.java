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
package org.eclipse.persistence.tools.workbench.test.mappings;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.workbench.mappings.JavaSourceGenerator;
import org.eclipse.persistence.tools.workbench.mappingsio.ProjectIOManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.AbstractExportRuntimeProjectJavaSourceTests;
import org.eclipse.persistence.tools.workbench.test.models.projects.LegacyEmployeeProject;
import org.eclipse.persistence.tools.workbench.test.utility.JavaTools;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;

public class ExportRuntimeProjectJavaSourceCommandLineTests extends AbstractExportRuntimeProjectJavaSourceTests {

	public static Test suite() {
		TestTools.setUpJUnitThreadContextClassLoader();
		return new TestSuite(ExportRuntimeProjectJavaSourceCommandLineTests.class);
	}

	public ExportRuntimeProjectJavaSourceCommandLineTests(String name) {
		super(name);
	}

	// TODO change this to use EmployeeProject once the runtime 
	// has put directMapMapping in the ProjectClassGenerator
	public void testEmployeeProject() throws Exception {
		verifyJavaExport(new LegacyEmployeeProject().getProject());
	}

	private void verifyJavaExport(MWRelationalProject project) throws Exception {
		this.configureDeploymentLogin(project);
		// write the project first, so it can be read via the command-line
		project.setSaveDirectory(this.tempDir);
		new ProjectIOManager().write(project);

		project.setProjectSourceDirectoryName(this.tempDir.getPath());
		project.setProjectSourceClassName(this.className(project));

		List classpathEntries = new ArrayList();
		classpathEntries.add(this.tempDir.getAbsolutePath());
		classpathEntries.add(Classpath.locationFor(MWProject.class));	// elmwcore.jar
		classpathEntries.add(Classpath.locationFor(JavaSourceGenerator.class));	// eclipselinkmw.jar
		classpathEntries.add(Classpath.locationFor(ValueHolderInterface.class));	// ecilpselink.jar
		classpathEntries.add(Classpath.locationFor(XMLParserConfiguration.class));  // xercesImpl.jar
		classpathEntries.add(FileTools.resourceFile("/platforms.dpr").getParentFile().getAbsolutePath());	// config dir
		Classpath classpath = new Classpath(classpathEntries);

		String input = project.saveFile().getAbsolutePath();
		String output = project.projectSourceFile().getAbsolutePath();
//		String log = new File(this.tempDir, "JavaSourceGenerator.log").getAbsolutePath();
		String[] args = new String[] {input, output};

		JavaTools.java(JavaSourceGenerator.class.getName(), classpath.path(), args);
//		JavaSourceGenerator.main(args);

		this.compileAndCheckJavaExport(project);
	}

}
