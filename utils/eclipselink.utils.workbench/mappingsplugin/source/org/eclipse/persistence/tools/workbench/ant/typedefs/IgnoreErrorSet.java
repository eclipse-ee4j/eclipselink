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
package org.eclipse.persistence.tools.workbench.ant.typedefs;

import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;

public class IgnoreErrorSet extends MappingsType implements Cloneable {

    private Vector ignoreErrors;

    /**
     * The default constructor.
     */
    public IgnoreErrorSet() {
        super();
    }
    /**
     * A copy constructor.
     *
     * @param ignoreErrorSet a <code>IgnoreErrorSet</code> value
     */
    protected IgnoreErrorSet( IgnoreErrorSet ignoreErrorSet) {

        this.ignoreErrors = ignoreErrorSet.ignoreErrors;
        this.setProject( ignoreErrorSet.getProject());
    }

    protected void initialize() {
        super.initialize();
        
        this.ignoreErrors = new Vector();
    }
    /**
     * Add a nested &lt;IgnoreError&gt; nested element.
     *
     * @param ignoreError a error to ignore.
     */
    public void addIgnoreError( IgnoreError ignoreError) {
        if( isReference()) {
            throw tooManyAttributes();
        }
        this.ignoreErrors.add( ignoreError);
    }
    
    public boolean contains( IgnoreError ignoreError) {
        String code = ignoreError.getCode();
        
        for( Iterator i = this.ignoreErrors.iterator(); i.hasNext(); ) {
            IgnoreError error = ( IgnoreError)i.next();
            if( code.equals( error.getCode())) 
                return true;            
        }
        return false;
    } 
    
    public Vector getIgnoreErrors( Project project) {
        if( isReference()) {
            return getRef( project).getIgnoreErrors( project);
        }
        Vector ignoreErrors = new Vector( this.ignoreErrors.size());
        ignoreErrors.addAll( this.ignoreErrors);
        return ignoreErrors;
    }
    
    
    public Vector getIgnoreErrorCodes( Project project) {
        if( isReference()) {
            return getRef( project).getIgnoreErrorCodes( project);
        }
        Vector codes = new Vector();
        for( Iterator i = this.ignoreErrors.iterator(); i.hasNext(); ) {
            IgnoreError ignoreError = ( IgnoreError)i.next();
            codes.add( ignoreError.getCode());
        }
        return codes;
    }
    /**
     * Performs the check for circular references and returns the
     * referenced IgnoreErrorSet.
     * @param project the current project
     * @return the IgnoreErrorSet represented by a referenced IgnoreErrorSet.
     */
    protected IgnoreErrorSet getRef( Project project) {
        if( !isChecked()) {
            Stack stack = new Stack();
            stack.push(this);
            dieOnCircularReference( stack, project);
        }

        Object anObject = getRefid().getReferencedObject( project);
        if( anObject instanceof IgnoreErrorSet) {
            return ( IgnoreErrorSet)anObject;
        } 
        else {
            throw new BuildException( this.stringRepository.getString( "notNotAIgnoreErrorSet", getRefid().getRefId()));
        }
    }
    /**
     * Makes this instance in effect a reference to another IgnoreErrorSet instance.
     *
     * <p>You must not set another attribute or nest elements inside
     * this element if you make it a reference.</p>
     * @param r the reference to another IgnoreErrorSet.
     * @exception BuildException if an error occurs.
     */
    public void setRefid( Reference r) throws BuildException {
        if ( this.ignoreErrors.size() != 0) {
            throw tooManyAttributes();
        }
        super.setRefid( r);
    }
	
	private static final String CR = System.getProperty("line.separator");
	public void toString( StringBuffer sb) {
		super.toString( sb);
		
		sb.append(" [").append(CR);
		for( Iterator i = this.ignoreErrors.iterator(); i.hasNext(); ) {
		    IgnoreError ignoreError = ( IgnoreError)i.next();
			sb.append( "\t\t");
			ignoreError.toString( sb);
			if( i.hasNext()) {
				sb.append( ",").append(CR);
			}
		}
		sb.append(" ] ");
	}
}
