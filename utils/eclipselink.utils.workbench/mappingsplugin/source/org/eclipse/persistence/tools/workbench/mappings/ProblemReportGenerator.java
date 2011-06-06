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
package org.eclipse.persistence.tools.workbench.mappings;

import java.io.File;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.resources.DefaultStringRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.StringRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ProblemsBundle;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;



/**
 * This class generates the list of problems list an existing MW project.
 * 
 * usage:
 *     java ProblemReportGenerator inputFile outputFile [logFile]
 */
public class ProblemReportGenerator implements Generator.Adapter 
{
	private StringRepository mappingsStringRepository;
	private StringRepository problemsStringRepository;
	
	public static void main( String args[]) {

		new Generator( new ProblemReportGenerator()).execute( args);
	    System.out.println();
	}
	
	public ProblemReportGenerator() {
		super();
		this.mappingsStringRepository = new DefaultStringRepository( MappingsBundle.class);
		this.problemsStringRepository = new DefaultStringRepository( ProblemsBundle.class);
	}
	
	/**
	 * @see Generator.Adapter#export(org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject, java.io.File)
	 */
    public void export( MWProject project, File outputFile) {

		System.out.println( this.mappingsStringRepository.getString( 
		        "projectsProblems", project.displayString(), String.valueOf( project.branchProblemsSize())));

		for( Iterator i = project.branchProblems(); i.hasNext(); ) {
		    Problem problem = ( Problem)i.next();
		    String source = problem.getSource().displayString();
		    String key = problem.getMessageKey();
		    String problemMsg = this.problemsStringRepository.getString( key, problem.getMessageArguments());
		    
		    System.err.println( source + " - " + key + ":" + problemMsg); 
		}
	}
}
