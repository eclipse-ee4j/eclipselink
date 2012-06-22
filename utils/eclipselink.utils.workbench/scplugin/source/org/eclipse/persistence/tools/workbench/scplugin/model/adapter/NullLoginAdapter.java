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

/**
 * Session Configuration model adapter class for Null Login
 * 
 * @author Tran Le
 */
public class NullLoginAdapter extends LoginAdapter {

	// singleton
	private static NullLoginAdapter INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized LoginAdapter instance() {
		if( INSTANCE == null) {
			INSTANCE = new NullLoginAdapter();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private NullLoginAdapter() {
		super();
	}
	
	protected Object buildModel() {

		return null;
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
	/**
	 * Returns this config model property.
	 */
	public String getEncryptionClass() {
		
		return "";
	}
	/**
	 * Returns this config model property.
	 */
	public String getPassword() {
		
		return "";
	}
	@Override
	public Boolean isSavePassword() {
		return Boolean.FALSE;
	}
	
	@Override
	public Boolean isSaveUsername() {
		return Boolean.FALSE;
	}
	
	/**
	 * Returns this config model property.
	 */
	public String getPlatformClass() {
		
		return "";
	}
	/**
	 * Returns this config model property.
	 */
	public String getPlatformName() {
		
		return "";
	}
	/**
	 * Returns this config model property.
	 */
	public String getTableQualifier() {
		
		return "";
	}
	/**
	 * Returns this userName.
	 */
	public String getUserName() {
		
		return "";
	}
	
	/**
	 * Sets this config model property.
	 */
	public void setEncryptionClass( String value) {
	}	
	/**
	* Sets usesExternalConnectionPooling and the config model.
	*/
    public void setExternalConnectionPooling( boolean value) {
    }	
	/**
	 * Sets this config model property.
	 */
	public void setPassword( String value) {
	}

	@Override
	public void setSavePassword(Boolean savePassword) {
	}

	@Override
	public void setSaveUsername(Boolean saveUsername) {
	}

	/**
	 * Sets this config model property.
	 */
	public void setPlatformClass( String value) {
	}	
	/**
	 * Sets this config model property.
	 */
	public void setTableQualifier( String value) {
	}	
	/**
	 * Sets this userName and the config model.
	 */
	public void setUserName( String name) {
	}	
	/**
	 * Sets this config model property.
	 */
	public void setUsesExternalTransactionController( boolean value) {
	}	
	/**
	 * Returns usesExternalConnectionPooling.
	 */
	public boolean usesExternalConnectionPooling() {
			
		return false;
	}	
	/**
	 * Returns this config model property..
	 */
	public boolean usesExternalTransactionController() {
			
		return false;
	}
}
