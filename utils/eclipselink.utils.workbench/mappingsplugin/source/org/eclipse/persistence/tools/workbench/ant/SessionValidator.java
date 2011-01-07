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

import java.io.PrintStream;

import org.apache.tools.ant.BuildException;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;

public class SessionValidator extends ProjectRunner implements SessionValidatorInterface {

	private DatabaseSession databaseSession;

    public SessionValidator() {
		super();
	}    
    
	public SessionValidator( PrintStream log) {
		super( log);
	}	
	/**
	 * Test TopLink deployment descriptor XML by running TopLink
	 * 
	 * @return 0 if the there is no error in the project.
	 */
	public int execute( String sessionName, String sessionsFileName, String url, String driverclass, String user, String password) {
		int status = 0;
        try {
            this.login( sessionName, sessionsFileName, url, driverclass, user, password);
        } 
        catch ( DatabaseException e) {
		    String msg = ( e.getMessage() == null) ? e.toString() : e.getMessage();

			throw new BuildException( this.stringRepository.getString( "couldNotLogin", msg), e);
        }
		catch( Throwable e) {
		    Throwable t = ( e.getCause() == null) ? e : e.getCause();
		    String msg = ( t.getMessage() == null) ? t.toString() : t.getMessage();

			throw new BuildException( this.stringRepository.getString( "errorWhileValidating", msg), e);
		}
        finally {
            this.logout();
        }
		return status;
	}
	
    private void login( String sessionName, String sessionsFileName, String url, String driverclass, String user, String password) {
        
        this.databaseSession = ( DatabaseSession)SessionManager.getManager().
        		getSession(
                new XMLSessionConfigLoader( sessionsFileName), 
                sessionName, PrivilegedAccessHelper.getClassLoaderForClass( getClass()), false, false, false);
        
        DatabaseLogin sessionLogin = this.databaseSession.getLogin();
        if( url != "") {
            if( url != "") sessionLogin.setDatabaseURL( url);
            if( driverclass != "") sessionLogin.setDriverClassName( driverclass);
            if( user != "") sessionLogin.setUserName( user);
            if( password != "") sessionLogin.setPassword( password);
        }   
        this.databaseSession.login( sessionLogin);
    }

    private void logout() {
        if( this.databaseSession != null) {
            this.databaseSession.logout();
        }
    }
}
