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


import org.apache.tools.ant.types.DataType;
import org.eclipse.persistence.tools.workbench.ant.AntExtensionBundle;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultStringRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.StringRepository;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

public abstract class MappingsType extends DataType {
    
    protected StringRepository stringRepository;
    
	/**
	 * Default constructor.
	 */
	protected MappingsType() {
		super();
		this.initialize();
	}
	/**
	 * Initializes this new instance.
	 */
	protected void initialize() {
		this.stringRepository = new DefaultStringRepository( AntExtensionBundle.class);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		StringTools.buildSimpleToStringOn( this, sb);
		sb.append( " (");
		this.toString(sb);
		sb.append( ')');
		return sb.toString();
	}

	public void toString( StringBuffer sb) {
		// subclasses should override this to do something a bit more helpful
	    
        if( isReference()) {
    		sb.append( "refid=\"").append( this.getRefid().getRefId()).append( "\"");		
        }
	}
}
