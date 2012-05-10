/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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


import org.eclipse.persistence.internal.sessions.factories.model.transport.UserDefinedTransportManagerConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class UserDefinedTransportManagerConfig
 * 
 * @see UserDefinedTransportManagerConfig
 * 
 * @author Tran Le
 */
public final class UserDefinedTransportManagerAdapter extends TransportManagerAdapter {
	// property change
	public final static String TRANSPORT_CLASS_PROPERTY = "transportClassConfig";

	/**
	 * Creates a new UserDefinedTransportManagerAdapter for the specified model object.
	 */
	UserDefinedTransportManagerAdapter( SCAdapter parent, UserDefinedTransportManagerConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new UserDefinedTransportManager.
	 */
	protected UserDefinedTransportManagerAdapter( SCAdapter parent) {	
		super( parent);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new UserDefinedTransportManagerConfig();
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final UserDefinedTransportManagerConfig manager() {
		
		return ( UserDefinedTransportManagerConfig)this.getModel();
	}
	/**
	 * Returns this TransportClass.
	 */
	public String getTransportClass() {
		
		return this.manager().getTransportClass();
	}
	/**
	 * Sets config model.
	 */
	public void setTransportClass( String name) {
		
		Object old = this.manager().getTransportClass();

		this.manager().setTransportClass( name);
		this.firePropertyChanged( TRANSPORT_CLASS_PROPERTY, old, name);
	}

	/**
	 * Add any problems from this adapter to the given set.
	 */
	protected void addProblemsTo( List branchProblems) {

		super.addProblemsTo(branchProblems);

		verifyProblemTransportClass(branchProblems);
	}

	private void verifyProblemTransportClass( List branchProblems) {

		if( StringTools.stringIsEmpty( getTransportClass())) {

			branchProblems.add(buildProblem(SCProblemsConstants.USER_DEFINED_TRANSPORT_CLASS, displayString()));
		}
	}
}
