/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsio.legacy;

import java.io.File;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.tools.workbench.mappingsio.ProjectIOManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.NullPreferences;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffEngine;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public class BackwardCompatibilityCustomerTests
	extends BackwardCompatibilityTestCase
{
	private ProjectIOManager ioMgr;


	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", BackwardCompatibilityCustomerTests.class.getName()});

	//  to run a single test, comment out the line of code above
	//  and uncomment the following line of code:
//	  System.out.println(TestTools.execute(new BackwardCompatibilityCustomerTests("testAmbassadorProject")));

	//  current tests:
	//	  testAmbassadorProject
	//	  testTavantDevProject
	//	  testTisCoverProject
	}

	public static Test suite() {
		TestTools.setUpJUnitThreadContextClassLoader();
		return new TestSuite(BackwardCompatibilityCustomerTests.class);
	}

	public BackwardCompatibilityCustomerTests(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		this.ioMgr = new ProjectIOManager();
	}

	public void testAmbassadorProject() throws Exception {
		this.verifyReadWrite("AmbassadorProject", "9.0.4");
	}

	public void testTavantDevProject() throws Exception {
		this.verifyReadWrite("tavantDev", "9.0.4");
	} 

	public void testTisCoverProject() throws Exception {
		this.verifyReadWrite("TisCover", "9.0.4");
	}

//    public void testPsr01Project() throws Exception {
//		this.verifyReadWrite("psr01", "4.5");
//    } 
//    
//    public void testMarketSoftProject() throws Exception {
//		this.verifyReadWrite("MarketSoft", "4.0");
//    } 
//
	private void verifyReadWrite(String projectName, String version) throws Exception {
		this.verifyReadWrite(this.readOldProjectNamed(projectName, version));
	}

	/**
	 * rename and read the corresponding legacy project
	 */
	private MWProject readOldProjectNamed(String projectName, String version) throws Exception {
		// this is the *original* (un-renamed) legacy project
		File projectDirectory = FileTools.resourceFile("/backwards-compatibility/customer/" + version + "/" + projectName);

		// run the package renamer on it
		projectDirectory = this.renameDirectoryTree(projectDirectory, version);

		// read the resulting (renamed) project and return it
		return this.ioMgr.read(new File(projectDirectory, projectName + ".mwp"), NullPreferences.instance());
	}

	private void verifyReadWrite(MWProject oldProject) throws Exception {
		oldProject.setSaveDirectory(FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName()));

		this.ioMgr.write(oldProject);
		MWProject project2 = this.ioMgr.read(oldProject.saveFile(), NullPreferences.instance());

		//diffEngine.setLog(this.buildDiffEngineLog());
		Diff diff = this.diff(oldProject, project2);

		// sometimes we dump the branch size so we can compare it to the
		// number of objects compared by the diff engine
		// System.out.println("project size: " + project1.branchSize());

		assertTrue(diff.getDescription(), diff.identical());

		Project originalProject = project2.buildRuntimeProject();

		File deploymentXmlFile = tempFile(originalProject);

		XMLProjectWriter.write(deploymentXmlFile.getAbsolutePath(), originalProject);
	}

	private File tempFile(Project project) {
		return this.tempFile(project.getName());
	}

	private File tempFile(String projectName) {
		return new File(FileTools.temporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName()), projectName + ".xml");
	}

}
