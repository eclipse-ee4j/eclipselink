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

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.transport.JMSTopicTransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.JNDINamingServiceConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class JMSTopicTransportManagerConfig
 * 
 * @see JMSTopicTransportManagerConfig
 * 
 * @author Tran Le
 */
public final class JMSTopicTransportManagerAdapter extends TransportManagerAdapter implements JNDINamingService {
	// property change
	public final static String JNDI_NAMING_SERVICE_PROPERTY = "jndiNamingService";
	private volatile JNDINamingServiceAdapter namingService;
	public final static String TOPIC_HOST_URL_PROPERTY = "topicHostURL";
	public final static String TOPIC_CONNECTION_FACTORY_NAME_PROPERTY = "topicConnectionFactoryName";
	public final static String TOPIC_NAME_PROPERTY = "topicName";
	// JNDI property change
	public final static String JNDI_ENCRYPTION_CLASS_PROPERTY = "encryptionClass";
	public final static String JNDI_PROPERTIES_COLLECTION = "properties";
	/**
	 * Creates a new TransportManager for the specified model object.
	 */
	JMSTopicTransportManagerAdapter( SCAdapter parent, JMSTopicTransportManagerConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new TransportManager.
	 */
	protected JMSTopicTransportManagerAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final JMSTopicTransportManagerConfig manager() {
		
		return ( JMSTopicTransportManagerConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new JMSTopicTransportManagerConfig();
	}
	/**
	 * Initializes this adapter from the config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		super.initializeFromModel( scConfig);

		this.namingService = ( JNDINamingServiceAdapter)this.adapt(
				this.manager().getJNDINamingServiceConfig());
	}
	/**
	 * Initializes default value.
	 */
	protected void initializeDefaults() {
		super.initializeDefaults();

		setTopicConnectionFactoryName( XMLSessionConfigProject.TOPIC_CONNECTION_FACTORY_NAME_DEFAULT);
		setTopicName( XMLSessionConfigProject.TOPIC_NAME_DEFAULT);
	}
	/**
	 * Initializes this new model.
	 */
	protected void initialize( Object newConfig) {
		super.initialize( newConfig);

		this.setJNDINamingService( buildJNDINamingService());
	}
	/**
	 * Adds the children of this adapter to the given list.
	 * @param children The list of children
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);

		children.add( namingService);
	}
	/**
	 * Returns this TopicHostURL.
	 */
	public String getTopicHostURL() {
		
		return this.manager().getTopicHostURL();
	}
	/**
	 * Sets config model.
	 */
	public void setTopicHostURL( String host) {
		
		Object old = this.manager().getTopicHostURL();

		this.manager().setTopicHostURL( host);
		this.firePropertyChanged( TOPIC_HOST_URL_PROPERTY, old, host);
	}
	/**
	 * Returns this TopicConnectionFactoryName.
	 */
	public String getTopicConnectionFactoryName() {
		
		return this.manager().getTopicConnectionFactoryName();
	}
	/**
	 * Sets config model.
	 */
	public void setTopicConnectionFactoryName( String name) {
		
		Object old = this.manager().getTopicConnectionFactoryName();

		this.manager().setTopicConnectionFactoryName( name);
		this.firePropertyChanged( TOPIC_CONNECTION_FACTORY_NAME_PROPERTY, old, name);
	}
	/**
	 * Returns this TopicName.
	 */
	public String getTopicName() {
		
		return this.manager().getTopicName();
	}
	/**
	 * Sets config model.
	 */
	public void setTopicName( String name) {
		
		Object old = this.manager().getTopicName();

		this.manager().setTopicName( name);
		this.firePropertyChanged( TOPIC_NAME_PROPERTY, old, name);
	}
	/**
	 *  Sets this JNDINamingService.
	 */
	private void setJNDINamingService( JNDINamingServiceAdapter service) {
		
		Object old = this.namingService;

		this.namingService = service;
		this.manager().setJNDINamingServiceConfig(( JNDINamingServiceConfig)service.getModel());
		this.firePropertyChanged( JNDI_NAMING_SERVICE_PROPERTY, old, service);
	}
	// ********** Convinience methods to JNDI NamingService behaviors **********
	/**
	 * Returns jndiNamingService URL.
	 */
	public String getURL() {
		
		return this.namingService.getURL();
	}
	/**
	 * Sets config model.
	 */
	public void setURL( String url) {

		this.namingService.setURL( url);
	}
	/**
	 * Returns jndiNamingService UserName.
	 */
	public String getUserName() {
		
		return this.namingService.getUserName();
	}
	/**
	 * Sets config model.
	 */
	public void setUserName( String name) {

		Object old = this.namingService.getUserName();

		this.namingService.setUserName( name);
		this.firePropertyChanged( JNDI_USER_NAME_PROPERTY, old, name);
	}
	/**
	 * Returns jndiNamingService Password.
	 */
	public String getPassword() {
		
		return this.namingService.getPassword();
	}
	/**
	 * Sets config model.
	 */
	public void setPassword( String pw) {

		Object old = this.namingService.getPassword();

		this.namingService.setPassword( pw);
		this.firePropertyChanged( JNDI_PASSWORD_PROPERTY, old, pw);
	}
	/**
	 * Returns this EncryptionClass.
	 */
	public String getEncryptionClass() {
		
		return this.namingService.getEncryptionClass();
	}
	/**
	 * Sets config model.
	 */
	public void setEncryptionClass( String name) {
		
		Object old = this.namingService.getEncryptionClass();

		this.namingService.setEncryptionClass( name);
		this.firePropertyChanged( JNDI_ENCRYPTION_CLASS_PROPERTY, old, name);
	}
	/**
	 * Returns this InitialContextFactoryName.
	 */
	public String getInitialContextFactoryName() {
		
		return this.namingService.getInitialContextFactoryName();
	}
	/**
	 * Sets config model.
	 */
	public void setInitialContextFactoryName( String name) {
		
		Object old = this.namingService.getInitialContextFactoryName();

		this.namingService.setInitialContextFactoryName( name);
		this.firePropertyChanged( INITIAL_CONTEXT_FACTORY_NAME_PROPERTY, old, name);
	}
	/**
	 * Returns an iterator on this collection of properties.
	 */
	public Iterator properties() {
		
		return this.namingService.properties();
	}
	/**
	 * Returns the count of property.
	 */
	public int propertySize() {
		
		return this.namingService.propertySize();
	}
	/**
	 * Adds the given properties and fire notification.
	 */
	public PropertyAdapter addProperty( String name, String value) {

		PropertyAdapter property = this.namingService.addProperty( name, value);
		this.fireItemAdded( PROPERTY_COLLECTION, property);
		return property;
	}
	/**
	 * Removes the given properties and fire notification.
	 */
	public void removeProperty(PropertyAdapter property) {

		this.namingService.removeProperty(property);
		this.fireItemRemoved( PROPERTY_COLLECTION, property);
	}
}
