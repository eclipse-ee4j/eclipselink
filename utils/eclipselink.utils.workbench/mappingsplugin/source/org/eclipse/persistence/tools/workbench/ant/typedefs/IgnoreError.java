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
package org.eclipse.persistence.tools.workbench.ant.typedefs;

import org.apache.tools.ant.BuildException;

/**
 * Defines TopLink Workbench project error to ignore.
 */
public class IgnoreError extends MappingsType {
    private String code;
    
    public IgnoreError() {
        super();
    }
    /**
     * A copy constructor.
     */
    protected IgnoreError( IgnoreError ignoreError) {
        this.code = ignoreError.code;
        setProject( ignoreError.getProject());
    }
    
    public IgnoreError( String code) {
        this.setCode( code);
    }

    public String getCode() {
        return this.code;
    }
    /**
     * Set the error ignore.
     */
    public void setCode( String code) throws BuildException {
        if( isReference()) {
            throw tooManyAttributes();
        }
        this.code = code;
    }
    
	public void toString( StringBuffer sb) {
		super.toString( sb);
		
		sb.append( this.code);
    }

}
