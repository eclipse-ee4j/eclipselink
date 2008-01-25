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

import org.eclipse.persistence.internal.sessions.factories.model.platform.WebLogic_6_1_PlatformConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class WebLogic_6_1_PlatformConfig
 * 
 * @see WebLogic_6_1_PlatformConfig
 * 
 * @author Tran Le
 */
public class WebLogic_6_1_PlatformAdapter extends ServerPlatformAdapter {

	/**
	 * Default constructor
	 */
    WebLogic_6_1_PlatformAdapter() {
		super();
	}
	/**
	 * Creates a new Platform for the specified model object.
	 */
    WebLogic_6_1_PlatformAdapter( SCAdapter parent, WebLogic_6_1_PlatformConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new Platform.
	 */
	protected WebLogic_6_1_PlatformAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final WebLogic_6_1_PlatformConfig platformConfig() {
		
		return ( WebLogic_6_1_PlatformConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new WebLogic_6_1_PlatformConfig();
	}
	
	@Override
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		buildObsoleteProblem(currentProblems);
	}
	
	private void buildObsoleteProblem(List problems) {
		problems.add(buildProblem(SCProblemsConstants.OBSOLETE_SERVER_PLATFORM_PROBLEM, ServerPlatformManager.WEBLOGIC_6_1_ID));
	}

}
