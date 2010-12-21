/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.io.File;

import org.apache.tools.ant.BuildException;

/**
 * Base Ant task for TopLink projects.
 */
public abstract class TopLinkTask extends ProjectTask {

    	private String sessionsFile;
    	private String sessionName;
    	
        public static final String SESSIONS_XML = "sessions.xml";

    	protected TopLinkTask() {
    	    super();
    	}
    		
    	protected void initialize() {
    	    super.initialize();

    	    this.sessionsFile = "";
    	    this.sessionName = "";
    	}

        protected String getSessionsFile() {
            return this.sessionsFile;
        }
     
        public void setSessionsFile( String sessionsFile) {
            this.sessionsFile = sessionsFile;
    	}    

        protected String getSessionName() {
            return this.sessionName;
        }
     
        public void setSessionName( String sessionName) {
            this.sessionName = sessionName;
    	}    
        
    	protected void preExecute() throws BuildException {
    	    super.preExecute();

            if( this.sessionsFile.length() == 0) {
                this.setSessionsFile( SESSIONS_XML);
            }
           else {
       			File sessions = new File( this.sessionsFile);
       			String path = sessions.getParent();
       			String fileName = sessions.getName();
       			
                this.setSessionsFile( fileName);
                if( path != null)
                    this.setUserClasspath( path);
            }
            if( this.sessionName.length() == 0) {

                throw new BuildException( this.stringRepository.getString( "notDefined", "SessionName"));
            }
    	}
}
