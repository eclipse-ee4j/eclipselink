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

import javax.swing.Icon;

import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class ProjectConfig
 * 
 * @see ProjectConfig
 * 
 * @author Tran Le
 */
public abstract class ProjectAdapter extends SCAdapter {
	// property change
	public final static String PROJECT_STRING_PROPERTY = "projectStringConfig";

	/**
	 * Creates a new ProjectAdapter for the specified model object.
	 */
	ProjectAdapter( SCAdapter parent, ProjectConfig scConfig) {
		super( parent, scConfig);
	}
	/**
	 * Creates a new ProjectAdapter.
	 */
	protected ProjectAdapter( SCAdapter parent, String name) {
		super( parent);
		
		this.setName( name);
	}

	public Icon icon() {
		return null;
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final ProjectConfig project() {
		
		return ( ProjectConfig)this.getModel();
	}
	/**
	 * Returns this config model property.
	 */
	public String getName() {
		
		return this.project().getProjectString();
	}
	/**
	 * Sets this config model property.
	 */
	private void setName( String name) {
		
		if (name != null)
			name = name.replace('\\', '/');

		this.project().setProjectString( name);
	}
	
	public boolean isXml() {
	  return false;
	}
  
	public boolean isClass() {
	  return false;
	}
}
