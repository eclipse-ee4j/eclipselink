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
package org.eclipse.persistence.tools.workbench.ant;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.prefs.Preferences;
import org.apache.tools.ant.BuildException;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultStringRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.StringRepository;
import org.eclipse.persistence.tools.workbench.mappingsio.ProjectIOManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWError;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.UiProjectBundle;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

/**
 * Export TopLink deployment descriptor XML and the ejb-jar.xml depending 
 * on the specified Workbench project
 */
public class ProjectExporter extends ProjectRunner implements ProjectExplorerInterface {
	private StringRepository uiProjectStringRepository;
	  
	public ProjectExporter() {
		super();
	}  
	
	public ProjectExporter( PrintStream log) {
		super( log);
	}
	
	protected void initialize( PrintStream log) {
	    super.initialize( log);

		this.uiProjectStringRepository = new DefaultStringRepository( UiProjectBundle.class);
	}
	/**
	 * Generate TopLink deployment descriptor XML or the ejb-jar.xml depending 
	 * on the specified Workbench project
	 * 
	 * @param projectFileName fully qualified project filename (.mwp or .xml)
	 * @param deploymentFileName fully qualified name for the deployment file.
	 */
	public int execute( String projectFileName, String deploymentFileName, String ejbJarXMLDir, Vector ignoreErrorCodes, Boolean failOnErrorObject, String url, String driverclass, String user, String password) {
		boolean failOnError = failOnErrorObject.booleanValue();
		File projectFile = new File( projectFileName);
		File deploymentFile = new File( deploymentFileName);

		try {
			MWProject project = new ProjectIOManager().read( projectFile, Preferences.userNodeForPackage( this.getClass()));
			project.validateBranch();
			if( project.hasBranchProblems()) {
			    Collection problems = CollectionTools.collection( project.branchProblems());
			    
			    Collection igoredProblems = this.getIgnoredProblems( problems, ignoreErrorCodes);
			    
				log( this.igoredProblemsSummary( igoredProblems, ignoreErrorCodes).append( '\n'));
				
			    if( failOnError && ( problems.size() - igoredProblems.size()) > 0) {

				        throw new BuildException( this.problemsSummary( project, project.branchProblemsSize()).toString());
			    }
			}
			this.export( project, deploymentFile, ejbJarXMLDir, failOnError, url, driverclass, user, password);
		}
		catch( Throwable e) {
		    Throwable t = ( e.getCause() == null) ? e : e.getCause();
		    String msg = ( t.getMessage() == null) ? t.toString() : t.getMessage();

			throw new BuildException( this.stringRepository.getString( "errorWhileExporting", msg), e);
		}
		return 0;
	}
    /**
	 * Generate TopLink deployment descriptor XML or the ejb-jar.xml depending the type of project.
	 */
    protected void export( MWProject project, File deploymentFile, String ejbJarXMLDir, boolean failOnError, String url, String driverclass, String user, String password) {
    	
		if( project instanceof MWOXProject) {
			
		    this.exportOXProject( project, deploymentFile);		    
		}
		else if( project instanceof MWEisProject) {
			
		    this.exportEisProject( project, deploymentFile, url, driverclass, user, password);		    
		}
		else {
			this.exportRelationalProject( project, deploymentFile, url, driverclass, user, password);		    
		}
	}
	/**
	 * Generate TopLink deployment descriptor XML for relational project.
	 */
    private void exportRelationalProject( MWProject project, File deploymentFile, String url, String driverclass, String user, String password) {

		if( deploymentFile.getPath() != "") {
		    project.setDeploymentXMLFileName( deploymentFile.getPath());
		}
		if( project.getDeploymentXMLFileName() == "") {
		    throw new RuntimeException( this.stringRepository.getString( "notDefined", "DeploymentFileName"));
		}
        MWLoginSpec loginSpec = project.getDatabase().getDeploymentLoginSpec();
        if( url != "") {
            if( url != "") loginSpec.setURL( url);
            if( driverclass != "") loginSpec.setDriverClassName( driverclass);
            if( user != "") loginSpec.setUserName( user);
            if( password != "") loginSpec.setPassword( password);
        }   
		log( this.stringRepository.getString( "exportingXml", project.getDeploymentXMLFileName()) + "\n");
		project.exportDeploymentXML();
    }
	/**
	 * Generate TopLink deployment descriptor XML.for MWEisProject and MWOXProject
	 */
    private void exportOXProject( MWProject project, File deploymentFile) {

		if( deploymentFile.getPath() != "") {
		    project.setDeploymentXMLFileName( deploymentFile.getPath());
		}
		if( project.getDeploymentXMLFileName() == "") {
		    throw new RuntimeException( this.stringRepository.getString( "notDefined", "DeploymentFileName"));
		}
           
		log( this.stringRepository.getString( "exportingXml", project.getDeploymentXMLFileName()) + "\n");
		project.exportDeploymentXML();
    }
	/**
	 * Generate TopLink deployment descriptor XML for EIS project.
	 * 
     * @param eisPlatform - can be Oracle AQ, Attunity Connect, IBM MQSeries
     */
    private void exportEisProject( MWProject project, File deploymentFile, String url, String eisPlatform, String user, String password) {

		if( deploymentFile.getPath() != "") {
		    project.setDeploymentXMLFileName( deploymentFile.getPath());
		}
		if( project.getDeploymentXMLFileName() == "") {
		    throw new RuntimeException( this.stringRepository.getString( "notDefined", "DeploymentFileName"));
		}
		MWEisLoginSpec loginSpec = (( MWEisProject)project).getEisLoginSpec();
        if( url != "") {
            if( url != "") loginSpec.setConnectionFactoryURL( url);
            if( eisPlatform != "") loginSpec.setJ2CAdapterName( eisPlatform); 
            if( user != "") loginSpec.setUserName( user);
            if( password != "") loginSpec.setPassword( password);
        }   
		log( this.stringRepository.getString( "exportingXml", project.getDeploymentXMLFileName()) + "\n");
		project.exportDeploymentXML();
    }
}
