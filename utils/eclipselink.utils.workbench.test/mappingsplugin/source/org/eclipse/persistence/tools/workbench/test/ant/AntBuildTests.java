/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.launch.Launcher;
import org.apache.tools.ant.taskdefs.Ant;
import org.eclipse.persistence.tools.workbench.test.utility.JavaTools;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * Launch the Ant build in resource/ant. If this JUnit test is launch inside eclipse
 * it will take the build-eclipse.xml otherwise it will take the standard build.xml
 * Prerequisite: 
 * a MWP project must exist in resource/ant/temp/mw (run ProjectValidatorTests first).
 */
public class AntBuildTests extends TestCase {
	private File resourceDir;
	
	public static void main( String[] args) {
	    
		TestRunner.main(new String[] { "-c", AntBuildTests.class.getName()});
	}

	public static Test suite() {
		return new TestSuite( AntBuildTests.class);
	}

	public AntBuildTests( String name) {
        super( name);
    }

	public void testAntBuild() throws Exception {

		List classpathEntries = new ArrayList();
		classpathEntries.add( this.resourceDir.getAbsolutePath());

		classpathEntries.add( Classpath.locationFor( Ant.class));	// ant.jar
		classpathEntries.add( Classpath.locationFor( Launcher.class));	// ant-launcher.jar

		Classpath classpath = new Classpath(classpathEntries);
		String[] javaOptions = TestTools.buildOracleProxyCommandLineOptions();
		
		File buildXml = new File( this.resourceDir, getBuildXMLName());
		String[] args = new String[] { "-f", buildXml.getAbsolutePath()};

		JavaTools.java( "org.apache.tools.ant.launch.Launcher", classpath.path(), javaOptions, args);
	}
	
	protected void setUp() throws Exception {
		super.setUp();

		this.resourceDir = FileTools.resourceFile( "/ant");
		
		File projectFile = new File( this.resourceDir, "temp/" + ProjectRunnerTests.MW + "/Employee.mwp");
	    if( !projectFile.exists()) {
	        throw new IllegalStateException( projectFile.getAbsolutePath() + " Not Found (run ProjectValidatorTests first)");
	    }
	}
	
	private boolean testIsLauchInsideEclipse() {
	    return ( System.getProperty( "ant.home") == null);
	}
	
	private String getBuildXMLName() {
	    return testIsLauchInsideEclipse() ? "build-eclipse.xml" : "build.xml";
	}
	
}
