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
package org.eclipse.persistence.tools.workbench.ant;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.ant.taskdefs.MappingsValidateTask;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultStringRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.StringRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ProblemsBundle;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;



/**
 * Base class for MW project runner inside an Ant task.
 */
public abstract class ProjectRunner {
    protected StringRepository stringRepository;
    protected StringRepository problemsStringRepository;
	private PrintStream log;
    public static String CR = System.getProperty( "line.separator");
	
	public ProjectRunner() {
		super();
		initialize( System.out);
	}
	
	public ProjectRunner( PrintStream log) {
		super();
		initialize( log);
	}
	
	protected void initialize( PrintStream log) {

	    this.log = log;
		this.stringRepository = new DefaultStringRepository( AntExtensionBundle.class);
		this.problemsStringRepository = new DefaultStringRepository( ProblemsBundle.class);
	}
	
    protected StringBuffer problemsReport( Collection problems) {
        StringBuffer sb = new StringBuffer( 256);
        
		for( Iterator i = problems.iterator(); i.hasNext(); ) {
		    Problem problem = ( Problem)i.next();
		    String source = problem.getSource().displayString();
		    String key = problem.getMessageKey();

		    String problemMsg = this.problemsStringRepository.getString( key, problem.getMessageArguments());
		    sb.append( '\n').append( key).append( " - ").append( source).append( " - ").append( problemMsg); 
		}
		return sb;
	}
    
    protected StringBuffer igoredProblemsReport( int numberProblemsIgnored, Collection ignoreErrorCodes) {
        StringBuffer sb = new StringBuffer( 256);
        
        sb.append( this.stringRepository.getString( "ignoringProblems", String.valueOf( numberProblemsIgnored)));
                
        Iterator i = ignoreErrorCodes.iterator();
        sb.append(( String)i.next());
        while( i.hasNext()) {
            sb.append( ", ").append(( String)i.next());
        }
        return sb.append( ")");
    }
    /**
     * Returns the actual problems that will be ignored, from a list of ignoreErrorCodes.
     * Go through the actual project's problems and list the ones that will be ignored.
     * 
     * @param problems project's problems
     * @param ignoreErrorCodes list of error codes to ignore
     * @return the actual problems that will be ignored
     */
    protected Vector getIgnoredProblems( Collection problems, Collection ignoreErrorCodes) {
    
        Vector igoredProblems = new Vector();
        if( ignoreErrorCodes.size() == 0) 
            return igoredProblems;
        
        for( Iterator i = problems.iterator(); i.hasNext(); ) {
            Problem problem = ( Problem)i.next();
            String code = problem.getMessageKey();
    
            if( ignoreErrorCodes.contains( code))
                igoredProblems.add( problem);
        }
        return igoredProblems;
    }
   
    protected StringBuffer problemsSummary( MWProject project, int numberOfProblems) {
        StringBuffer sb = new StringBuffer( 80);
        
        return sb.append( this.stringRepository.getString( "numberOfProblems", project.displayString(), String.valueOf( numberOfProblems)));
    }
    
    protected StringBuffer igoredProblemsSummary( Collection igoredProblems, Collection ignoreErrorCodes) {
        // Display warning when problems are ignored
	    if( igoredProblems.size() > 0) {
            if ( ignoreErrorCodes.size() > 0) {
                return igoredProblemsReport( igoredProblems.size(), ignoreErrorCodes);
            }
	    }
	    return new StringBuffer();
    }

    protected void log( StringBuffer stringBuffer) {
        this.log( stringBuffer.toString());
    }
    
    protected void log( String message) {
        this.log.print( message.replaceAll( "\n", CR));
    }
    /**
     * Takes a StringBuffer and builds a project report file in the given format (txt or html)
     */
    protected void buildReportFile( String reportfileName, String reportformat, StringBuffer sb, String title) throws FileNotFoundException {
    	PrintStream ps = null;
    	
    	ps = new PrintStream( new FileOutputStream( reportfileName));
    	        
        if( reportformat.equals( MappingsValidateTask.HTML))
            sb = buildHtmlReport( sb, title);
        else
            sb.insert( 0, title + "\n\n");
        
    	ps.print( sb.toString().replaceAll( "\n", CR));
    
    	ps.close();    
    }
    /**
     * Builds a project report file (html) from the given StringBuffer.
     */
    protected StringBuffer buildHtmlReport( StringBuffer stringBuffer, String title) {
        
        String report = stringBuffer.toString().replaceAll( "\n", "<br>\n");
        
        StringBuffer sb = new  StringBuffer( 2048);
    
        sb.append( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 TRANSITIONAL//EN\">\n").
        	append( "<HTML><HEAD><TITLE>").append( title).append( "</TITLE>\n").
        	append( "<META HTTP-EQUIV=CONTENT-TYPE CONTENT=\"TEXT/HTML; CHARSET=ISO-8859-1\">\n").
        	append( "<STYLE>\n").
        	append( "BODY { FONT-FAMILY: ARIAL; FONT-SIZE: 10PT; }\n"). 
        	append( "</STYLE></HEAD><BODY><BLOCKQUOTE><DIV>\n");
    
        sb.append( "<BIG><B>").
        	append( title).
        	append( "</B></BIG><P>\n").
        	append( report).
        	append( "</DIV></BLOCKQUOTE></BODY></HTML>\n");
    
        return sb;
    }
}
