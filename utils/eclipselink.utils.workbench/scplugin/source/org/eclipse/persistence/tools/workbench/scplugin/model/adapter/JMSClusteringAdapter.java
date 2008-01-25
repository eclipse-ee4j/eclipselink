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
import org.eclipse.persistence.internal.sessions.factories.model.clustering.JMSClusteringConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class JMSClusteringConfig
 * 
 * @see JMSClusteringConfig
 * 
 * @author Tran Le
 */
public final class JMSClusteringAdapter extends JNDIClusteringServiceAdapter {
	// property change
	public final static String TOPIC_CONNECTION_FACTORY_NAME_PROPERTY = "topicConnectionFactoryName";
	public final static String TOPIC_NAME_PROPERTY = "topicName";
	/**
	 * Creates a new JMSClusteringAdapter for the specified model object.
	 */
	JMSClusteringAdapter( SCAdapter parent, JMSClusteringConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new JMSClusteringAdapter.
	 */
	protected JMSClusteringAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final JMSClusteringConfig config() {
		
		return ( JMSClusteringConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new JMSClusteringConfig();
	}
	
	protected void initializeDefaults() {
		super.initializeDefaults();

		setTopicConnectionFactoryName( XMLSessionConfigProject.TOPIC_CONNECTION_FACTORY_NAME_DEFAULT);
		setTopicName( XMLSessionConfigProject.TOPIC_NAME_DEFAULT);
	}
	/**
	 * Returns this TopicConnectionFactoryName.
	 */
	public String getTopicConnectionFactoryName() {
		
		return this.config().getJMSTopicConnectionFactoryName();
	}
	/**
	 * Sets config model.
	 */
	public void setTopicConnectionFactoryName( String name) {
		
		Object old = this.config().getJMSTopicConnectionFactoryName();

		this.config().setJMSTopicConnectionFactoryName( name);
		this.firePropertyChanged( TOPIC_CONNECTION_FACTORY_NAME_PROPERTY, old, name);
	}
	/**
	 * Returns this TopicName.
	 */
	public String getTopicName() {
		
		return this.config().getJMSTopicName();
	}
	/**
	 * Sets config model.
	 */
	public void setTopicName( String name) {
		
		Object old = this.config().getJMSTopicName();

		this.config().setJMSTopicName( name);
		this.firePropertyChanged( TOPIC_NAME_PROPERTY, old, name);
	}
}