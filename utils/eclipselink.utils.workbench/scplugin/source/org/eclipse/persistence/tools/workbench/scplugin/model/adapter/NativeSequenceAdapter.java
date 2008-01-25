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

import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;

import org.eclipse.persistence.internal.sessions.factories.model.sequencing.NativeSequenceConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class NativeSequenceConfig
 * 
 * @see NativeSequenceConfig
 * 
 * @author Tran Le
 */
class NativeSequenceAdapter extends SequenceAdapter {
	
	/**
	 * Creates a new NativeSequence for the specified model object.
	 */
	NativeSequenceAdapter( SCAdapter parent, NativeSequenceConfig scConfig) {
			
		super( parent, scConfig);
	}
	/**
	 * Creates a new NativeSequence.
	 */
	protected NativeSequenceAdapter( SCAdapter parent, String name, int preallocationSize) {
			
		super( parent, name, preallocationSize);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final NativeSequenceConfig config() {
			
		return ( NativeSequenceConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new NativeSequenceConfig();
	}

	public boolean isNative() {
			
		return true;
	}	
	
	protected void initializeDefaults() {
		super.initializeDefaults();
		
		LoginAdapter login = ( LoginAdapter)getParent().getParent();
		String platformClass = login.getPlatformClass();
		if( platformClass != null) {
			DatabasePlatform platform = DatabasePlatformRepository.getDefault().platformForRuntimePlatformClassNamed( platformClass);
			
			// Only Oracle support preallocation size for Native Sequencing
			if( !platform.getName().startsWith( "Oracle")) {
				setPreallocationSize( 0);
			}
		}
	}
}
