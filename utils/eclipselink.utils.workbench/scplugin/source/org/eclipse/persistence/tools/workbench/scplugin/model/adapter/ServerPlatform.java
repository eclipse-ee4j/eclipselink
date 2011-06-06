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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import java.lang.reflect.Constructor;

import org.eclipse.persistence.tools.workbench.scplugin.model.ServerPlatformManager;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * Simple class for describing a Server platform.
 * It has knowledge of its runtime, config and adapter class.
 * It also serves as a factory for building its associated adapter.
 * 
 * @author Tran Le
 */
public class ServerPlatform {

	private String platformId;
	
	/**
	 * Constructor for a ServerPlatform.
	 */
	public ServerPlatform( String shortClassName) {
		super();
		
		initialize( shortClassName);
	}

	private void initialize( String shortClassName) {	    
	    this.platformId = ServerPlatformManager.instance().getIdFor( shortClassName);
	}
	/**
	 * Returns this server platform runtime class name.
	 */
	public String getRuntimeClassName() {	    
	    return this.getRuntimePlatformClassNameFor( this.platformId);
	}
	/**
	 * Returns this server platform config class name.
	 */
	public String getConfigClassName() {	    
	    return ClassTools.shortNameForClassNamed( ServerPlatformManager.instance().getRuntimePlatformConfigClassNameForPlatformId(this.platformId));
	}
	/**
	 * Returns this server platform adapter class.
	 */
	public Class getAdapterClass() {    
	    return SCAdapter.adapterClassNamed( this.getConfigClassName());
	}
	/**
	 * Returns the runtime class name for the given server platform id.
	 */
	private String getRuntimePlatformClassNameFor( String platformId) {	    
	    return ServerPlatformManager.instance().getRuntimePlatformClassNameForClass(platformId);
	}
	/**
	 * Factory method for this server platform adapter.
	 */
	ServerPlatformAdapter buildAdapter( SessionAdapter parent) {
	    
	    if( this.platformId.equals( ServerPlatformManager.NO_SERVER_ID)) {
			return NullServerPlatformAdapter.instance();
		}

		Constructor adapterConstructor = SCAdapter.adapterConstructor( this.getAdapterClass());
		
		ServerPlatformAdapter adapter = ( ServerPlatformAdapter)SCAdapter.buildsAdapterWith( adapterConstructor, new Object[] { parent });
		return adapter;
	}
}
