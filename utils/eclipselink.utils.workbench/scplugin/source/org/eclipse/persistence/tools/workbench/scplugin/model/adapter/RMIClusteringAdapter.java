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

import org.eclipse.persistence.internal.sessions.factories.model.clustering.RMIClusteringConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class RMIClusteringConfig
 * 
 * @see RMIClusteringConfig
 * 
 * @author Tran Le
 */
public final class RMIClusteringAdapter extends ClusteringServiceAdapter {

	/**
	 * Creates a new JMSClusteringAdapter for the specified model object.
	 */
	RMIClusteringAdapter( SCAdapter parent, RMIClusteringConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new JMSClusteringAdapter.
	 */
	protected RMIClusteringAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final RMIClusteringConfig clustering() {
		
		return ( RMIClusteringConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new RMIClusteringConfig();
	}
}