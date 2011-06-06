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
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.eclipse.persistence.tools.workbench.ant.ProjectValidatorInterface;

/**
 * An Ant task to test and list all the problems in a Workbench project (.mwp).
 * This task provides the ability to log all the problems to a file in text format or html format.
 */
public class MappingsValidateTask extends MappingsTask implements ProjectValidatorInterface {
    
	private String reportfile;
	private String reportformat;

    public static final String TXT = "txt";
    public static final String HTML = "html";

	public MappingsValidateTask() {
	    super();
	}
	
	protected void initialize() {
	    super.initialize();
	    
	    this.reportfile = "";
	    this.reportformat = TXT;
	}
	
    public void execute() throws BuildException {
        super.execute();

        int status = this.execute(  this.getProjectFile(), this.getReportFile(), this.getReportFormat(), getIgnoreErrorCodes());

        if( status == 0) {
            if( getProperty() != null)
                this.getProject().setNewProperty( getProperty(), "true");
        }
	}
    /**
     * Executes the MappingsRunner.
     * Returns 0 if the there is no problem in the project.
     */
	public int execute( String projectFile, String reportfile, String reportformat, Vector ignoreErrorCodes) {
	        
		Object[] args = { projectFile, reportfile, reportformat, ignoreErrorCodes};          

       return this.execute( args);
	}
    
    protected String getProjectRunnerClassName() {
        
        return "org.eclipse.persistence.tools.workbench.ant.ProjectValidator";
    }
    
    protected String getReportFile() {
        return this.reportfile;
    }
 
    public void setReportFile( String reportfile) {
        this.reportfile = reportfile;
	}    

    protected String getReportFormat() {
        return this.reportformat;
    }
 
    public void setReportFormat( Format format) {
        this.reportformat = format.getValue();
	}
    /**
     * Enumerated type for report format attribute
     *
     * @see EnumeratedAttribute
     */
    public static class Format extends EnumeratedAttribute {
        /**
         * get the values
         * @return an array of the allowed values for this attribute.
         */
        public String[] getValues() {
            return new String[] { TXT, HTML};
        }
    }
}
