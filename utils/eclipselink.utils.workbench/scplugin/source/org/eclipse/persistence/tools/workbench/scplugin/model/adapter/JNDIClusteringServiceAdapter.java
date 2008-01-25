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

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.clustering.JNDIClusteringServiceConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class JNDIClusteringServiceConfig
 * 
 * @see JNDIClusteringServiceConfig
 * 
 * @author Tran Le
 */
public abstract class JNDIClusteringServiceAdapter extends ClusteringServiceAdapter {
	// property change
	public final static String JNDI_USER_NAME_PROPERTY = "jndiUsername";
	public final static String JNDI_PASSWORD_PROPERTY = "jndiPassword";
	public final static String NAMING_SERVICE_INITIAL_CONTEXT_FACTORY_NAME_PROPERTY = "namingServiceInitialContextFactoryName";

	/**
	 * Creates a new JNDIClusteringServiceAdapter for the specified model object.
	 */
	JNDIClusteringServiceAdapter( SCAdapter parent, JNDIClusteringServiceConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new JNDIClusteringServiceAdapter.
	 */
	protected JNDIClusteringServiceAdapter( SCAdapter parent) {
		
		super( parent);
	}
	
	protected void initializeDefaults() {
		super.initializeDefaults();

		setJNDIPassword( XMLSessionConfigProject.PASSWORD_DEFAULT);
		setJNDIUsername( XMLSessionConfigProject.USERNAME_DEFAULT);
		setNamingServiceInitialContextFactoryName( XMLSessionConfigProject.INITIAL_CONTEXT_FACTORY_NAME_DEFAULT);
	}
	/**
	 * Returns config.
	 */
	public String getJNDIUsername() {
		
		return this.jndiClusteringConfig().getJNDIUsername();
	}
	/**
	 * Sets config model.
	 */
	public void setJNDIUsername( String name) {
		
		Object old = this.jndiClusteringConfig().getJNDIUsername();

		this.jndiClusteringConfig().setJNDIUsername( name);
		this.firePropertyChanged( JNDI_USER_NAME_PROPERTY, old, name);
	}
	/**
	 * Returns config.
	 */
	public String getJNDIPassword() {
		
		return this.jndiClusteringConfig().getJNDIPassword();
	}
	/**
	 * Sets config model.
	 */
	public void setJNDIPassword( String pw) {
		
		Object old = this.jndiClusteringConfig().getJNDIPassword();

		this.jndiClusteringConfig().setJNDIPassword( pw);
		this.firePropertyChanged( JNDI_PASSWORD_PROPERTY, old, pw);
	}
	/**
	 * Returns config.
	 */
	public String getNamingServiceInitialContextFactoryName() {
		
		return this.jndiClusteringConfig().getNamingServiceInitialContextFactoryName();
	}
	/**
	 * Sets config model.
	 */
	public void setNamingServiceInitialContextFactoryName( String name) {
		
		Object old = this.jndiClusteringConfig().getNamingServiceInitialContextFactoryName();

		this.jndiClusteringConfig().setNamingServiceInitialContextFactoryName( name);
		this.firePropertyChanged( NAMING_SERVICE_INITIAL_CONTEXT_FACTORY_NAME_PROPERTY, old, name);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final JNDIClusteringServiceConfig jndiClusteringConfig() {
		
		return ( JNDIClusteringServiceConfig)this.getModel();
	}
}
