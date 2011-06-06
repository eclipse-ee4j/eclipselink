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

import java.util.List;

import org.eclipse.persistence.tools.workbench.scplugin.SCProblemsConstants;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


import org.eclipse.persistence.internal.sessions.factories.model.platform.CustomServerPlatformConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class CustomServerPlatformConfig
 * 
 * @see CustomServerPlatformConfig
 * 
 * @author Tran Le
 */
public class CustomServerPlatformAdapter extends ServerPlatformAdapter {
	// property change
	public final static String SERVER_CLASS_NAME_PROPERTY = "serverClassName";
	public final static String EXTERNAL_TRANSACTION_CONTROLLER_CLASS_PROPERTY = "externalTransactionControllerClass";

	/**
	 * Default constructor
	 */
	CustomServerPlatformAdapter() {
		super();
	}
	/**
	 * Creates a new Platform for the specified model object.
	 */
    CustomServerPlatformAdapter( SCAdapter parent, CustomServerPlatformConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new Platform.
	 */
	protected CustomServerPlatformAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final CustomServerPlatformConfig platformConfig() {
		
		return ( CustomServerPlatformConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new CustomServerPlatformConfig();
	}

	public boolean isCustom() {
		
		return true;
	}
	/**
	 * Returns this config model property.
	 */
	public String getServerClassName() {
		
		return this.platformConfig().getServerClassName();
	}
	/**
	 * Sets this config model property.
	 */
	public void setServerClassName( String name) {
		
		Object old = this.platformConfig().getServerClassName();

		this.platformConfig().setServerClassName( name);
		this.firePropertyChanged( SERVER_CLASS_NAME_PROPERTY, old, name);
	}
	/**
	 * Returns this config model property.
	 */
	public String getExternalTransactionControllerClass() {
		
		return this.platformConfig().getExternalTransactionControllerClass();
	}
	/**
	 * Sets this config model property.
	 */
	public void setExternalTransactionControllerClass( String name) {
		
 		Object old = this.platformConfig().getExternalTransactionControllerClass();
		
		this.platformConfig().setExternalTransactionControllerClass( name);
		this.firePropertyChanged( EXTERNAL_TRANSACTION_CONTROLLER_CLASS_PROPERTY, old, name);
	}

	/**
	 * Add any problems from this adapter to the given set.
	 */
	protected void addProblemsTo( List branchProblems) {
	
		super.addProblemsTo(branchProblems);

		verifyExternalTransactionControllerClass(branchProblems);
		verifyServerClassName(branchProblems);
	}

	private void verifyExternalTransactionControllerClass( List branchProblems) {

		String className = getExternalTransactionControllerClass();

		if( StringTools.stringIsEmpty( className)) {
			branchProblems.add( buildProblem( SCProblemsConstants.CUSTOM_SERVER_PLATFORM_JTA, getParent().displayString()));
		}
	}

	private void verifyServerClassName( List branchProblems) {

		String className = getServerClassName();

		if( StringTools.stringIsEmpty( className)) {
			branchProblems.add( buildProblem( SCProblemsConstants.CUSTOM_SERVER_PLATFORM_SERVER_CLASS_NAME, getParent().displayString()));
		}
	}
}
