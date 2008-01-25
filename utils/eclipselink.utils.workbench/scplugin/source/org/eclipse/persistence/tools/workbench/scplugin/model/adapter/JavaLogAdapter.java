/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import org.eclipse.persistence.internal.sessions.factories.model.log.JavaLogConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class JavaLogConfig
 * 
 * @see JavaLogConfig
 * 
 * @author Tran Le
 */
public final class JavaLogAdapter extends LogAdapter {

	public static final String DEFAULT_LOGGING_CLASS = "java.util.logging.*";

	/**
	 * Creates a new JavaLogAdapter for the specified model object.
	 */
	JavaLogAdapter( SCAdapter parent, JavaLogConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new JavaLogAdapter.
	 */
	protected JavaLogAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new JavaLogConfig();
	}
	
	public void toString( StringBuffer sb) {
		
		sb.append( "TODO: toString()");
	}
	
	/**
	 * Returns this Config Model Object.
	 */
	private final JavaLogConfig log() {
		
		return ( JavaLogConfig)this.getModel();
	}
}
