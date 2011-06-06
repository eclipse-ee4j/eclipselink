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
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;
import java.util.prefs.Preferences;


import org.apache.tools.ant.BuildException;
import org.eclipse.persistence.tools.workbench.mappingsio.ProjectIOManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

public class ProjectValidator extends ProjectRunner implements ProjectValidatorInterface {
	
    public ProjectValidator() {
		super();
	}
    
	public ProjectValidator( PrintStream log) {
		super( log);
	}	
	
	/**
	 * Validates a Workbench project.
	 * 
	 * @param input fully qualified project filename (.mwp or .xml)
	 * @param output fully qualified report filename
	 * @return 0 if the there is no problem in the project.
	 */
	public int execute( String projectFileName, String reportfile, String reportformat, Vector ignoreErrorCodes) {
		int status = 0;
		File projectFile = new File( projectFileName);

		try {
			MWProject project = new ProjectIOManager().read( projectFile, Preferences.userNodeForPackage( this.getClass()));

			project.validateBranch();
			 
		    Collection problems = CollectionTools.collection( project.branchProblems());
		    
		    Collection igoredProblems = this.getIgnoredProblems( problems, ignoreErrorCodes);
		    
		    CollectionTools.removeAll( problems, igoredProblems.iterator());
		    
		    this.generateReport( reportfile, reportformat, project, problems, igoredProblems, ignoreErrorCodes);

			status = problems.size();
		} 
		catch( Throwable e) {
		    Throwable t = ( e.getCause() == null) ? e : e.getCause();
		    String msg = ( t.getMessage() == null) ? t.toString() : t.getMessage();

			throw new BuildException( this.stringRepository.getString( "errorWhileValidating", msg), e);
		}
		return status;
	}

	protected void generateReport( String reportfileName, String reportformat, MWProject project, Collection problems, Collection igoredProblems, Collection ignoreErrorCodes) throws FileNotFoundException {

        StringBuffer sb = this.problemsSummary( project, project.branchProblemsSize());

        sb.append( this.problemsReport( problems)).append( '\n');
		
        sb.append( this.igoredProblemsSummary( igoredProblems, ignoreErrorCodes));     
        sb.append( this.problemsReport( igoredProblems)).append( '\n');
        
        log( sb);

		if( reportfileName.length() > 0) {
		    log( this.stringRepository.getString( "generatingReport", reportfileName) + "\n");
		    String title = project.displayString() + " - " + DateFormat.getDateInstance().format( new Date());
		    this.buildReportFile( reportfileName, reportformat, sb, title);
		}
	}
}
