/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
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
