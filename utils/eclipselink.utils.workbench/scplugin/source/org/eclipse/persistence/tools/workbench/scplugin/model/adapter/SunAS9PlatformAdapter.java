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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import org.eclipse.persistence.internal.sessions.factories.model.platform.SunAS9PlatformConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class SunAS9PlatformConfig
 * 
 * @see SunAS9PlatformConfig
 * 
 * @author Les Davis
 */
public final class SunAS9PlatformAdapter extends ServerPlatformAdapter {

	/**
	* Default constructor
	*/
	SunAS9PlatformAdapter() {
			super();
		}
		
		/**
		* Creates a new Platform for the specified model object.
		*/
	SunAS9PlatformAdapter( SCAdapter parent, SunAS9PlatformConfig scConfig) {
			super( parent, scConfig);
		}
			
		/**
		* Creates a new Platform.
		*/
		protected SunAS9PlatformAdapter( SCAdapter parent) {
			super( parent);
		}
		
		/**
		* Returns this Config Model Object.
		*/
		private final SunAS9PlatformConfig platformConfig() {
			return ( SunAS9PlatformConfig)this.getModel();
		}
		
		/**
		* Factory method for building this model.
		*/
		protected Object buildModel() {
			return new SunAS9PlatformConfig();
		}

}
