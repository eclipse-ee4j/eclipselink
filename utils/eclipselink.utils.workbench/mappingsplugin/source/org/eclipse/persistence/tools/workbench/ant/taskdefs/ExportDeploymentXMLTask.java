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
package org.eclipse.persistence.tools.workbench.ant.taskdefs;

import java.util.Vector;


import org.apache.tools.ant.BuildException;
import org.eclipse.persistence.tools.workbench.ant.ProjectExplorerInterface;
import org.eclipse.persistence.tools.workbench.ant.typedefs.LoginSpec;

/**
 * An Ant task to export TopLink deployment descriptor XML or the ejb-jar.xml 
 * depending on the specified Workbench project (.mwp). Validation of the specified 
 * Workbench project will be performed before the export operation. A BuildException 
 * will be thrown when validation failed. 
 */
public class ExportDeploymentXMLTask extends MappingsTask implements ProjectExplorerInterface {
	
	private String deploymentFile;
	private String ejbJarXMLDir;
    private LoginSpec loginSpec;
    
	public ExportDeploymentXMLTask() {
	    super();
	}
	
	protected void initialize() {
	    super.initialize();
	    
	    this.deploymentFile = "";
	    this.ejbJarXMLDir = "";
	    this.loginSpec = null;
	}
    /**
     * Adds Sessions LoginSpec.
     */
    public void addLoginSpec( LoginSpec loginSpec) {

        this.loginSpec = loginSpec;
    }
    
    protected String getDeploymentFile() {
        return this.deploymentFile;
    }
 
    public void setDeploymentFile( String deploymentFile) {
        this.deploymentFile = deploymentFile;
	}    

    protected String getEjbJarXMLDir() {
        return this.ejbJarXMLDir;
    }
 
    public void setEjbJarXMLDir( String ejbJarXMLDir) {
        this.ejbJarXMLDir = ejbJarXMLDir;
	}    
    
    protected boolean getFailOnError() {
        return this.failonerror;
    }
    /**
     * If false, note errors to the output but keep going.
     * @param failonerror true or false
     */
    public void setFailOnError( boolean failonerror) {
        this.failonerror = failonerror;
    }
    
    public void execute() throws BuildException {
        super.execute();
        
	    String url = this.getUrl();
	    String driverclass = this.getDriverClass();
	    String user = this.getUser();
	    String password = this.getPassword();
	    
        int status = this.execute( this.getProjectFile(), this.getDeploymentFile(), this.getEjbJarXMLDir(), getIgnoreErrorCodes(), new Boolean( failonerror), url, driverclass, user, password);
        
        if( status == 0) {
            if( getProperty() != null)
                this.getProject().setNewProperty( getProperty(), "true");
        }
	}
    /**
     * Generates TopLink deployment descriptor XML or the ejb-jar.xml 
     * depending on the specified Workbench project.
     * Returns 0 if the generation is successful.
     */
	public int execute( String projectFile, String deploymentFile, String ejbJarXMLDir, Vector ignoreErrorCodes, Boolean failOnError, String url, String driverclass, String user, String password) {
	    
		Object[] args = { projectFile, deploymentFile, ejbJarXMLDir, ignoreErrorCodes, failOnError, url, driverclass, user, password};          
		
		return this.execute( args);
	}
    
    protected String getProjectRunnerClassName() {
        
        return "org.eclipse.persistence.tools.workbench.ant.ProjectExporter";
    }
	
	protected void preExecute() throws BuildException {
	    super.preExecute();
	    
        if( this.loginSpec != null) {
            if( this.loginSpec.getUrl( this.getProject()).length() == 0) {
                throw new BuildException( this.stringRepository.getString( "notDefined", "LoginSpec Datasource URL"));
            }
        }
	}
	
	protected String getUrl() {
	    return ( this.loginSpec== null) ? "" : this.loginSpec.getUrl( this.getProject());
	}
	
	protected String getDriverClass() {
	    return ( this.loginSpec == null) ? "" : this.loginSpec.getDriverClass( this.getProject());
	}	
	
	protected String getUser() {
	    return ( this.loginSpec == null) ? "" : this.loginSpec.getUser( this.getProject());
	}	
	
	protected String getPassword() {
	    return ( this.loginSpec == null) ? "" : this.loginSpec.getPassword( this.getProject());
	}

}
