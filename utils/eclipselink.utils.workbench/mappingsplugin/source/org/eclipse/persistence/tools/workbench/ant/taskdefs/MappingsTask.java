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

import java.util.Iterator;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.eclipse.persistence.tools.workbench.ant.typedefs.IgnoreError;
import org.eclipse.persistence.tools.workbench.ant.typedefs.IgnoreErrorSet;


/**
 * Base Ant task for TopLink Workbench projects.
 */
public abstract class MappingsTask extends ProjectTask {
    
	private String projectFile;
    private Vector ignoreErrorSets;
    private String property;
    
    protected boolean failonerror;
    
	protected MappingsTask() {
	    super();
	}
		
	protected void initialize() {
	    super.initialize();
	    
	    this.projectFile = "";
	    this.ignoreErrorSets = new Vector();
	    this.failonerror = true;
	    this.property = null;
	}

    protected String getProjectFile() {
        return this.projectFile;
    }
 
    public void setProjectFile( String projectFile) {
        this.projectFile = projectFile;
	}    
    
	protected void preExecute() throws BuildException {
	    super.preExecute();
	    
        if( this.projectFile.length() == 0) {
            throw new BuildException( this.stringRepository.getString( "notDefined", "ProjectFile"));
        }
	}
    /**
     * Adds TopLink Workbench project error to ignore.
     */
    public void addIgnoreError( IgnoreError ignoreError) {

        IgnoreErrorSet ignoreErrorSet = new IgnoreErrorSet();
        ignoreErrorSet.addIgnoreError( ignoreError);
        ignoreErrorSets.add( ignoreErrorSet);
    }
    /**
     * Adds a set of error to ignore.
     */
    public void addIgnoreErrorSet( IgnoreErrorSet aSet) {
        ignoreErrorSets.add( aSet);
    }
    /**
     * Returns a collection of error codes to ignore.
     */
    protected Vector getIgnoreErrorCodes() {

        Vector ignoreErrors = new Vector();
        for( Iterator i = this.ignoreErrorSets.iterator(); i.hasNext(); ) {
            IgnoreErrorSet ignoreErrorSet = ( IgnoreErrorSet)i.next();
            
            Vector ignoreErrorCodes = ignoreErrorSet.getIgnoreErrorCodes( getProject());

            ignoreErrors.addAll( ignoreErrorCodes);
        }
        return ignoreErrors;
    }
    
    protected String getProperty() {
        return this.property;
    }
    /**
     * Set the name of the property which will be set if the task completed successfuly.
     *
     * @param property the name of the property to set.
     */
    public void setProperty( String property) {
        this.property = property;
    }
}
