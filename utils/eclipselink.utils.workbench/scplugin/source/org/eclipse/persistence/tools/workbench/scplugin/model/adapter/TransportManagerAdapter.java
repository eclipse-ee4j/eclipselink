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

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.transport.TransportManagerConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class TransportManagerConfig
 * 
 * @see TransportManagerConfig
 * 
 * @author Tran Le
 */
public abstract class TransportManagerAdapter extends SCAdapter {

	public final static String ON_CONNECTION_ERROR_PROPERTY = "onConnectionErrorConfig";
	
	private static final String DISCARD_CONNECTION = "DiscardConnection";
	private static final String KEEP_CONNECTION = "KeepConnection";

	/**
	 * Creates a new TransportManager for the specified model object.
	 */
	TransportManagerAdapter( SCAdapter parent, TransportManagerConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new TransportManager.
	 */
	protected TransportManagerAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final TransportManagerConfig transportManager() {
		
		return ( TransportManagerConfig)this.getModel();
	}
	/**
	 * Factory method for building a child default JNDINamingServiceAdapter.
	 */
	protected JNDINamingServiceAdapter buildJNDINamingService() {

		return new JNDINamingServiceAdapter( this);
	}
	
	protected void initializeDefaults() {
		super.initializeDefaults();

		if( XMLSessionConfigProject.REMOVE_CONNECTION_ON_ERROR_DEFAULT)
			this.setRemoveConnectionOnError();
	}

	public boolean removeConnectionOnError() {
		
		String value =  this.transportManager().getOnConnectionError();
		
		return (( value != null) && value.equalsIgnoreCase( DISCARD_CONNECTION));
	}
	/**
	 * Convenience method to set the transportManager removeConnectionOnError.
	 */
	public void setRemoveConnectionOnError( boolean remove) {
		
		if( remove)
			this.setRemoveConnectionOnError();
		else
			this.setKeepConnectionOnError();
	}
	
	private void setRemoveConnectionOnError() {
		this.setOnConnectionError( DISCARD_CONNECTION);
	}
	
	private void setKeepConnectionOnError() {
		this.setOnConnectionError( KEEP_CONNECTION);
	}
	
	private void setOnConnectionError( String remove) {
		
		String old = this.transportManager().getOnConnectionError();
		this.transportManager().setOnConnectionError( remove);
		this.firePropertyChanged( ON_CONNECTION_ERROR_PROPERTY, old, remove);
	}	
	
}
