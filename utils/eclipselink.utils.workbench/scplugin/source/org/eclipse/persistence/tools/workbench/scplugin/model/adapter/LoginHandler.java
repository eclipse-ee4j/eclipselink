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

/**
 * Defines the interface to an object that contains a DatabaseLogin.
 * 
 * @see DatabaseLoginConfig
 * 
 * @author Tran Le
 */
public interface LoginHandler {

	public LoginAdapter getLogin();
	
	public void setExternalConnectionPooling( boolean value);
	
	public boolean usesExternalConnectionPooling();
	
}
