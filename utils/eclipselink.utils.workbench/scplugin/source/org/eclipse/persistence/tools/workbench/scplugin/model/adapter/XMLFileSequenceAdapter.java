/*
 * @(#)NativeSequenceAdapter.java
 *
 * Copyright 2004 by Oracle Corporation,
 * 500 Oracle Parkway, Redwood Shores, California, 94065, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Oracle Corporation.
 */
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import org.eclipse.persistence.internal.sessions.factories.model.sequencing.NativeSequenceConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.XMLFileSequenceConfig;
import org.eclipse.persistence.tools.workbench.scplugin.model.SequenceType;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class NativeSequenceConfig
 * 
 * @see NativeSequenceConfig
 * 
 * @author Tran Le
 */
public class XMLFileSequenceAdapter extends SequenceAdapter {
	
	/**
	 * Creates a new XMLFileSequence for the specified model object.
	 */
	XMLFileSequenceAdapter( SCAdapter parent, XMLFileSequenceConfig scConfig) {
			
		super( parent, scConfig);
	}
	/**
	 * Creates a new NativeSequence.
	 */
	protected XMLFileSequenceAdapter( SCAdapter parent, String name, int preallocationSize) {
			
		super( parent, name, preallocationSize);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final XMLFileSequenceConfig config() {
			
		return ( XMLFileSequenceConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new XMLFileSequenceConfig();
	}

	@Override
	public boolean isXMLFile() {
		return true;
	}
	
	@Override
	public SequenceType getType() {
		return SequenceType.XML_FILE;
	}
}
