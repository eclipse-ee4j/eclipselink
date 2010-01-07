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

import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.EisPlatformManager;

import org.eclipse.persistence.internal.sessions.factories.model.login.EISLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequencingConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class EISLoginConfig
 * 
 * @see EISLoginConfig
 * 
 * @author Tran Le
 */
public final class EISLoginAdapter extends LoginAdapter {
	// property change
	public final static String CONNECTION_FACTORY_URL_PROPERTY = "connectionFactoryURL";
	public final static String CONNECTION_SPEC_CLASS_PROPERTY = "connectionSpecClass";

	/**
	 * Creates a new EISLoginAdapter for the specified model object.
	 */
	EISLoginAdapter( SCAdapter parent, EISLoginConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new DatabaseLoginAdapter.
	 */
	protected EISLoginAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		
		return new EISLoginConfig();
	}
	/**
	 * Factory method for building a child default SequencingAdapter.
	 */
	protected SequencingAdapter buildSequencing() {
		SequencingAdapter sequencing = super.buildSequencing();

		this.login().setSequencingConfig(( SequencingConfig)sequencing.getModel());
		
		return sequencing;
	}
	/**
	 * Returns the datasource platform class from user's preference.
	 */
	protected String getDefaultPlatformClassName() {
	    
        String platformName = this.preferences().get( SCPlugin.EIS_PLATFORM_PREFERENCE, SCPlugin.EIS_PLATFORM_PREFERENCE_DEFAULT);

	    return DataSource.getClassNameForEisPlatform( platformName);
	}

	/**
	 * Returns this connectionFactoryURL.
	 */
	public String getConnectionFactoryURL() {
		
		return this.login().getConnectionFactoryURL();
	}
	/**
	 * Sets this config model datasource platform class.
	 */
	public void setPlatformClass( String platformClassName) {
		super.setPlatformClass( platformClassName);
		
		setDefaultConnectionSpecClassName( platformClassName);
	}

	private void setDefaultConnectionSpecClassName( String platformClassName) {

		String connectionSpecClassName = EisPlatformManager.instance().getRuntimeConnectionSpecClassName( platformClassName);
		setConnectionSpecClassName( connectionSpecClassName);
	}
	/**
	 * Sets this connectionFactoryURL and the config model.
	 */
	public void setConnectionFactoryURL( String url) {
		
		Object old = this.login().getConnectionFactoryURL();

		this.login().setConnectionFactoryURL( url);
		this.firePropertyChanged( CONNECTION_FACTORY_URL_PROPERTY, old, url);
	}
	/**
	 * Returns this connectionSpecClassName.
	 */
	public String getConnectionSpecClassName() {
		
		return this.login().getConnectionSpecClass();
	}
	/**
	 * Sets this connectionSpecClassName and the config model.
	 */
	public void setConnectionSpecClassName( String name) {
		
		Object old = this.login().getConnectionSpecClass();

		this.login().setConnectionSpecClass( name);
		this.firePropertyChanged( CONNECTION_SPEC_CLASS_PROPERTY, old, name);
	}
	
	public void toString( StringBuffer sb) {
		super.toString( sb);
		
		sb.append( ", ").append( this.getConnectionSpecClassName());
	}
    
	boolean platformIsEis() {
		
		return true;
	}
	/**
	 * Returns the adapter's Config Model Object.
	 */
	private final EISLoginConfig login() {
		
		return ( EISLoginConfig)this.getModel();
	}

}
