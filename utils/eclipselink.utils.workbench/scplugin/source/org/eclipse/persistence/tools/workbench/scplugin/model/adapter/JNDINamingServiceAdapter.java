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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.property.PropertyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.JNDINamingServiceConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class JNDINamingServiceConfig
 * 
 * @see JNDINamingServiceConfig
 * 
 * @author Tran Le
 */
final class JNDINamingServiceAdapter extends SCAdapter
												 implements JNDINamingService {

	private Collection properties;

	/**
	 * Creates a new JNDINamingServiceAdapter for the specified model object.
	 */
	JNDINamingServiceAdapter( SCAdapter parent, JNDINamingServiceConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new JNDINamingService.
	 */
	protected JNDINamingServiceAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new JNDINamingServiceConfig();
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final JNDINamingServiceConfig namingService() {
		
		return ( JNDINamingServiceConfig)this.getModel();
	}
	/**
	 * Initializes this adapter from the config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		
		super.initializeFromModel( scConfig);

		if ( this.namingService().getPropertyConfigs() != null) {
			this.properties.addAll( this.adaptAll( this.namingService().getPropertyConfigs()));
		}
	}
	/**
	 * Initializes default value.
	 */
	protected void initializeDefaults() {
	    
		this.setInitialContextFactoryName( XMLSessionConfigProject.INITIAL_CONTEXT_FACTORY_NAME_DEFAULT);
		this.namingService().setPassword( XMLSessionConfigProject.PASSWORD_DEFAULT);
		this.setUserName( XMLSessionConfigProject.USERNAME_DEFAULT);
	}
	/**
	 * Initializes this adapter.
	 */
	protected void initialize() {
		
		super.initialize();
	
		this.properties = new Vector();
	}
	/**
	 * Returns this URL.
	 */
	public String getURL() {
		
		return this.namingService().getURL();
	}
	/**
	 * Sets config model.
	 */
	public void setURL( String url) {

		Object old = this.namingService().getURL();
		this.namingService().setURL( url);
		this.firePropertyChanged( JNDI_URL_PROPERTY, old, url);
	}
	/**
	 * Returns this UserName.
	 */
	public String getUserName() {
		
		return this.namingService().getUsername();
	}
	/**
	 * Sets config model.
	 */
	public void setUserName( String name) {
	
		Object old = this.namingService().getUsername();
		this.namingService().setUsername( name);
		this.firePropertyChanged( JNDI_USER_NAME_PROPERTY, old, name);
	}
	/**
	 * Returns this Password.
	 */
	public String getPassword() {
		
		return this.namingService().getPassword();
	}
	/**
	 * Sets config model.
	 */
	public void setPassword( String pw) {
		
		Object old = this.namingService().getPassword();
		this.namingService().setPassword( pw);
		this.firePropertyChanged( JNDI_PASSWORD_PROPERTY, old, pw);
	}
	/**
	 * Returns this EncryptionClass.
	 */
	String getEncryptionClass() {
		
		return this.namingService().getEncryptionClass();
	}
	/**
	 * Sets config model.
	 */
	void setEncryptionClass( String name) {

		this.namingService().setEncryptionClass( name);
	}
	/**
	 * Returns this InitialContextFactoryName.
	 */
	public String getInitialContextFactoryName() {
		
		return this.namingService().getInitialContextFactoryName();
	}
	/**
	 * Sets config model.
	 */
	public void setInitialContextFactoryName( String name) {

		Object old = this.namingService().getInitialContextFactoryName();
		this.namingService().setInitialContextFactoryName( name);
		this.firePropertyChanged( INITIAL_CONTEXT_FACTORY_NAME_PROPERTY, old, name);
	}
	/**
	 * Returns an iterator on this collection of properties.
	 */
	public Iterator properties() {
		
		return this.getProperties().iterator();
	}
	/**
	 * Returns an iterator on this collection of properties.
	 */
	public int propertySize() {
		
		return this.getProperties().size();
	}
	/**
	 * Returns the collection of properties from the config model.
	 */
	private Collection getProperties() {
		
		return this.properties;
	}
	/**
	 * Returns the collection of properties from the config model.
	 */
	List getPropertyConfigs() {

		if ( this.namingService().getPropertyConfigs() == null)
			this.namingService().setPropertyConfigs( new Vector());

		return this.namingService().getPropertyConfigs();
	}
	private PropertyAdapter buildPropertyAdapter(String name, String value) {

		return new PropertyAdapter( this, buildPropertyConfig( name, value));
	}

	private PropertyConfig buildPropertyConfig( String name, String value) {

		PropertyConfig config = new PropertyConfig();
		config.setName(name);
		config.setValue(value);
		return config;
	}
	/**
	 * Adds the given properties and fire notification.
	 */
	public PropertyAdapter addProperty( String name, String value) {

		PropertyAdapter property = this.buildPropertyAdapter( name, value);
		this.getPropertyConfigs().add( property.propertyConfig());
		this.addItemToCollection( property, getProperties(), PROPERTY_COLLECTION);
		return property;
	}
	/**
	 * Removes the given properties and fire notification.
	 */
	public void removeProperty( PropertyAdapter property) {
		
		this.getPropertyConfigs().remove( property.propertyConfig());
		this.removeItemFromCollection( property, getProperties(), PROPERTY_COLLECTION);
	}
}
