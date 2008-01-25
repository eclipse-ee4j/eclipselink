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

import org.eclipse.persistence.internal.sessions.factories.model.platform.oc4j.Oc4j_10_1_2_PlatformConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class Oc4j_10_1_2_PlatformConfig
 * 
 * @see Oc4j_10_1_2_PlatformConfig
 * 
 * @author Tran Le
 */
public class Oc4j_10_1_2_PlatformAdapter extends ServerPlatformAdapter {

	/**
	 * Default constructor
	 */
    Oc4j_10_1_2_PlatformAdapter() {
		super();
	}
	/**
	 * Creates a new Platform for the specified model object.
	 */
    Oc4j_10_1_2_PlatformAdapter( SCAdapter parent, Oc4j_10_1_2_PlatformConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new Platform.
	 */
	protected Oc4j_10_1_2_PlatformAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final Oc4j_10_1_2_PlatformConfig platformConfig() {
		
		return ( Oc4j_10_1_2_PlatformConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new Oc4j_10_1_2_PlatformConfig();
	}
	
	@Override
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		buildObsoleteProblem(currentProblems);
	}
	
	private void buildObsoleteProblem(List problems) {
		problems.add(buildProblem(SCProblemsConstants.OBSOLETE_SERVER_PLATFORM_PROBLEM, ServerPlatformManager.OC4J_10_1_2_ID));
	}

}
