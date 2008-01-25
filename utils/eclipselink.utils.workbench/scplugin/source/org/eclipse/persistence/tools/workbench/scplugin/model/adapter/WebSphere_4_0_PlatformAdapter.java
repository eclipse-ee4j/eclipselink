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

import java.util.List;

import org.eclipse.persistence.tools.workbench.scplugin.SCProblemsConstants;
import org.eclipse.persistence.tools.workbench.scplugin.model.ServerPlatformManager;

import org.eclipse.persistence.internal.sessions.factories.model.platform.WebSphere_4_0_PlatformConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class WebSphere_4_0_PlatformConfig
 * 
 * @see WebSphere_4_0_PlatformConfig
 * 
 * @author Les Davis
 */
public class WebSphere_4_0_PlatformAdapter extends ServerPlatformAdapter {

	/**
	 * Default constructor
	 */
    WebSphere_4_0_PlatformAdapter() {
		super();
	}
	/**
	 * Creates a new Platform for the specified model object.
	 */
    WebSphere_4_0_PlatformAdapter( SCAdapter parent, WebSphere_4_0_PlatformConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new Platform.
	 */
	protected WebSphere_4_0_PlatformAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	@SuppressWarnings("unused")
	private final WebSphere_4_0_PlatformConfig platformConfig() {
		
		return ( WebSphere_4_0_PlatformConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	@Override
	protected Object buildModel() {
		return new WebSphere_4_0_PlatformConfig();
	}
	
	@Override
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		buildObsoleteProblem(currentProblems);
	}
	
	private void buildObsoleteProblem(List problems) {
		problems.add(buildProblem(SCProblemsConstants.OBSOLETE_SERVER_PLATFORM_PROBLEM, ServerPlatformManager.WEBSPHERE_4_0_ID));
	}

}
