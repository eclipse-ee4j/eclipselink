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
package org.eclipse.persistence.tools.workbench.ant.taskdefs;

import org.eclipse.persistence.sessions.DatabaseLogin;

import org.apache.tools.ant.BuildException;
import org.eclipse.persistence.tools.workbench.ant.SessionValidatorInterface;
import org.eclipse.persistence.tools.workbench.ant.typedefs.LoginSpec;

public class SessionValidateTask extends TopLinkTask implements SessionValidatorInterface {

    private String property;
    private LoginSpec loginSpec;
    
	public SessionValidateTask() {
	    super();
	}
	
	protected void initialize() {
	    super.initialize();
	    
	    this.property = null;
	    this.loginSpec = null;
	}
    /**
     * Adds Sessions LoginSpec.
     */
    public void addLoginSpec( LoginSpec loginSpec) {

        this.loginSpec = loginSpec;
    }
    
    public void execute() throws BuildException {
        super.execute();
	    
        int status = this.execute( this.getSessionName(),  this.getSessionsFile(), 
                								this.getUrl(), this.getDriverClass(), this.getUser(), this.getPassword());

        if( status == 0) {
            if( this.property != null)
                this.getProject().setNewProperty( this.property, "true");
        }
	}
    /**
     * Executes the MappingsRunner.
     * Returns 0 if the there is no problem in the project.
     */
	public int execute( String sessionName, String sessionsFileName, String url, String driverclass, String user, String password) {
	        
		Object[] args = { sessionName, sessionsFileName, url, driverclass, user, password};          

       return this.execute( args);
	}
    
    protected String getProjectRunnerClassName() {
        
        return "org.eclipse.persistence.tools.workbench.ant.SessionValidator";
    }
    /**
     * Allows to override the session login.
     * @return the DatabaseLogin to use for the session
     */
	protected DatabaseLogin buildLogin() {
	    DatabaseLogin login = null;
	    if( this.loginSpec != null) {
	        login = new DatabaseLogin();
	        
		    String url = this.loginSpec.getUrl( this.getProject());
		    String driverclass = this.loginSpec.getDriverClass( this.getProject());
		    String user = this.loginSpec.getUser( this.getProject());
		    String password = this.loginSpec.getPassword( this.getProject());
		    
		    if( url != "") login.setDatabaseURL( url);
		    if( driverclass != "") login.setDriverClassName( driverclass);
		    if( user != "") login.setUserName( user);
		    if( password != "") login.setPassword( password);
	    }
	    return login;
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
    /**
     * Set the name of the property which will be set if the there is no problem in the project.
     *
     * @param property the name of the property to set.
     */
    public void setProperty( String property) {
        this.property = property;
    }
}
