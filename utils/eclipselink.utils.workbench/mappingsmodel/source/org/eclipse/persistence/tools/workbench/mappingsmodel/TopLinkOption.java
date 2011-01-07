/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsmodel;

/**
 * Use this class to wrap an mw String, a resource key and a toplink
 * option.  This simplifies rendering options in the UI, converting 
 * objects to runtime, and persistence. 
 * 
 * The toplinkModelOption is optional, sometimes the runtime uses
 * should*() type methods instead of a simple setter that takes an int or a String.
 * In this case you will not be able to avoid the many if/elses when converting
 * to runtime.
 * 
 * Use the addConversionValueForTopLink static api to set up persistence for
 * TopLinkOption using an ObjectTypeConverter.  The mwModelOption will be displayed in the xml.
 */
public abstract class TopLinkOption {
    /** 
     * model option, this String will be used for persistence and helps to make debuggins easier.
     * If toplink had more useful options (instead of ints and shorts), we would really have no
     * need for this mwModelOption.  The current standard for these is a String of the format 'fooBar' though we
     * could change this to be 'fooBar', 'foo-bar', etc.
     */
	private String mwModelOption;
	
	/** Use this for the UI renderer, just include the reosurceKey in the appropriate resourceBundle */
	private String resourceKey;
	
	/**
	 * Optional if the runtime has separate model options instead of setShould*() methods and no
	 * corresponding model options.
	 */
	private Object topLinkModelOption;
	
	
	protected TopLinkOption(String mwModelString, String resourceKey, Object topLinkModelOption) {
		this.mwModelOption = mwModelString;
		this.resourceKey = resourceKey;
		this.topLinkModelOption = topLinkModelOption;
	}
	
	protected TopLinkOption(String mwModelString, String resourceKey) {
		this.mwModelOption = mwModelString;
		this.resourceKey = resourceKey;
	}	
	
	public String resourceKey() {
		return this.resourceKey;
	}
	
	public String getMWModelOption() {
		return this.mwModelOption;
	}
	
	public Object getTopLinkModelOption() {
		return this.topLinkModelOption;
	}	

	
	/**
	 * Convert the object to runtime, call the appropriate runtime method and
	 * pass it the toplinkModelOption.
	 */
	public abstract void setMWOptionOnTopLinkObject(Object topLinkObject);
	

}
