/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import org.eclipse.persistence.internal.sessions.factories.model.login.XMLLoginConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class XMLLoginConfig
 * 
 * @see XMLLoginConfig
 * 
 * @author Tran Le
 */
public final class XMLLoginAdapter extends LoginAdapter {

    	/**
    	 * Creates a new XMLLoginConfig for the specified model object.
    	 */
    XMLLoginAdapter( SCAdapter parent, XMLLoginConfig scConfig) {
    		
    		super( parent, scConfig);
    	}
    	/**
    	 * Creates a new XMLLoginAdapter.
    	 */
    	protected XMLLoginAdapter( SCAdapter parent) {
    		
    		super( parent);
    	}
    	/**
    	 * Factory method for building this model.
    	 */
    	protected Object buildModel() {
    		
    		return new XMLLoginConfig();
    	}
    	/**
    	 * Returns the datasource platform.
    	 */
    	public String getPlatformName() {
    	    return "";
    	}
    	/**
    	 * Returns the datasource platform class from user's preference.
    	 */
    	protected String getDefaultPlatformClassName() {

    	    return null;
    	}
        /**
         * Returns true when this uses the default Platform Class.
         */
        protected boolean platformClassIsDefault() {
            return true;
        }
        
    	boolean platformIsXml() {
    		
    		return true;
    	}
    	/**
    	 * Returns the adapter's Config Model Object.
    	 */
    	private final XMLLoginConfig login() {
    		
    		return ( XMLLoginConfig)this.getModel();
    	}

    }
