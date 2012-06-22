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
package org.eclipse.persistence.tools.workbench.ant.typedefs;

import java.util.Stack;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;

public class LoginSpec extends MappingsType {
    private String url;
    private String driverClass;
    private String user;
    private String password;
    
    public LoginSpec() {
        super();
    }
    /**
     * A copy constructor.
     */
    protected LoginSpec( LoginSpec loginSpec) {
        
	    this.setProject( loginSpec.getProject());
	    this.url = loginSpec.url;
	    this.driverClass = loginSpec.driverClass;
	    this.user = loginSpec.user;
	    this.password = loginSpec.password;
    }
	/**
	 * Initializes this new instance.
	 */
	protected void initialize() {
	    this.url = "";
	    this.driverClass = "";
	    this.user = "";
	    this.password = "";
	}
	
    public String getUrl( Project p) {
        if( isReference()) {
            return getRef( p).getUrl( p);
        }
        return this.url;
    }
    
    public void setUrl( String url) throws BuildException {
        if( isReference()) {
            throw tooManyAttributes();
        }
        this.url = url;
    }
	
    public String getDriverClass( Project p) {
        if( isReference()) {
            return getRef( p).getDriverClass( p);
        }
        return this.driverClass;
    }
    
    public void setDriverClass( String driverClass) throws BuildException {
        if( isReference()) {
            throw tooManyAttributes();
        }
        this.driverClass = driverClass;
    }
	
    public String getUser( Project p) {
        if( isReference()) {
            return getRef( p).getUser( p);
        }
        return this.user;
    }
    
    public void setUser( String user) throws BuildException {
        if( isReference()) {
            throw tooManyAttributes();
        }
        this.user = user;
    }
	
    public String getPassword( Project p) {
        if( isReference()) {
            return getRef( p).getPassword( p);
        }
        return this.password;
    }
    
    public void setPassword( String password) throws BuildException {
        if( isReference()) {
            throw tooManyAttributes();
        }
        this.password = password;
    }
    /**
     * Performs the check for circular references and returns the
     * referenced LoginSpec.
     * @param project the current project
     * @return the LoginSpec represented by a referenced LoginSpec.
     */
    protected LoginSpec getRef( Project project) {
        if( !isChecked()) {
            Stack stack = new Stack();
            stack.push(this);
            dieOnCircularReference( stack, project);
        }

        Object anObject = getRefid().getReferencedObject( project);
        if( anObject instanceof LoginSpec) {
            return ( LoginSpec)anObject;
        } 
        else {
            throw new BuildException( this.stringRepository.getString( "notNotALoginSpec", getRefid().getRefId()));
        }
    }
    /**
     * Makes this instance in effect a reference to another LoginSpec instance.
     *
     * <p>You must not set another attribute or nest elements inside
     * this element if you make it a reference.</p>
     * @param r the reference to another LoginSpec.
     * @exception BuildException if an error occurs.
     */
    public void setRefid( Reference r) throws BuildException {
        if ( this.url.length() != 0) {
            throw tooManyAttributes();
        }
        super.setRefid( r);
    }
    
	private static final String CR = System.getProperty("line.separator");
	public void toString( StringBuffer sb) {
		super.toString( sb);
		
        if( !isReference()) {
    		sb.append( "\tdatasource URL = \"").append( this.url).append( "\"").append(CR);
    		if( this.driverClass != "")
    			sb.append( "\tdriver class = \"").append( this.driverClass).append( "\"").append(CR);
       		if( this.user != "")
       		    sb.append( "\tuser name = \"").append( this.user).append( "\"").append(CR);
       		if( this.password != "")
       		    sb.append( "\tuser password = \"").append( this.password).append( "\"").append(CR);
        }
    }
}
